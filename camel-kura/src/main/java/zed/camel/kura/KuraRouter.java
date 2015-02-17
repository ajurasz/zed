package zed.camel.kura;

import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.core.osgi.OsgiDefaultCamelContext;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class KuraRouter extends RouteBuilder implements BundleActivator {

    protected final Logger LOG = LoggerFactory.getLogger(getClass());

    protected BundleContext bundleContext;

    protected CamelContext camelContext;

    @Override
    public void start(BundleContext bundleContext) throws Exception {
        this.bundleContext = bundleContext;
        LOG.debug("Initializing bundle {}.", bundleContext.getBundle().getBundleId());
        camelContext = createCamelContext();
        camelContext.addRoutes(this);
        beforeStart(camelContext);
        camelContext.start();
        LOG.debug("Bundle {} started.", bundleContext.getBundle().getBundleId());
    }

    @Override
    public void stop(BundleContext bundleContext) throws Exception {
        LOG.debug("Stopping bundle {}.", bundleContext.getBundle().getBundleId());
        camelContext.stop();
        LOG.debug("Bundle {} stopped.", bundleContext.getBundle().getBundleId());
    }

    // Callbacks

    protected CamelContext createCamelContext() {
        return new OsgiDefaultCamelContext(bundleContext);
    }

    protected void beforeStart(CamelContext camelContext) {
        LOG.debug("Empty KuraRouter CamelContext before start configuration - skipping.");
    }

}