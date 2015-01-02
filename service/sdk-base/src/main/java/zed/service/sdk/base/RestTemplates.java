package zed.service.sdk.base;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;
import static com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES;

public final class RestTemplates {

    private RestTemplates() {
    }

    public static RestTemplate defaultRestTemplate() {
        ObjectMapper objectMapper = new ObjectMapper().
                configure(FAIL_ON_UNKNOWN_PROPERTIES, false).setSerializationInclusion(NON_NULL);
        objectMapper.getSerializationConfig().getDefaultVisibilityChecker().
                withFieldVisibility(JsonAutoDetect.Visibility.PUBLIC_ONLY);
        MappingJackson2HttpMessageConverter jacksonConverter = new MappingJackson2HttpMessageConverter(objectMapper);
        return new RestTemplate(Arrays.asList(jacksonConverter));
    }

}
