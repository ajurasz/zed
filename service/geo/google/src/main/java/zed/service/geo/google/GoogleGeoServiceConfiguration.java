package zed.service.geo.google;

import org.apache.camel.CamelContext;
import org.apache.camel.RoutesBuilder;
import org.apache.camel.spring.SpringCamelContext;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@SpringBootApplication
public class GoogleGeoServiceConfiguration {

    public static void main(String[] args) {
        new SpringApplication(GoogleGeoServiceConfiguration.class).run(args);
    }

    // TODO Migrate to camel-spring-boot when Camel 2.15.0 is out -
    // http://camel.apache.org/spring-boot.html
    @Bean
    CamelContext camelContext(RoutesBuilder[] routes, ApplicationContext applicationContext) throws Exception {
        SpringCamelContext camelContext = new SpringCamelContext(applicationContext);
        for (RoutesBuilder route : routes) {
            camelContext.addRoutes(route);
        }
        return camelContext;
    }

    @Bean
    Filter corsFilter() {
        return new Filter() {

            public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
                HttpServletResponse response = (HttpServletResponse) res;
                response.setHeader("Access-Control-Allow-Origin", "*");
                response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE");
                response.setHeader("Access-Control-Max-Age", "3600");
                response.setHeader("Access-Control-Allow-Headers", "x-requested-with");
                chain.doFilter(req, res);
            }

            public void init(FilterConfig filterConfig) {
            }

            public void destroy() {
            }

        };
    }

}