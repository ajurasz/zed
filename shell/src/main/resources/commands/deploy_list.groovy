package commands

import org.crsh.cli.Command
import org.crsh.command.InvocationContext
import zed.deployer.DeploymentManager
import zed.deployer.StatusResolver

class deploy_list {

    @Command
    def main(InvocationContext context) {
        DeploymentManager deployer = context.getAttributes().get('spring.beanfactory').getBean(DeploymentManager.class)
        StatusResolver statusResolver = context.getAttributes().get('spring.beanfactory').getBean(StatusResolver.class)
        def deployments = deployer.list().collect { deploy -> "${deploy.id()}\t${deploy.pid()}\t${statusResolver.status(deploy.id())}\t${deploy.uri()}" }
        return "Deployments:\n[ID]\t[PID]\t[Running]\t[URI]\n" + deployments.join("\n")
    }

}