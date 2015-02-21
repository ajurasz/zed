package zed.camel.rpi.benchmark;

import org.apache.camel.CamelContext;
import org.apache.camel.RoutesBuilder;
import org.apache.camel.spring.SpringCamelContext;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import zed.camel.rpi.benchmark.statistic.Statistic;
import zed.camel.rpi.benchmark.statistic.StatisticImpl;

@SpringBootApplication
public class RpiBenchmarkConfiguration {

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
    Statistic statistic() {
        return new StatisticImpl();
    }

}
