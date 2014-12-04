package zed.quickstarts.fuse61.blueprint.camel.dozer;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.converter.dozer.DozerTypeConverterLoader;
import org.dozer.DozerBeanMapper;

public class DozerBeanMapperProcessor implements Processor {

    private final DozerBeanMapper dozerBeanMapper;

    public DozerBeanMapperProcessor(DozerTypeConverterLoader dozerTypeConverterLoader) {
        this.dozerBeanMapper = dozerTypeConverterLoader.getMapper();
    }

    @Override
    public void process(Exchange exchange) throws Exception {
        Bar bar = dozerBeanMapper.map(new Foo("mappedByDozerBeanMapper"), Bar.class);
        exchange.getIn().setBody(bar.getValue());
    }

}
