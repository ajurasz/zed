package zed.deployer.executor;

import zed.deployer.manager.DeployablesManager;
import zed.deployer.manager.DeploymentDescriptor;
import zed.deployer.manager.ZedHome;
import zed.utils.Reflections;

import java.io.File;
import java.io.IOException;

public class FatJarLocalProcessExecutionHandler implements ProcessExecutorHandler {

    ZedHome zedHome;

    DeployablesManager deployablesManager;

    public FatJarLocalProcessExecutionHandler(ZedHome zedHome, DeployablesManager deployablesManager) {
        this.zedHome = zedHome;
        this.deployablesManager = deployablesManager;
    }

    @Override
    public boolean supports(String uri) {
        return uri.startsWith("fatjar:mvn:");
    }

    @Override
    public String start(String deploymentId) {
        DeploymentDescriptor deploymentDescriptor = deployablesManager.deployment(deploymentId);

        String name = deploymentDescriptor.uri();
        name = name.substring(name.indexOf('/') + 1);
        name = name.replaceFirst("/", "-");
        name = name.replace('/', '.');

        File toRun = new File(new File(zedHome.deployDirectory(), deploymentDescriptor.workspace()), name);
        try {
            Process process = Runtime.getRuntime().exec(new String[]{"java", "-Dzed.deployable.id=" + deploymentId, "-jar", toRun.getAbsolutePath()});
            int pid = Reflections.readField(process, "pid", Integer.class);
            return pid + "";
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}