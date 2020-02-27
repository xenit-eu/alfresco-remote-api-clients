package eu.xenit.alfresco.restapi.client.spi.model;


import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class Pagination {

    private Integer count;
    private Boolean hasMoreItems;
    private Integer totalItems;
    private Integer skipCount;
    private Integer maxItems;

}
