package eu.xenit.alfresco.solrapi.client.spring;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import eu.xenit.alfresco.client.solrapi.api.model.ChildAssociation;
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


}