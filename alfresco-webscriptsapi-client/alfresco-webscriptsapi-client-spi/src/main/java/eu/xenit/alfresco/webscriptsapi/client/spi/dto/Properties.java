package eu.xenit.alfresco.webscriptsapi.client.spi.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@EqualsAndHashCode(callSuper=false)
public class Properties extends CustomPropertiesJsonPOJO {
    @JsonProperty(value = "{http://www.alfresco.org/model/content/1.0}content")
    private String content = null; //"contentUrl=store://2019/9/9/11/37/f4a559c0-8877-487c-b70a-c55adaf885b4.bin|mimetype=application/x-yaml|size=788|encoding=UTF-8|locale=en_US_|id=274",
    @JsonProperty(value = "{http://www.alfresco.org/model/content/1.0}created")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    private String created = null;//: "2019-09-09T11:37:03.173Z",
    @JsonProperty(value = "{http://www.alfresco.org/model/content/1.0}modified")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    private String modified = null;//: "2019-09-09T11:37:13.374Z"
    @JsonProperty(value = "{http://www.alfresco.org/model/content/1.0}modifier")
    private String modifier = null;//: "admin",
    @JsonProperty(value = "{http://www.alfresco.org/model/content/1.0}title")
    private String title = null;//: "",
    @JsonProperty(value = "{http://www.alfresco.org/model/content/1.0}description")
    private String description = null;//: "",
    @JsonProperty(value = "{http://www.alfresco.org/model/content/1.0}creator")
    private String creator = null;//: "admin",
    @JsonProperty(value = "{http://www.alfresco.org/model/system/1.0}node-uuid")
    private String nodeUuid = null;//: "a087aeed-75fd-47ce-b10e-4d1b1029c18d",
    @JsonProperty(value = "{http://www.alfresco.org/model/content/1.0}name")
    private String name = null;//: "inflow-config-1 (2)-1.yml",
    @JsonProperty(value = "{http://www.alfresco.org/model/system/1.0}store-identifier")
    private String storeIdentifier = null;//: "SpacesStore",
    @JsonProperty(value = "{http://www.alfresco.org/model/system/1.0}store-protocol")
    private String storeProtocol = null;//: "workspace",
    @JsonProperty(value = "{http://www.alfresco.org/model/application/1.0}editInline")
    private String editInline = null;//: true,
    @JsonProperty(value = "{http://www.alfresco.org/model/system/1.0}cascadeCRC")
    private String cascadeCRC;//: 3994999376,
    @JsonProperty(value = "{http://www.alfresco.org/model/system/1.0}cascadeTx")
    private String cascadeTx;//: 30,
    @JsonProperty(value = "{http://www.alfresco.org/model/system/1.0}node-dbid")
    private String nodeDbId;//: 888,
    @JsonProperty(value = "{http://www.alfresco.org/model/system/1.0}locale")
    private String locale;//: "en_US",
    @JsonProperty(value = "{http://www.alfresco.org/model/content/1.0}autoVersion")
    private String autoVersion = null;//: true,
    @JsonProperty(value = "{http://www.alfresco.org/model/content/1.0}versionType")
    private String versionType = null;//: "MINOR",
    @JsonProperty(value = "{http://www.alfresco.org/model/content/1.0}versionLabel")
    private String versionLabel = null;//: "1.1",
    @JsonProperty(value = "{http://www.alfresco.org/model/content/1.0}autoVersionOnUpdateProps")
    private String autoVersionOnUpdateProps = null;//: false,
    @JsonProperty(value = "{http://www.alfresco.org/model/content/1.0}initialVersion")
    private String initialVersion = null;//: true,
}
