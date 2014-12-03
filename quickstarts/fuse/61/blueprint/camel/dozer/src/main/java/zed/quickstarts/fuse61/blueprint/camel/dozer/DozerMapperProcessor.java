package zed.quickstarts.fuse61.blueprint.camel.dozer;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.converter.dozer.DozerTypeConverterLoader;
import org.dozer.DozerBeanMapper;

public class DozerMapperProcessor implements Processor {

    @Override
    public void process(Exchange exchange) throws Exception {
        DozerTypeConverterLoader dozerTypeConverterLoader =
                (DozerTypeConverterLoader) exchange.getContext().getRegistry().lookupByName("dozerConverterLoader");
        DozerBeanMapper dozerBeanMapper = dozerTypeConverterLoader.getMapper();
        Bar bar = dozerBeanMapper.map(new Foo("someValue"), Bar.class);
        exchange.getIn().setBody(bar.getValue());
    }

}
