package zed.service.attachment.file.routing

import org.apache.camel.Exchange
import org.apache.camel.Processor

/**
 * Created by hekonsek on 30.12.14.
 */
class CamelGroovy {

    static Processor groovy(Closure closure) {
        return { Exchange exchange -> closure(new RichExchange(exchange)) }
    }

}

class RichExchange {

    private final Exchange exchange;

    RichExchange(Exchange exchange) {
        this.exchange = exchange
    }

    Exchange exchange() {
        exchange
    }

    String id() {
        exchange.getExchangeId()
    }

    def body() {
        exchange.getIn().getBody()
    }

    public <T> T body(Class<T> type) {
        exchange.getIn().getBody(type)
    }

    void setBody(Object body) {
        exchange.getIn().setBody(body)
    }

}
