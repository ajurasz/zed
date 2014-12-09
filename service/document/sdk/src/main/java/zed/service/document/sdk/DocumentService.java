package zed.service.document.sdk;

import java.util.List;

public interface DocumentService {

    <T> T save(T document);

    <T> T findOne(Class<T> documentClass, String id);

    <T> List<T> findMany(Class<T> documentClass, String... ids);

    long count(Class<?> documentClass);

    <C, Q> List<C> findByQuery(Class<C> documentClass, QueryBuilder<Q> query);

    <C, Q> long countByQuery(Class<C> documentClass, QueryBuilder<Q> query);

    void remove(Class<?> documentClass, String id);

}
