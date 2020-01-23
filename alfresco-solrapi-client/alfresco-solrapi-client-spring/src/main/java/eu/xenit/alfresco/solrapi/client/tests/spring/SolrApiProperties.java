package eu.xenit.alfresco.solrapi.client.tests.spring;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.util.UriComponentsBuilder;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SolrApiProperties {

    String url = "https://localhost:8443/alfresco/";

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder{

        private UriComponentsBuilder inner = UriComponentsBuilder.fromHttpUrl("https://localhost:8443/alfresco/");

        public Builder scheme(String scheme) {
            this.inner.scheme(scheme);
            return this;
        }

        public Builder host(String host) {
            this.inner.host(host);
            return this;
        }

        public Builder port(int port) {
            this.inner.port(port);
            return this;
        }

        public Builder path(String path) {
            return this;
        }

        public SolrApiProperties build() {
            return new SolrApiProperties(this.inner.build().toString());
        }
    }
}
