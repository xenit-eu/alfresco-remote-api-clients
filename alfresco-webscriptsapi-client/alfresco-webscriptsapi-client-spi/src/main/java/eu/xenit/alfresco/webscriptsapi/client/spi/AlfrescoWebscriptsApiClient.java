package eu.xenit.alfresco.webscriptsapi.client.spi;

import eu.xenit.alfresco.webscriptsapi.client.spi.dto.Metadata;
import eu.xenit.alfresco.webscriptsapi.client.spi.dto.search.afts.SlingshotNodeStoreSearchResult;

public interface AlfrescoWebscriptsApiClient {
    Metadata getMetadata(String nodeRef);
    SlingshotNodeStoreSearchResult getSlingshotNodeSearch(String store, String query, String lang);

    public static class Stores {
        private static final String DELIMITER = "://";
        private static final String STORE_PROTOCOL_WORKSPACE = "workspace";

        public static final String WORKSPACE_SPACESSTORE = STORE_PROTOCOL_WORKSPACE + DELIMITER + "SpacesStore";
    }

    public static class QueryLanguages {
        public static final String FTS_ALFRESCO = "fts-alfresco";
    }
}
