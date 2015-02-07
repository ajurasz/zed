package zed.service.document.mongo;

import boot.mongo.MongoDbEndpoint;
import boot.mongo.MongoDbMvcEndpoint;
import com.mongodb.Mongo;
import com.mongodb.MongoClient;
import com.mongodb.MongoTimeoutException;
import org.apache.camel.CamelContext;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.RoutesBuilder;
import org.apache.camel.component.swagger.spring.SpringRestSwaggerApiDeclarationServlet;
import org.apache.camel.spring.SpringCamelContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.embedded.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.core.MongoTemplate;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.UnknownHostException;

@SpringBootApplication
public class MongoDbDocumentServiceConfiguration {

    static final Logger LOG = LoggerFactory.getLogger(MongoDbDocumentServiceConfiguration.class);

    // TODO Migrate to camel-spring-boot when Camel 2.15.0 is out -
    // http://camel.apache.org/spring-boot.html
    @Bean
    CamelContext camelContext(RoutesBuilder[] routes) throws Exception {
        CamelContext camelContext = new SpringCamelContext();
        for (RoutesBuilder route : routes) {
            camelContext.addRoutes(route);
        }
        return camelContext;
    }

    @Bean
    ProducerTemplate producerTemplate(CamelContext camelContext) throws Exception {
        return camelContext.createProducerTemplate();
    }

    @Bean
    @ConditionalOnProperty(value = "zed.service.document.mongodb.springbootconfig", matchIfMissing = true, havingValue = "false")
    Mongo mongo() throws UnknownHostException {
        try {
            LOG.info("Attempting to connect to the MongoDB server at localhost:27017.");
            Mongo mongo = new MongoClient("mongodb");
            mongo.getDatabaseNames();
            return mongo;
        } catch (MongoTimeoutException e) {
            LOG.info("Can't connect to the MongoDB server at mongodb:27017. Falling back to the localhost:27017.");
            return new MongoClient();
        }
    }

    @Bean
    MongoDbEndpoint mongoDbEndpoint(MongoTemplate mongoTemplate) {
        return new MongoDbEndpoint(mongoTemplate);
    }

    @Bean
    MongoDbMvcEndpoint mongoDbMvcEndpoint(MongoDbEndpoint mongoDbEndpoint) {
        return new MongoDbMvcEndpoint(mongoDbEndpoint);
    }

    @Bean
    ServletRegistrationBean swaggerServlet(@Value("${server.port:15000}") int serverPort, @Value("${zed.service.api.port:15001}") int restPort) {
        ServletRegistrationBean swaggerServlet = new ServletRegistrationBean();
        swaggerServlet.setName("ApiDeclarationServlet");
        swaggerServlet.setServlet(new SpringRestSwaggerApiDeclarationServlet());
        swaggerServlet.addInitParameter("base.path", String.format("http://localhost:%d/api", restPort));
        swaggerServlet.addInitParameter("api.path", String.format("http://localhost:%d/api/contract", serverPort));
        swaggerServlet.addInitParameter("api.version", "1.2.3");
        swaggerServlet.addInitParameter("api.title", "User Services");
        swaggerServlet.addInitParameter("api.description", "Camel Rest Example with Swagger that provides an User REST service");
        swaggerServlet.addInitParameter("cors", "true");
        swaggerServlet.setLoadOnStartup(2);
        swaggerServlet.addUrlMappings("/api/contract/*");
        return swaggerServlet;
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