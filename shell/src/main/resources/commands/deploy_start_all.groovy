package commands

import org.crsh.cli.Command
import org.crsh.command.InvocationContext
import zed.deployer.executor.ProcessExecutor
import zed.deployer.manager.DeployablesManager
import zed.deployer.manager.DeploymentDescriptor

class deploy_start_all {

    @Command
    def main(InvocationContext context) {
        ProcessExecutor processExecutor = context.getAttributes().get('spring.beanfactory').getBean(ProcessExecutor.class)
        DeployablesManager deployer = context.getAttributes().get('spring.beanfactory').getBean(DeployablesManager.class)
        String message = ""
        for (DeploymentDescriptor descriptor : deployer.list()) {
            def pid = processExecutor.start(descriptor.id())
            message += "Deployment ${descriptor.id()} has been started with PID ${pid}.\n"
        }
        return message
    }

}