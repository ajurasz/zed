package zed.service.document.sdk;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

import static com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES;
import static java.lang.String.format;
import static zed.service.document.sdk.Pojos.pojoClassToCollection;
import static zed.service.document.sdk.Reflections.classOfArrayOfClass;
import static zed.service.document.sdk.Reflections.writeField;

public class RestDocumentService implements DocumentService {

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
        this(baseUrl, createDefaultRestTemplate());
    }

    public RestDocumentService() {
        this("http://localhost:" + DEFAULT_DOCUMENT_SERVICE_PORT);
    }

    // Overridden

    @Override
    public <T> T save(T document) {
        String id = restClient.postForObject(format("%s/save/%s", baseUrl, pojoClassToCollection(document.getClass())), document, String.class);
        writeField(document, "id", id);
        return document;
    }

    @Override
    public <T> T findOne(Class<T> documentClass, String id) {
        return restClient.getForObject(format("%s/findOne/%s/%s", baseUrl, pojoClassToCollection(documentClass), id), documentClass);
    }

    @Override
    public <T> List<T> findMany(Class<T> documentClass, String... ids) {
        T[] results = restClient.postForObject(format("%s/findMany/%s", baseUrl, pojoClassToCollection(documentClass)), ids, classOfArrayOfClass(documentClass));
        return ImmutableList.copyOf(results);
    }

    @Override
    public long count(Class<?> documentClass) {
        return restClient.getForObject(format("%s/count/%s", baseUrl, pojoClassToCollection(documentClass)), Long.class);
    }

    @Override
    public <T> List<T> findByQuery(Class<T> documentClass, QueryBuilder queryBuilder) {
        String collection = pojoClassToCollection(documentClass);
        T[] documents = restClient.postForObject(format("%s/findByQuery/%s", baseUrl, collection), queryBuilder, classOfArrayOfClass(documentClass));
        return ImmutableList.copyOf(documents);
    }

    @Override
    public <C> long countByQuery(Class<C> documentClass, QueryBuilder queryBuilder) {
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

    static RestTemplate createDefaultRestTemplate() {
        ObjectMapper objectMapper = new ObjectMapper().
                configure(FAIL_ON_UNKNOWN_PROPERTIES, false).setSerializationInclusion(JsonInclude.Include.NON_NULL);
        MappingJackson2HttpMessageConverter jacksonConverter = new MappingJackson2HttpMessageConverter(objectMapper);
        return new RestTemplate(Arrays.<HttpMessageConverter<?>>asList(jacksonConverter));
    }

}
