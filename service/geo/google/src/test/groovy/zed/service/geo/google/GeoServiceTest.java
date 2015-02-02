package zed.service.geo.google;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import zed.service.geo.sdk.GeoService;
import zed.service.geo.sdk.RestGeoService;

import static org.springframework.util.SocketUtils.findAvailableTcpPort;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {GoogleGeoServiceConfiguration.class})
@IntegrationTest
public class GeoServiceTest extends Assert {

    static int apiPort = findAvailableTcpPort();

    @BeforeClass
    public static void beforeClass() {
        System.setProperty("server.port", findAvailableTcpPort() + "");
        System.setProperty("zed.service.api.port", apiPort + "");
    }

    GeoService geoService = new RestGeoService("http://localhost:" + apiPort);

    @Test
    public void shouldGenerateGoogleStaticMapsApiUrl() {
        assertTrue(geoService.generateFenceMap(10, 10, 10, 10, 1000).toString().startsWith("http://maps.google.com/maps/api/staticmap"));
    }

    @Test
    public void shouldBeMaximumDistanceWithinTheFence() {
        // Given
        double fenceRadius = 1000;

        // When
        double distance = geoService.metersOutsideFence(10, 10, 10, 10, fenceRadius);

        // Then
        assertEquals(fenceRadius * -1, distance, 0);
    }

    @Test
    public void shouldBeOutsideTheFence() {
        // Given
        double fenceRadius = 1000;

        // When
        double distance = geoService.metersOutsideFence(10, 10, 10.1, 10.1, fenceRadius);

        // Then
        assertTrue(distance > 0);
    }

}
