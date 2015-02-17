package zed.camel.kura;

import org.apache.camel.CamelContext;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.core.osgi.OsgiDefaultCamelContext;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class KuraRouter extends RouteBuilder implements BundleActivator {

    protected final Logger log = LoggerFactory.getLogger(getClass());

    protected BundleContext bundleContext;

    protected CamelContext camelContext;

    protected ProducerTemplate producerTemplate;

    @Override
    public void start(BundleContext bundleContext) throws Exception {
        this.bundleContext = bundleContext;
        log.debug("Initializing bundle {}.", bundleContext.getBundle().getBundleId());
        camelContext = createCamelContext();
        camelContext.addRoutes(this);
        beforeStart(camelContext);
        camelContext.start();
        producerTemplate = camelContext.createProducerTemplate();
        log.debug("Bundle {} started.", bundleContext.getBundle().getBundleId());
    }

    @Override
    public void stop(BundleContext bundleContext) throws Exception {
        log.debug("Stopping bundle {}.", bundleContext.getBundle().getBundleId());
        camelContext.stop();
        log.debug("Bundle {} stopped.", bundleContext.getBundle().getBundleId());
    }

    // Callbacks

    protected CamelContext createCamelContext() {
        return new OsgiDefaultCamelContext(bundleContext);
    }

    protected void beforeStart(CamelContext camelContext) {
        log.debug("Empty KuraRouter CamelContext before start configuration - skipping.");
    }

}