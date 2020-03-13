package eu.xenit.alfresco.solrapi.client.spring.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
public class HttpProperties {

    private boolean insecureSsl;

    private TimeoutProperties timeout = new TimeoutProperties();

    @Data
    @Accessors(chain = true)
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TimeoutProperties {

        private int socket = 5000;
        private int connect = 5000;
        private int connectionRequest = 5000;

    }


}
