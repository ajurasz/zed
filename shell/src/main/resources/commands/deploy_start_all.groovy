package commands

import org.crsh.cli.Command
import org.crsh.command.InvocationContext
import zed.deployer.StatusResolver
import zed.deployer.executor.ProcessExecutor
import zed.deployer.manager.DeployableDescriptor
import zed.deployer.manager.DeployablesManager

class deploy_start_all {

    @Command
    def main(InvocationContext context) {
        ProcessExecutor processExecutor = context.getAttributes().get('spring.beanfactory').getBean(ProcessExecutor.class)
        DeployablesManager deployer = context.getAttributes().get('spring.beanfactory').getBean(DeployablesManager.class)
        String message = ""
        for (DeployableDescriptor descriptor : deployer.list()) {
            def pid = processExecutor.start(descriptor.id())
            if (pid.isPresent()) {
                message += "Deployment ${descriptor.id()} has been started with PID ${pid.get()}.\n"
            } else {
                message += "Deployment ${descriptor.id()} is already running on PID ${descriptor.pid()}.\n"
            }
        }
        return message
    }

}