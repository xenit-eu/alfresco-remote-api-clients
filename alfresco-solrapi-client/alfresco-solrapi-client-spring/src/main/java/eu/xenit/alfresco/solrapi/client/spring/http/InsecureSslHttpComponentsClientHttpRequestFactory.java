package eu.xenit.alfresco.solrapi.client.spring.http;

import javax.net.ssl.SSLContext;
import lombok.SneakyThrows;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;

public class InsecureSslHttpComponentsClientHttpRequestFactory extends HttpComponentsClientHttpRequestFactory {

    @SneakyThrows
    public InsecureSslHttpComponentsClientHttpRequestFactory() {
        SSLContext sslContext = SSLContextBuilder
                .create()
                .loadTrustMaterial(new TrustSelfSignedStrategy())
                .build();

        this.setHttpClient(HttpClients.custom()
                .setSSLContext(sslContext)
                .setSSLHostnameVerifier(new NoopHostnameVerifier())
                .build());
    }
}
