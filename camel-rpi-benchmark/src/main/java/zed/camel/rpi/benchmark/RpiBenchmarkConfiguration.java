package zed.camel.rpi.benchmark;

import org.apache.camel.CamelContext;
import org.apache.camel.RoutesBuilder;
import org.apache.camel.spring.SpringCamelContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import zed.camel.rpi.benchmark.statistic.Statistic;
import zed.camel.rpi.benchmark.statistic.StatisticImpl;

@Configuration
@ComponentScan
public class RpiBenchmarkConfiguration {

    @Value("${statistics.save.period:30}")
    private int savePeriod;

    @EnableAutoConfiguration
    @ConditionalOnExpression("${activemq.enabled:true}")
    protected static class ActiveMqConfiguration {
    }

    @EnableAutoConfiguration(exclude = ActiveMqConfiguration.class)
    @ConditionalOnExpression("!${activemq.enabled:true}")
    protected static class SedaConfiguration {
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
    Statistic statistic() {
        return new StatisticImpl(savePeriod);
    }

}
