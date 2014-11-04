package zed.service.jsoncrud.mongo.routing;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class RestGatewayRoute extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        // TODO:CAMEL
        // Collection should not be required for dynamic endpoints
        from("direct:savePojo").
                to("mongodb:mongoClient?database=zed_json_crud&collection=flights&operation=insert&dynamicity=true");
    }

}
