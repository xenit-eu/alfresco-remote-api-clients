package eu.xenit.alfresco.webscriptsapi.client.spring;

import eu.xenit.alfresco.webscriptsapi.client.spi.AlfrescoWebscriptsApiClient;
import eu.xenit.alfresco.webscriptsapi.client.spi.dto.Metadata;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.AutoConfigureWebClient;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.util.AssertionErrors.assertEquals;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@RestClientTest
@AutoConfigureWebClient(registerRestTemplate = true)
class AlfrescoAPIClientImplTest {

    @Configuration
    public static class TestConfig {
        @Bean
        public AlfrescoWebscriptsApiClient AlfrescoApiClient(RestTemplate restTemplate) {
            return new AlfrescoWebscriptsApiClientImpl("https://localhost:8443/alfresco/", restTemplate);
        }
    }

    @Autowired
    private AlfrescoWebscriptsApiClient client;

    @Autowired
    private RestTemplate restTemplate;

    @Test
    void compliesWithAlfrescoRemoteAPI_alfresco_service_api_metadata() {
        String nodeRef = "workspace://SpacesStore/" + UUID.randomUUID();
        MockRestServiceServer.createServer(restTemplate)
                .expect(request -> assertEquals("Request URI path", "/alfresco/service/api/metadata", request.getURI().getPath()))
                .andExpect(method(HttpMethod.GET))
                .andExpect(queryParam("nodeRef", nodeRef))
                .andRespond(withSuccess(String.join(System.lineSeparator(),
                        "{" +
                                "    \"nodeRef\": \"" + nodeRef + "\"," +
                                "    \"aspects\": [" +
                                "        \"{http://www.agvespa.be/model/vespa}hasTemplateName\"," +
                                "        \"{http://www.agvespa.be/model/vespa}hasDocStatus\"," +
                                "        \"{http://www.alfresco.org/model/content/1.0}copiedfrom\"," +
                                "        \"{http://www.alfresco.org/model/content/1.0}titled\"," +
                                "        \"{http://www.agvespa.be/model/vespa}hasDocumentType\"," +
                                "        \"{http://www.agvespa.be/model/vespa}hasFailedTransferStatus\"," +
                                "        \"{http://www.agvespa.be/model/vespa}isTransferPossible\"," +
                                "        \"{http://www.alfresco.org/model/content/1.0}auditable\"," +
                                "        \"{http://www.alfresco.org/model/system/1.0}referenceable\"," +
                                "        \"{http://www.alfresco.org/model/system/1.0}localized\"," +
                                "        \"{http://www.alfresco.org/model/content/1.0}author\"" +
                                "    ]," +
                                "    \"mimetype\": \"application/vnd.openxmlformats-officedocument.wordprocessingml.document\"," +
                                "    \"type\": \"{http://www.agvespa.be/model/vespa}document\"," +
                                "    \"properties\": {" +
                                "        \"{http://www.alfresco.org/model/content/1.0}created\": \"2020-01-22T09:35:52.826Z\"," +
                                "        \"{http://www.agvespa.be/model/vespa}transferPossible\": false," +
                                "        \"{http://www.agvespa.be/model/vespa}documentType\": \"workspace://SpacesStore/ee15d425-6db7-4add-8472-b7a01c4a1dbd\"," +
                                "        \"{http://www.alfresco.org/model/content/1.0}creator\": \"Ilse Van Gelder\"," +
                                "        \"{http://www.alfresco.org/model/system/1.0}node-uuid\": \"b2073987-0ab5-4369-9012-1a62d8017915\"," +
                                "        \"{http://www.alfresco.org/model/content/1.0}name\": \"Ontwerp onderhandse verkoopovereenkomst.docx\"," +
                                "        \"{http://www.alfresco.org/model/content/1.0}content\": \"contentUrl=store://2019/8/2/11/5/676dea99-82f1-4f73-8a59-278e506cb012.bin|mimetype=application/vnd.openxmlformats-officedocument.wordprocessingml.document|size=69192|encoding=UTF-8|locale=nl_|id=651491\"," +
                                "        \"{http://www.alfresco.org/model/system/1.0}store-identifier\": \"SpacesStore\"," +
                                "        \"{http://www.agvespa.be/model/vespa}failedTransferStatus\": \"Dimensies onvolledig/ontbrekend\"," +
                                "        \"{http://www.agvespa.be/model/vespa}docstat\": \"Werkversie\"," +
                                "        \"{http://www.agvespa.be/model/vespa}templateName\": \"VG_Verkoopovereenkomst (bieding)_28 08 2017.docx\"," +
                                "        \"{http://www.alfresco.org/model/system/1.0}store-protocol\": \"workspace\"," +
                                "        \"{http://www.alfresco.org/model/system/1.0}node-dbid\": 718584," +
                                "        \"{http://www.alfresco.org/model/system/1.0}locale\": \"nl\"," +
                                "        \"{http://www.alfresco.org/model/content/1.0}modifier\": \"admin\"," +
                                "        \"{http://www.alfresco.org/model/content/1.0}modified\": \"2020-01-22T12:00:00.626Z\"," +
                                "        \"{http://www.alfresco.org/model/content/1.0}author\": \"Katrien Clukkers\"" +
                                "    }" +
                                "}"), MediaType.APPLICATION_JSON));

        Metadata metadata = this.client.getMetadata(nodeRef);

        assertThat(metadata)
                .isNotNull()
                .satisfies(meta -> Assertions.assertThat(meta.getNodeRef())
                        .as("check node reference")
                        .isEqualTo(nodeRef))
                .satisfies(meta -> Assertions.assertThat(meta.getProperties().getNodeDbId())
                        .as("check node reference")
                        .isEqualTo("718584"));
    }
}