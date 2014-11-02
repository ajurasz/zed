package zed.service.jsoncrud.mongo;

import org.apache.camel.CamelContext;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.apache.camel.ServiceStatus.Started;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {MongoJsonCrudServiceConfiguration.class, MongoJsonCrudServiceTest.class})
@IntegrationTest
public class MongoJsonCrudServiceTest extends Assert {

    @Autowired
    CamelContext camelContext;

    @Test
    public void shouldStartCamelContext() {
        assertEquals(Started, camelContext.getStatus());
    }

    @Test
    public void shouldLoadRoutes() {
        assertFalse(camelContext.getRoutes().isEmpty());
    }

}
