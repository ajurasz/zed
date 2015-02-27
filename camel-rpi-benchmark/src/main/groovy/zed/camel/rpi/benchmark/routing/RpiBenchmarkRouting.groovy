package zed.camel.rpi.benchmark.routing

import org.apache.camel.builder.RouteBuilder
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class RpiBenchmarkRouting extends RouteBuilder {

    @Value('${sensors.number:3}')
    private int sensorsNumber;

    @Value('${sensors.mock.period:1}')
    private int period;

    @Value('${broker.consumers:5}')
    private int consumers;

    @Value('${queue.type:jms}')
    private String queueType;

    @Override
    void configure() {
        errorHandler(deadLetterChannel("seda:DLQ"))
        context.getShutdownStrategy().setTimeout(5)

        for (int i = 0; i < sensorsNumber; i++) {
            from("timer://myTimer?period=${period}")
                    .threads(1, 100)
                    .process {
                it.getIn().setBody(UUID.randomUUID().toString())
            }
            .multicast()
                    .to("bean:statistic?method=updateCreated", "${queueType}://queue:RPi")
        }
            
        from("${queueType}://queue:RPi?concurrentConsumers=${consumers}")
                .to("bean:statistic?method=updateConsumed")
    }
}
