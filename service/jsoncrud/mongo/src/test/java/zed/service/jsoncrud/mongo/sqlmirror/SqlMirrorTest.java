package zed.service.jsoncrud.mongo.sqlmirror;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.math.BigDecimal;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {SqlMirrorTest.class})
@ActiveProfiles("test")
@EnableAutoConfiguration
@IntegrationTest
@ComponentScan
public class SqlMirrorTest extends Assert {

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Autowired
    DynamicSchemaExpander schemaExpander;

    @Autowired
    CrossStoreStatementsGenerator crossStoreStatementsGenerator;

    @Test
    public void shouldCreateTable() {
        jdbcTemplate.execute("DROP TABLE Invoice");

        schemaExpander.expandPojoSchema(Invoice.class);

        long invoices = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM Invoice", Long.class);
        assertEquals(0, invoices);
    }

    @Test
    public void shouldNotCreateTableTwice() {
        schemaExpander.expandPojoSchema(Invoice.class);
        schemaExpander.expandPojoSchema(Invoice.class);
    }

    @Test
    public void should() {
        jdbcTemplate.execute("DROP TABLE Invoice IF EXISTS");

        crossStoreStatementsGenerator.insert(new Invoice("id", new InvoiceCorrection(BigDecimal.TEN)));

        long invoices = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM Invoice", Long.class);
        assertEquals(1, invoices);

    }

}

class Invoice {

    private final String invoiceId;

    private final InvoiceCorrection correction;

    Invoice(String invoiceId, InvoiceCorrection correction) {
        this.invoiceId = invoiceId;
        this.correction = correction;
    }

}

class InvoiceCorrection {

    private final BigDecimal correctionValue;

    InvoiceCorrection(BigDecimal correctionValue) {
        this.correctionValue = correctionValue;
    }
}