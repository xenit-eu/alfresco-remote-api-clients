package eu.xenit.alfresco.solrapi.client.spring;

import eu.xenit.alfresco.client.solrapi.api.model.Acl;
import eu.xenit.alfresco.client.solrapi.api.model.AclChangeSet;
import eu.xenit.alfresco.client.solrapi.api.model.AclChangeSetList;
import eu.xenit.alfresco.client.solrapi.api.model.AclReaders;
import eu.xenit.alfresco.client.solrapi.api.model.ChildAssociation;
import eu.xenit.alfresco.client.solrapi.api.model.NodeNamePaths;
import eu.xenit.alfresco.client.solrapi.api.model.NodePathInfo;
import eu.xenit.alfresco.client.solrapi.api.model.SolrNode;
import eu.xenit.alfresco.client.solrapi.api.model.SolrNodeMetadata;
import eu.xenit.alfresco.client.solrapi.api.model.SolrTransaction;
import eu.xenit.alfresco.client.solrapi.api.model.SolrTransactions;
import eu.xenit.alfresco.solrapi.client.spring.dto.AclChangeSetListModel;
import eu.xenit.alfresco.solrapi.client.spring.dto.AclChangeSetModel;
import eu.xenit.alfresco.solrapi.client.spring.dto.AclListModel;
import eu.xenit.alfresco.solrapi.client.spring.dto.AclReadersListModel;
import eu.xenit.alfresco.solrapi.client.spring.dto.AclReadersModel;
import eu.xenit.alfresco.solrapi.client.spring.dto.NodeNamePathsModel;
import eu.xenit.alfresco.solrapi.client.spring.dto.NodePathInfoModel;
import eu.xenit.alfresco.solrapi.client.spring.dto.SolrNodeListModel;
import eu.xenit.alfresco.solrapi.client.spring.dto.SolrNodeMetadataModel;
import eu.xenit.alfresco.solrapi.client.spring.dto.SolrNodeMetadataListModel;
import eu.xenit.alfresco.solrapi.client.spring.dto.SolrNodeModel;
import eu.xenit.alfresco.solrapi.client.spring.dto.SolrTransactionModel;
import eu.xenit.alfresco.solrapi.client.spring.dto.SolrTransactionsModel;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class SpringModelMapper {

    public AclChangeSetList toApiModel(AclChangeSetListModel model) {
        Objects.requireNonNull(model, "Argument 'model' is required");
        return new AclChangeSetList(
                model.getAclChangeSets().stream().map(this::toApiModel).collect(Collectors.toList()),
                model.getMaxChangeSetCommitTime(),
                model.getMaxChangeSetId());
    }

    private AclChangeSet toApiModel(AclChangeSetModel model) {
        Objects.requireNonNull(model, "Argument 'model' is required");
        return new AclChangeSet(model.getId(), model.getCommitTimeMs(), model.getAclCount());
    }

    public List<Acl> toApiModel(AclListModel model) {
        Objects.requireNonNull(model, "Argument 'model' is required");
        return model.getAcls().stream()
                .map(acl -> new Acl(acl.getAclChangeSetId(), acl.getId(), acl.getInheritedId()))
                .collect(Collectors.toList());
    }

    public List<AclReaders> toApiModel(AclReadersListModel model) {
        Objects.requireNonNull(model, "Argument 'model' is required");
        return model.getAclsReaders().stream().map(this::toApiModel).collect(Collectors.toList());
    }

    private AclReaders toApiModel(AclReadersModel model) {
        Objects.requireNonNull(model, "Argument 'model' is required");
        return new AclReaders(model.getAclId(), model.getReaders(), model.getDenied(), model.getAclChangeSetId(),
                model.getTenantDomain());
    }

    public List<SolrNode> toApiModel(SolrNodeListModel model) {
        Objects.requireNonNull(model, "Argument 'model' is required");
        return model.getNodes().stream().map(this::toApiModel).collect(Collectors.toList());
    }

    private SolrNode toApiModel(SolrNodeModel model) {
        Objects.requireNonNull(model, "Argument 'model' is required");
        return new SolrNode(model.getId(), model.getNodeRef(), model.getTxnId(), model.getStatus(), model.getTenant(),
                model.getAclId(), model.getShardPropertyValue());
    }

    public SolrTransactions toApiModel(SolrTransactionsModel model) {
        Objects.requireNonNull(model, "Argument 'model' is required");
        return new SolrTransactions(
                model.getTransactions().stream().map(this::toApiModel).collect(Collectors.toList()),
                model.getMaxTxnCommitTime(),
                model.getMaxTxnId());
    }

    private SolrTransaction toApiModel(SolrTransactionModel model) {
        Objects.requireNonNull(model, "Argument 'model' is required");
        return new SolrTransaction(model.getId(), model.getCommitTimeMs(), model.getUpdates(), model.getDeletes());
    }

    public List<SolrNodeMetadata> toApiModel(SolrNodeMetadataListModel model) {
        Objects.requireNonNull(model, "Argument 'model' is required");
        return model.getNodes().stream().map(this::toApiModel).collect(Collectors.toList());
    }

    SolrNodeMetadata toApiModel(SolrNodeMetadataModel model) {
        return new SolrNodeMetadata(
                model.getId(),
                model.getAclId(),
                model.getTxnId(),
                model.getNodeRef(),
                model.getType(),
                model.getProperties(),
                model.getAspects(),
                model.getPaths() == null ? null
                        : model.getPaths().stream().map(this::toApiModel).collect(Collectors.toList()),
                model.getNamePaths() == null ? null
                        : model.getNamePaths().stream().map(this::toApiModel).collect(Collectors.toList()),
                model.getAncestors(),
                model.getParentAssocs() == null ? null
                        : model.getParentAssocs().stream().map(this::createChildAssociation)
                                .collect(Collectors.toList()),
                model.getParentAssocsCrc(),
                model.getChildAssocs() == null ? null
                        : model.getChildAssocs().stream().map(this::createChildAssociation)
                                .collect(Collectors.toList()),
                model.getChildIds(),
                model.getOwner(),
                model.getTenantDomain());
    }

    public ChildAssociation createChildAssociation(String model) {
        String[] split = model.split("\\|");

        String parentRef = split[0];
        String sourceRef = split[1];
        String assocType = split[2];
        String childName = split[3];
        boolean primary = Boolean.parseBoolean(split[4]);
        int index = Integer.parseInt(split[5]);

        return new ChildAssociation(parentRef, sourceRef, assocType, childName, primary, index);

    }

    private NodePathInfo toApiModel(NodePathInfoModel model) {
        return new NodePathInfo(model.getApath(), model.getPath(), model.getQname());
    }

    public NodeNamePaths toApiModel(NodeNamePathsModel model) {
        return new NodeNamePaths(model.getNamePath());
    }

}
