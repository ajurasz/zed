package zed.service.jsoncrud.mongo;

import org.apache.camel.CamelContext;
import org.apache.camel.spring.SpringCamelContext;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableAutoConfiguration
public class MongoJsonCrudServiceConfiguration {

    // TODO Migrate to camel-spring-boot when Camel 2.15.0 is out -
    // http://camel.apache.org/spring-boot.html
    @Bean
    CamelContext camelContext() {
        return new SpringCamelContext();
    }

}