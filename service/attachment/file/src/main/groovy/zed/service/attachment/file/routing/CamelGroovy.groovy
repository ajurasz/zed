package zed.service.attachment.file.routing

import org.apache.camel.Exchange
import org.apache.camel.Processor

/**
 * Created by hekonsek on 30.12.14.
 */
class CamelGroovy {

    static Processor groovy(Closure closure) {
        return { Exchange exchange -> closure(new ExchangeContext(exchange)) }
    }

}

