package zed.service.attachment.file;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableAutoConfiguration
@ComponentScan({"zed.service.attachment.file", "zed.service.document"})
public class FileAttachmentServiceConfiguration {

}