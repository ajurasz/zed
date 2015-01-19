package zed.service.attachment.sdk;

import java.io.Serializable;
import java.util.Base64;
import java.util.Date;

public class Attachment implements Serializable {

    public Attachment() {
    }

    public Attachment(String data) {
        this.data = data;
    }

    public Attachment(byte[] data) {
        this(Base64.getEncoder().encodeToString(data));
    }

    private String id;

    private Date created = new Date();

    private Date edited = new Date();

    private String title;

    private String data;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public Date getEdited() {
        return edited;
    }

    public void setEdited(Date edited) {
        this.edited = edited;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

}
