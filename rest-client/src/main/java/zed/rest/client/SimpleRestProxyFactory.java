package zed.rest.client;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import static java.lang.reflect.Proxy.newProxyInstance;
import static zed.utils.Reflections.classNameToCamelCase;

public class SimpleRestProxyFactory {

    private final RestOperations restOperations;

    public SimpleRestProxyFactory(RestOperations restOperations) {
        this.restOperations = restOperations;
    }

    public SimpleRestProxyFactory() {
        this.restOperations = new RestTemplate();
    }

    @SuppressWarnings("unchecked")
    public <T> RestProxy<T> proxyService(Class<T> serviceClass, String baseServiceUrl) {
        return (RestProxy<T>) newProxyInstance(SimpleRestProxyFactory.class.getClassLoader(), new Class[]{RestProxy.class}, new RestProxyHandler(serviceClass, baseServiceUrl));
    }

    // private helpers

    private String normalizeBaseServiceUrl(String baseServiceUrl) {
        return baseServiceUrl.
                replaceFirst("/+$", "");
    }

    // private classes

    private class RestProxyHandler implements InvocationHandler {

        private final Class<?> serviceClass;

        private final String baseServiceUrl;

        private RestProxyHandler(Class<?> serviceClass, String baseServiceUrl) {
            this.serviceClass = serviceClass;
            this.baseServiceUrl = baseServiceUrl;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            boolean isGet = method.getName().equals("get");
            Header[] headers = args.length > 0 ? (Header[]) args[0] : new Header[0];
            return newProxyInstance(SimpleRestProxyFactory.class.getClassLoader(), new Class[]{serviceClass}, new HttpMethodHandler(isGet, headers));
        }

        private class HttpMethodHandler implements InvocationHandler {

            private final boolean isGet;

            private final Header[] headers;

            private HttpMethodHandler(boolean isGet, Header... headers) {
                this.isGet = isGet;
                this.headers = headers;
            }

            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                if (args == null && !isGet) {
                    throw new IllegalStateException("You can't send POST to the service method without parameters.");
                }

                Class<?> returnType = method.getReturnType();
                String normalizedBaseServiceUrl = normalizeBaseServiceUrl(baseServiceUrl);
                String url = normalizedBaseServiceUrl + "/" + classNameToCamelCase(serviceClass) + "/" + method.getName();
                if (args != null) {
                    int argumentsInUri = isGet ? args.length : args.length - 1;
                    for (int i = 0; i < argumentsInUri; i++) {
                        url += "/" + args[i].toString();
                    }
                }

                HttpMethod httpMethod = isGet ? HttpMethod.GET : HttpMethod.POST;

                HttpHeaders effectiveHeaders = new HttpHeaders();
                for (Header header : headers) {
                    effectiveHeaders.set(header.key(), header.value());
                }
                HttpEntity<?> entity = isGet ? new HttpEntity<>(effectiveHeaders) : new HttpEntity<>(args[args.length - 1], effectiveHeaders);

                if (returnType == Void.TYPE) {
                    return restOperations.exchange(url, httpMethod, entity, String.class).getBody();
                }
                return restOperations.exchange(url, httpMethod, entity, returnType).getBody();
            }
        }
    }

}