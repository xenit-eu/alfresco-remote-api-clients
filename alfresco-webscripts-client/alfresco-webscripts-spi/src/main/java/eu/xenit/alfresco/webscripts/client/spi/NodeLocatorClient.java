package eu.xenit.alfresco.webscripts.client.spi;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Client for the Alfresco "${host}/alfresco/service/api/nodelocator/" endpoints
 * <p>
 * See: ${host}/alfresco/service/index/package/org/alfresco/repository/nodelocator
 */
public interface NodeLocatorClient {

    @RequiredArgsConstructor
    enum Locator {
        COMPANY_HOME("companyhome");

        @Getter
        private final String value;
    }

    default String getCompanyHome() {
        return get(Locator.COMPANY_HOME);
    }

    default String get(Locator locator) {
        return get(locator.getValue());
    }

    default String get(String locator) {
        return get(locator, Collections.emptyMap());
    }

    default String get(Locator locator, Map<String, List<String>> params) {
        return get(locator.getValue(), params);
    }

    String get(String locatorName, Map<String, List<String>> params);
}
