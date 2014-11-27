package zed.service.jsoncrud.mongo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.web.SpringBootServletInitializer;

public class MongoJsonCrudServiceBoot extends SpringBootServletInitializer {

    public static void main(String[] args) {
        new SpringApplication(MongoJsonCrudServiceConfiguration.class).run(args);
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(MongoJsonCrudServiceConfiguration.class);
    }

}