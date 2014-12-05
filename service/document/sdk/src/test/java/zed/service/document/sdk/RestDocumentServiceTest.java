package zed.service.document.sdk;

import org.junit.Assert;
import org.junit.Test;

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

}
