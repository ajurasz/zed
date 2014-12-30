package zed.service.attachment.sdk;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestOperations;
import zed.service.document.sdk.DocumentService;
import zed.service.document.sdk.QueryBuilder;
import zed.service.document.sdk.RestDocumentService;

import java.io.IOException;
import java.net.URL;
import java.util.List;

import static java.lang.String.format;
import static zed.service.sdk.base.RestTemplates.defaultRestTemplate;
import static zed.utils.Reflections.writeField;

public class RestAttachmentService<T extends Attachment> implements AttachmentService<T> {

    private static final Logger LOG = LoggerFactory.getLogger(RestAttachmentService.class);

    private static final int DEFAULT_DOCUMENT_SERVICE_PORT = 15003;

    private final String baseUrl;

    private final RestOperations restClient;

    private final DocumentService<T> documentService;

    public RestAttachmentService(String baseUrl, RestOperations restClient) {
        this.baseUrl = baseUrl;
        this.restClient = restClient;

        this.documentService = new RestDocumentService<T>(baseUrl, restClient);
    }

    public RestAttachmentService(String baseUrl) {
        this(baseUrl, defaultRestTemplate());
    }

    public static <V extends Attachment> RestAttachmentService<V> discover() {
        LOG.debug("Starting attachment service discovery process.");
        String serviceUrl = "http://localhost:" + DEFAULT_DOCUMENT_SERVICE_PORT;
        RestAttachmentService<V> service = new RestAttachmentService<>(serviceUrl);
        try {
            service.count(Attachment.class);
        } catch (ResourceAccessException e) {
            String message = format("Can't connect to the document service %s . " +
                    "Are you sure there is a DocumentService instance running there? " +
                    "%s has been chosen as a default connection URL for DocumentService.", serviceUrl, serviceUrl);
            LOG.debug(message);
            throw new DocumentServiceDiscoveryException(message, e);
        }
        return service;
    }

    @Override
    public T upload(T attachment) {
        String id = restClient.postForObject(baseUrl + "/api/attachment/upload", attachment, String.class);
        writeField(attachment, "id", id);
        return attachment;
    }

    @Override
    public byte[] download(String id) {
        try {
            return IOUtils.toByteArray(new URL(baseUrl + "/api/attachment/download/" + id).openStream());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public T save(T document) {
        return documentService.save(document);
    }

    @Override
    public T findOne(Class<T> documentClass, String id) {
        return documentService.findOne(documentClass, id);
    }

    @Override
    public List<T> findMany(Class<T> documentClass, String... ids) {
        return documentService.findMany(documentClass, ids);
    }

    @Override
    public long count(Class<?> documentClass) {
        return 0;
    }

    @Override
    public List<T> findByQuery(Class<T> documentClass, QueryBuilder queryBuilder) {
        return null;
    }

    @Override
    public long countByQuery(Class<T> documentClass, QueryBuilder queryBuilder) {
        return 0;
    }

    @Override
    public void remove(Class<?> documentClass, String id) {

    }

}