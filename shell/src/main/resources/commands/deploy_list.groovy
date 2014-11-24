package commands

import org.crsh.cli.Command
import org.crsh.command.InvocationContext
import zed.deployer.DeploymentManager

class deploy_list {

    @Command
    def main(InvocationContext context) {
        DeploymentManager deployer = context.getAttributes().get('spring.beanfactory').getBean(DeploymentManager.class)
        def deployments = deployer.list()
        return "Deployments:\n" + deployments.join("\n")
    }

}