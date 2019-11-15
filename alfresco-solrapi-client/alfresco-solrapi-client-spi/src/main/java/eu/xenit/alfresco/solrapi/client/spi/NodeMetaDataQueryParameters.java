/*
 * #%L
 * Alfresco Solr Client
 * %%
 * Copyright (C) 2005 - 2016 Alfresco Software Limited
 * %%
 * This file is part of the Alfresco software. 
 * If the software was purchased under a paid Alfresco license, the terms of 
 * the paid license agreement will prevail.  Otherwise, the software is 
 * provided under the following open source license terms:
 * 
 * Alfresco is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Alfresco is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */
package eu.xenit.alfresco.solrapi.client.spi;

import java.util.List;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * Query parameters for fetching node information.
 */

@Data
@Accessors(fluent = true)
public class NodeMetaDataQueryParameters
{
    private List<Long> transactionIds;
    private Long fromTxnId;
    private Long toTxnId;

    // 0 means no limit
    private int maxResults = 0;

    private Long fromNodeId;
    private Long toNodeId;
    private List<Long> nodeIds;

    private boolean includeAclId = true;
    private boolean includeAspects = true;
    private boolean includeNodeRef = true;
    private boolean includeOwner = true;
    private boolean includeProperties = true;
    private boolean includePaths = true;
    private boolean includeType = true;
    private boolean includeChildAssociations = true;
    private boolean includeParentAssociations = true;
    private boolean includeChildIds = true;
    private boolean includeTxnId = true;
}
