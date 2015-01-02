package zed.service.attachment.file;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import zed.service.attachment.file.service.BinaryStorage;
import zed.service.attachment.file.service.FileSystemBinaryStorage;

import java.io.File;

import static com.google.common.io.Files.createTempDir;

@Configuration
@EnableAutoConfiguration
@ComponentScan({"zed.service.attachment.file", "zed.service.document"})
public class FileAttachmentServiceConfiguration {

    @Value("${zed.service.attachment.file.path}")
    File storage = createTempDir();

    @Bean
    BinaryStorage binaryStorage() {
        return new FileSystemBinaryStorage(storage);
    }

}