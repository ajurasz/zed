package zed.service.attachment.file.strategy;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import zed.service.attachment.file.FileAttachmentServiceConfiguration;

import java.io.File;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = FileAttachmentServiceConfiguration.class)
public class CustomIdToFileMappingStrategyTest extends Assert {

    @Autowired
    IdToFileMappingStrategy idToFileMappingStrategy;

    @Test
    public void shouldCreateCorrectFileWhenIdLessThanTenCharacters() {
        // Given
        String id = "abcd";

        File file = idToFileMappingStrategy.mapIdToFile(id);

        assertEquals("a/b/c/d/abcd", file.getPath());
    }

    @Test
    public void shouldCreateCorrectFileWhenIdGraterThanTenCharacters() {
        // Given
        String id = "abcde1234567";

        File file = idToFileMappingStrategy.mapIdToFile(id);

        assertEquals("a/b/c/d/e/1/2/3/4/5/abcde1234567", file.getPath());
    }
}
