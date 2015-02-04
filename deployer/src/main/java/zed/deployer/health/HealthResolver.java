package zed.deployer.health;

public interface HealthResolver {

    Health resolveHealth(String deployableId);

}
