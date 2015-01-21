package zed.service.attachment.file;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import zed.service.attachment.file.service.BinaryStorage;
import zed.service.attachment.file.service.FileSystemBinaryStorage;
import zed.service.attachment.file.strategy.TenFirstLettersCustomIdToFileMappingStrategy;
import zed.service.attachment.file.strategy.IdToFileMappingStrategy;

import java.io.File;

import static com.google.common.io.Files.createTempDir;

@SpringBootApplication
@ComponentScan({"zed.service.attachment.file", "zed.service.document"})
public class FileAttachmentServiceConfiguration {

    @Value("${zed.service.attachment.file.path:}")
    File storage = createTempDir();

    @Bean
    @ConditionalOnMissingBean(IdToFileMappingStrategy.class)
    IdToFileMappingStrategy idToFileMappingStrategy() {
        return new TenFirstLettersCustomIdToFileMappingStrategy();
    }

    @Bean
    BinaryStorage binaryStorage(IdToFileMappingStrategy idToFileMappingStrategy) {
        return new FileSystemBinaryStorage(storage, idToFileMappingStrategy);
    }

}