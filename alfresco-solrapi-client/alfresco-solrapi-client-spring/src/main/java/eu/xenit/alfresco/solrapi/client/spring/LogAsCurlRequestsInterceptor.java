package eu.xenit.alfresco.solrapi.client.spring;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

@Slf4j
public class LogAsCurlRequestsInterceptor implements ClientHttpRequestInterceptor {

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution)
            throws IOException {

        if (log.isDebugEnabled()) {
            logRequest(request, body);
        }

        return execution.execute(request, body);
    }

    private void logRequest(HttpRequest request, byte[] body) {
        StringBuilder curl = new StringBuilder("curl")
                .append(" -k"); // --insecure/-k flag because we're using self signed TLS certificates

        // add 'Content-Type' header(s)
        List<String> contentType = request.getHeaders().get("Content-Type");
        if (contentType != null) {
            contentType.forEach(ct -> curl.append(" -H 'Content-Type: ").append(ct).append("'"));
        }

        // add TLS client certificate
        curl.append(" -E alfresco-client.pem");

        // URI
        curl.append(" ").append(request.getURI());

        // JSON payload ?
        if (body.length > 0) {
            curl.append(" -d '").append(new String(body, StandardCharsets.UTF_8)).append("'");
        }

        log.debug(curl.toString());
    }
}
