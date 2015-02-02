package zed.service.geo.sdk

interface GeoService {

    URL generateFenceMap(double centerLat, double centerLng, double markerLat, double markerLng, double fenceRadiusInMeters)

    double metersOutsideFence(double centerLat, double centerLng, double markerLat, double markerLng, double fenceRadiusInMeters)

}