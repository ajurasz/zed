package zed.service.jsoncrud.mongo;

import com.mongodb.Mongo;
import org.bson.types.ObjectId;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import spring.boot.EmbedMongoConfiguration;
import zed.service.jsoncrud.sdk.JsonCrudService;
import zed.service.jsoncrud.sdk.QueryBuilder;
import zed.service.jsoncrud.sdk.RestJsonCrudServiceClient;

import java.net.UnknownHostException;
import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {EmbedMongoConfiguration.class, MongoJsonCrudServiceConfiguration.class, MongoJsonCrudServiceTest.class})
@IntegrationTest
@ActiveProfiles("test")
public class MongoJsonCrudServiceTest extends Assert {

    @Autowired
    JsonCrudService crudService;

    @Autowired
    Mongo mongo;

    @Bean
    JsonCrudService jsonCrudService() {
        return new RestJsonCrudServiceClient("http://0.0.0.0:18080");
    }

    @BeforeClass
    public static void beforeClass() {
        System.setProperty("spring.data.mongodb.port", EmbedMongoConfiguration.port + "");
    }

    @Before
    public void before() throws UnknownHostException {
        mongo.getDB("zed_json_crud").dropDatabase();
    }

    // Tests

    @Test
    public void shouldCreatePojo() throws UnknownHostException, InterruptedException {
        // When
        crudService.save(new Invoice("invoice001"));

        // Then
        assertEquals(1, mongo.getDB("zed_json_crud").getCollection("Invoice").count());
    }

    @Test
    public void shouldUpdatePojoWithAssignedId() {
        // Given
        Invoice invoice = new Invoice();
        String oid = crudService.save(invoice);
        invoice.setId(oid);

        // When
        crudService.save(invoice);

        // Then
        List<Invoice> invs = crudService.findByQuery(Invoice.class, new QueryBuilder<>(new InvoiceQuery()));
        System.out.println();
        assertEquals(1, mongo.getDB("zed_json_crud").getCollection("Invoice").count());
    }

    @Test
    public void shouldUpdateLoadedPojo() {
        // Given
        String oid = crudService.save(new Invoice());
        Invoice invoice = crudService.findOne(Invoice.class, oid);

        // When
        crudService.save(invoice);

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
        assertEquals(savedOid, invoice.getId());
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
        long invoices = crudService.countByQuery(Invoice.class, new QueryBuilder(query));

        // Then
        assertEquals(1, invoices);
    }

    @Test
    public void shouldCountNegativeByQuery() {
        // Given
        crudService.save(new Invoice("invoice001"));
        InvoiceQuery query = new InvoiceQuery("randomValue");

        // When
        long invoices = crudService.countByQuery(Invoice.class, new QueryBuilder(query));

        // Then
        assertEquals(0, invoices);
    }

}

class Invoice {

    private String id;

    private String invoiceId;

    Invoice() {
    }

    Invoice(String invoiceId) {
        this.invoiceId = invoiceId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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