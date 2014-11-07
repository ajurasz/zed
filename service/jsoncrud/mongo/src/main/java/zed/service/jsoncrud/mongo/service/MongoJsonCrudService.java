package zed.service.jsoncrud.mongo.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import org.apache.camel.ProducerTemplate;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import zed.service.jsoncrud.api.JsonCrudService;

import java.io.IOException;

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
    public String save(String collection, String json) {
        return producerTemplate.requestBodyAndHeader("direct:save", json, COLLECTION, collection, String.class);
    }

    @Override
    public <T> T findOne(Class<T> pojoClass, String oid) {
        DBObject document = producerTemplate.requestBodyAndHeader("direct:findOne", new ObjectId(oid), COLLECTION, pojoClass.getSimpleName(), DBObject.class);
        if (document == null) {
            return null;
        }
        document.put("_id", oid);
        try {
            return new ObjectMapper().readValue(document.toString(), pojoClass);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String findOneJson(String collection, String oid) {
        DBObject document = producerTemplate.requestBodyAndHeader("direct:findOne", new ObjectId(oid), COLLECTION, collection, DBObject.class);
        if (document == null) {
            return null;
        }
        return document.put("_id", oid).toString();
    }

    @Override
    public long count(Class<?> pojoClass) {
        // Fix : you can pass "irrelevantBody" anymore
        return producerTemplate.requestBodyAndHeader("direct:count", new BasicDBObject(), COLLECTION, pojoClass.getSimpleName(), Long.class);
    }

    @Override
    public long count(String collection) {
        // Fix : you can pass "irrelevantBody" anymore
        return producerTemplate.requestBodyAndHeader("direct:count", new BasicDBObject(), COLLECTION, collection, Long.class);
    }


}