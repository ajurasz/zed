package zed.service.attachment.file.strategy;

import java.io.File;

public interface IdToFileMappingStrategy {
    File mapIdToFile(String id);
}
