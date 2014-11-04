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
    public void save(Object pojo) {
        producerTemplate.sendBodyAndHeader("direct:savePojo", pojo, COLLECTION, pojo.getClass().getSimpleName());
    }

    @Override
    public void save(String collection, String json) {

    }

}