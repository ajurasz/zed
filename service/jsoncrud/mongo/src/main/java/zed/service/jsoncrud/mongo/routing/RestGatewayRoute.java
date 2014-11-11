package zed.service.jsoncrud.mongo.routing;

import com.mongodb.DBObject;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class RestGatewayRoute extends RouteBuilder {

    // TODO:CAMEL Collection should not be required for dynamic endpoints
    private static final String BASE_MONGO_ENDPOINT = "mongodb:mongoClient?database=zed_json_crud&collection=default&dynamicity=true&operation=";

    @Override
    public void configure() throws Exception {
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
                to(BASE_MONGO_ENDPOINT + "count");

    }

}
