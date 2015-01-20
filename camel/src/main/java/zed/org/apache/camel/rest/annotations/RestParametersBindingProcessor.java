package zed.org.apache.camel.rest.annotations;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.TypeConverter;

import java.lang.reflect.Method;

import static org.apache.camel.Exchange.HTTP_PATH;
import static zed.org.apache.camel.rest.annotations.RestAnnotations.findRestOperations;

public class RestParametersBindingProcessor implements Processor {

    public static RestParametersBindingProcessor restParametersBindingProcessor() {
        return new RestParametersBindingProcessor();
    }

    @Override
    public void process(Exchange exchange) throws Exception {
        CamelContext context = exchange.getContext();
        TypeConverter typeConverter = context.getTypeConverter();

        // Path parsing
        String path = exchange.getIn().getHeader(HTTP_PATH, String.class);
        String[] pathSegments = path.split("/");
        String serviceName = pathSegments[1];
        String operationName = pathSegments[2];

        // Service method extraction
        Object serviceBean = context.getRegistry().lookupByName(serviceName);
        Class<?> serviceBeanType = serviceBean.getClass();
        Method serviceMethod = findRestOperations(serviceBeanType, operationName).get(0);

        Object[] methodParameters = new Object[serviceMethod.getParameterCount()];
        for (int i = 0; i < methodParameters.length; i++) {
            Object convertedParameter = typeConverter.convertTo(serviceMethod.getParameterTypes()[i], exchange.getIn().getHeader("p" + i));
            methodParameters[i] = convertedParameter;
        }
        exchange.getIn().setBody(methodParameters);
        if (serviceMethod.getReturnType() == Void.TYPE) {
            exchange.getIn().setHeader("CAMEL_REST_VOID_OPERATION", true);
        }
    }

}
