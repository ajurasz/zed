package zed.camel.rpi.benchmark.rest

import org.apache.camel.builder.RouteBuilder
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import zed.camel.rpi.benchmark.statistic.Details

import static org.apache.camel.model.rest.RestBindingMode.json

@Component
class RpiBenchmarkRest extends RouteBuilder {

    @Value('${statistics.api.port:9900}')
    private int restPort;

    @Override
    void configure() throws Exception {
        restConfiguration().component("netty-http").
                host("0.0.0.0").port(restPort).bindingMode(json)

        rest("/statistic")
                .consumes("application/json").produces("application/json")

                .get("/details").outType(Details.class)
                    .to("bean:statistic?method=details")

                .get("/list").outType(List.class)
                    .to("bean:statistic?method=list")
    }
}
