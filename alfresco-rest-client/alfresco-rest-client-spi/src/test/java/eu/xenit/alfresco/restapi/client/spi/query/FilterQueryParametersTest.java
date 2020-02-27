package eu.xenit.alfresco.restapi.client.spi.query;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class FilterQueryParametersTest {

    private FilterQueryParameters parameters;

    @BeforeEach
    void reset() {
        parameters = new FilterQueryParameters();
    }

    @Test
    void whereIsFile() {
        parameters.whereIsFile(true);
        assertThat(parameters.calculateWhereString()).isEqualTo("(isFile=true)");
    }

    @Test
    void whereIsFolder() {
        parameters.whereIsFolder(false);
        assertThat(parameters.calculateWhereString()).isEqualTo("(isFolder=false)");
    }

    @Test
    void whereNodeType() {
        parameters.whereNodeType("my:specialNodeType");
        assertThat(parameters.calculateWhereString()).isEqualTo("(nodeType='my:specialNodeType')");
    }

    @Test
    void whereNodeType_includeSubTypes() {
        parameters.whereNodeType("my:specialNodeType INCLUDESUBTYPES");
        assertThat(parameters.calculateWhereString()).isEqualTo("(nodeType='my:specialNodeType INCLUDESUBTYPES')");
    }

    @Test
    void whereIsPrimary() {
        parameters.whereIsPrimary(true);
        assertThat(parameters.calculateWhereString()).isEqualTo("(isPrimary=true)");
    }

    @Test
    void whereAssocType() {
        parameters.whereAssocType("my:specialAssocType");
        assertThat(parameters.calculateWhereString()).isEqualTo("(assocType='my:specialAssocType')");
    }

    @Test
    void whereIsPrimary_And_whereAssocType() {
        parameters.whereIsPrimary(false);
        parameters.whereAssocType("my:specialAssocType");
        assertThat(parameters.calculateWhereString())
                .isEqualTo("(isPrimary=false and assocType='my:specialAssocType')");
    }

}