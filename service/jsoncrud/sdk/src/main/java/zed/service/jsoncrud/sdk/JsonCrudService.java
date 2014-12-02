package zed.service.jsoncrud.sdk;

import java.util.List;

public interface JsonCrudService {

    String save(Object pojo);

    <T> T findOne(Class<T> pojoClass, String oid);

    long count(Class<?> pojoClass);

    <C, Q> List<C> findByQuery(Class<C> documentClass, QueryBuilder<Q> query);

    <C, Q> long countByQuery(Class<C> documentClass, QueryBuilder<Q> query);

}