package zed.service.attachment.file.service;

import zed.service.attachment.file.strategy.IdToFileMappingStrategy;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

import static com.google.common.io.Files.write;

public class FileSystemBinaryStorage implements BinaryStorage {

    private final File storage;
    private final IdToFileMappingStrategy idToFileMappingStrategy;

    public FileSystemBinaryStorage(File storage, IdToFileMappingStrategy idToFileMappingStrategy) {
        this.storage = storage;
        this.idToFileMappingStrategy = idToFileMappingStrategy;
    }

    @Override
    public InputStream readData(String id) {
        try {
            return new FileInputStream(new File(storage, idToFileMappingStrategy.mapIdToFile(id).getPath()));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void stageData(String stagingId, byte[] data) {
        try {
            write(data, new File(storage, stagingId));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void commitData(String stagingId, String finalId) {
        try {
            File src = new File(storage, stagingId);
            File dest = new File(storage, idToFileMappingStrategy.mapIdToFile(finalId).getPath());
            dest.getParentFile().mkdirs();
            Files.move(src.toPath(), dest.toPath());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
