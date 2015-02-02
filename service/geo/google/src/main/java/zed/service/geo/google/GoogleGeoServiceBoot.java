package zed.service.geo.google;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.web.SpringBootServletInitializer;

public class GoogleGeoServiceBoot extends SpringBootServletInitializer {

    public static void main(String... args) {
        new SpringApplication(GoogleGeoServiceConfiguration.class).run(args);
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(GoogleGeoServiceConfiguration.class);
    }

}