package zed.service.attachment.file.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import static com.google.common.io.Files.write;

public class FileSystemBinaryStorage implements BinaryStorage {

    private final File storage;

    public FileSystemBinaryStorage(File storage) {
        this.storage = storage;
    }

    @Override
    public InputStream readData(String id) {
        try {
            return new FileInputStream(new File(storage, id));
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
        new File(storage, stagingId).renameTo(new File(storage, finalId));
    }

}
