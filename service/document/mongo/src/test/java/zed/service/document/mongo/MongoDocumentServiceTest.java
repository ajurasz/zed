package zed.service.document.mongo;

import com.mongodb.Mongo;
import org.bson.types.ObjectId;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import spring.boot.EmbedMongoConfiguration;
import zed.service.document.sdk.DocumentService;
import zed.service.document.sdk.QueryBuilder;
import zed.service.document.sdk.RestDocumentService;

import java.net.UnknownHostException;
import java.util.List;

import static org.springframework.util.SocketUtils.findAvailableTcpPort;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {EmbedMongoConfiguration.class, MongoDbDocumentServiceConfiguration.class, MongoDocumentServiceTestConfiguration.class})
@IntegrationTest
@ActiveProfiles("test")
public class MongoDocumentServiceTest extends Assert {

    @Autowired
    DocumentService crudService;

    @Autowired
    Mongo mongo;

    Invoice invoice = new Invoice("invoice001");

    @BeforeClass
    public static void beforeClass() {
        System.setProperty("zed.service.document.rest.port", findAvailableTcpPort() + "");
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
    public void shouldFindMany() {
        // Given
        String firstId = crudService.save(new Invoice("invoice001"));
        String secondId = crudService.save(new Invoice("invoice002"));

        // When
        List<Invoice> invoices = crudService.findMany(Invoice.class, firstId, secondId);

        // Then
        assertEquals(2, invoices.size());
        assertEquals(firstId, invoices.get(0).getId());
        assertEquals(secondId, invoices.get(1).getId());
    }

    @Test
    public void shouldNotFindMany() {
        // When
        List<Invoice> invoices = crudService.findMany(Invoice.class, ObjectId.get().toString(), ObjectId.get().toString());

        // Then
        assertEquals(0, invoices.size());
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
        crudService.save(invoice);
        InvoiceQuery query = new InvoiceQuery(invoice.getInvoiceId());

        // When
        List<Invoice> invoices = crudService.findByQuery(Invoice.class, new QueryBuilder(query));

        // Then
        assertEquals(1, invoices.size());
        assertEquals(invoice.getInvoiceId(), invoices.get(0).getInvoiceId());
    }

    @Test
    public void shouldFindAllByQuery() {
        // Given
        crudService.save(invoice);
        crudService.save(invoice);

        // When
        InvoiceQuery query = new InvoiceQuery();
        List<Invoice> invoices = crudService.findByQuery(Invoice.class, new QueryBuilder(query));

        // Then
        assertEquals(2, invoices.size());
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
    public void shouldFindByQueryWithContains() {
        // Given
        crudService.save(invoice);
        InvoiceQuery query = new InvoiceQuery();
        query.setInvoiceIdContains("voice");

        // When
        List<Invoice> invoices = crudService.findByQuery(Invoice.class, new QueryBuilder(query));

        // Then
        assertEquals(1, invoices.size());
        assertEquals(invoice.getInvoiceId(), invoices.get(0).getInvoiceId());
    }

    @Test
    public void shouldNotFindByQueryWithContains() {
        // Given
        crudService.save(invoice);
        InvoiceQuery query = new InvoiceQuery();
        query.setInvoiceIdContains("randomString");

        // When
        List<Invoice> invoices = crudService.findByQuery(Invoice.class, new QueryBuilder(query));

        // Then
        assertEquals(0, invoices.size());
    }

    @Test
    public void shouldCountPositiveByQuery() {
        // Given
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
        crudService.save(invoice);
        InvoiceQuery query = new InvoiceQuery("randomValue");

        // When
        long invoices = crudService.countByQuery(Invoice.class, new QueryBuilder(query));

        // Then
        assertEquals(0, invoices);
    }

    @Test
    public void shouldCountPositiveByQueryWithContains() {
        // Given
        crudService.save(invoice);
        InvoiceQuery query = new InvoiceQuery();
        query.setInvoiceIdContains("voice");

        // When
        long invoices = crudService.countByQuery(Invoice.class, new QueryBuilder(query));

        // Then
        assertEquals(1, invoices);
    }

    @Test
    public void shouldCountNegativeByQueryWithContains() {
        // Given
        crudService.save(invoice);
        InvoiceQuery query = new InvoiceQuery();
        query.setInvoiceIdContains("randomString");

        // When
        long invoices = crudService.countByQuery(Invoice.class, new QueryBuilder(query));

        // Then
        assertEquals(0, invoices);
    }

    @Test
    public void shouldRemoveDocument() {
        // Given
        String id = crudService.save(invoice);

        // When
        crudService.remove(Invoice.class, id);

        // Then
        long count = crudService.count(Invoice.class);
        assertEquals(0, count);
    }

}

@Configuration
class MongoDocumentServiceTestConfiguration {

    @Value("${zed.service.document.rest.port}")
    int restPort;

    @Bean
    DocumentService documentService() {
        return new RestDocumentService("http://0.0.0.0:" + restPort);
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

    private String invoiceIdContains;

    InvoiceQuery() {
    }

    InvoiceQuery(String invoiceId) {
        this.setInvoiceId(invoiceId);
    }

    public String getInvoiceId() {
        return invoiceId;
    }

    public void setInvoiceId(String invoiceId) {
        this.invoiceId = invoiceId;
    }

    public String getInvoiceIdContains() {
        return invoiceIdContains;
    }

    public void setInvoiceIdContains(String invoiceIdLike) {
        this.invoiceIdContains = invoiceIdLike;
    }

}