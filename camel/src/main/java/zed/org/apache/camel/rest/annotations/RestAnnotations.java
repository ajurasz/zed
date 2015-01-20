package zed.org.apache.camel.rest.annotations;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.spi.Registry;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static java.util.Collections.unmodifiableList;
import static zed.org.apache.camel.rest.annotations.RestParametersBindingProcessor.restParametersBindingProcessor;

public class RestAnnotations {

    public static List<Method> findRestOperations(Class<?> type, String name) {
        List<Method> annotatedMethods = new LinkedList<>();
        for (Method method : type.getMethods()) {
            if (method.isAnnotationPresent(RestOperation.class)) {
                if (name == null || method.getName().equals(name)) {
                    annotatedMethods.add(method);
                }
            }
        }
        for (Class<?> iface : type.getInterfaces()) {
            annotatedMethods.addAll(findRestOperations(iface, name));
        }
        return unmodifiableList(annotatedMethods);
    }

    public static List<Method> findRestOperations(Class<?> type) {
        return findRestOperations(type, null);
    }

    public static Map<String, Object> findBeansWithRestOperations(Registry registry) {
        Map<String, Object> beans = new HashMap<>();
        for (Map.Entry<String, Object> bean : registry.findByTypeWithName(Object.class).entrySet()) {
            if (!findRestOperations(bean.getValue().getClass()).isEmpty()) {
                beans.put(bean.getKey(), bean.getValue());
            }
        }
        return beans;
    }

    public static void exposeAnnotatedBeans(RouteBuilder routeBuilder) {
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