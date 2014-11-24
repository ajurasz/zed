package zed.quickstarts.fuse61.blueprint.camel.dozer;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;

public class DozerProcessor implements Processor {

    @Override
    public void process(Exchange exchange) throws Exception {
        Bar bar = exchange.getContext().getTypeConverter().convertTo(Bar.class, new Foo("someValue"));
        exchange.getIn().setBody(bar.getValue());
    }

}
