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

import eu.xenit.alfresco.solrapi.client.spi.dto.Acl;
import eu.xenit.alfresco.solrapi.client.spi.dto.AclChangeSetList;
import eu.xenit.alfresco.solrapi.client.spi.dto.AclReaders;
import eu.xenit.alfresco.solrapi.client.spi.dto.AlfrescoModel;
import eu.xenit.alfresco.solrapi.client.spi.dto.AlfrescoModelDiff;
import eu.xenit.alfresco.solrapi.client.spi.dto.GetTextContentResponse;
import eu.xenit.alfresco.solrapi.client.spi.dto.SolrNode;
import eu.xenit.alfresco.solrapi.client.spi.dto.SolrNodeMetaData;
import eu.xenit.alfresco.solrapi.client.spi.dto.SolrTransactions;
import eu.xenit.alfresco.solrapi.client.spi.query.AclReadersQueryParameters;
import eu.xenit.alfresco.solrapi.client.spi.query.AclsQueryParameters;
import eu.xenit.alfresco.solrapi.client.spi.query.NodeMetaDataQueryParameters;
import eu.xenit.alfresco.solrapi.client.spi.query.NodesQueryParameters;
import java.util.List;


/**
 * A client interface for the API that Alfresco provides for Solr - much like the standard
 *  that mirrors most of the native Alfresco SolrAPIClient - but with the following important differences:
 *
 *  <ul>
 *      <li>No dependency on org.alfresco:alfresco-data-model and its huge set of transitive dependencies.
 *      This means this dependency is a bit more Stringy-typed than the native client.</li>
 *      <li>Better suited for integration testing, especially with xenit-ditto</li>
 *      <li>No support for multi-tenancy</li>
 *  </ul>
 */
public interface SolrApiClient {

    public AclChangeSetList getAclChangeSets(Long fromId, Long fromTime, int maxResults);

    List<Acl> getAcls(AclsQueryParameters parameters);

    List<AclReaders> getAclReaders(AclReadersQueryParameters parameters);

    /**
     * Get all transactions that are within the given boundaries
     *
     * @param fromCommitTime commit time in ms inclusive lower bound restriction - can be {@code null}
     * @param minTxnId minimum transaction id (inclusive) - can be {@code null}
     * @param toCommitTime commit time in ms exclusive upper bound restriction - can be {@code null}
     * @param maxTxnId maximum transaction id (exclusive) - can be {@code null}
     * @param maxResults maximum number of results - use {@code 0} or {@code Integer.MAX_VALUE} in order to omit
     * @return a collection of transactions plus max commit time and transaction id
     */
    SolrTransactions getTransactions(Long fromCommitTime, Long minTxnId, Long toCommitTime, Long maxTxnId, int maxResults);

    List<SolrNode> getNodes(NodesQueryParameters parameters) ;

    List<SolrNodeMetaData> getNodesMetaData(NodeMetaDataQueryParameters params);

    GetTextContentResponse getTextContent(Long nodeId, String propertyQName);

    AlfrescoModel getModel(String coreName, String modelName);

    List<AlfrescoModelDiff> getModelsDiff(String coreName, List<AlfrescoModel> currentModels);

    void close();
}
