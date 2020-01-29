package eu.xenit.alfresco.webscripts.client.spi;

import lombok.Getter;

import java.util.Map;

public interface NodeLocatorClient {
    public enum Locator {
        COMPANY_HOME("companyhome");

        @Getter
        private final String value;

        Locator(String value) {
            this.value = value;
        }
    }

    default String getCompanyHomeNodeRef() {
        return get(Locator.COMPANY_HOME);
    }

    default String get(Locator locator) {
        return get(locator.getValue());
    }

    default String get(Locator locator, Map<String, String> params) {
        return get(locator.getValue(), params);
    }

    String get(String locatorName);
    String get(String locatorName, Map<String, String> params);
}
