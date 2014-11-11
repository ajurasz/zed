package zed.service.jsoncrud.mongo.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import org.apache.camel.ProducerTemplate;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import zed.service.jsoncrud.api.JsonCrudService;
import zed.service.jsoncrud.api.QueryBuilder;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import static org.apache.camel.component.mongodb.MongoDbConstants.COLLECTION;

@Component
public class MongoJsonCrudService implements JsonCrudService {

    @Autowired
    private ProducerTemplate producerTemplate;

    @Override
    public String save(Object pojo) {
        return producerTemplate.requestBodyAndHeader("direct:save", pojo, COLLECTION, pojo.getClass().getSimpleName(), String.class);
    }

    @Override
    public <T> T findOne(Class<T> pojoClass, String oid) {
        DBObject document = producerTemplate.requestBodyAndHeader("direct:findOne", new ObjectId(oid), COLLECTION, pojoClass.getSimpleName(), DBObject.class);
        if (document == null) {
            return null;
        }
        return documentToPojo(document, pojoClass);
    }

    @Override
    public long count(Class<?> pojoClass) {
        // Fix : you can pass "irrelevantBody" anymore
        return producerTemplate.requestBodyAndHeader("direct:count", new BasicDBObject(), COLLECTION, pojoClass.getSimpleName(), Long.class);
    }

    @Override
    public <C, Q> List<C> findByQuery(final QueryBuilder<C, Q> query) {
        List<DBObject> dbObjects = producerTemplate.requestBodyAndHeader("direct:findByQuery", query.query(), COLLECTION, query.classifier().getSimpleName(), List.class);
        return dbObjects.parallelStream().map(document -> documentToPojo(document, query.classifier())).collect(Collectors.toList());
    }

    @Override
    public <C, Q> long countByQuery(QueryBuilder<C, Q> query) {
        return producerTemplate.requestBodyAndHeader("direct:count", query.query(), COLLECTION, query.classifier().getSimpleName(), Long.class);
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