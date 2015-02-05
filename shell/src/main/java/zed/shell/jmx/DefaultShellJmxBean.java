package zed.shell.jmx;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.jmx.export.annotation.ManagedOperation;
import org.springframework.jmx.export.annotation.ManagedOperationParameter;
import org.springframework.jmx.export.annotation.ManagedOperationParameters;
import org.springframework.jmx.export.annotation.ManagedResource;
import org.springframework.stereotype.Component;
import zed.ssh.client.SshClient;

@ManagedResource(
        objectName = "zed:name=zedShell",
        description = "My Managed Bean",
        log = true,
        logFile = "jmx.log",
        currencyTimeLimit = 15,
        persistPolicy = "OnUpdate",
        persistPeriod = 200,
        persistLocation = "foo",
        persistName = "bar")
@Component
public class DefaultShellJmxBean implements ShellJmxBean {

    @Value("${shell.ssh.port}")
    int port;

    @Override
    @ManagedOperation(description = "Add two numbers")
    @ManagedOperationParameters({
            @ManagedOperationParameter(name = "x", description = "The first number")})
    public String[] invokeCommand(String command) {
        return new SshClient("localhost", port).command(command).toArray(new String[0]);
    }

}
