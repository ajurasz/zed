package zed.service.attachment.sdk;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;
import zed.service.document.sdk.DocumentService;
import zed.service.document.sdk.QueryBuilder;
import zed.service.document.sdk.RestDocumentService;

import java.util.Arrays;
import java.util.List;

import static com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES;
import static zed.utils.Reflections.writeField;

public class RestAttachmentService<T extends Attachment> implements AttachmentService<T> {

    String baseUrl;

    RestOperations restClient;

    DocumentService<T> documentService;

    public RestAttachmentService(String baseUrl, RestOperations restClient) {
        this.baseUrl = baseUrl;
        this.restClient = restClient;

        this.documentService = new RestDocumentService<T>(baseUrl, restClient);
    }

    public RestAttachmentService(String baseUrl) {
        this(baseUrl, createDefaultRestTemplate());
    }

    @Override
    public T upload(T attachment) {
        String id = restClient.postForObject(baseUrl + "/api/attachment/upload", attachment, String.class);
        writeField(attachment, "id", id);
        return attachment;
    }

    @Override
    public byte[] download(String id) {
        return new byte[0];
    }

    @Override
    public T save(T document) {
        return documentService.save(document);
    }

    @Override
    public T findOne(Class<T> documentClass, String id) {
        return documentService.findOne(documentClass, id);
    }

    @Override
    public List<T> findMany(Class<T> documentClass, String... ids) {
        return documentService.findMany(documentClass, ids);
    }

    @Override
    public long count(Class<?> documentClass) {
        return 0;
    }

    @Override
    public List<T> findByQuery(Class<T> documentClass, QueryBuilder queryBuilder) {
        return null;
    }

    @Override
    public long countByQuery(Class<T> documentClass, QueryBuilder queryBuilder) {
        return 0;
    }

    @Override
    public void remove(Class<?> documentClass, String id) {

    }

    static RestTemplate createDefaultRestTemplate() {
        ObjectMapper objectMapper = new ObjectMapper().
                configure(FAIL_ON_UNKNOWN_PROPERTIES, false).setSerializationInclusion(JsonInclude.Include.NON_NULL);
        MappingJackson2HttpMessageConverter jacksonConverter = new MappingJackson2HttpMessageConverter(objectMapper);
        return new RestTemplate(Arrays.<HttpMessageConverter<?>>asList(jacksonConverter));
    }

}
