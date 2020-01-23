package eu.xenit.alfresco.webscriptsapi.client.spi.dto.search.afts;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class SlingshotNodeStoreSearchResultNode {
    private String nodeRef;
    @JsonProperty(value = "qnamePath")
    private QName qNamePath;
    @JsonProperty(value = "name")
    private QName qName;
    private String parentNodeRef;
}
