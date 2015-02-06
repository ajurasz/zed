package commands

import org.crsh.cli.Argument
import org.crsh.cli.Command
import org.crsh.cli.Required
import org.crsh.command.InvocationContext
import zed.deployer.StatusResolver
import zed.deployer.executor.ProcessExecutor
import zed.deployer.manager.DeployablesManager

class deploy_start {

    @Command
    def main(InvocationContext context, @Required @Argument String deploymentId) {
        ProcessExecutor processExecutor = context.getAttributes().get('spring.beanfactory').getBean(ProcessExecutor.class)
        DeployablesManager deployer = context.getAttributes().get('spring.beanfactory').getBean(DeployablesManager.class)
        def pid = processExecutor.start(deploymentId)
        if (pid.isPresent()) {
            return "Deployment ${deploymentId} has been started with PID ${pid.get()}."
        } else {
            return "Deployment ${deploymentId} is already running on PID ${deployer?.deployment(deploymentId)?.pid()}."
        }
    }

}