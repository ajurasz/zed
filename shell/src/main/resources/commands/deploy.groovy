package commands

import org.crsh.cli.Argument
import org.crsh.cli.Command
import org.crsh.cli.Required
import org.crsh.cli.Usage
import org.crsh.command.InvocationContext
import zed.deployer.Deployer

class deploy {

    @Command
    def main(InvocationContext context, @Usage("URI of the artifact to deploy.") @Required @Argument String uri) {
        Deployer deployer = context.getAttributes().get('spring.beanfactory').getBean(Deployer.class)
        deployer.deploy(uri)
        return "Deployed " + uri
    }

}