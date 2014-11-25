package zed.service.jsoncrud.mongo;

import org.apache.camel.CamelContext;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.RoutesBuilder;
import org.apache.camel.spring.SpringCamelContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableAutoConfiguration
@ComponentScan
public class MongoJsonCrudServiceConfiguration {

    public static void main(String[] args) {
        new SpringApplication(MongoJsonCrudServiceConfiguration.class).run(args);
    }

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

}