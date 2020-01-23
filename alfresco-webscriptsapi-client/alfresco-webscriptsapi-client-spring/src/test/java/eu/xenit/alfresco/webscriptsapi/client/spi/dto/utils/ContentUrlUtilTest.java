package eu.xenit.alfresco.webscriptsapi.client.spi.dto.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import eu.xenit.alfresco.webscriptsapi.client.spi.dto.Metadata;
import eu.xenit.alfresco.webscriptsapi.client.spi.dto.Properties;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.util.MimeTypeUtils;
import org.springframework.util.StringUtils;

import java.nio.charset.Charset;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class ContentUrlUtilTest {
    private static String json;
    private static String contentUrl = "store://2019/8/2/11/5/676dea99-82f1-4f73-8a59-278e506cb012.bin";
    private static String mimetype = "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
    private static String size = "12345676";
    private static String encoding = "UTF-8";
    private static String locale = "nl_";
    private static String id = "651491";

    @BeforeAll
    public static void init() {
        json = String.join(System.lineSeparator(),"{" ,
                "\"nodeRef\": \"workspace://SpacesStore/b2073987-0ab5-4369-9012-1a62d8017915\"," ,
                "\"aspects\": [" ,
                "\"{http://www.agvespa.be/model/vespa}hasTemplateName\"," ,
                "\"{http://www.agvespa.be/model/vespa}hasDocStatus\"," ,
                "\"{http://www.alfresco.org/model/content/1.0}copiedfrom\"," ,
                "\"{http://www.alfresco.org/model/content/1.0}titled\"," ,
                "\"{http://www.agvespa.be/model/vespa}hasDocumentType\"," ,
                "\"{http://www.agvespa.be/model/vespa}hasFailedTransferStatus\"," ,
                "\"{http://www.agvespa.be/model/vespa}isTransferPossible\"," ,
                "\"{http://www.alfresco.org/model/content/1.0}auditable\"," ,
                "\"{http://www.alfresco.org/model/system/1.0}referenceable\"," ,
                "\"{http://www.alfresco.org/model/system/1.0}localized\"," ,
                "\"{http://www.alfresco.org/model/content/1.0}author\"" ,
                "]," ,
                "\"mimetype\": \"application/vnd.openxmlformats-officedocument.wordprocessingml.document\"," ,
                "\"type\": \"{http://www.agvespa.be/model/vespa}document\"," ,
                "\"properties\": {" ,
                "\"{http://www.alfresco.org/model/content/1.0}created\": \"2020-01-22T09:35:52.826Z\"," ,
                "\"{http://www.agvespa.be/model/vespa}transferPossible\": false," ,
                "\"{http://www.agvespa.be/model/vespa}documentType\": \"workspace://SpacesStore/ee15d425-6db7-4add-8472-b7a01c4a1dbd\"," ,
                "\"{http://www.alfresco.org/model/content/1.0}creator\": \"John Doe\"," ,
                "\"{http://www.alfresco.org/model/system/1.0}node-uuid\": \"b2073987-0ab5-4369-9012-1a62d8017915\"," ,
                "\"{http://www.alfresco.org/model/content/1.0}name\": \"Ontwerp onderhandse verkoopovereenkomst.docx\"," ,
                "\"{http://www.alfresco.org/model/content/1.0}content\": \"contentUrl=" + contentUrl + "|mimetype=" + mimetype + "|size=" , size , "|encoding=" + encoding + "|locale=" + locale + "|id=" + id + "\"," ,
                "\"{http://www.alfresco.org/model/system/1.0}store-identifier\": \"SpacesStore\"," ,
                "\"{http://www.agvespa.be/model/vespa}failedTransferStatus\": \"Dimensies onvolledig/ontbrekend\"," ,
                "\"{http://www.agvespa.be/model/vespa}docstat\": \"Werkversie\"," ,
                "\"{http://www.agvespa.be/model/vespa}templateName\": \"VG_Verkoopovereenkomst (bieding)_28 08 2017.docx\"," ,
                "\"{http://www.alfresco.org/model/system/1.0}store-protocol\": \"workspace\"," ,
                "\"{http://www.alfresco.org/model/system/1.0}node-dbid\": 718584," ,
                "\"{http://www.alfresco.org/model/system/1.0}locale\": \"nl\"," ,
                "\"{http://www.alfresco.org/model/content/1.0}modifier\": \"admin\"," ,
                "\"{http://www.alfresco.org/model/content/1.0}modified\": \"2020-01-22T12:00:00.626Z\"," ,
                "\"{http://www.alfresco.org/model/content/1.0}author\": \"Katrien Clukkers\"" ,
                "}" ,
                "}");
    }

    @Test
    void getContentUrlTest() throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        Metadata metadata = objectMapper.readValue(json, Metadata.class);
        Properties props = metadata.properties();
        assertEquals( contentUrl, ContentUrlUtil.getContentUrl(props.content()));
    }

    @Test
    void getMimetypeTest() throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        Metadata metadata = objectMapper.readValue(json, Metadata.class);
        Properties props = metadata.properties();
        String propsMimetype = ContentUrlUtil.getMimetype(props.content());
        assertEquals( mimetype, propsMimetype);
        assertNotNull(MimeTypeUtils.parseMimeType(propsMimetype));
    }

    @Test
    void getSizeTest() throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        Metadata metadata = objectMapper.readValue(json, Metadata.class);
        Properties props = metadata.properties();
        String propsSize = ContentUrlUtil.getSize(props.content());
        assertEquals( size, propsSize);
        assertEquals(12345676, Integer.parseInt(propsSize));
    }

    @Test
    void getEncodingTest() throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        Metadata metadata = objectMapper.readValue(json, Metadata.class);
        Properties props = metadata.properties();
        String propsEncoding = ContentUrlUtil.getEncoding(props.content());
        assertEquals( encoding, propsEncoding);
        assertNotNull(Charset.forName(propsEncoding));
    }

    @Test
    void getLocaleTest() throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        Metadata metadata = objectMapper.readValue(json, Metadata.class);
        Properties props = metadata.properties();
        String propsLocale = ContentUrlUtil.getLocale(props.content());
        assertEquals( locale, propsLocale);
        StringUtils.parseLocaleString(propsLocale);
    }

    @Test
    void getIdTest() throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        Metadata metadata = objectMapper.readValue(json, Metadata.class);
        Properties props = metadata.properties();
        String propsId = ContentUrlUtil.getId(props.content());
        assertEquals( id, propsId);
        assertEquals(651491, Integer.parseInt(propsId));
    }
}