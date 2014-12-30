package zed.service.attachment.file.routing;

public class DownloadOperation {

    private final String id;

    public DownloadOperation(String id) {
        this.id = id;
    }

    public String id() {
        return id;
    }

}
