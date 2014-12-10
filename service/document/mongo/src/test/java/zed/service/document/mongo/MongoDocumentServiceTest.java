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
import java.util.Date;
import java.util.List;

import static org.joda.time.DateTime.now;
import static org.springframework.util.SocketUtils.findAvailableTcpPort;
import static zed.service.document.sdk.QueryBuilder.buildQuery;

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
        System.setProperty("server.port", findAvailableTcpPort() + "");
        System.setProperty("zed.service.document.rest.port", findAvailableTcpPort() + "");
        System.setProperty("spring.data.mongodb.host", "localhost");
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
        invoice = crudService.save(invoice);

        // When
        crudService.save(invoice);

        // Then
        assertEquals(1, mongo.getDB("zed_json_crud").getCollection("Invoice").count());
    }

    @Test
    public void shouldUpdateLoadedPojo() {
        // Given
        invoice = crudService.save(invoice);
        invoice = crudService.findOne(Invoice.class, invoice.getId());

        // When
        crudService.save(invoice);

        // Then
        assertEquals(1, mongo.getDB("zed_json_crud").getCollection("Invoice").count());
    }

    @Test
    public void shouldGenerateOid_fromPojo() throws UnknownHostException, InterruptedException {
        // When
        invoice = crudService.save(invoice);

        // Then
        String recordOid = mongo.getDB("zed_json_crud").getCollection("Invoice").find().iterator().next().get("_id").toString();
        assertEquals(invoice.getId(), recordOid);
    }

    @Test
    public void shouldFindOne() {
        // Given
        invoice = crudService.save(invoice);

        // When
        Invoice invoiceFound = crudService.findOne(Invoice.class, invoice.getId());

        // Then
        assertEquals(invoice.getId(), invoiceFound.getId());
    }

    @Test
    public void shouldFindMany() {
        // Given
        Invoice firstInvoice = crudService.save(new Invoice("invoice001"));
        Invoice secondInvoice = crudService.save(new Invoice("invoice002"));

        // When
        List<Invoice> invoices = crudService.findMany(Invoice.class, firstInvoice.getId(), secondInvoice.getId());

        // Then
        assertEquals(2, invoices.size());
        assertEquals(firstInvoice.getId(), invoices.get(0).getId());
        assertEquals(secondInvoice.getId(), invoices.get(1).getId());
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
        crudService.save(new Invoice());
        crudService.save(new Invoice());

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
    public void shouldReturnPageByQuery() {
        // Given
        Invoice firstInvoice = crudService.save(new Invoice());
        Invoice secondInvoice = crudService.save(new Invoice());
        Invoice thirdInvoice = crudService.save(new Invoice());

        // When
        List<Invoice> firstPage = crudService.findByQuery(Invoice.class, buildQuery(new InvoiceQuery()).page(0).size(2));
        List<Invoice> secondPage = crudService.findByQuery(Invoice.class, buildQuery(new InvoiceQuery()).page(1).size(2));

        // Then
        assertEquals(2, firstPage.size());
        assertEquals(1, secondPage.size());
        assertEquals(firstInvoice.getId(), firstPage.get(0).getId());
        assertEquals(secondInvoice.getId(), firstPage.get(1).getId());
        assertEquals(thirdInvoice.getId(), secondPage.get(0).getId());
    }

    @Test
    public void shouldSortDescending() {
        // Given
        Invoice firstInvoice = crudService.save(new Invoice().invoiceId("1"));
        Invoice secondInvoice = crudService.save(new Invoice().invoiceId("2"));
        Invoice thirdInvoice = crudService.save(new Invoice().invoiceId("3"));

        // When
        List<Invoice> firstPage = crudService.findByQuery(Invoice.class, buildQuery(
                new InvoiceQuery()).size(2).orderBy("invoiceId").sortAscending(false).page(0));
        List<Invoice> secondPage = crudService.findByQuery(Invoice.class, buildQuery(
                new InvoiceQuery()).size(2).orderBy("invoiceId").sortAscending(false).page(1));

        // Then
        assertEquals(2, firstPage.size());
        assertEquals(1, secondPage.size());
        assertEquals(thirdInvoice.getId(), firstPage.get(0).getId());
        assertEquals(secondInvoice.getId(), firstPage.get(1).getId());
        assertEquals(firstInvoice.getId(), secondPage.get(0).getId());
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
    public void shouldFindByQueryWithIn() {
        // Given
        crudService.save(invoice);
        InvoiceQuery query = new InvoiceQuery().invoiceIdIn(invoice.getInvoiceId(), "foo", "bar");

        // When
        List<Invoice> invoices = crudService.findByQuery(Invoice.class, new QueryBuilder(query));

        // Then
        assertEquals(1, invoices.size());
        assertEquals(invoice.getInvoiceId(), invoices.get(0).getInvoiceId());
    }

    @Test
    public void shouldNotFindByQueryWithIn() {
        // Given
        crudService.save(invoice);
        InvoiceQuery query = new InvoiceQuery().invoiceIdIn("foo", "bar");

        // When
        List<Invoice> invoices = crudService.findByQuery(Invoice.class, new QueryBuilder(query));

        // Then
        assertEquals(0, invoices.size());
    }

    @Test
    public void shouldFindByQueryWithNotIn() {
        // Given
        crudService.save(invoice);
        InvoiceQuery query = new InvoiceQuery().invoiceIdNotIn("foo", "bar");

        // When
        List<Invoice> invoices = crudService.findByQuery(Invoice.class, new QueryBuilder(query));

        // Then
        assertEquals(1, invoices.size());
        assertEquals(invoice.getInvoiceId(), invoices.get(0).getInvoiceId());
    }

    @Test
    public void shouldNotFindByQueryWithNotIn() {
        // Given
        crudService.save(invoice);
        InvoiceQuery query = new InvoiceQuery().invoiceIdNotIn(invoice.getInvoiceId(), "foo", "bar");

        // When
        List<Invoice> invoices = crudService.findByQuery(Invoice.class, new QueryBuilder(query));

        // Then
        assertEquals(0, invoices.size());
    }

    @Test
    public void shouldFindByQueryBetweenDateRange() {
        // Given
        Invoice todayInvoice = crudService.save(invoice);
        invoice = new Invoice();
        invoice.setTimestamp(now().minusDays(2).toDate());
        crudService.save(invoice);
        invoice = new Invoice();
        invoice.setTimestamp(now().plusDays(2).toDate());
        crudService.save(invoice);

        InvoiceQuery query = new InvoiceQuery();
        query.setTimestampGreaterThanEqual(now().minusDays(1).toDate());
        query.setTimestampLessThan(now().plusDays(1).toDate());

        // When
        List<Invoice> invoices = crudService.findByQuery(Invoice.class, new QueryBuilder(query));

        // Then
        assertEquals(1, invoices.size());
        assertEquals(todayInvoice.getId(), invoices.get(0).getId());
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
        invoice = crudService.save(invoice);

        // When
        crudService.remove(Invoice.class, invoice.getId());

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

    private Date timestamp = new Date();

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

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public String getInvoiceId() {
        return invoiceId;
    }

    public void setInvoiceId(String invoiceId) {
        this.invoiceId = invoiceId;
    }

    public Invoice invoiceId(String invoiceId) {
        this.invoiceId = invoiceId;
        return this;
    }

}

class InvoiceQuery {

    private String invoiceId;

    private String invoiceIdContains;

    private String[] invoiceIdIn;

    private String[] invoiceIdNotIn;

    private Date timestampLessThan;

    private Date timestampGreaterThanEqual;

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

    public String[] getInvoiceIdIn() {
        return invoiceIdIn;
    }

    public void setInvoiceIdIn(String[] invoiceIdIn) {
        this.invoiceIdIn = invoiceIdIn;
    }

    public InvoiceQuery invoiceIdIn(String... invoiceIdIn) {
        this.invoiceIdIn = invoiceIdIn;
        return this;
    }

    public String[] getInvoiceIdNotIn() {
        return invoiceIdNotIn;
    }

    public void setInvoiceIdNotIn(String[] invoiceIdNotIn) {
        this.invoiceIdNotIn = invoiceIdNotIn;
    }

    public InvoiceQuery invoiceIdNotIn(String... invoiceIdNotIn) {
        this.invoiceIdNotIn = invoiceIdNotIn;
        return this;
    }

    public Date getTimestampLessThan() {
        return timestampLessThan;
    }

    public void setTimestampLessThan(Date timestampLessThan) {
        this.timestampLessThan = timestampLessThan;
    }

    public Date getTimestampGreaterThanEqual() {
        return timestampGreaterThanEqual;
    }

    public void setTimestampGreaterThanEqual(Date timestampGreaterThanEqual) {
        this.timestampGreaterThanEqual = timestampGreaterThanEqual;
    }

}