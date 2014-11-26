package zed.service.jsoncrud.mongo.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.DBObject;
import org.apache.camel.ProducerTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import zed.service.jsoncrud.api.JsonCrudService;
import zed.service.jsoncrud.api.QueryBuilder;

import java.io.IOException;
import java.util.List;

import static org.apache.camel.component.mongodb.MongoDbConstants.COLLECTION;
import static zed.service.jsoncrud.api.client.Pojos.pojoClassToCollection;

@Component
public class MongoJsonCrudService implements JsonCrudService {

    @Autowired
    private ProducerTemplate producerTemplate;

    @Override
    public String save(Object pojo) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T> T findOne(Class<T> pojoClass, String oid) {
        throw new UnsupportedOperationException();
    }

    @Override
    public long count(Class<?> pojoClass) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <C, Q> List<C> findByQuery(Class<C> documentClass, QueryBuilder<Q> query) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <C, Q> long countByQuery(Class<C> documentClass, QueryBuilder<Q> query) {
        // Fix : you can pass "irrelevantBody" anymore
        return producerTemplate.requestBodyAndHeader("direct:countByQuery", query.query(), COLLECTION, pojoClassToCollection(documentClass), Long.class);
    }

    // private

    private <T> T documentToPojo(DBObject document, Class<T> pojoClass) {
        try {
            document.put("_id", document.get("_id").toString());
            return new ObjectMapper().readValue(document.toString(), pojoClass);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}