package zed.service.geo.sdk;

import zed.rest.client.RestProxy;
import zed.rest.client.SimpleRestProxyFactory;
import zed.service.sdk.base.Discoveries;
import zed.service.sdk.base.HealthCheck;

import java.net.URL;

public class RestGeoService implements GeoService {

    private static final int DEFAULT_GEO_SERVICE_PORT = 15007;

    private final RestProxy<GeoService> geoServiceProxy;

    public RestGeoService(String baseUrl) {
        this.geoServiceProxy = new SimpleRestProxyFactory().proxyService(GeoService.class, baseUrl);
    }

    public static RestGeoService discover() {
        String serviceUrl = Discoveries.discoverServiceUrl("geo", DEFAULT_GEO_SERVICE_PORT, new HealthCheck() {
            @Override
            public void check(String serviceUrl) {
                new RestGeoService(serviceUrl).generateFenceMap(10, 10, 10, 10, 1000);
            }
        });
        return new RestGeoService(serviceUrl);
    }

    @Override
    public URL generateFenceMap(double centerLat, double centerLng, double markerLat, double markerLng, double fenceRadiusInMeters) {
        return geoServiceProxy.get().generateFenceMap(centerLat, centerLng, markerLat, markerLng, fenceRadiusInMeters);
    }

    @Override
    public double metersOutsideFence(double centerLat, double centerLng, double markerLat, double markerLng, double fenceRadiusInMeters) {
        return geoServiceProxy.get().metersOutsideFence(centerLat, centerLng, markerLat, markerLng, fenceRadiusInMeters);
    }

}
