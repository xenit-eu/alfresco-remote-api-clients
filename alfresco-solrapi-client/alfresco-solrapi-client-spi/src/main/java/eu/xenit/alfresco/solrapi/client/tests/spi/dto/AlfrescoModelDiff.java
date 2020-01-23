package eu.xenit.alfresco.solrapi.client.tests.spi.dto;

import lombok.Data;

@Data
public class AlfrescoModelDiff
{
    public enum TYPE
    {
        NEW,
        CHANGED,
        REMOVED
    }
    
    private final String modelName;
    private final TYPE type;
    private final Long oldChecksum;
    private final Long newChecksum;
}
