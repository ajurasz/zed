package zed.service.document.sdk;

import com.google.common.collect.ImmutableList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.RestOperations;
import zed.service.sdk.base.HealthCheck;

import java.util.List;

import static java.lang.String.format;
import static zed.service.document.sdk.Pojos.pojoClassToCollection;
import static zed.service.sdk.base.Discoveries.discoverServiceUrl;
import static zed.service.sdk.base.RestTemplates.defaultRestTemplate;
import static zed.utils.Reflections.classOfArrayOfClass;
import static zed.utils.Reflections.writeField;

public class RestDocumentService<T> implements DocumentService<T> {

    private static final Logger LOG = LoggerFactory.getLogger(RestDocumentService.class);

    private static final int DEFAULT_DOCUMENT_SERVICE_PORT = 15001;

    // Configuration members

    private final String baseUrl;

    // Collaborators members

    private final RestOperations restClient;

    // Constructors

    public RestDocumentService(String baseUrl, RestOperations restClient) {
        this.baseUrl = baseUrlWithContextPath(baseUrl);
        this.restClient = restClient;
    }

    public RestDocumentService(String baseUrl) {
        this(baseUrl, defaultRestTemplate());
    }

    public RestDocumentService(int restApiPort) {
        this("http://localhost:" + restApiPort);
    }

    public static RestDocumentService discover() {
        String serviceUrl = discoverServiceUrl("document", DEFAULT_DOCUMENT_SERVICE_PORT, new HealthCheck() {
            @Override
            public void check(String serviceUrl) {
                new RestDocumentService<>(serviceUrl).count(RestDocumentServiceConnectivityTest.class);
            }
        });
        return new RestDocumentService<>(serviceUrl);
    }

    // Overridden

    @Override
    public T save(T document) {
        String id = restClient.postForObject(format("%s/save/%s", baseUrl, pojoClassToCollection(document.getClass())), document, String.class);
        writeField(document, "id", id);
        return document;
    }

    @Override
    public T findOne(Class<T> documentClass, String id) {
        return restClient.getForObject(format("%s/findOne/%s/%s", baseUrl, pojoClassToCollection(documentClass), id), documentClass);
    }

    @Override
    public List<T> findMany(Class<T> documentClass, String... ids) {
        T[] results = restClient.postForObject(format("%s/findMany/%s", baseUrl, pojoClassToCollection(documentClass)), ids, classOfArrayOfClass(documentClass));
        return ImmutableList.copyOf(results);
    }

    @Override
    public long count(Class<?> documentClass) {
        return restClient.getForObject(format("%s/count/%s", baseUrl, pojoClassToCollection(documentClass)), Long.class);
    }

    @Override
    public List<T> findByQuery(Class<T> documentClass, QueryBuilder queryBuilder) {
        String collection = pojoClassToCollection(documentClass);
        T[] documents = restClient.postForObject(format("%s/findByQuery/%s", baseUrl, collection), queryBuilder, classOfArrayOfClass(documentClass));
        return ImmutableList.copyOf(documents);
    }

    @Override
    public long countByQuery(Class<T> documentClass, QueryBuilder queryBuilder) {
        return restClient.postForObject(format("%s/countByQuery/%s", baseUrl, pojoClassToCollection(documentClass)), queryBuilder, Long.class);
    }

    @Override
    public void remove(Class<?> documentClass, String id) {
        restClient.delete(format("%s/remove/%s/%s", baseUrl, pojoClassToCollection(documentClass), id));
    }

    // Helpers

    static String baseUrlWithContextPath(String baseUrl) {
        baseUrl = baseUrl.trim();
        return baseUrl + "/api/document";
    }

    private static final class RestDocumentServiceConnectivityTest {
    }

}
