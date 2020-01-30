package eu.xenit.alfresco.webscripts.client.spring;

import org.springframework.boot.web.client.RestTemplateBuilder;

public abstract class WebscriptsSpringClientTestsBase {
    protected RestTemplateBuilder restTemplateBuilder() {
        return new RestTemplateBuilder()
                .basicAuthentication("admin", "admin")
                .rootUri("http://" + alfrescoHost() + ":" + alfrescoPort() + "/alfresco/service");
    }

    private static String alfrescoHost() {
        return System.getProperty("alfresco.host", "localhost");
    }

    private static Integer alfrescoPort() {
        return Integer.parseInt(System.getProperty("alfresco.tcp.8080", "8080"));
    }
}
