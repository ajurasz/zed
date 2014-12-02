package zed.deployer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class ProcessUriStatusResolver implements UriStatusResolver {

    @Override
    public boolean support(String uri) {
        return uri.startsWith("fatJar:mvn");
    }

    @Override
    public boolean status(DeploymentDescriptor deploymentDescriptor) {
        String pid = deploymentDescriptor.pid();
        if (pid == null) {
            return false;
        }

        try {
            Process proc = Runtime.getRuntime().exec("ps aux");
            InputStream stream = proc.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.split("\\s+")[1].equals(pid)) {
                    return true;
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return false;
    }

}