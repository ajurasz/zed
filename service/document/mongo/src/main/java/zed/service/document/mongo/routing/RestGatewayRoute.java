package zed.service.document.mongo.routing;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.rest.RestBindingMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

import static org.apache.camel.component.mongodb.MongoDbConstants.COLLECTION;
import static zed.service.document.mongo.bson.BsonMapperProcessor.mapBsonToJson;
import static zed.service.document.mongo.bson.BsonMapperProcessor.mapJsonToBson;
import static zed.service.document.mongo.query.MongoQueryBuilderProcessor.queryBuilder;

@Component
public class RestGatewayRoute extends RouteBuilder {

    // TODO:CAMEL Collection should not be required for dynamic endpoints
    private static final String BASE_MONGO_ENDPOINT = "mongodb:mongo?database=zed_json_crud&collection=default&dynamicity=true&operation=";

    private final int restPort;

    @Autowired
    public RestGatewayRoute(@Value("${zed.service.document.rest.port:18080}") int restPort) {
        this.restPort = restPort;
    }

    @Override
    public void configure() throws Exception {

        // REST API facade

        restConfiguration().component("netty-http").host("0.0.0.0").port(restPort).bindingMode(RestBindingMode.auto);

        rest("/api/document").
                post("/save/{collection}").route().
                setBody().groovy("new zed.service.document.mongo.routing.SaveOperation(headers['collection'], body)").
                to("direct:save").setBody().groovy("body.toString()");

        rest("/api/document").
                get("/count/{collection}").route().
                        // TODO:CAMEL Auto imports for Groovy? http://mrhaki.blogspot.com/2011/06/groovy-goodness-add-imports.html
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
                        to(BASE_MONGO_ENDPOINT + "save").
                setBody().groovy("exchange.properties['original'].get('_id')");

        from("direct:findOne").
                setHeader(COLLECTION).groovy("body.collection").
                setBody().groovy("new org.bson.types.ObjectId(body.id)").
                to(BASE_MONGO_ENDPOINT + "findById").
                process(mapBsonToJson());

        from("direct:findMany").
                setHeader(COLLECTION).groovy("body.collection").
                setBody().groovy("new com.mongodb.BasicDBObject('_id', new com.mongodb.BasicDBObject('$in', body.ids.collect{new org.bson.types.ObjectId(it)}))").
                to(BASE_MONGO_ENDPOINT + "findAll").
                process(mapBsonToJson());

        from("direct:findByQuery").
                setHeader(COLLECTION).groovy("body.collection").
                setBody().groovy("body.queryBuilder.query").
                process(queryBuilder()).
                to(BASE_MONGO_ENDPOINT + "findAll").
                process(mapBsonToJson());

        from("direct:count").
                setHeader(COLLECTION).groovy("body.collection").
                setBody().constant(new BasicDBObject()).
                to(BASE_MONGO_ENDPOINT + "count");

        from("direct:countByQuery").
                setHeader(COLLECTION).groovy("body.collection").
                setBody().groovy("body.queryBuilder.query").
                process(queryBuilder()).
                to(BASE_MONGO_ENDPOINT + "count");

        from("direct:remove").
                setHeader(COLLECTION).groovy("body.collection").
                setBody().groovy("new com.mongodb.BasicDBObject('_id', new org.bson.types.ObjectId(body.id))").
                to(BASE_MONGO_ENDPOINT + "remove").
                process(mapBsonToJson());

    }

}
