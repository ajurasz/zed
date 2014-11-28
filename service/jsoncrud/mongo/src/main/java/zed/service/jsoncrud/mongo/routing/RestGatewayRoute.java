package zed.service.jsoncrud.mongo.routing;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.rest.RestBindingMode;
import org.springframework.stereotype.Component;

import static org.apache.camel.component.mongodb.MongoDbConstants.COLLECTION;
import static zed.service.jsoncrud.mongo.bson.BsonMapperProcessor.mapBsonToJson;
import static zed.service.jsoncrud.mongo.bson.BsonMapperProcessor.mapJsonToBson;
import static zed.service.jsoncrud.mongo.query.MongoQueryBuilderProcessor.queryBuilder;

@Component
public class RestGatewayRoute extends RouteBuilder {

    // TODO:CAMEL Collection should not be required for dynamic endpoints
    private static final String BASE_MONGO_ENDPOINT = "mongodb:mongo?database=zed_json_crud&collection=default&dynamicity=true&operation=";

    @Override
    public void configure() throws Exception {

        // REST API facade

        restConfiguration().component("netty-http").host("0.0.0.0").port(18080).bindingMode(RestBindingMode.auto);

        rest("/api/jsonCrud").
                post("/save/{collection}").route().
                setBody().groovy("new zed.service.jsoncrud.mongo.routing.SaveOperation(request.headers['collection'], request.body)").
                to("direct:save").setBody().groovy("request.body.toString()");

        rest("/api/jsonCrud").
                get("/count/{collection}").route().
                // TODO:CAMEL Bind 'body' and 'headers' to Groovy script
                        // TODO:CAMEL Auto imports for Groovy? http://mrhaki.blogspot.com/2011/06/groovy-goodness-add-imports.html
                        setBody().groovy("new zed.service.jsoncrud.mongo.routing.CountOperation(request.headers['collection'])").
                to("direct:count");

        rest("/api/jsonCrud").
                get("/findOne/{collection}/{oid}").route().
                setBody().groovy("new zed.service.jsoncrud.mongo.routing.FindOneOperation(request.headers['collection'], request.headers['oid'])").
                to("direct:findOne");

        rest("/api/jsonCrud").
                post("/findByQuery/{collection}").route().
                setBody().groovy("new zed.service.jsoncrud.mongo.routing.FindByQueryOperation(request.headers['collection'], request.body)").
                to("direct:findByQuery");

        rest("/api/jsonCrud").
                post("/countByQuery/{collection}").route().
                setBody().groovy("new zed.service.jsoncrud.mongo.routing.CountByQueryOperation(request.headers['collection'], request.body)").
                to("direct:countByQuery");

        // Operations handlers

        from("direct:save").
                setHeader(COLLECTION).groovy("request.body.collection").
                setBody().groovy("request.body.pojo").
                convertBodyTo(DBObject.class). // FIXED:CAMEL-7996
                process(mapJsonToBson()).
                setProperty("original", body()).
                // TODO:CAMEL
                        to(BASE_MONGO_ENDPOINT + "save").
                setBody().groovy("exchange.properties['original'].get('_id')");

        from("direct:findOne").
                setHeader(COLLECTION).groovy("request.body.collection").
                setBody().groovy("new org.bson.types.ObjectId(request.body.oid)").
                to(BASE_MONGO_ENDPOINT + "findById").
                process(mapBsonToJson());

        from("direct:findByQuery").
                setHeader(COLLECTION).groovy("request.body.collection").
                setBody().groovy("request.body.queryBuilder.query").
                process(queryBuilder()).
                to(BASE_MONGO_ENDPOINT + "findAll").
                process(mapBsonToJson());

        from("direct:count").
                setHeader(COLLECTION).groovy("request.body.collection").
                setBody().constant(new BasicDBObject()).
                to(BASE_MONGO_ENDPOINT + "count");

        from("direct:countByQuery").
                setHeader(COLLECTION).groovy("request.body.collection").
                setBody().groovy("request.body.queryBuilder.query").
                process(queryBuilder()).
                to(BASE_MONGO_ENDPOINT + "count");

    }

}
