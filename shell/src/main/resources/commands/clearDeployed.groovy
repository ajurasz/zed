package commands

import org.crsh.cli.Command
import org.crsh.command.InvocationContext
import zed.deployer.Deployer

class clearDeployed {

    @Command
    def main(InvocationContext context) {
        Deployer deployer = context.getAttributes().get('spring.beanfactory').getBean(Deployer.class)
        deployer.clear()
        return "Deploy directory cleared."
    }

}