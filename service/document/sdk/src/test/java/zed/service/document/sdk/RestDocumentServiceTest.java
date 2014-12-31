package zed.service.document.sdk;

import org.junit.Assert;
import org.junit.Test;
import zed.service.sdk.base.ServiceDiscoveryException;

import static zed.service.document.sdk.RestDocumentService.baseUrlWithContextPath;

public class RestDocumentServiceTest extends Assert {

    @Test
    public void shouldTrimUrl() {
        // Given
        String urlWithSpaces = " http://app.com ";

        // When
        String normalizedUrl = baseUrlWithContextPath(urlWithSpaces);

        // Then
        assertEquals("http://app.com/api/document", normalizedUrl);
    }

    @Test
    public void should() {
        try {
            RestDocumentService.discover();
        } catch (ServiceDiscoveryException e) {
            assertTrue(e.getMessage().contains("Are you sure"));
            assertTrue(e.getMessage().contains("default connection URL for document service"));
            return;
        }
        fail();
    }

}
