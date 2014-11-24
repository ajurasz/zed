package commands

import org.crsh.cli.Command
import org.crsh.command.InvocationContext
import zed.deployer.DeploymentManager

class clearDeployed {

    @Command
    def main(InvocationContext context) {
        DeploymentManager deployer = context.getAttributes().get('spring.beanfactory').getBean(DeploymentManager.class)
        deployer.clear()
        return "Deploy directory cleared."
    }

}