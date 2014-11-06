package zed.service.jsoncrud.mongo;

import com.mongodb.MongoClient;
import org.apache.camel.CamelContext;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import spring.boot.EmbedMongoConfiguration;
import zed.service.jsoncrud.api.JsonCrudService;

import java.net.UnknownHostException;

import static org.apache.camel.ServiceStatus.Started;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {EmbedMongoConfiguration.class, MongoJsonCrudServiceConfiguration.class, MongoJsonCrudServiceTest.class})
@IntegrationTest
@ActiveProfiles("test")
public class MongoJsonCrudServiceTest extends Assert {

    @Autowired
    CamelContext camelContext;

    @Autowired
    JsonCrudService jsonCrudService;

    @Autowired
    MongoClient mongoClient;

    @Before
    public void before() throws UnknownHostException {
        mongoClient.getDB("zed_json_crud").dropDatabase();
    }

    @Test
    public void shouldStartCamelContext() {
        assertEquals(Started, camelContext.getStatus());
    }

    @Test
    public void shouldLoadRoutes() {
        assertFalse(camelContext.getRoutes().isEmpty());
    }

    @Test
    public void shouldSavePojo() throws UnknownHostException, InterruptedException {
        jsonCrudService.save(new Invoice("invoice001"));

        assertEquals(1, mongoClient.getDB("zed_json_crud").getCollection("Invoice").count());
    }

    @Test
    public void shouldGenerateOid_fromPojo() throws UnknownHostException, InterruptedException {
        // When
        String oid = jsonCrudService.save(new Invoice("invoice001"));

        // Then
        String recordOid = mongoClient.getDB("zed_json_crud").getCollection("Invoice").find().iterator().next().get("_id").toString();
        assertEquals(oid, recordOid);
    }

    @Test
    public void shouldGenerateOid_fromJson() throws UnknownHostException, InterruptedException {
        // When
        String oid = jsonCrudService.save("Invoice", "{invoiceId: 'id'}");

        // Then
        String recordOid = mongoClient.getDB("zed_json_crud").getCollection("Invoice").find().iterator().next().get("_id").toString();
        assertEquals(oid, recordOid);
    }

}

class Invoice {

    private String invoiceId;

    Invoice() {
    }

    Invoice(String invoiceId) {
        this.invoiceId = invoiceId;
    }

    public String getInvoiceId() {
        return invoiceId;
    }

    public void setInvoiceId(String invoiceId) {
        this.invoiceId = invoiceId;
    }

}