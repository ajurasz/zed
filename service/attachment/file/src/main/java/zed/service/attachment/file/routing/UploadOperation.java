package zed.service.attachment.file.routing;

import java.util.Base64;
import java.util.Map;

public class UploadOperation {

    private final Map<String, Object> attachment;

    public UploadOperation(Map<String, Object> attachment) {
        this.attachment = attachment;
    }

    public String id() {
        return (String) attachment.get("id");
    }

    public byte[] data() {
        return Base64.getDecoder().decode((String) attachment.get("data"));
    }

    public Map<String, Object> attachment() {
        return attachment;
    }

}
