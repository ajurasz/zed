package zed.org.apache.camel.rest.annotations;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.TypeConverter;

import java.lang.reflect.Method;

import static org.apache.camel.Exchange.HTTP_URI;
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
        String path = exchange.getIn().getHeader(HTTP_URI, String.class);
        String[] pathSegments = path.split("/");
        String serviceName = pathSegments[1];
        String operationName = pathSegments[2];

        // Service method extraction
        Object serviceBean = context.getRegistry().lookupByName(serviceName);
        Class<?> serviceBeanType = serviceBean.getClass();
        Method serviceMethod = findRestOperations(serviceBeanType, operationName).get(0);

        Object[] methodParameters = new Object[serviceMethod.getParameterCount()];
        Class<?>[] parameterTypes = serviceMethod.getParameterTypes();
        for (int i = 0; i < methodParameters.length; i++) {
            Object incomingParameter = exchange.getIn().getHeader("p" + i);
            Object convertedParameter = typeConverter.convertTo(parameterTypes[i], incomingParameter);
            methodParameters[i] = convertedParameter;
        }
        exchange.getIn().setBody(methodParameters);

        // Mark void operations invocations
        if (serviceMethod.getReturnType() == Void.TYPE) {
            exchange.getIn().setHeader("CAMEL_REST_VOID_OPERATION", true);
        }
    }

}
