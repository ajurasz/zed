package zed.service.jsoncrud.mongo.service;

import org.apache.camel.ProducerTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import zed.service.jsoncrud.api.JsonCrudService;

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

}