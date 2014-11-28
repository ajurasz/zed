package zed.service.jsoncrud.sdk;

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
import static zed.service.jsoncrud.sdk.Pojos.pojoClassToCollection;

public class RestJsonCrudServiceClient implements JsonCrudService {

    private final String baseUrl;

    private final RestTemplate restTemplate;

    public RestJsonCrudServiceClient(String baseUrl, RestTemplate restTemplate) {
        this.baseUrl = baseUrlWithContextPath(baseUrl);
        this.restTemplate = restTemplate;
    }

    public RestJsonCrudServiceClient(String baseUrl) {
        this(baseUrl, createDefaultRestTemplate());
    }

    @Override
    public String save(Object pojo) {
        return restTemplate.postForObject(format("%s/save/%s", baseUrl, pojoClassToCollection(pojo.getClass())), pojo, String.class);
    }

    @Override
    public <T> T findOne(Class<T> pojoClass, String oid) {
        return restTemplate.getForObject(format("%s/findOne/%s/%s", baseUrl, pojoClassToCollection(pojoClass), oid), pojoClass);
    }

    @Override
    public long count(Class<?> pojoClass) {
        return restTemplate.getForObject(format("%s/count/%s", baseUrl, pojoClassToCollection(pojoClass)), Long.class);
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

    private String baseUrlWithContextPath(String baseUrl) {
        return baseUrl + "/api/jsonCrud";
    }

    private static RestTemplate createDefaultRestTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        MappingJackson2HttpMessageConverter jacksonConverter = new MappingJackson2HttpMessageConverter();
        jacksonConverter.setObjectMapper(
                new ObjectMapper().configure(FAIL_ON_UNKNOWN_PROPERTIES, false).setSerializationInclusion(JsonInclude.Include.NON_NULL)
        );
        restTemplate.setMessageConverters(Arrays.<HttpMessageConverter<?>>asList(jacksonConverter));
        return restTemplate;
    }

}
