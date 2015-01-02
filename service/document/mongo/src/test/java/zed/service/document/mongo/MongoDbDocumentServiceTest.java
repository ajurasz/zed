package zed.service.document.mongo;

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
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import spring.boot.EmbedMongoConfiguration;
import zed.service.document.mongo.Invoice.Address;
import zed.service.document.sdk.DocumentService;
import zed.service.document.sdk.QueryBuilder;
import zed.service.document.sdk.RestDocumentService;

import java.net.UnknownHostException;
import java.util.Date;
import java.util.List;

import static org.joda.time.DateTime.now;
import static org.springframework.util.SocketUtils.findAvailableTcpPort;
import static zed.service.document.mongo.crossstore.sql.Pojos.pojoClassToCollection;
import static zed.service.document.sdk.QueryBuilder.buildQuery;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {EmbedMongoConfiguration.class, MongoDbDocumentServiceConfiguration.class, MongoDocumentServiceTestConfiguration.class})
@IntegrationTest
@ActiveProfiles("test")
public class MongoDbDocumentServiceTest extends Assert {

    @Autowired
    DocumentService<Invoice> documentService;

    @Autowired
    MongoTemplate mongo;

    Invoice invoice = new Invoice("invoice001");

    @BeforeClass
    public static void beforeClass() {
        System.setProperty("server.port", findAvailableTcpPort() + "");
        System.setProperty("zed.service.api.port", findAvailableTcpPort() + "");
        System.setProperty("spring.data.mongodb.host", "localhost");
        System.setProperty("spring.data.mongodb.port", EmbedMongoConfiguration.port + "");
    }

    @Before
    public void before() {
        mongo.dropCollection(pojoClassToCollection(Invoice.class));
    }

    // Tests

    @Test
    public void shouldCreatePojo() throws UnknownHostException, InterruptedException {
        // When
        invoice = documentService.save(invoice);

        // Then
        Invoice loadedInvoice = documentService.findOne(Invoice.class, invoice.getId());
        assertNotNull(loadedInvoice);
    }

    @Test
    public void shouldUpdatePojoWithAssignedId() {
        // Given
        invoice = documentService.save(invoice);

        // When
        documentService.save(invoice);

        // Then
        assertEquals(1, documentService.count(Invoice.class));
    }

    @Test
    public void shouldUpdateLoadedDocument() {
        // Given
        invoice = documentService.save(invoice);
        Invoice loadedInvoice = documentService.findOne(Invoice.class, invoice.getId());

        // When
        documentService.save(loadedInvoice);

        // Then
        assertEquals(1, documentService.count(Invoice.class));
    }

    @Test
    public void shouldGenerateId() {
        // When
        invoice = documentService.save(invoice);

        // Then
        assertNotNull(invoice.getId());
    }

    @Test
    public void shouldFindOne() {
        // Given
        invoice = documentService.save(invoice);

        // When
        Invoice invoiceFound = documentService.findOne(Invoice.class, invoice.getId());

        // Then
        assertEquals(invoice.getId(), invoiceFound.getId());
    }

    @Test
    public void shouldFindMany() {
        // Given
        Invoice firstInvoice = documentService.save(new Invoice("invoice001"));
        Invoice secondInvoice = documentService.save(new Invoice("invoice002"));

        // When
        List<Invoice> invoices = documentService.findMany(Invoice.class, firstInvoice.getId(), secondInvoice.getId());

        // Then
        assertEquals(2, invoices.size());
        assertEquals(firstInvoice.getId(), invoices.get(0).getId());
        assertEquals(secondInvoice.getId(), invoices.get(1).getId());
    }

    @Test
    public void shouldNotFindMany() {
        // When
        List<Invoice> invoices = documentService.findMany(Invoice.class, ObjectId.get().toString(), ObjectId.get().toString());

        // Then
        assertEquals(0, invoices.size());
    }

    @Test
    public void shouldNotFindOne() {
        // When
        Invoice invoice = documentService.findOne(Invoice.class, ObjectId.get().toString());

        // Then
        assertNull(invoice);
    }

    @Test
    public void shouldCount() throws UnknownHostException, InterruptedException {
        // Given
        documentService.save(new Invoice("invoice001"));

        // When
        long invoices = documentService.count(Invoice.class);

        // Then
        assertEquals(1, invoices);
    }

    @Test
    public void shouldFindByQuery() {
        // Given
        documentService.save(invoice);
        InvoiceQuery query = new InvoiceQuery().invoiceId(invoice.getInvoiceId());

        // When
        List<Invoice> invoices = documentService.findByQuery(Invoice.class, new QueryBuilder(query));

        // Then
        assertEquals(1, invoices.size());
        assertEquals(invoice.getInvoiceId(), invoices.get(0).getInvoiceId());
    }

