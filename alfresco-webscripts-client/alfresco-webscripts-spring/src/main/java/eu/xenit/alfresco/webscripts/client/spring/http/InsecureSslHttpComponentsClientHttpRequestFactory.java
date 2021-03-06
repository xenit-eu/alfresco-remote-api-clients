package eu.xenit.alfresco.webscripts.client.spring.http;

import java.util.function.Consumer;
import javax.net.ssl.SSLContext;
import lombok.SneakyThrows;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;

public class InsecureSslHttpComponentsClientHttpRequestFactory extends HttpComponentsClientHttpRequestFactory {

    @SneakyThrows
    public InsecureSslHttpComponentsClientHttpRequestFactory(Consumer<HttpClientBuilder> httpClientBuilderCustomizer) {
        SSLContext sslContext = SSLContextBuilder
                .create()
                .loadTrustMaterial(new TrustSelfSignedStrategy())
                .build();

        HttpClientBuilder httpClientBuilder = HttpClients.custom()
                .setSSLContext(sslContext)
                .setSSLHostnameVerifier(new NoopHostnameVerifier());
        httpClientBuilderCustomizer.accept(httpClientBuilder);

        this.setHttpClient(httpClientBuilder.build());
    }
}
