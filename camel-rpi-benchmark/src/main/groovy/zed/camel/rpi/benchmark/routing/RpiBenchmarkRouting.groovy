package zed.camel.rpi.benchmark.routing

import org.apache.camel.builder.RouteBuilder
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class RpiBenchmarkRouting extends RouteBuilder {

    @Value('${sensors.mock.period:1}')
    private int period;

    @Override
    void configure() {
        from("timer://myTimer?period=${period}")
            .process{
                it.getIn().setBody(UUID.randomUUID().toString())
            }
        .multicast()
            .to("jms://queue:RPi", "bean:statistic?method=updateCreated")

        from("jms://queue:RPi")
        .to("bean:statistic?method=updateConsumed")
    }
}
