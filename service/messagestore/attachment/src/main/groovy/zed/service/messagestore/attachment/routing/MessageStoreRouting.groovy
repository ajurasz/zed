package zed.service.messagestore.attachment.routing

import org.apache.camel.builder.RouteBuilder
import org.springframework.stereotype.Component
import zed.service.attachment.file.routing.ExchangeContext
import zed.service.document.mongo.routing.SaveOperation
import zed.service.messagestore.attachment.TextMessage

@Component
class MessageStoreRouting extends RouteBuilder {

    private static def TOPIC_PREFIX = 'topic://'

    @Override
    void configure() {
        from("jms:topic:>?connectionFactory=#jmsConnectionFactory").
                filter(body().isNotNull()).
                setBody().expression {
            def exc = new ExchangeContext(it)
            def textMessage = new TextMessage(new String(exc.body()))
            def collection = exc.stringHeader('JmsDestination').substring(TOPIC_PREFIX.length())
            new SaveOperation(collection, textMessage)
        }.
                to("direct:save")
    }

}
