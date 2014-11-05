package zed.service.jsoncrud.mongo.routing;

import com.mongodb.DBObject;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class RestGatewayRoute extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        // TODO:CAMEL
        // Collection should not be required for dynamic endpoints
        from("direct:savePojo").
                convertBodyTo(DBObject.class).
                setProperty("original", body()). // see CAMEL-7996
                to("mongodb:mongoClient?database=zed_json_crud&collection=flights&operation=insert&dynamicity=true").process(new Processor() {
            @Override
            public void process(Exchange exchange) throws Exception {
                System.out.println();
            }
        });
    }

}
