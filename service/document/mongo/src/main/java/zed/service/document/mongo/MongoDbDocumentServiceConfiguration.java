package zed.service.document.mongo;

import boot.mongo.MongoDbEndpoint;
import boot.mongo.MongoDbMvcEndpoint;
import org.apache.camel.CamelContext;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.RoutesBuilder;
import org.apache.camel.spring.SpringCamelContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;

@Configuration
@EnableAutoConfiguration
@ComponentScan
public class MongoDbDocumentServiceConfiguration {

    @Autowired
    RoutesBuilder[] routes;

    // TODO Migrate to camel-spring-boot when Camel 2.15.0 is out -
    // http://camel.apache.org/spring-boot.html
    @Bean
    CamelContext camelContext() throws Exception {
        CamelContext camelContext = new SpringCamelContext();
        for (RoutesBuilder route : routes) {
            camelContext.addRoutes(route);
        }
        return camelContext;
    }

    @Bean
    ProducerTemplate producerTemplate() throws Exception {
        return camelContext().createProducerTemplate();
    }

    @Bean
    MongoDbEndpoint mongoDbEndpoint(MongoTemplate mongoTemplate) {
        return new MongoDbEndpoint(mongoTemplate);
    }

    @Bean
    MongoDbMvcEndpoint mongoDbMvcEndpoint(MongoDbEndpoint mongoDbEndpoint) {
        return new MongoDbMvcEndpoint(mongoDbEndpoint);
    }

}