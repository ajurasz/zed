package zed.deployer.health;

import java.util.Map;

public class Health {

    private final String status;

    private final Map<String, Object> details;

    public Health(String status, Map<String, Object> details) {
        this.status = status;
        this.details = details;
    }

    public String getStatus() {
        return status;
    }

    public Map<String, Object> getDetails() {
        return details;
    }

}
