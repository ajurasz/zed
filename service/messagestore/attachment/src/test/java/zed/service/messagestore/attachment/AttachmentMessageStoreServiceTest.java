package zed.service.messagestore.attachment;

import org.apache.activemq.broker.BrokerService;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import zed.service.document.sdk.DocumentService;
import zed.service.document.sdk.RestDocumentService;

import static com.jayway.awaitility.Awaitility.await;
import static java.lang.Boolean.TRUE;
import static org.springframework.util.SocketUtils.findAvailableTcpPort;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {EmbedMongoConfiguration.class, AttachmentMessageStoreServiceTest.class, AttachmentMessageStoreServiceConfiguration.class})
@IntegrationTest
public class AttachmentMessageStoreServiceTest extends Assert {

    static int restApiPort = findAvailableTcpPort();

    DocumentService<TextMessage> documentService;

    @BeforeClass
    public static void beforeClass() {
        System.setProperty("server.port", findAvailableTcpPort() + "");
        System.setProperty("zed.service.api.port", restApiPort + "");

        System.setProperty("zed.service.document.mongodb.springbootconfig", TRUE.toString());
        System.setProperty("spring.data.mongodb.port", EmbedMongoConfiguration.port + "");
    }

    @Before
    public void before() {
        documentService = new RestDocumentService<>("http://localhost:" + restApiPort);
    }

    @Test
    public void shouldSaveMessage() throws MqttException, InterruptedException {

        // When
        MqttClient sampleClient = new MqttClient("tcp://localhost:1883", "clientId", new MemoryPersistence());
        MqttConnectOptions connOpts = new MqttConnectOptions();
        connOpts.setCleanSession(true);
        sampleClient.connect(connOpts);
        MqttMessage message = new MqttMessage("msg".getBytes());
        message.setQos(2);
        sampleClient.publish("TextMessage", message);
        sampleClient.disconnect();

        // Then
        await().until(() -> documentService.count(TextMessage.class) > 0);
        long documents = documentService.count(TextMessage.class);
        assertEquals(1, documents);
    }

    @Bean(initMethod = "start", destroyMethod = "stop")
    BrokerService brokerService() throws Exception {
        BrokerService broker = new BrokerService();
        broker.addConnector("mqtt://0.0.0.0:1883");
        broker.addConnector("tcp://0.0.0.0:61616");
        return broker;
    }

}

