package eu.xenit.alfresco.restapi.client.spring;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.util.UriComponentsBuilder;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AlfrescoRestProperties {

    private String url = "http://localhost:8080/alfresco/";
    private String tenant = "-default-";
    private String user = "admin";
    private String password = "admin";

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private UriComponentsBuilder inner = UriComponentsBuilder.fromHttpUrl("http://localhost:8080/alfresco/");
        private String tenant = "-default-";
        private String user = "admin";
        private String password = "admin";

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

        public Builder tenant(String tenant) {
            this.tenant = tenant;
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

        public AlfrescoRestProperties build() {
            return new AlfrescoRestProperties(this.inner.build().toString(), tenant, user, password);
        }

    }
}
