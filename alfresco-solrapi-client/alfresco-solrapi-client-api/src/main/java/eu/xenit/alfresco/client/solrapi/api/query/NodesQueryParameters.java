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
package eu.xenit.alfresco.client.solrapi.api.query;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@JsonInclude(Include.NON_NULL)
public class NodesQueryParameters
{
    private List<Long> txnIds;

    private Long fromTxnId;
    private Long toTxnId;

    private Long fromNodeId;
    private Long toNodeId;
    
    private String storeProtocol;
    private String storeIdentifier;
    
    private Set<String> includeNodeTypes;
    private Set<String> excludeNodeTypes;
    
    private Set<String> includeAspects;
    private Set<String> excludeAspects;
    
    private String shardProperty;

    private int maxResults;

    public NodesQueryParameters withTxnIds(Long ... ids) {
        this.txnIds = Arrays.asList(ids);
        return this;
    }
}
