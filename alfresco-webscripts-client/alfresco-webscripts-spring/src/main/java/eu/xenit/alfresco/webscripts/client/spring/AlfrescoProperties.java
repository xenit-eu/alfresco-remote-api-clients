package eu.xenit.alfresco.webscripts.client.spring;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.util.UriComponentsBuilder;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AlfrescoProperties {

    private String url = "http://localhost:8080/alfresco/";

    private String user = "admin";
    private String password = "admin";

    private boolean insecure = false;

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private UriComponentsBuilder inner = UriComponentsBuilder.fromHttpUrl("http://localhost:8080/alfresco/");
        private String user = "admin";
        private String password = "admin";
        private boolean insecure = false;

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

        public Builder insecure(boolean insecure) {
            this.insecure = insecure;
            return this;
        }

        public Builder user(String user) {
            this.user = user;
            return this;
        }

        public Builder password(String password) {
            this.password = password;
            return this;
        }

        public AlfrescoProperties build() {
            return new AlfrescoProperties(this.inner.build().toString(), this.user, this.password, this.insecure);
        }
    }

}
