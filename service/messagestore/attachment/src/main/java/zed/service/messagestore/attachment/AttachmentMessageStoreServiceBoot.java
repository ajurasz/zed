package zed.service.messagestore.attachment;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.web.SpringBootServletInitializer;

public class AttachmentMessageStoreServiceBoot extends SpringBootServletInitializer {

    public static void main(String... args) {
        new SpringApplication(AttachmentMessageStoreServiceConfiguration.class).run(args);
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(AttachmentMessageStoreServiceConfiguration.class);
    }

}