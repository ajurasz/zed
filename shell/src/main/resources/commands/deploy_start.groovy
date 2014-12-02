package commands

import org.crsh.cli.Argument
import org.crsh.cli.Command
import org.crsh.cli.Required
import org.crsh.command.InvocationContext
import zed.deployer.executor.ProcessExecutor

class deploy_start {

    @Command
    def main(InvocationContext context, @Required @Argument String deploymentId) {
        ProcessExecutor processExecutor = context.getAttributes().get('spring.beanfactory').getBean(ProcessExecutor.class)
        def pid = processExecutor.start(deploymentId)
        return "Deployment ${deploymentId} has been started with PID ${pid}."
    }

}