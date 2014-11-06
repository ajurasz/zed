package zed.service.jsoncrud.mongo.routing;

import com.mongodb.DBObject;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class RestGatewayRoute extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        from("direct:save").
                convertBodyTo(DBObject.class). // <= see CAMEL-7996
                setProperty("original", body()).
                // TODO:CAMEL
                        // Collection should not be required for dynamic endpoints
                        to("mongodb:mongoClient?database=zed_json_crud&collection=flights&operation=insert&dynamicity=true").
                setBody().groovy("exchange.properties['original'].get('_id')");
    }

}
