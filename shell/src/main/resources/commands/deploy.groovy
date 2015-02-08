package commands

import org.crsh.cli.Argument
import org.crsh.cli.Command
import org.crsh.cli.Required
import org.crsh.cli.Usage
import org.crsh.command.InvocationContext
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import zed.deployer.manager.DeployablesManager
import zed.mavenrepo.MavenDependencyResolutionException

class deploy {

    private final static Logger LOG = LoggerFactory.getLogger(deploy.class)

    @Command
    def main(InvocationContext context, @Usage("URI of the artifact to deploy.") @Required @Argument String uri) {
        try {
            DeployablesManager deployer = context.getAttributes().get('spring.beanfactory').getBean(DeployablesManager.class)
            deployer.deploy(uri)
            return "Deployed " + uri
        } catch (MavenDependencyResolutionException e) {
            return e.getCause().getMessage()
        } catch (RuntimeException e) {
            LOG.info("Error occured when deploying ${uri} .", e)
            return e.getMessage()
        }
    }

}