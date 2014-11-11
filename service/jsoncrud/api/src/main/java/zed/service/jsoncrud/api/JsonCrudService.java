package zed.service.jsoncrud.api;

import java.util.List;

public interface JsonCrudService {

    String save(Object pojo);

    <T> T findOne(Class<T> pojoClass, String oid);

    long count(Class<?> pojoClass);

    <C, Q> List<C> findByQuery(QueryBuilder<C, Q> query);

    <C, Q> long countByQuery(QueryBuilder<C, Q> query);

}
