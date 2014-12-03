package org.springframework.boot.autoconfigure.spotifydocker;

import com.spotify.docker.client.DockerClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionOutcome;
import org.springframework.boot.autoconfigure.condition.SpringBootCondition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

import static org.springframework.boot.autoconfigure.spotifydocker.Dockers.isConnected;

public class OnDockerCondition extends SpringBootCondition {

    @Autowired
    DockerClient dockerx;

    @Override
    public ConditionOutcome getMatchOutcome(ConditionContext context, AnnotatedTypeMetadata metadata) {
        DockerClient docker = context.getBeanFactory().getBean(DockerClient.class);
        if (docker == null) {
            return ConditionOutcome.noMatch("No Docker client found in application context.");
        }
        if (isConnected(docker)) {
            return ConditionOutcome.match("Docker client available and connected to the server.");
        } else {
            return ConditionOutcome.noMatch("Docker client can't connect to the server.");
        }
    }

}