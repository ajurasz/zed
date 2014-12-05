package zed.service.document.sdk;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;

import static com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES;
import static java.lang.String.format;
import static zed.service.document.sdk.Pojos.pojoClassToCollection;

public class RestDocumentService implements DocumentService {

    private final String baseUrl;

    private final RestTemplate restTemplate;

    public RestDocumentService(String baseUrl, RestTemplate restTemplate) {
        this.baseUrl = baseUrlWithContextPath(baseUrl);
        this.restTemplate = restTemplate;
    }

    public RestDocumentService(String baseUrl) {
        this(baseUrl, createDefaultRestTemplate());
    }

    @Override
    public String save(Object document) {
        return restTemplate.postForObject(format("%s/save/%s", baseUrl, pojoClassToCollection(document.getClass())), document, String.class);
    }

    @Override
    public <T> T findOne(Class<T> documentClass, String id) {
        return restTemplate.getForObject(format("%s/findOne/%s/%s", baseUrl, pojoClassToCollection(documentClass), id), documentClass);
    }

    @Override
    public long count(Class<?> documentClass) {
        return restTemplate.getForObject(format("%s/count/%s", baseUrl, pojoClassToCollection(documentClass)), Long.class);
    }

    @Override
    public <C, Q> List<C> findByQuery(Class<C> documentClass, QueryBuilder<Q> query) {
        Class<C[]> returnType = (Class<C[]>) Array.newInstance(documentClass, 1).getClass();
        String collection = pojoClassToCollection(documentClass);
        C[] documents = restTemplate.postForObject(format("%s/findByQuery/%s", baseUrl, collection), query, returnType);
        return ImmutableList.copyOf(documents);
    }

    @Override
    public <C, Q> long countByQuery(Class<C> documentClass, QueryBuilder<Q> query) {
        return restTemplate.postForObject(format("%s/countByQuery/%s", baseUrl, pojoClassToCollection(documentClass)), query, Long.class);
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
