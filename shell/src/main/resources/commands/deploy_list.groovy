package commands

import org.crsh.cli.Command
import org.crsh.command.InvocationContext
import zed.deployer.StatusResolver
import zed.deployer.manager.DeployablesManager

class deploy_list {

    @Command
    def main(InvocationContext context) {
        DeployablesManager deployer = context.getAttributes().get('spring.beanfactory').getBean(DeployablesManager.class)
        StatusResolver statusResolver = context.getAttributes().get('spring.beanfactory').getBean(StatusResolver.class)
        def deployments = deployer.list().collect { deploy -> "${deploy.id()}\t${deploy.pid()}\t${statusResolver.status(deploy.id())}\t${deploy.uri()}" }
        return "Deployments:\n[ID]\t[PID]\t[Running]\t[URI]\n" + deployments.join("\n")
    }

}