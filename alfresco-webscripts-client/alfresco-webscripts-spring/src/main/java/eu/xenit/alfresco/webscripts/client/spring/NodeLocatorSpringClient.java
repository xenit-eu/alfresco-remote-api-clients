package eu.xenit.alfresco.webscripts.client.spring;

import eu.xenit.alfresco.webscripts.client.spi.NodeLocatorClient;
import eu.xenit.alfresco.webscripts.client.spi.NodeRefJsonData;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

public class NodeLocatorSpringClient implements NodeLocatorClient {

    private final RestTemplate restTemplate;

    public NodeLocatorSpringClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public String get(String locatorName) {
        return get(locatorName, new HashMap<>());
    }

    @Override
    public String get(String locatorName, Map<String, String> params) {
        String url = addParamsToUrl("/api/nodelocator/{locatorName}", params);
        params.put("locatorName", locatorName);
        return getNodeReference(url, params);
    }

    private String getNodeReference(String url,  Map<String, String> params) {
        NodeRefJsonData nodeRefData = this.restTemplate.getForObject(url, NodeRefJsonData.class, params);
        if(nodeRefData == null || nodeRefData.getData() == null) {
            return null;
        }
        return nodeRefData.getData().getNodeRef();
    }

    private String addParamsToUrl(String url, Map<String, String> params) {
        StringBuilder urlBuilder = new StringBuilder(url);
        if(params.size() > 0) {
            urlBuilder.append("?");
            for(String key : params.keySet()) {
                urlBuilder.append(key).append("={").append(key).append("}").append("&");
            }
            urlBuilder.setLength(urlBuilder.length() - 1);
        }
        return urlBuilder.toString();
    }
}
