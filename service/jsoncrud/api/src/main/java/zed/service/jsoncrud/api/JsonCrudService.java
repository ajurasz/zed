package zed.service.jsoncrud.api;

public interface JsonCrudService {

    String save(Object pojo);

    String save(String collection, String json);

}
