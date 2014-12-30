package zed.service.document.mongo.routing;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.rest.RestBindingMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

import static java.lang.String.format;
import static org.apache.camel.component.mongodb.MongoDbConstants.COLLECTION;
import static org.apache.camel.component.mongodb.MongoDbConstants.LIMIT;
import static org.apache.camel.component.mongodb.MongoDbConstants.NUM_TO_SKIP;
import static org.apache.camel.component.mongodb.MongoDbConstants.SORT_BY;
import static zed.service.document.mongo.bson.BsonMapperProcessor.mapBsonToJson;
import static zed.service.document.mongo.bson.BsonMapperProcessor.mapJsonToBson;
import static zed.service.document.mongo.query.MongoDbSortConditionExpression.sortCondition;
import static zed.service.document.mongo.query.MongoQueryBuilderProcessor.queryBuilder;

@Component
public class RestGatewayRoute extends RouteBuilder {

    private final String documentsDbName;

    private final int restPort;

    @Autowired
    public RestGatewayRoute(
            @Value("${zed.service.document.mongo.db:zed_service_document}") String documentsDbName,
            @Value("${zed.service.api.port:15001}") int restPort) {
        this.documentsDbName = documentsDbName;
        this.restPort = restPort;
    }

    @Override
    public void configure() throws Exception {

        // REST API facade

        restConfiguration().component("netty-http").host("0.0.0.0").port(restPort).bindingMode(RestBindingMode.auto);

        rest("/api/document").
                post("/save/{collection}").type(Object.class).route().
                setBody().groovy("new zed.service.document.mongo.routing.SaveOperation(headers['collection'], body)").
                to("direct:save");

        rest("/api/document").
                get("/count/{collection}").route().
                setBody().groovy("new zed.service.document.mongo.routing.CountOperation(headers['collection'])").
                to("direct:count");

        rest("/api/document").
                get("/findOne/{collection}/{id}").route().
                setBody().groovy("new zed.service.document.mongo.routing.FindOneOperation(headers['collection'], headers['id'])").
                to("direct:findOne");

        rest("/api/document").
                post("/findMany/{collection}").type(List.class).route().
                setBody().groovy("new zed.service.document.mongo.routing.FindManyOperation(headers['collection'], body)").
                to("direct:findMany");

        rest("/api/document").
                post("/findByQuery/{collection}").route().
                setBody().groovy("new zed.service.document.mongo.routing.FindByQueryOperation(headers['collection'], body)").
                to("direct:findByQuery");

        rest("/api/document").
                post("/countByQuery/{collection}").route().
                setBody().groovy("new zed.service.document.mongo.routing.CountByQueryOperation(headers['collection'], body)").
                to("direct:countByQuery");

        rest("/api/document").
                delete("/remove/{collection}/{id}").route().
                setBody().groovy("new zed.service.document.mongo.routing.RemoveOperation(headers['collection'], headers['id'])").
                to("direct:remove");

        // Operations handlers

        from("direct:save").
                setHeader(COLLECTION).groovy("body.collection").
                setBody().groovy("body.pojo").
                convertBodyTo(DBObject.class). // FIXED:CAMEL-7996
                process(mapJsonToBson()).
                setProperty("original", body()).
                // TODO:CAMEL
                        to(baseMongoDbEndpoint() + "save").
                setBody().groovy("exchange.properties['original'].get('_id').toString()");

        from("direct:findOne").
                setHeader(COLLECTION).groovy("body.collection").
                setBody().groovy("new org.bson.types.ObjectId(body.id)").
                to(baseMongoDbEndpoint() + "findById").
                process(mapBsonToJson());

        from("direct:findMany").
                setHeader(COLLECTION).groovy("body.collection").
                setBody().groovy("new com.mongodb.BasicDBObject('_id', new com.mongodb.BasicDBObject('$in', body.ids.collect{new org.bson.types.ObjectId(it)}))").
                to(baseMongoDbEndpoint() + "findAll").
                process(mapBsonToJson());

        from("direct:findByQuery").
                setHeader(COLLECTION).groovy("body.collection").
                setHeader(LIMIT).groovy("body.queryBuilder.size").
                setHeader(NUM_TO_SKIP).groovy("body.queryBuilder.page * body.queryBuilder.size").
                setHeader(SORT_BY).expression(sortCondition()).
                setBody().groovy("body.queryBuilder.query").
                process(queryBuilder()).
                to(baseMongoDbEndpoint() + "findAll").
                process(mapBsonToJson());

        from("direct:count").
                setHeader(COLLECTION).groovy("body.collection").
                setBody().constant(new BasicDBObject()).
                to(baseMongoDbEndpoint() + "count");

        from("direct:countByQuery").
                setHeader(COLLECTION).groovy("body.collection").
                setBody().groovy("body.queryBuilder.query").
                process(queryBuilder()).
                to(baseMongoDbEndpoint() + "count");

        from("direct:remove").
                setHeader(COLLECTION).groovy("body.collection").
                setBody().groovy("new com.mongodb.BasicDBObject('_id', new org.bson.types.ObjectId(body.id))").
                to(baseMongoDbEndpoint() + "remove").
                process(mapBsonToJson());

    }

    private String baseMongoDbEndpoint() {
        // TODO:CAMEL Collection should not be required for dynamic endpoints
        return format("mongodb:mongo?database=%s&collection=default&dynamicity=true&operation=", documentsDbName);
    }

}
