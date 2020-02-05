package eu.xenit.alfresco.solrapi.client.spi.dto;

import lombok.Data;

@Data
public class AlfrescoModel
{
    private final Object model;
    private final Long checksum;
}
