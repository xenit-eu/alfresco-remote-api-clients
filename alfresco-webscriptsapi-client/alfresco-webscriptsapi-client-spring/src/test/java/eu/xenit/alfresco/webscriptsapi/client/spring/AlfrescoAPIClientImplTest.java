package eu.xenit.alfresco.webscriptsapi.client.spring;

import eu.xenit.alfresco.webscriptsapi.client.spi.AlfrescoWebscriptsApiClient;
import eu.xenit.alfresco.webscriptsapi.client.spi.dto.Metadata;
import eu.xenit.alfresco.webscriptsapi.client.spi.dto.search.afts.SlingshotNodeStoreSearchResult;
import eu.xenit.alfresco.webscriptsapi.client.spi.dto.search.afts.SlingshotNodeStoreSearchResultNode;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.AutoConfigureWebClient;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriUtils;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
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
        String nodeRef = AlfrescoWebscriptsApiClient.Stores.WORKSPACE_SPACESSTORE + UUID.randomUUID();
        MockRestServiceServer.createServer(restTemplate)
                .expect(request -> requestUrlMatches(request, "/alfresco/service/api/metadata"))
                .andExpect(method(HttpMethod.GET))
                .andExpect(queryParam("nodeRef", nodeRef))
                .andRespond(withSuccess(String.join(System.lineSeparator(),
                        "{" ,
                                "\"nodeRef\": \"" + nodeRef + "\"," ,
                                "\"aspects\": [" ,
                                "    \"{http://www.agvespa.be/model/vespa}hasTemplateName\"," ,
                                "    \"{http://www.agvespa.be/model/vespa}hasDocStatus\"," ,
                                "    \"{http://www.alfresco.org/model/content/1.0}copiedfrom\"," ,
                                "    \"{http://www.alfresco.org/model/content/1.0}titled\"," ,
                                "    \"{http://www.agvespa.be/model/vespa}hasDocumentType\"," ,
                                "    \"{http://www.agvespa.be/model/vespa}hasFailedTransferStatus\"," ,
                                "    \"{http://www.agvespa.be/model/vespa}isTransferPossible\"," ,
                                "    \"{http://www.alfresco.org/model/content/1.0}auditable\"," ,
                                "    \"{http://www.alfresco.org/model/system/1.0}referenceable\"," ,
                                "    \"{http://www.alfresco.org/model/system/1.0}localized\"," ,
                                "    \"{http://www.alfresco.org/model/content/1.0}author\"" ,
                                "]," ,
                                "\"mimetype\": \"application/vnd.openxmlformats-officedocument.wordprocessingml.document\"," ,
                                "\"type\": \"{http://www.agvespa.be/model/vespa}document\"," ,
                                "\"properties\": {" ,
                                "    \"{http://www.alfresco.org/model/content/1.0}created\": \"2020-01-22T09:35:52.826Z\"," ,
                                "    \"{http://www.agvespa.be/model/vespa}transferPossible\": false," ,
                                "    \"{http://www.agvespa.be/model/vespa}documentType\": \"workspace://SpacesStore/ee15d425-6db7-4add-8472-b7a01c4a1dbd\"," ,
                                "    \"{http://www.alfresco.org/model/content/1.0}creator\": \"Ilse Van Gelder\"," ,
                                "    \"{http://www.alfresco.org/model/system/1.0}node-uuid\": \"b2073987-0ab5-4369-9012-1a62d8017915\"," ,
                                "    \"{http://www.alfresco.org/model/content/1.0}name\": \"Ontwerp onderhandse verkoopovereenkomst.docx\"," ,
                                "    \"{http://www.alfresco.org/model/content/1.0}content\": \"contentUrl=store://2019/8/2/11/5/676dea99-82f1-4f73-8a59-278e506cb012.bin|mimetype=application/vnd.openxmlformats-officedocument.wordprocessingml.document|size=69192|encoding=UTF-8|locale=nl_|id=651491\"," ,
                                "    \"{http://www.alfresco.org/model/system/1.0}store-identifier\": \"SpacesStore\"," ,
                                "    \"{http://www.agvespa.be/model/vespa}failedTransferStatus\": \"Dimensies onvolledig/ontbrekend\"," ,
                                "    \"{http://www.agvespa.be/model/vespa}docstat\": \"Werkversie\"," ,
                                "    \"{http://www.agvespa.be/model/vespa}templateName\": \"VG_Verkoopovereenkomst (bieding)_28 08 2017.docx\"," ,
                                "    \"{http://www.alfresco.org/model/system/1.0}store-protocol\": \"workspace\"," ,
                                "    \"{http://www.alfresco.org/model/system/1.0}node-dbid\": 718584," ,
                                "    \"{http://www.alfresco.org/model/system/1.0}locale\": \"nl\"," ,
                                "    \"{http://www.alfresco.org/model/content/1.0}modifier\": \"admin\"," ,
                                "    \"{http://www.alfresco.org/model/content/1.0}modified\": \"2020-01-22T12:00:00.626Z\"," ,
                                "    \"{http://www.alfresco.org/model/content/1.0}author\": \"Katrien Clukkers\"" ,
                                "}" ,
                                "}"), MediaType.APPLICATION_JSON));

        Metadata metadata = this.client.getMetadata(nodeRef);

        assertThat(metadata)
                .isNotNull()
                .satisfies(meta -> Assertions.assertThat(meta.getNodeRef())
                        .as("check node reference")
                        .isEqualTo(nodeRef))
                .satisfies(meta -> Assertions.assertThat(meta.getMetadataProperties().getNodeDbId())
                        .as("check node reference")
                        .isEqualTo("718584"));
    }

    @Test
    void compliesWithAlfrescoRemoteAPI_alfresco_service_slingshot_node_search() throws UnsupportedEncodingException {
        String nodeRef = AlfrescoWebscriptsApiClient.Stores.WORKSPACE_SPACESSTORE + UUID.randomUUID();
        String longQName_CompanyHome = "{http://www.alfresco.org/model/application/1.0}company_home";
        String shortQName_CompanyHome = "app:company_home";
        String store = AlfrescoWebscriptsApiClient.Stores.WORKSPACE_SPACESSTORE;
        String query = new StringBuilder("PATH:").append("\"/").append(shortQName_CompanyHome).append("\"").toString();
        String lang = AlfrescoWebscriptsApiClient.QueryLanguages.FTS_ALFRESCO;
        MockRestServiceServer.createServer(restTemplate)
                .expect(request -> requestUrlMatches(request, "/alfresco/service/slingshot/node/search"))
                .andExpect(method(HttpMethod.GET))
                .andExpect(queryParam("store", store))
                .andExpect(queryParam("q", urlEncode(query)))
                .andExpect(queryParam("lang", lang))
                .andRespond(withSuccess(String.join(System.lineSeparator(),
                        "{" ,
                                "\"numResults\": 1," ,
                                "\"results\": [" ,
                                "   {" ,
                                "      \"nodeRef\": \"" + nodeRef + "\"," ,
                                "      \"qnamePath\": {" ,
                                "         \"name\": \"\\/" + longQName_CompanyHome + "\"," ,
                                "         \"prefixedName\": \"\\/" + shortQName_CompanyHome + "\"" ,
                                "      }," ,
                                "      \"name\": {" ,
                                "         \"name\": \"" + longQName_CompanyHome + "\"," ,
                                "         \"prefixedName\": \"" + shortQName_CompanyHome + "\"" ,
                                "      }," ,
                                "      \"parentNodeRef\": \"\"" ,
                                "   }" ,
                                "]," ,
                                "\"searchElapsedTime\": 7" ,
                                "}"), MediaType.APPLICATION_JSON));

        SlingshotNodeStoreSearchResult result = this.client.getSlingshotNodeSearch(store, query, lang);
        SlingshotNodeStoreSearchResultNode node = result.getResults().get(0);
        assertThat(result)
                .isNotNull()
                .satisfies(r -> Assertions.assertThat(r.getNumResults())
                        .as("Checking numResults")
                        .isEqualTo(1))
                .satisfies(r -> Assertions.assertThat(r.getResults().size())
                        .as("Checking results size")
                        .isEqualTo(1))
                .satisfies(r -> Assertions.assertThat(node.getNodeRef())
                        .as("Checking first node nodeRef")
                        .isEqualTo(nodeRef))
                .satisfies(r -> Assertions.assertThat(node.getQName().getShortQName())
                        .as("Checking first node qName shortQName")
                        .isEqualTo(shortQName_CompanyHome))
                .satisfies(r -> Assertions.assertThat(node.getQName().getFullyQuallifiedQName())
                        .as("Checking first node qName fullyQuallifiedQName")
                        .isEqualTo(longQName_CompanyHome))
                .satisfies(r -> Assertions.assertThat(node.getQNamePath().getShortQName())
                        .as("Checking first node qNamePath shortQName")
                        .isEqualTo("/" + shortQName_CompanyHome))
                .satisfies(r -> Assertions.assertThat(node.getQNamePath().getFullyQuallifiedQName())
                        .as("Checking first node qNamePath fullyQuallifiedQName")
                        .isEqualTo("/" + longQName_CompanyHome));
    }

    private String urlEncode(String value) {
        return UriUtils.encodePath(value, StandardCharsets.UTF_8.toString());
    }

    private void requestUrlMatches(ClientHttpRequest request, String url) {
        assertEquals("Request URI path", url, request.getURI().getPath());
    }
}