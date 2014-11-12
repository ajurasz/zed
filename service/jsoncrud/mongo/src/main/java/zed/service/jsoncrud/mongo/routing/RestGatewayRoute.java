package zed.service.jsoncrud.mongo.routing;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mongodb.MongoDbConstants;
import org.apache.camel.model.rest.RestBindingMode;
import org.springframework.stereotype.Component;

@Component
public class RestGatewayRoute extends RouteBuilder {

    // TODO:CAMEL Collection should not be required for dynamic endpoints
    private static final String BASE_MONGO_ENDPOINT = "mongodb:mongoClient?database=zed_json_crud&collection=default&dynamicity=true&operation=";

    @Override
    public void configure() throws Exception {

        // REST API facade

        restConfiguration().component("netty-http").host("0.0.0.0").port(18080).bindingMode(RestBindingMode.auto);

        rest("/api/jsonCrud").
                get("/count/{collection}").route().
                // TODO:CAMEL Bind 'body' and 'headers' to Groovy script
                        // TODO:CAMEL Auto imports for Groovy? http://mrhaki.blogspot.com/2011/06/groovy-goodness-add-imports.html
                        setBody().groovy("new zed.service.jsoncrud.mongo.routing.CountOperation(request.headers['collection'])").
                to("direct:count");

        // Operations handlers

        from("direct:save").
                convertBodyTo(DBObject.class). // FIXED:CAMEL-7996
                setProperty("original", body()).
                // TODO:CAMEL
                        to(BASE_MONGO_ENDPOINT + "insert").
                setBody().groovy("exchange.properties['original'].get('_id')");

        from("direct:findOne").
                to(BASE_MONGO_ENDPOINT + "findById");

        from("direct:findByQuery").
                to(BASE_MONGO_ENDPOINT + "findAll");

        from("direct:count").
                setHeader(MongoDbConstants.COLLECTION).groovy("request.body.collection").
                setBody().constant(new BasicDBObject()).
                to(BASE_MONGO_ENDPOINT + "count");

        from("direct:countByQuery").
                to(BASE_MONGO_ENDPOINT + "count");

    }

}
