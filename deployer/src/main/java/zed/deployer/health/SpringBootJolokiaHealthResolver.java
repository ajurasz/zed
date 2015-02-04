package zed.deployer.health;

import com.fasterxml.jackson.databind.ObjectMapper;
import zed.deployer.manager.DeployableDescriptor;
import zed.deployer.manager.DeployablesManager;

import java.io.IOException;
import java.net.URL;
import java.util.Map;

public class SpringBootJolokiaHealthResolver implements HealthResolver {

    private final DeployablesManager deployablesManager;

    public SpringBootJolokiaHealthResolver(DeployablesManager deployablesManager) {
        this.deployablesManager = deployablesManager;
    }

    @Override
    public Health resolveHealth(String deployableId) {
        try {
            DeployableDescriptor descriptor = deployablesManager.deployment(deployableId);
            String baseJolokiaUrl = discoverDeployableJolokiaEndpoint(descriptor);
            String healthEndpoint = baseJolokiaUrl + "exec/org.springframework.boot:type=Endpoint,name=healthEndpoint/getData";
            Map<String, Object> jolokiaHealthResponse = new ObjectMapper().readValue(new URL(healthEndpoint), Map.class);
            Map<String, Object> rawHealthResponse = (Map<String, Object>) jolokiaHealthResponse.get("value");
            return new Health((String) rawHealthResponse.get("status"), rawHealthResponse);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String discoverDeployableJolokiaEndpoint(DeployableDescriptor descriptor) {
        return null;
    }

}