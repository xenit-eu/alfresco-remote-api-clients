package eu.xenit.alfresco.webscripts.client.spring;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Collections;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.impl.client.HttpClients;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.client.support.BasicAuthenticationInterceptor;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

public class RestTemplateHelper {

    private RestTemplateHelper() {
        // private ctor to hide implicit public one
    }

    /**
     * Build a RestTemplate, but side-step all features that use classpath-detection. That gives superfluous errors when
     * used in environments with a special classloader (e.g. Fusion connector)
     */
    public static RestTemplate buildRestTemplate(AlfrescoProperties props) {
        RestTemplate client = new RestTemplate(Collections.singletonList(
                new MappingJackson2HttpMessageConverter(new ObjectMapper()))
        );
        client.setRequestFactory(requestFactory(props.isInsecure()));
        client.getInterceptors().add(new BasicAuthenticationInterceptor(props.getUser(), props.getPassword()));
        return client;
    }

    private static ClientHttpRequestFactory requestFactory(boolean insecure) {
        HttpComponentsClientHttpRequestFactory ret = new HttpComponentsClientHttpRequestFactory();

        if (insecure) {
            ret.setHttpClient(HttpClients.custom()
                    .setSSLHostnameVerifier(new NoopHostnameVerifier())
                    .build());
        }

        return ret;
    }


}
