package zed.shell;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.web.SpringBootServletInitializer;

public class ShellBoot extends SpringBootServletInitializer {

    public static void main(String[] args) {
        new SpringApplication(ShellConfiguration.class).run(args);
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(ShellConfiguration.class);
    }

}