package zed.service.jsoncrud.api;

public interface JsonCrudService {

    String save(Object pojo);

    String save(String collection, String json);

    <T> T findOne(Class<T> pojoClass, String oid);

    String findOneJson(String collection, String oid);

    long count(Class<?> pojoClass);

    long count(String collection);

}
