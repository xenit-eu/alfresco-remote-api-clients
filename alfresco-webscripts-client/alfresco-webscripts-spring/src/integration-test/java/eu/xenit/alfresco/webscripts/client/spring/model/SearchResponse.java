package eu.xenit.alfresco.webscripts.client.spring.model;

import com.fasterxml.jackson.annotation.JsonSetter;
import java.util.List;
import lombok.Data;

@Data
public class SearchResponse {

    private ResponseList list;

    @Data
    public static class ResponseList {
        private Pagination pagination;
        private List<EntryContainer> entries;
    }

    @Data
    public static class Pagination {
        Long count;
        boolean hasMoreItems;
        Long totalItems;
        Long skipCount;
        Long maxItems;
    }

    @Data
    public static class EntryContainer {
        Entry entry;
    }


    @Data
    public static class Entry {
        String createdAt;
        boolean isFolder;
        Search search;
        boolean isFile;
        User createdByUser;
        String modifiedAt;
        User modifiedByUser;
        String name;
        String location;
        String id;
        String nodeType;
        String parentId;
    }

    @Data
    public static class Search {
        Long score;
    }

    @Data
    public static class User {
        String id;
        String displayName;
    }

}