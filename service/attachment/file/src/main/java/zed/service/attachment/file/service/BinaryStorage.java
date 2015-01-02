package zed.service.attachment.file.service;

import java.io.InputStream;

public interface BinaryStorage {

    InputStream readData(String id);

    void stageData(String stagingId, byte[] data);

    void commitData(String stagingId, String finalId);

}
