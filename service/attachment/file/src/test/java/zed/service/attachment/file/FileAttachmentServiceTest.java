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
import zed.service.attachment.file.strategy.IdToFileMappingStrategy;
import zed.service.attachment.sdk.Attachment;
import zed.service.attachment.sdk.AttachmentQuery;
import zed.service.attachment.sdk.AttachmentService;
import zed.service.attachment.sdk.RestAttachmentService;
import zed.service.document.sdk.QueryBuilder;

import java.io.File;
import java.util.List;
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

    @Autowired
    IdToFileMappingStrategy idToFileMappingStrategy;

    static File storage = Files.createTempDir();

    String data = "foo";

    Attachment attachment = new Attachment(data.getBytes());

    @BeforeClass
    public static void beforeClass() {
        System.setProperty("server.port", findAvailableTcpPort() + "");
        System.setProperty("zed.service.api.port", findAvailableTcpPort() + "");
        System.setProperty("zed.service.attachment.file.path", storage.getAbsolutePath());
        System.setProperty("spring.data.mongodb.host", "localhost");
        System.setProperty("spring.data.mongodb.port", EmbedMongoConfiguration.port + "");
    }

    @Before
    public void before() {
        mongo.getDB(documentsDbName).dropDatabase();

        attachment.setTitle("someTitle");
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
        Set<String> files = newHashSet(new File(storage, idToFileMappingStrategy.mapIdToFile(id).getParent()).list());
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

    @Test
    public void shouldFindByQuery() {
        // Given
        attachment = attachmentService.upload(attachment);
        AttachmentQuery query = new AttachmentQuery();
        query.setTitle(attachment.getTitle());

        // When
        List<Attachment> attachments = attachmentService.findByQuery(Attachment.class, new QueryBuilder(query));

        // Then
        assertEquals(1, attachments.size());
        assertEquals(attachment.getId(), attachments.get(0).getId());
    }

    @Test
    public void shouldNotFindByQuery() {
        // Given
        attachment = attachmentService.upload(attachment);
        AttachmentQuery query = new AttachmentQuery();
        query.setTitle("some random title");

        // When
        List<Attachment> attachments = attachmentService.findByQuery(Attachment.class, new QueryBuilder(query));

        // Then
        assertEquals(0, attachments.size());
    }

}

@Configuration
class FileAttachmentServiceTestConfiguration {

    @Bean
    AttachmentService<Attachment> attachmentService(@Value("${zed.service.api.port}") int restApiPort) {
        return new RestAttachmentService<>(restApiPort);
    }

}