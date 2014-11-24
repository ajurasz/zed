package commands

import org.crsh.cli.Argument
import org.crsh.cli.Command
import org.crsh.cli.Required
import org.crsh.cli.Usage
import org.crsh.command.InvocationContext
import zed.deployer.DeploymentManager

class deploy {

    @Command
    def main(InvocationContext context, @Usage("URI of the artifact to deploy.") @Required @Argument String uri) {
        DeploymentManager deployer = context.getAttributes().get('spring.beanfactory').getBean(DeploymentManager.class)
        deployer.deploy(uri)
        return "Deployed " + uri
    }

}