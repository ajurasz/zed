package zed.org.apache.camel.rest.annotations;

import org.apache.camel.builder.RouteBuilder;

import java.lang.reflect.Method;
import java.util.Map;

import static zed.org.apache.camel.rest.annotations.RestAnnotations.findBeansWithRestOperations;
import static zed.org.apache.camel.rest.annotations.RestAnnotations.findRestOperations;
import static zed.org.apache.camel.rest.annotations.RestParametersBindingProcessor.restParametersBindingProcessor;

public class RestAnnotationsExposer {

    private final RouteBuilder routeBuilder;

    public RestAnnotationsExposer(RouteBuilder routeBuilder) {
        this.routeBuilder = routeBuilder;
    }

    public static void exposeAnnotatedBeans(RouteBuilder routeBuilder) {
        new RestAnnotationsExposer(routeBuilder).exposeAnnotatedBeans();
    }

    public void exposeAnnotatedBeans() {
        for (Map.Entry<String, Object> bean : findBeansWithRestOperations(routeBuilder.getContext().getRegistry()).entrySet()) {
            for (Method method : findRestOperations(bean.getValue().getClass())) {
                String uri = "/" + bean.getKey() + "/" + method.getName();
                for (int i = 0; i < method.getParameterCount(); i++) {
                    uri += "/{p" + i + "}";
                }
                routeBuilder.rest(uri).get().route().process(restParametersBindingProcessor()).
                        to("bean:" + bean.getKey() + "?method=" + method.getName() + "&multiParameterArray=true").
                        choice().when(routeBuilder.header("CAMEL_REST_VOID_OPERATION").isNotNull()).setBody().constant("").endChoice();
            }
        }
    }

}
