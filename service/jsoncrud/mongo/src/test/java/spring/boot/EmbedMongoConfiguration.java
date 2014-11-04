package spring.boot;

import com.mongodb.MongoClient;
import de.flapdoodle.embed.mongo.MongodExecutable;
import de.flapdoodle.embed.mongo.MongodStarter;
import de.flapdoodle.embed.mongo.config.IMongodConfig;
import de.flapdoodle.embed.mongo.config.MongodConfigBuilder;
import de.flapdoodle.embed.mongo.config.Net;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.net.UnknownHostException;

import static de.flapdoodle.embed.mongo.distribution.Version.V2_6_1;
import static de.flapdoodle.embed.process.runtime.Network.localhostIsIPv6;
import static org.springframework.util.SocketUtils.findAvailableTcpPort;

@Configuration
public class EmbedMongoConfiguration {

    private final int port = findAvailableTcpPort();

    @Bean(initMethod = "start", destroyMethod = "stop")
    public MongodExecutable mongodExecutable() throws IOException {
        IMongodConfig mongodConfig = new MongodConfigBuilder()
                .version(V2_6_1)
                .net(new Net(port, localhostIsIPv6()))
                .build();
        return MongodStarter.getDefaultInstance().prepare(mongodConfig);
    }

    @Bean
    public MongoClient mongoClient() throws UnknownHostException {
        return new MongoClient("0.0.0.0", port);
    }

}
