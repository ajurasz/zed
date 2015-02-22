package zed.camel.rpi.benchmark.routing

import org.apache.camel.builder.RouteBuilder
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class RpiBenchmarkRouting extends RouteBuilder {

    @Value('${sensors.mock.period:1}')
    private int period;

    @Value('${broker.consumers:20}')
    private int consumers;

    @Override
    void configure() {
        from("timer://myTimer?period=${period}")
        .threads(consumers)
            .process{
                it.getIn().setBody(UUID.randomUUID().toString())
            }
        .multicast()
                .to("bean:statistic?method=updateCreated", "jms://queue:RPi?concurrentConsumers=${consumers}")

        from("jms://queue:RPi")
        .to("bean:statistic?method=updateConsumed")
    }
}
