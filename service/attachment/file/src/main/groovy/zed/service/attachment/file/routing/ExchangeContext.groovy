package zed.service.attachment.file.routing

import org.apache.camel.Exchange

/**
 * Created by hekonsek on 30.12.14.
 */
class ExchangeContext {

    private final Exchange exchange;

    ExchangeContext(Exchange exchange) {
        this.exchange = exchange
    }

    Exchange exchange() {
        exchange
    }

    String id() {
        exchange.getExchangeId()
    }

    void header(String key, String value) {
        exchange.getIn().headers.put(key, value)
    }

    Object header(String key) {
        exchange.getIn().getHeader(key)
    }

    String stringHeader(String key) {
        exchange.getIn().getHeader(key, String.class)
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

    public <T> T bean(Class<T> type) {
        exchange.getContext().getRegistry().findByType(type).iterator().next()
    }

}
