package zed.service.jsoncrud.mongo;

import com.mongodb.Mongo;
import org.apache.camel.CamelContext;
import org.bson.types.ObjectId;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import spring.boot.EmbedMongoConfiguration;
import zed.service.jsoncrud.api.JsonCrudService;
import zed.service.jsoncrud.api.QueryBuilder;
import zed.service.jsoncrud.api.client.RestJsonCrudServiceClient;

import java.net.UnknownHostException;
import java.util.List;

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

    JsonCrudService crudService = new RestJsonCrudServiceClient("http://0.0.0.0:18080");

    @Autowired
    Mongo mongo;

    @BeforeClass
    public static void beforeClass() {
        System.setProperty("spring.data.mongodb.port", EmbedMongoConfiguration.port + "");
    }

    @Before
    public void before() throws UnknownHostException {
        mongo.getDB("zed_json_crud").dropDatabase();
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
        // When
        crudService.save(new Invoice("invoice001"));

        // Then
        assertEquals(1, mongo.getDB("zed_json_crud").getCollection("Invoice").count());
    }

    @Test
    public void shouldGenerateOid_fromPojo() throws UnknownHostException, InterruptedException {
        // When
        String oid = crudService.save(new Invoice("invoice001"));

        // Then
        String recordOid = mongo.getDB("zed_json_crud").getCollection("Invoice").find().iterator().next().get("_id").toString();
        assertEquals(oid, recordOid);
    }

    @Test
    public void shouldFindOne() {
        // Given
        String savedOid = crudService.save(new Invoice("invoice001"));

        // When
        Invoice invoice = crudService.findOne(Invoice.class, savedOid);

        // Then
        assertEquals(savedOid, invoice.get_id());
    }

    @Test
    public void shouldNotFindOne() {
        // When
        Invoice invoice = crudService.findOne(Invoice.class, ObjectId.get().toString());

        // Then
        assertNull(invoice);
    }

    @Test
    public void shouldCount() throws UnknownHostException, InterruptedException {
        // Given
        crudService.save(new Invoice("invoice001"));

        // When
        long invoices = crudService.count(Invoice.class);

        // Then
        assertEquals(1, invoices);
    }

    @Test
    public void shouldFindByQuery() {
        // Given
        Invoice invoice = new Invoice("invoice001");
        crudService.save(invoice);
        InvoiceQuery query = new InvoiceQuery(invoice.getInvoiceId());

        // When
        List<Invoice> invoices = crudService.findByQuery(Invoice.class, new QueryBuilder(query));

        // Then
        assertEquals(1, invoices.size());
        assertEquals(invoice.getInvoiceId(), invoices.get(0).getInvoiceId());
    }

    @Test
    public void shouldNotFindByQuery() {
        // Given
        crudService.save(new Invoice("invoice001"));
        InvoiceQuery query = new InvoiceQuery("randomValue");

        // When
        List<Invoice> invoices = crudService.findByQuery(Invoice.class, new QueryBuilder(query));

        // Then
        assertEquals(0, invoices.size());
    }

    @Test
    public void shouldCountPositiveByQuery() {
        // Given
        Invoice invoice = new Invoice("invoice001");
        crudService.save(invoice);
        InvoiceQuery query = new InvoiceQuery(invoice.getInvoiceId());

        // When
        long invoices = jsonCrudService.countByQuery(Invoice.class, new QueryBuilder(query));

        // Then
        assertEquals(1, invoices);
    }

    @Test
    public void shouldCountNegativeByQuery() {
        // Given
        crudService.save(new Invoice("invoice001"));
        InvoiceQuery query = new InvoiceQuery("randomValue");

        // When
        long invoices = jsonCrudService.countByQuery(Invoice.class, new QueryBuilder(query));

        // Then
        assertEquals(0, invoices);
    }

}

class Invoice {

    private String _id;

    private String invoiceId;

    Invoice() {
    }

    Invoice(String invoiceId) {
        this.invoiceId = invoiceId;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getInvoiceId() {
        return invoiceId;
    }

    public void setInvoiceId(String invoiceId) {
        this.invoiceId = invoiceId;
    }

}

class InvoiceQuery {

    private String invoiceId;

    InvoiceQuery() {
    }

    InvoiceQuery(String invoiceId) {
        this.invoiceId = invoiceId;
    }

    public String getInvoiceId() {
        return invoiceId;
    }

    public void setInvoiceId(String invoiceId) {
        this.invoiceId = invoiceId;
    }

}