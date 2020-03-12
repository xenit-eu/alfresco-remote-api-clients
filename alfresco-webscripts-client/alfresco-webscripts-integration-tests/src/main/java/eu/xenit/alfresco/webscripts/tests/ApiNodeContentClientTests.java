package eu.xenit.alfresco.webscripts.tests;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import eu.xenit.alfresco.webscripts.client.spi.ApiNodeContentClient;
import eu.xenit.alfresco.webscripts.client.spi.NodeLocatorClient;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import org.junit.jupiter.api.Test;

public interface ApiNodeContentClientTests {

    // Fixed NodeRef in bootstrapped Alfresco:
    // '/app:company_home/st:sites/cm:swsdp/cm:documentLibrary/cm:Budget_x0020_Files/cm:budget.xls'
    String FIXED_NODE_UUID_BUDGET_XLS = "5fa74ad3-9b5b-461b-9df5-de407f1f4fe7";
    String FIXED_NODEREF_BUDGET_XLS = "workspace://SpacesStore/" + FIXED_NODE_UUID_BUDGET_XLS;

    ApiNodeContentClient apiNodeContentClient();

    NodeLocatorClient nodeLocatorClient();

    @Test
    default void testCompanyHome() {
        String companyHomeNodeRef = nodeLocatorClient().getCompanyHome();

        assertThat(apiNodeContentClient().hasContent(companyHomeNodeRef)).isFalse();

        assertThrows(RuntimeException.class, () -> {
            try (OutputStream outputStream = new ByteArrayOutputStream()) {
                apiNodeContentClient().getContent(companyHomeNodeRef, outputStream);
            }
        });
    }

    @Test
    default void testBudgedXls() throws IOException {
        assertThat(apiNodeContentClient().hasContent(FIXED_NODEREF_BUDGET_XLS)).isTrue();

        String data;
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            apiNodeContentClient().getContent(FIXED_NODEREF_BUDGET_XLS, outputStream);
            data = new String(outputStream.toByteArray());
        }
        assertThat(data).isNotBlank();

        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            apiNodeContentClient().getContent(FIXED_NODEREF_BUDGET_XLS, "cm:content", outputStream);
            data = new String(outputStream.toByteArray());
        }
        assertThat(data).isNotBlank();
    }

}