    @Test
    public void shouldFindAllByQuery() {
        // Given
        documentService.save(new Invoice());
        documentService.save(new Invoice());

        // When
        InvoiceQuery query = new InvoiceQuery();
        List<Invoice> invoices = documentService.findByQuery(Invoice.class, new QueryBuilder(query));

        // Then
        assertEquals(2, invoices.size());
    }

    @Test
    public void shouldNotFindByQuery() {
        // Given
        documentService.save(new Invoice("invoice001"));
        InvoiceQuery query = new InvoiceQuery().invoiceId("randomValue");

        // When
        List<Invoice> invoices = documentService.findByQuery(Invoice.class, new QueryBuilder(query));

        // Then
        assertEquals(0, invoices.size());
    }

    @Test
    public void shouldFindByNestedQuery() {
        // Given
        String street = "someStreet";
        invoice.setAddress(new Address(street));
        invoice = documentService.save(invoice);

        InvoiceQuery query = new InvoiceQuery().invoiceId(invoice.getInvoiceId()).address_street(street);

        // When
        List<Invoice> invoices = documentService.findByQuery(Invoice.class, new QueryBuilder(query));

        // Then
        assertEquals(1, invoices.size());
        assertEquals(street, invoices.get(0).getAddress().getStreet());
    }

    @Test
    public void shouldNotFindByNestedQuery() {
        // Given
        String street = "someStreet";
        invoice.setAddress(new Address(street));
        invoice = documentService.save(invoice);

        InvoiceQuery query = new InvoiceQuery().invoiceId(invoice.getInvoiceId()).address_street("someRandomStreet");

        // When
        List<Invoice> invoices = documentService.findByQuery(Invoice.class, new QueryBuilder(query));

        // Then
        assertEquals(0, invoices.size());
    }

