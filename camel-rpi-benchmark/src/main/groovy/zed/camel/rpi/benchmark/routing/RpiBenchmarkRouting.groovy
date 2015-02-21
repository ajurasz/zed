package zed.camel.rpi.benchmark.routing

import org.apache.camel.builder.RouteBuilder
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class RpiBenchmarkRouting extends RouteBuilder {

    @Value('${zed.service.timer.period:200}')
    private int period;

    @Override
    void configure() {
        from("timer://myTimer?period=${period}")
            .setBody().simple(UUID.randomUUID().toString())
            .to("jms://queue:RPi")

        from("jms://queue:RPi").to("bean:statistic?method=call")
    }
}
