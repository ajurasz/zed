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

import static java.lang.Boolean.TRUE;
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

    Invoice invoice = new Invoice().invoiceId("invoice001");

    @BeforeClass
    public static void beforeClass() {
        System.setProperty("server.port", findAvailableTcpPort() + "");
        System.setProperty("zed.service.api.port", findAvailableTcpPort() + "");

        System.setProperty("zed.service.document.mongodb.springbootconfig", TRUE.toString());
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
        Invoice loadedInvoice = documentService.findOne(Invoice.class, invoice.id);
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
        Invoice loadedInvoice = documentService.findOne(Invoice.class, invoice.id);

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
        assertNotNull(invoice.id);
    }

    @Test
    public void shouldFindOne() {
        // Given
        invoice = documentService.save(invoice);

        // When
        Invoice invoiceFound = documentService.findOne(Invoice.class, invoice.id);

        // Then
        assertEquals(invoice.id, invoiceFound.id);
    }

    @Test
    public void shouldFindMany() {
        // Given
        Invoice firstInvoice = documentService.save(new Invoice().invoiceId("invoice001"));
        Invoice secondInvoice = documentService.save(new Invoice().invoiceId("invoice002"));

        // When
        List<Invoice> invoices = documentService.findMany(Invoice.class, firstInvoice.id, secondInvoice.id);

        // Then
        assertEquals(2, invoices.size());
        assertEquals(firstInvoice.id, invoices.get(0).id);
        assertEquals(secondInvoice.id, invoices.get(1).id);
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
        documentService.save(new Invoice().invoiceId("invoice001"));

        // When
        long invoices = documentService.count(Invoice.class);

        // Then
        assertEquals(1, invoices);
    }

    @Test
    public void shouldFindByQuery() {
        // Given
        documentService.save(invoice);
        InvoiceQuery query = new InvoiceQuery().invoiceId(invoice.invoiceId);

        // When
        List<Invoice> invoices = documentService.findByQuery(Invoice.class, new QueryBuilder(query));

        // Then
        assertEquals(1, invoices.size());
        assertEquals(invoice.invoiceId, invoices.get(0).invoiceId);
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
        documentService.save(new Invoice().invoiceId("invoice001"));
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
        invoice.address = new Address().street(street);
        invoice = documentService.save(invoice);

        InvoiceQuery query = new InvoiceQuery().invoiceId(invoice.invoiceId).address_street(street);

        // When
        List<Invoice> invoices = documentService.findByQuery(Invoice.class, new QueryBuilder(query));

        // Then
        assertEquals(1, invoices.size());
        assertEquals(street, invoices.get(0).address.street);
    }

    @Test
    public void shouldNotFindByNestedQuery() {
        // Given
        String street = "someStreet";
        invoice.address = new Address().street(street);
        invoice = documentService.save(invoice);

        InvoiceQuery query = new InvoiceQuery().invoiceId(invoice.invoiceId).address_street("someRandomStreet");

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
        assertEquals(firstInvoice.id, firstPage.get(0).id);
        assertEquals(secondInvoice.id, firstPage.get(1).id);
        assertEquals(thirdInvoice.id, secondPage.get(0).id);
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
        assertEquals(thirdInvoice.id, firstPage.get(0).id);
        assertEquals(secondInvoice.id, firstPage.get(1).id);
        assertEquals(firstInvoice.id, secondPage.get(0).id);
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
        assertEquals(invoice.invoiceId, invoices.get(0).invoiceId);
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
        InvoiceQuery query = new InvoiceQuery().invoiceIdIn(invoice.invoiceId, "foo", "bar");

        // When
        List<Invoice> invoices = documentService.findByQuery(Invoice.class, new QueryBuilder(query));

        // Then
        assertEquals(1, invoices.size());
        assertEquals(invoice.invoiceId, invoices.get(0).invoiceId);
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
        assertEquals(invoice.invoiceId, invoices.get(0).invoiceId);
    }

    @Test
    public void shouldNotFindByQueryWithNotIn() {
        // Given
        documentService.save(invoice);
        InvoiceQuery query = new InvoiceQuery().invoiceIdNotIn(invoice.invoiceId, "foo", "bar");

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
        invoice.timestamp = now().minusDays(2).toDate();
        documentService.save(invoice);
        invoice = new Invoice();
        invoice.timestamp = now().plusDays(2).toDate();
        documentService.save(invoice);

        InvoiceQuery query = new InvoiceQuery();
        query.setTimestampGreaterThanEqual(now().minusDays(1).toDate());
        query.setTimestampLessThan(now().plusDays(1).toDate());

        // When
        List<Invoice> invoices = documentService.findByQuery(Invoice.class, new QueryBuilder(query));

        // Then
        assertEquals(1, invoices.size());
        assertEquals(todayInvoice.id, invoices.get(0).id);
    }

    @Test
    public void shouldCountPositiveByQuery() {
        // Given
        documentService.save(invoice);
        InvoiceQuery query = new InvoiceQuery().invoiceId(invoice.invoiceId);

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
        documentService.remove(Invoice.class, invoice.id);

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

    public String id;

    public Date timestamp = new Date();

    public String invoiceId;

    public Address address;

    Invoice invoiceId(String invoiceId) {
        this.invoiceId = invoiceId;
        return this;
    }

    static class Address {

        public String street;

        Address street(String street) {
            this.street = street;
            return this;
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