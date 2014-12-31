package zed.service.attachment.sdk;

import zed.service.document.sdk.DocumentService;
import zed.service.document.sdk.QueryBuilder;

import java.util.List;

public interface AttachmentService<T extends Attachment> extends DocumentService<T> {

    T upload(T attachment);

    byte[] download(String id);

    //

    @Override
    T save(T document);

    @Override
    T findOne(Class<T> documentClass, String id);

    @Override
    List<T> findMany(Class<T> documentClass, String... ids);

    @Override
    long count(Class<?> documentClass);

    @Override
    List<T> findByQuery(Class<T> documentClass, QueryBuilder queryBuilder);

    @Override
    long countByQuery(Class<T> documentClass, QueryBuilder queryBuilder);

    @Override
    void remove(Class<T> documentClass, String id);

}
