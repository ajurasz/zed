package zed.service.sdk.base;

import org.slf4j.Logger;

import static org.slf4j.LoggerFactory.getLogger;

public final class Discoveries {

    private static final Logger LOG = getLogger(Discoveries.class);

    private Discoveries() {
    }

    public static String discoverServiceUrl(String service, int defaultPort, HealthCheck healthCheck) {
        LOG.debug("Starting {} service discovery process.", service);

        String serviceUrl = "http://localhost:" + defaultPort;
        try {
            healthCheck.check(serviceUrl);
        } catch (Exception e) {
            String message = String.format("Can't connect to the %s service %s . " +
                            "Are you sure there is a %s service instance running there? " +
                            "%s has been chosen as a default connection URL for %s service.",
                    service, serviceUrl, service, serviceUrl, service);
            LOG.debug(message);
            throw new ServiceDiscoveryException(message, e);
        }
        return serviceUrl;
    }

}
