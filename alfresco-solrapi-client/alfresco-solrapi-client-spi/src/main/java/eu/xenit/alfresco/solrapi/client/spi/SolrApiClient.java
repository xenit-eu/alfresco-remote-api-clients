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

    /**
     * Get the ACL ChangeSets
     *
     * @param fromCommitTime the lowest commit time (optional)
     * @param minAclChangeSetId the lowest ChangeSet ID (optional)
     * @param maxResults the maximum number of results (a reasonable value only)
     * @return the ACL ChangeSets in order of commit time and ID
     */
    AclChangeSets getAclChangeSets(Long fromCommitTime, Long minAclChangeSetId, Long toCommitTime,
            Long maxAclChangeSetId, int maxResults);

    /**
     * Get the ACLs associated with a given list of ACL ChangeSets.  The ACLs may be truncated for the last ACL
     * ChangeSet in the return values - the ACL count from the {@link #getAclChangeSets(Long, Long, Long, Long, int) ACL
     * ChangeSets}.
     *
     * @param aclChangeSets the ACL ChangeSets to include
     * @param minAclId the lowest ACL ID (may be <tt>null</tt>)
     * @param maxResults the maximum number of results to retrieve
     * @return the ACLs (includes ChangeSet ID)
     */
    List<Acl> getAcls(List<AclChangeSet> aclChangeSets, Long minAclId, int maxResults);


    /**
     * Get the ACL readers for a given list of ACLs
     *
     * @param acls the ACLs
     * @return the readers for the ACLs
     */
    List<AclReaders> getAclReaders(List<Acl> acls);


    /**
     * Get all transactions that are within the given boundaries
     *
     * @param fromCommitTime commit time in ms inclusive lower bound restriction - can be {@code null}
     * @param minTxnId minimum transaction id (inclusive) - can be {@code null}
     * @param toCommitTime commit time in ms exclusive upper bound restriction - can be {@code null}
     * @param maxTxnId maximum transaction id (exclusive) - can be {@code null}
     * @param maxResults maximum number of results - can be {@code null}
     * @return a collection of transactions plus max commit time and transaction id
     */
    SolrTransactions getTransactions(Long fromCommitTime, Long minTxnId, Long toCommitTime, Long maxTxnId, int maxResults);

    List<SolrNode> getNodes(NodesQueryParameters parameters) ;

    List<SolrNodeMetaData> getNodesMetaData(NodeMetaDataQueryParameters params);

    AlfrescoModel getModel(String coreName, String modelName);

    List<AlfrescoModelDiff> getModelsDiff(String coreName, List<AlfrescoModel> currentModels);

    void close();
}
