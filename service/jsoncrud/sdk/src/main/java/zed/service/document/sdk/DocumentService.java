package zed.service.document.sdk;

import java.util.List;

public interface DocumentService {

    String save(Object document);

    <T> T findOne(Class<T> documentClass, String id);

    long count(Class<?> documentClass);

    <C, Q> List<C> findByQuery(Class<C> documentClass, QueryBuilder<Q> query);

    <C, Q> long countByQuery(Class<C> documentClass, QueryBuilder<Q> query);

}
