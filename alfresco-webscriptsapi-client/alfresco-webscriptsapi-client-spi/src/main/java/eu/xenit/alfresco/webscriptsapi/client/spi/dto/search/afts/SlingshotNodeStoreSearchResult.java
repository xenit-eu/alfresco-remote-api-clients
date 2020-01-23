package eu.xenit.alfresco.webscriptsapi.client.spi.dto.search.afts;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class SlingshotNodeStoreSearchResult {
    private int numResults;
    private List<SlingshotNodeStoreSearchResultNode> results;
    private int searchElapsedTime;
}
