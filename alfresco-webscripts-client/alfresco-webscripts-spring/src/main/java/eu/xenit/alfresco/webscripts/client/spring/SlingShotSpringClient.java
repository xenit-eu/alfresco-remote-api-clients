package eu.xenit.alfresco.webscripts.client.spring;

import eu.xenit.alfresco.webscripts.client.spi.SlingShotClient;
import eu.xenit.alfresco.webscripts.client.spi.model.slingshot.Metadata;
import eu.xenit.alfresco.webscripts.client.spring.http.RestTemplateHelper;
import java.net.URI;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

public class SlingShotSpringClient implements SlingShotClient {

    private final RestTemplate restClient;
    private final String url;

    public SlingShotSpringClient(AlfrescoProperties alfrescoProperties) {
        this(alfrescoProperties, RestTemplateHelper.buildRestTemplate(alfrescoProperties));
    }

    public SlingShotSpringClient(AlfrescoProperties alfrescoProperties, RestTemplate restTemplate) {
        this.url = UriComponentsBuilder.fromHttpUrl(alfrescoProperties.getUrl())
                .path("/service/slingshot/node/")
                .toUriString();

        this.restClient = restTemplate;
    }

    @Override
    public Metadata get(String nodeRef) {
        URI uri = UriComponentsBuilder
                .fromHttpUrl(url)
                .pathSegment(nodeRef.replaceAll("://","/").split("/"))
                .build().toUri();

        return this.restClient.getForObject(uri, Metadata.class);
    }
}
