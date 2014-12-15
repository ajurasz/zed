package zed.service.document.mongo;

import boot.mongo.MongoDbEndpoint;
import boot.mongo.MongoDbMvcEndpoint;
import org.apache.camel.CamelContext;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.RoutesBuilder;
import org.apache.camel.component.swagger.spring.SpringRestSwaggerApiDeclarationServlet;
import org.apache.camel.spring.SpringCamelContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.embedded.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;

@Configuration
@EnableAutoConfiguration
@ComponentScan
public class MongoDbDocumentServiceConfiguration {

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
    MongoDbEndpoint mongoDbEndpoint(MongoTemplate mongoTemplate) {
        return new MongoDbEndpoint(mongoTemplate);
    }

    @Bean
    MongoDbMvcEndpoint mongoDbMvcEndpoint(MongoDbEndpoint mongoDbEndpoint) {
        return new MongoDbMvcEndpoint(mongoDbEndpoint);
    }

    @Bean
    ServletRegistrationBean swaggerServlet(@Value("${server.port:15000}") int serverPort, @Value("${zed.service.document.rest.port:15001}") int restPort) {
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

}