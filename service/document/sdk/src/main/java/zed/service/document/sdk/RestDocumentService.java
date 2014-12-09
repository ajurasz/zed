package zed.service.document.sdk;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;

import static com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES;
import static java.lang.String.format;
import static org.apache.commons.lang3.reflect.FieldUtils.writeField;
import static zed.service.document.sdk.Pojos.pojoClassToCollection;

public class RestDocumentService implements DocumentService {

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

    // Overridden

    @Override
    public <T> T save(T document) {
        try {
            String id = restClient.postForObject(format("%s/save/%s", baseUrl, pojoClassToCollection(document.getClass())), document, String.class);
            writeField(document, "id", id, true);
            return document;
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public <T> T findOne(Class<T> documentClass, String id) {
        return restClient.getForObject(format("%s/findOne/%s/%s", baseUrl, pojoClassToCollection(documentClass), id), documentClass);
    }

    @Override
    public <T> List<T> findMany(Class<T> documentClass, String... ids) {
        Class<T[]> returnType = (Class<T[]>) Array.newInstance(documentClass, 1).getClass();
        T[] results = restClient.postForObject(format("%s/findMany/%s", baseUrl, pojoClassToCollection(documentClass)), ids, returnType);
        return ImmutableList.copyOf(results);
    }

    @Override
    public long count(Class<?> documentClass) {
        return restClient.getForObject(format("%s/count/%s", baseUrl, pojoClassToCollection(documentClass)), Long.class);
    }

    @Override
    public <C, Q> List<C> findByQuery(Class<C> documentClass, QueryBuilder<Q> query) {
        Class<C[]> returnType = (Class<C[]>) Array.newInstance(documentClass, 1).getClass();
        String collection = pojoClassToCollection(documentClass);
        C[] documents = restClient.postForObject(format("%s/findByQuery/%s", baseUrl, collection), query, returnType);
        return ImmutableList.copyOf(documents);
    }

    @Override
    public <C, Q> long countByQuery(Class<C> documentClass, QueryBuilder<Q> query) {
        return restClient.postForObject(format("%s/countByQuery/%s", baseUrl, pojoClassToCollection(documentClass)), query, Long.class);
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
