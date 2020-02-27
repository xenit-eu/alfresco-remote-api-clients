package eu.xenit.alfresco.restapi.client.spi.query;

import java.util.Collection;
import java.util.HashMap;

public interface QueryParameters {

    Params queryParameters();

    class Params extends HashMap<String, String> {

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

        @Override
        public String put(String key, String value) {
            String original = get(key);
            if (original == null || original.isEmpty()) {
                return super.put(key, value);
            }
            return super.put(key, original + "," + value);
        }
    }

}
