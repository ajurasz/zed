package zed.service.attachment.file;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.web.SpringBootServletInitializer;

public class FileAttachmentServiceBoot extends SpringBootServletInitializer {

    public static void main(String... args) {
        new SpringApplication(FileAttachmentServiceConfiguration.class).run(args);
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(FileAttachmentServiceConfiguration.class);
    }

}