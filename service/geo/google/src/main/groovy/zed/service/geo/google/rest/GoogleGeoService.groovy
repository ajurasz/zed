package zed.service.geo.google.rest

import com.google.maps.model.LatLng
import org.springframework.stereotype.Component
import zed.org.apache.camel.rest.annotations.RestOperation
import zed.service.geo.sdk.GeoService

import static zed.service.geo.google.util.GoogleMaps.generateFenceMap
import static zed.service.geo.google.util.GoogleMaps.metersOutsideFence

@Component("geoService")
class GoogleGeoService implements GeoService {

    @RestOperation
    URL generateFenceMap(double centerLat, double centerLng, double markerLat, double markerLng, double fenceRadiusInMeters) {
        generateFenceMap(new LatLng(centerLat, centerLng), new LatLng(markerLat, markerLng), fenceRadiusInMeters)
    }

    @RestOperation
    double metersOutsideFence(double centerLat, double centerLng, double markerLat, double markerLng, double fenceRadiusInMeters) {
        metersOutsideFence(new LatLng(centerLat, centerLng), new LatLng(markerLat, markerLng), fenceRadiusInMeters)
    }

}
