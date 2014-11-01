package zed;

public interface JsonCrudService {

    void save(Object pojo);

    void save(String collection, String json);

}
