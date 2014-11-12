package zed.service.jsoncrud.api.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;
import zed.service.jsoncrud.api.JsonCrudService;
import zed.service.jsoncrud.api.QueryBuilder;

import java.util.Arrays;
import java.util.List;

import static com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES;
import static java.lang.String.format;
import static zed.service.jsoncrud.api.client.Pojos.pojoClassToCollection;

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
        throw new UnsupportedOperationException("Not *yet* implemented.");
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
    public <C, Q> List<C> findByQuery(QueryBuilder<C, Q> query) {
        throw new UnsupportedOperationException("Not *yet* implemented.");
    }

    @Override
    public <C, Q> long countByQuery(QueryBuilder<C, Q> query) {
        throw new UnsupportedOperationException("Not *yet* implemented.");
    }

    // Helpers

    private String baseUrlWithContextPath(String baseUrl) {
        return baseUrl + "/api/jsonCrud";
    }

    private static RestTemplate createDefaultRestTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        MappingJackson2HttpMessageConverter jacksonConverter = new MappingJackson2HttpMessageConverter();
        jacksonConverter.setObjectMapper(new ObjectMapper().configure(FAIL_ON_UNKNOWN_PROPERTIES, false));
        restTemplate.setMessageConverters(Arrays.<HttpMessageConverter<?>>asList(jacksonConverter));
        return restTemplate;
    }

}
