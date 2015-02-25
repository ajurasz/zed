package zed.camel.rpi.benchmark.routing

import org.apache.camel.builder.RouteBuilder
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class RpiBenchmarkRouting extends RouteBuilder {

    @Value('${sensors.mock.period:1}')
    private int period;

    @Value('${broker.consumers:5}')
    private int consumers;

    @Override
    void configure() {
        from("timer://myTimer?period=${period}")
                .threads(1, 100)
                .process {
            it.getIn().setBody(UUID.randomUUID().toString())
        }
        .multicast()
                .to("bean:statistic?method=updateCreated", "jms://queue:RPi")

        from("jms://queue:RPi?concurrentConsumers=${consumers}")
                .to("bean:statistic?method=updateConsumed")
    }
}
