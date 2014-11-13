package zed.service.jsoncrud.mongo;

import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.web.SpringBootServletInitializer;

public class MongoJsonCrudServiceServletInitializer extends SpringBootServletInitializer {

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(MongoJsonCrudServiceConfiguration.class);
    }

}