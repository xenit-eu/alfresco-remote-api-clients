package eu.xenit.alfresco.solrapi.client.spring;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import eu.xenit.alfresco.client.solrapi.api.model.ChildAssociation;
import eu.xenit.alfresco.solrapi.client.spring.dto.AclChangeSetListModel;
import eu.xenit.alfresco.solrapi.client.spring.dto.AclListModel;
import eu.xenit.alfresco.solrapi.client.spring.dto.AclReadersListModel;
import eu.xenit.alfresco.solrapi.client.spring.dto.SolrNodeListModel;
import eu.xenit.alfresco.solrapi.client.spring.dto.SolrNodeMetadataListModel;
import eu.xenit.alfresco.solrapi.client.spring.dto.SolrNodeMetadataModel;
import eu.xenit.alfresco.solrapi.client.spring.dto.SolrTransactionsModel;
import org.junit.jupiter.api.Test;

class SpringModelMapperTest {

    private SpringModelMapper mapper = new SpringModelMapper();

    @Test
    void createChildAssociation() {

        // Note: forward slashes are escaped
        String childAssocStr = "workspace://SpacesStore/4c5cfb80-111e-4fc4-9176-e8eaefcaf632"
                + "|workspace://SpacesStore/0a49ddb4-214b-41e7-92c9-3cc5e1960148"
                + "|{http://www.alfresco.org/model/content/1.0}contains"
                + "|{http://www.alfresco.org/model/application/1.0}dictionary"
                + "|true|-1";

        ChildAssociation assoc = this.mapper.createChildAssociation(childAssocStr);

        assertThat(assoc).isNotNull();
        assertThat(assoc.getParentRef()).isEqualTo("workspace://SpacesStore/4c5cfb80-111e-4fc4-9176-e8eaefcaf632");
        assertThat(assoc.getChildRef()).isEqualTo("workspace://SpacesStore/0a49ddb4-214b-41e7-92c9-3cc5e1960148");
        assertThat(assoc.getAssocTypeQName()).isEqualTo("{http://www.alfresco.org/model/content/1.0}contains");
        assertThat(assoc.getChildQName()).isEqualTo("{http://www.alfresco.org/model/application/1.0}dictionary");
        assertThat(assoc.isPrimary()).isTrue();
        assertThat(assoc.getIndex()).isEqualTo(-1);

    }

    @Test
    void nullChildRef_throwsNPE() {
        assertThatExceptionOfType(NullPointerException.class).isThrownBy(() -> new ChildAssociation(
                "workspace://SpacesStore/4c5cfb80-111e-4fc4-9176-e8eaefcaf632",
                null /* <-- testing this */,
                "{http://www.alfresco.org/model/content/1.0}contains",
                "{http://www.alfresco.org/model/application/1.0}dictionary", true, -1));
    }

    @Test
    void toApiModel_SolrNodeMetadataModel_nullParentAssocs() {
        SolrNodeMetadataModel solrNodeMetadataModel = new SolrNodeMetadataModel();
        solrNodeMetadataModel.setParentAssocs(null);
        mapper.toApiModel(solrNodeMetadataModel);
    }

    @Test
    void toApiModel_SolrNodeMetadataListModel_nullNodes() {
        SolrNodeMetadataListModel solrNodeMetadataListModel = new SolrNodeMetadataListModel();
        solrNodeMetadataListModel.setNodes(null);
        mapper.toApiModel(solrNodeMetadataListModel);
    }

    @Test
    void toApiModel_SolrTransactionsModel_nullTransactions() {
        SolrTransactionsModel solrTransactionsModel = new SolrTransactionsModel();
        solrTransactionsModel.setTransactions(null);
        mapper.toApiModel(solrTransactionsModel);
    }

    @Test
    void toApiModel_SolrNodeListModel_nullNodes() {
        SolrNodeListModel solrNodeListModel = new SolrNodeListModel();
        solrNodeListModel.setNodes(null);
        mapper.toApiModel(solrNodeListModel);
    }

    @Test
    void toApiModel_AclReadersListModel_nullAclReaders() {
        AclReadersListModel aclReadersListModel = new AclReadersListModel();
        aclReadersListModel.setAclsReaders(null);
        mapper.toApiModel(aclReadersListModel);
    }

    @Test
    void toApiModel_AclListModel_nullAcls() {
        AclListModel aclListModel = new AclListModel();
        aclListModel.setAcls(null);
        mapper.toApiModel(aclListModel);
    }

    @Test
    void toApiModel_AclChangeSetListModel_nullAclChangeSets() {
        AclChangeSetListModel aclChangeSetListModel = new AclChangeSetListModel();
        aclChangeSetListModel.setAclChangeSets(null);
        mapper.toApiModel(aclChangeSetListModel);
    }


}