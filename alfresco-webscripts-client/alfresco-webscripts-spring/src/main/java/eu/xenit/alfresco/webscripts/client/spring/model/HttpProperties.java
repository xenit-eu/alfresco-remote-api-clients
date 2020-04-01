package eu.xenit.alfresco.webscripts.client.spring.model;

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

    private long connectionTimeToLive = -1L;
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
