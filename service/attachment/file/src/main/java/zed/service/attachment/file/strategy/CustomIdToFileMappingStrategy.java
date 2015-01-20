package zed.service.attachment.file.strategy;

import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.nio.file.Paths;

public class CustomIdToFileMappingStrategy implements IdToFileMappingStrategy {

    @Override
    public File mapIdToFile(String id) {
        String[] splitted = StringUtils.substring(id, 0, 10).split("");
        return new File(Paths.get("", splitted).toFile(), id);
    }
}