    @Test
    public void shouldReturnPageByQuery() {
        // Given
        Invoice firstInvoice = documentService.save(new Invoice());
        Invoice secondInvoice = documentService.save(new Invoice());
        Invoice thirdInvoice = documentService.save(new Invoice());

        // When
        List<Invoice> firstPage = documentService.findByQuery(Invoice.class, buildQuery(new InvoiceQuery()).page(0).size(2));
        List<Invoice> secondPage = documentService.findByQuery(Invoice.class, buildQuery(new InvoiceQuery()).page(1).size(2));

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
        Invoice firstInvoice = documentService.save(new Invoice().invoiceId("1"));
        Invoice secondInvoice = documentService.save(new Invoice().invoiceId("2"));
        Invoice thirdInvoice = documentService.save(new Invoice().invoiceId("3"));

        // When
        List<Invoice> firstPage = documentService.findByQuery(Invoice.class, buildQuery(
                new InvoiceQuery()).size(2).orderBy("invoiceId").sortAscending(false).page(0));
        List<Invoice> secondPage = documentService.findByQuery(Invoice.class, buildQuery(
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
        documentService.save(invoice);
        InvoiceQuery query = new InvoiceQuery();
        query.setInvoiceIdContains("voice");

        // When
        List<Invoice> invoices = documentService.findByQuery(Invoice.class, new QueryBuilder(query));

        // Then
        assertEquals(1, invoices.size());
        assertEquals(invoice.getInvoiceId(), invoices.get(0).getInvoiceId());
    }

    @Test
    public void shouldNotFindByQueryWithContains() {
        // Given
        documentService.save(invoice);
        InvoiceQuery query = new InvoiceQuery();
        query.setInvoiceIdContains("randomString");

        // When
        List<Invoice> invoices = documentService.findByQuery(Invoice.class, new QueryBuilder(query));

        // Then
        assertEquals(0, invoices.size());
    }

    @Test
    public void shouldFindByQueryWithIn() {
        // Given
        documentService.save(invoice);
        InvoiceQuery query = new InvoiceQuery().invoiceIdIn(invoice.getInvoiceId(), "foo", "bar");

        // When
        List<Invoice> invoices = documentService.findByQuery(Invoice.class, new QueryBuilder(query));

        // Then
        assertEquals(1, invoices.size());
        assertEquals(invoice.getInvoiceId(), invoices.get(0).getInvoiceId());
    }

    @Test
    public void shouldNotFindByQueryWithIn() {
        // Given
        documentService.save(invoice);
        InvoiceQuery query = new InvoiceQuery().invoiceIdIn("foo", "bar");

        // When
        List<Invoice> invoices = documentService.findByQuery(Invoice.class, new QueryBuilder(query));

        // Then
        assertEquals(0, invoices.size());
    }

    @Test
    public void shouldFindByQueryWithNotIn() {
        // Given
        documentService.save(invoice);
        InvoiceQuery query = new InvoiceQuery().invoiceIdNotIn("foo", "bar");

        // When
        List<Invoice> invoices = documentService.findByQuery(Invoice.class, new QueryBuilder(query));

        // Then
        assertEquals(1, invoices.size());
        assertEquals(invoice.getInvoiceId(), invoices.get(0).getInvoiceId());
    }

    @Test
    public void shouldNotFindByQueryWithNotIn() {
        // Given
        documentService.save(invoice);
        InvoiceQuery query = new InvoiceQuery().invoiceIdNotIn(invoice.getInvoiceId(), "foo", "bar");

        // When
        List<Invoice> invoices = documentService.findByQuery(Invoice.class, new QueryBuilder(query));

        // Then
        assertEquals(0, invoices.size());
    }

    @Test
    public void shouldFindByQueryBetweenDateRange() {
        // Given
        Invoice todayInvoice = documentService.save(invoice);
        invoice = new Invoice();
        invoice.setTimestamp(now().minusDays(2).toDate());
        documentService.save(invoice);
        invoice = new Invoice();
        invoice.setTimestamp(now().plusDays(2).toDate());
        documentService.save(invoice);

        InvoiceQuery query = new InvoiceQuery();
        query.setTimestampGreaterThanEqual(now().minusDays(1).toDate());
        query.setTimestampLessThan(now().plusDays(1).toDate());

        // When
        List<Invoice> invoices = documentService.findByQuery(Invoice.class, new QueryBuilder(query));

        // Then
        assertEquals(1, invoices.size());
        assertEquals(todayInvoice.getId(), invoices.get(0).getId());
    }

    @Test
    public void shouldCountPositiveByQuery() {
        // Given
        documentService.save(invoice);
        InvoiceQuery query = new InvoiceQuery().invoiceId(invoice.getInvoiceId());

        // When
        long invoices = documentService.countByQuery(Invoice.class, new QueryBuilder(query));

        // Then
        assertEquals(1, invoices);
    }

    @Test
    public void shouldCountNegativeByQuery() {
        // Given
        documentService.save(invoice);
        InvoiceQuery query = new InvoiceQuery().invoiceId("randomValue");

        // When
        long invoices = documentService.countByQuery(Invoice.class, new QueryBuilder(query));

        // Then
        assertEquals(0, invoices);
    }

    @Test
    public void shouldCountPositiveByQueryWithContains() {
        // Given
        documentService.save(invoice);
        InvoiceQuery query = new InvoiceQuery();
        query.setInvoiceIdContains("voice");

        // When
        long invoices = documentService.countByQuery(Invoice.class, new QueryBuilder(query));

        // Then
        assertEquals(1, invoices);
    }

    @Test
    public void shouldCountNegativeByQueryWithContains() {
        // Given
        documentService.save(invoice);
        InvoiceQuery query = new InvoiceQuery();
        query.setInvoiceIdContains("randomString");

        // When
        long invoices = documentService.countByQuery(Invoice.class, new QueryBuilder(query));

        // Then
        assertEquals(0, invoices);
    }

    @Test
    public void shouldRemoveDocument() {
        // Given
        invoice = documentService.save(invoice);

        // When
        documentService.remove(Invoice.class, invoice.getId());

        // Then
        long count = documentService.count(Invoice.class);
        assertEquals(0, count);
    }

}

@Configuration
class MongoDocumentServiceTestConfiguration {

    @Bean
    DocumentService documentService(@Value("${zed.service.api.port}") int restApiPort) {
        return new RestDocumentService(restApiPort);
    }

}

class Invoice {

    private String id;

    private Date timestamp = new Date();

    private String invoiceId;

    private Address address;

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

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    static class Address {

        private String street;

        public Address() {
        }

        public Address(String street) {
            this.street = street;
        }

        public String getStreet() {
            return street;
        }

        public void setStreet(String street) {
            this.street = street;
        }

    }

}

class InvoiceQuery {

    private String invoiceId;

    private String invoiceIdContains;

    private String[] invoiceIdIn;

    private String[] invoiceIdNotIn;

    private Date timestampLessThan;

    private Date timestampGreaterThanEqual;

    private String address_street;

    public String getInvoiceId() {
        return invoiceId;
    }

    public void setInvoiceId(String invoiceId) {
        this.invoiceId = invoiceId;
    }

    public InvoiceQuery invoiceId(String invoiceId) {
        this.invoiceId = invoiceId;
        return this;
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

    public String getAddress_street() {
        return address_street;
    }

    public void setAddress_street(String address_street) {
        this.address_street = address_street;
    }

    public InvoiceQuery address_street(String address_street) {
        this.setAddress_street(address_street);
        return this;
    }

}