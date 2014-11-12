package zed.service.jsoncrud.api.client;

import org.springframework.web.client.RestTemplate;
import zed.service.jsoncrud.api.JsonCrudService;
import zed.service.jsoncrud.api.QueryBuilder;

import java.util.List;

import static zed.service.jsoncrud.api.client.Pojos.pojoClassToCollection;

public class RestJsonCrudServiceClient implements JsonCrudService {

    private final String baseUrl;

    private final RestTemplate restTemplate;

    public RestJsonCrudServiceClient(String baseUrl) {
        this.baseUrl = baseUrl + "/api/jsonCrud";
        restTemplate = new RestTemplate();
    }

    @Override
    public String save(Object pojo) {
        throw new UnsupportedOperationException("Not *yet* implemented.");
    }

    @Override
    public <T> T findOne(Class<T> pojoClass, String oid) {
        throw new UnsupportedOperationException("Not *yet* implemented.");
    }

    @Override
    public long count(Class<?> pojoClass) {
        return restTemplate.getForObject(baseUrl + "/count/" + pojoClassToCollection(pojoClass), Long.class);
    }

    @Override
    public <C, Q> List<C> findByQuery(QueryBuilder<C, Q> query) {
        throw new UnsupportedOperationException("Not *yet* implemented.");
    }

    @Override
    public <C, Q> long countByQuery(QueryBuilder<C, Q> query) {
        throw new UnsupportedOperationException("Not *yet* implemented.");
    }

}
