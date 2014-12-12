package commands

import org.crsh.cli.Argument
import org.crsh.cli.Command
import org.crsh.cli.Required
import org.crsh.cli.Usage
import org.crsh.command.InvocationContext
import zed.deployer.manager.DeployablesManager
import zed.mavenrepo.MavenDependencyResolutionException

class deploy {

    @Command
    def main(InvocationContext context, @Usage("URI of the artifact to deploy.") @Required @Argument String uri) {
        try {
            DeployablesManager deployer = context.getAttributes().get('spring.beanfactory').getBean(DeployablesManager.class)
            deployer.deploy(uri)
            return "Deployed " + uri
        } catch (MavenDependencyResolutionException e) {
            return e.getCause().getMessage()
        } catch (RuntimeException e) {
            return e.getMessage()
        }
    }

}