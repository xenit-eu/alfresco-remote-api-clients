package eu.xenit.alfresco.webscripts.client.spring;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.util.UriComponentsBuilder;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MetadataApiProperties {

    String url = "http://localhost:8080/alfresco/";

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private UriComponentsBuilder inner = UriComponentsBuilder.fromHttpUrl("http://localhost:8080/alfresco/");

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

        public MetadataApiProperties build() {
            return new MetadataApiProperties(this.inner.build().toString());
        }
    }

}
