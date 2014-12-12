package commands

import org.crsh.cli.Command
import org.crsh.command.InvocationContext
import zed.deployer.manager.DeployablesManager

class deploy_clean {

    @Command
    def main(InvocationContext context) {
        DeployablesManager deployer = context.getAttributes().get('spring.beanfactory').getBean(DeployablesManager.class)
        deployer.clear()
        return "Deploy directory cleaned."
    }

}