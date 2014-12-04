package zed.service.document.mongo.routing;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.rest.RestBindingMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

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
                setBody().groovy("new zed.service.document.mongo.routing.SaveOperation(request.headers['collection'], request.body)").
                to("direct:save").setBody().groovy("request.body.toString()");

        rest("/api/document").
                get("/count/{collection}").route().
                // TODO:CAMEL Bind 'body' and 'headers' to Groovy script
                        // TODO:CAMEL Auto imports for Groovy? http://mrhaki.blogspot.com/2011/06/groovy-goodness-add-imports.html
                        setBody().groovy("new zed.service.document.mongo.routing.CountOperation(request.headers['collection'])").
                to("direct:count");

        rest("/api/document").
                get("/findOne/{collection}/{oid}").route().
                setBody().groovy("new zed.service.document.mongo.routing.FindOneOperation(request.headers['collection'], request.headers['oid'])").
                to("direct:findOne");

        rest("/api/document").
                post("/findByQuery/{collection}").route().
                setBody().groovy("new zed.service.document.mongo.routing.FindByQueryOperation(request.headers['collection'], request.body)").
                to("direct:findByQuery");

        rest("/api/document").
                post("/countByQuery/{collection}").route().
                setBody().groovy("new zed.service.document.mongo.routing.CountByQueryOperation(request.headers['collection'], request.body)").
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
