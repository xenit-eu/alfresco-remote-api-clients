package eu.xenit.alfresco.webscripts.client.spring.http;

import com.fasterxml.jackson.databind.ObjectMapper;
import eu.xenit.alfresco.webscripts.client.spring.model.AlfrescoProperties;
import eu.xenit.alfresco.webscripts.client.spring.model.HttpProperties;
import java.util.Collections;
import java.util.concurrent.TimeUnit;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.client.support.BasicAuthenticationInterceptor;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

public class RestTemplateHelper {

    private RestTemplateHelper() {
        // private ctor to hide implicit public one
    }

    /*
     * Build a RestTemplate, but side-step all features that use classpath-detection. That gives superfluous errors when
     * used in environments with a special classloader (e.g. Fusion connector)
     */
    public static RestTemplate buildRestTemplate(AlfrescoProperties props) {
        RestTemplate client = new RestTemplate(Collections.singletonList(
                new MappingJackson2HttpMessageConverter(new ObjectMapper()))
        );
        client.setRequestFactory(requestFactory(props.getHttp()));
        client.getInterceptors().add(new BasicAuthenticationInterceptor(props.getUser(), props.getPassword()));
        return client;
    }

    private static ClientHttpRequestFactory requestFactory(HttpProperties httpProperties) {

        HttpComponentsClientHttpRequestFactory ret;
        if (httpProperties.isInsecureSsl()) {
            ret = new InsecureSslHttpComponentsClientHttpRequestFactory(builder -> builder
                    .setConnectionTimeToLive(httpProperties.getConnectionTimeToLive(), TimeUnit.MILLISECONDS));
        } else {
            HttpClient client = HttpClientBuilder.create().useSystemProperties()
                    .setConnectionTimeToLive(httpProperties.getConnectionTimeToLive(), TimeUnit.MILLISECONDS)
                    .build();
            ret = new HttpComponentsClientHttpRequestFactory(client);
        }

        ret.setReadTimeout(httpProperties.getTimeout().getSocket());
        ret.setConnectTimeout(httpProperties.getTimeout().getConnect());
        ret.setConnectionRequestTimeout(httpProperties.getTimeout().getConnectionRequest());

        return ret;
    }
}
