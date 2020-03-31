package eu.xenit.alfresco.webscripts.client.spring.http;

import javax.net.ssl.SSLContext;
import lombok.SneakyThrows;
import org.apache.http.client.HttpClient;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;

public class InsecureSslHttpComponentsClientHttpRequestFactory extends HttpComponentsClientHttpRequestFactory {

    @SneakyThrows
    public InsecureSslHttpComponentsClientHttpRequestFactory(HttpClient httpClient) {
        super(httpClient);

        SSLContext sslContext = SSLContextBuilder
                .create()
                .loadTrustMaterial(new TrustSelfSignedStrategy())
                .build();

        this.setHttpClient(HttpClients.custom()
                .setSSLContext(sslContext)
                .setSSLHostnameVerifier(new NoopHostnameVerifier())
                .build());
    }

    public InsecureSslHttpComponentsClientHttpRequestFactory() {
        this(HttpClients.createSystem());
    }
}
