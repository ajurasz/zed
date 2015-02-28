package zed.boot.rpi.benchmark;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.web.SpringBootServletInitializer;

public class RpiBenchmarkBoot extends SpringBootServletInitializer {
    public static void main(String[] args) {
        new SpringApplication(RpiBenchmarkConfiguration.class).run(args);
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(RpiBenchmarkConfiguration.class);
    }

}
