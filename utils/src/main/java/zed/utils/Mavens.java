package zed.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;
import java.util.Properties;

import static java.lang.String.format;

public final class Mavens {

    private static final Properties VERSIONS = new Properties();

    private static final Logger LOG = LoggerFactory.getLogger(Mavens.class);

    private static final String DEPENDENCIES_PROPERTIES_PATH = "META-INF/maven/dependencies.properties";

    static {
        try {
            Enumeration<URL> dependenciesPropertiesStreams = Mavens.class.getClassLoader().getResources(DEPENDENCIES_PROPERTIES_PATH);
            while (dependenciesPropertiesStreams.hasMoreElements()) {
                InputStream propertiesStream = dependenciesPropertiesStreams.nextElement().openStream();
                LOG.debug("Loading properties: " + propertiesStream);
                VERSIONS.load(propertiesStream);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Mavens() {
    }

    public static String artifactVersion(String groupId, String artifactId) {
        return VERSIONS.getProperty(format("%s/%s/version", groupId, artifactId));
    }

}