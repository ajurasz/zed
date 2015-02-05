package zed.shell.jmx;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.jmx.export.annotation.ManagedOperation;
import org.springframework.jmx.export.annotation.ManagedOperationParameter;
import org.springframework.jmx.export.annotation.ManagedOperationParameters;
import org.springframework.jmx.export.annotation.ManagedResource;
import org.springframework.stereotype.Component;
import zed.ssh.client.SshClient;

@Component
@ManagedResource(objectName = "zed:name=zedShell", description = "Shell JMX endpoint.")
public class SshShellJmxEndpoint implements ShellJmxEndpoint {

    @Value("${shell.ssh.port}")
    int port;

    @Override
    @ManagedOperation(description = "Invoke shell command.")
    @ManagedOperationParameters({
            @ManagedOperationParameter(name = "command", description = "Command to execute.")})
    public String[] invokeCommand(String command) {
        return new SshClient("localhost", port).command(command).toArray(new String[0]);
    }

}
