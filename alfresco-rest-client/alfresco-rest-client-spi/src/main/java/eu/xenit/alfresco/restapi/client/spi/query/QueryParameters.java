package eu.xenit.alfresco.restapi.client.spi.query;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

public interface QueryParameters {

    Params queryParameters();

    class Params extends HashMap<String, List<String>> {

        void putIfNonEmpty(String key, String value) {
            if (value == null || value.trim().isEmpty()) {
                return;
            }
            put(key, value);
        }

        void putIfNonEmpty(String key, Collection<String> values) {
            if (values == null || values.isEmpty()) {
                return;
            }
            values.forEach(value -> this.put(key, value));
        }

        void put(String key, String value) {
            this.putIfAbsent(key, new ArrayList<>());
            this.get(key).add(value);
        }

    }

}
