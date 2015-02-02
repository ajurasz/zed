package zed.service.geo.google.util

import com.google.maps.internal.PolylineEncoding
import com.google.maps.model.LatLng

import static java.lang.Math.*

class GoogleMaps {

    public static final double EARTH_RADIUS_IN_KILOMETERS = 6371;

    public static String encodeCircle(LatLng center, double radiusInMeters) {
        double radiusInKilometers = radiusInMeters / 1000;

        double lat = (center.lat * PI) / 180;
        double lng = (center.lng * PI) / 180;

        int $Detail = 8;

        double $d = radiusInKilometers / EARTH_RADIUS_IN_KILOMETERS;

        List<LatLng> $points = new LinkedList<>();
        for (int $i = 0; $i <= 360; $i += $Detail) {
            double $brng = $i * PI / 180;

            double $pLat = asin(sin(lat) * cos($d) + cos(lat) * sin($d) * cos($brng));
            double $pLng = ((lng + atan2(sin($brng) * sin($d) * cos(lat), cos($d) - sin(lat) * sin($pLat))) * 180) / PI;
            $pLat = ($pLat * 180) / PI;

            $points.add(new LatLng($pLat, $pLng));
        }

        return PolylineEncoding.encode($points);
    }

    public static double gpsCoordinateToRadius(double value) {
        return (value * PI) / 180;
    }

    public static double meterBetweenPoints(LatLng a, LatLng b) {
        double dLat = gpsCoordinateToRadius(b.lat - a.lat);
        double dLon = gpsCoordinateToRadius(b.lng - a.lng);
        double lat1 = gpsCoordinateToRadius(a.lat);
        double lat2 = gpsCoordinateToRadius(b.lat);

        double aa = sin(dLat / 2) * sin(dLat / 2) +
                sin(dLon / 2) * sin(dLon / 2) * Math.cos(lat1) * Math.cos(lat2);
        double c = 2 * Math.atan2(Math.sqrt(aa), Math.sqrt(1 - aa));
        return EARTH_RADIUS_IN_KILOMETERS * c * 1000;
    }

    public static String encodeMarker(LatLng coordinates, char marketText, String color) {
        return "color:" + color + "%7Clabel:" + marketText + "%7C" + coordinates.lat + "," + coordinates.lng;
    }

    public static URL generateFenceMap(LatLng center, LatLng marker, double fenceRadiusInMeters) {
        String circle = encodeCircle(center, fenceRadiusInMeters);
        String encodedMarker = encodeMarker(marker, 'D' as char, "blue");

        try {
            return new URL("http://maps.google.com/maps/api/staticmap?center=" + center.lat + "," + center.lng + "&size=640x480&maptype=roadmap&path=fillcolor:0xE85F0E33%7Ccolor:0x'91A93A'00%7Cenc:" + circle + "&sensor=false&markers=" + encodedMarker);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    static double metersOutsideFence(LatLng center, LatLng marker, double fenceRadiusInMeters) {
        meterBetweenPoints(center, marker) - fenceRadiusInMeters
    }

}
