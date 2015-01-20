package zed.org.apache.camel.rest.annotations;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.spi.Registry;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static java.util.Collections.unmodifiableList;

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
                routeBuilder.rest(uri).get().route().process(new Processor() {
                    @Override
                    public void process(Exchange exchange) throws Exception {
                        String path = exchange.getIn().getHeader(Exchange.HTTP_PATH, String.class);
                        String[] seg = path.split("/");
                        Object bean = exchange.getContext().getRegistry().lookupByName(seg[1]);
                        Class<?> type = bean.getClass();
                        Method method = findRestOperations(type, seg[2]).get(0);
                        Object[] methodParameters = new Object[method.getParameterCount()];
                        for (int i = 0; i < method.getParameterCount(); i++) {
                            Object convertedParameter = exchange.getContext().getTypeConverter().convertTo(method.getParameterTypes()[i], exchange.getIn().getHeader("p" + i));
                            methodParameters[i] = convertedParameter;
                        }
                        exchange.getIn().setBody(methodParameters);
                        if (method.getReturnType() == Void.TYPE) {
                            exchange.getIn().setHeader("CAMEL_REST_VOID_OPERATION", true);
                        }
                    }
                }).to("bean:" + bean.getKey() + "?method=" + method.getName() + "&multiParameterArray=true").
                        choice().when(routeBuilder.header("CAMEL_REST_VOID_OPERATION").isNotNull()).setBody().constant("").endChoice();
            }
        }
    }

}