package zed.service.attachment.file;

import com.google.common.io.Files;
import com.mongodb.Mongo;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import spring.boot.EmbedMongoConfiguration;
import zed.service.attachment.sdk.Attachment;
import zed.service.attachment.sdk.AttachmentService;
import zed.service.attachment.sdk.RestAttachmentService;

import java.io.File;
import java.util.Set;

import static com.google.common.collect.Sets.newHashSet;
import static org.springframework.util.SocketUtils.findAvailableTcpPort;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {EmbedMongoConfiguration.class, FileAttachmentServiceConfiguration.class, FileAttachmentServiceTestConfiguration.class})
@IntegrationTest
@ActiveProfiles("test")
public class FileAttachmentServiceTest extends Assert {

    @Value("${zed.service.document.mongo.db:zed_service_document}")
    String documentsDbName;

    @Autowired
    AttachmentService<Attachment> attachmentService;

    @Autowired
    Mongo mongo;

    static File storage = Files.createTempDir();

    String data = "foo";

    Attachment attachment = new Attachment(data.getBytes());

    @BeforeClass
    public static void beforeClass() {
        System.setProperty("server.port", findAvailableTcpPort() + "");
        System.setProperty("zed.service.document.rest.port", findAvailableTcpPort() + "");
        System.setProperty("zed.service.attachment.file.path", storage.getAbsolutePath());
        System.setProperty("spring.data.mongodb.host", "localhost");
        System.setProperty("spring.data.mongodb.port", EmbedMongoConfiguration.port + "");
    }

    @Before
    public void before() {
        mongo.getDB(documentsDbName).dropDatabase();
    }

    // Tests

    @Test
    public void shouldSaveAttachment() {
        // When
        attachment = attachmentService.save(attachment);

        // Then
        Attachment loadedAttachment = attachmentService.findOne(Attachment.class, attachment.getId());
        assertNotNull(loadedAttachment);
    }

    @Test
    public void shouldUpload() {
        // When
        attachment = attachmentService.upload(attachment);

        // Then
        String id = attachment.getId();
        Set<String> files = newHashSet(storage.list());
        assertTrue(files.contains(id));
    }

    @Test
    public void shouldDownload() {
        // Given
        attachment = attachmentService.upload(attachment);

        // When
        byte[] downloadedData = attachmentService.download(attachment.getId());

        // Then
        assertEquals(data, new String(downloadedData));
    }

}

@Configuration
class FileAttachmentServiceTestConfiguration {

    @Value("${zed.service.document.rest.port}")
    int restPort;

    @Bean
    AttachmentService<Attachment> attachmentService() {
        return new RestAttachmentService<>("http://0.0.0.0:" + restPort);
    }

}