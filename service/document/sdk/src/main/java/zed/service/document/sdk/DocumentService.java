package zed.service.document.sdk;

import java.util.List;

public interface DocumentService {

    <T> T save(T document);

    <T> T findOne(Class<T> documentClass, String id);

    <T> List<T> findMany(Class<T> documentClass, String... ids);

    long count(Class<?> documentClass);

    <C> List<C> findByQuery(Class<C> documentClass, QueryBuilder queryBuilder);

    <C> long countByQuery(Class<C> documentClass, QueryBuilder queryBuilder);

    void remove(Class<?> documentClass, String id);

}
