package zed.service.attachment.file.strategy;

import org.junit.Assert;
import org.junit.Test;

import java.io.File;

public class TenFirstLettersCustomIdToFileMappingStrategyTest extends Assert {

    IdToFileMappingStrategy idToFileMappingStrategy = new TenFirstLettersCustomIdToFileMappingStrategy();

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
