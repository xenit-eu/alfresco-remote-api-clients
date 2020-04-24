package eu.xenit.alfresco.client.httprequest.converter;

import com.budjb.httprequests.HttpEntity;
import com.budjb.httprequests.converter.EntityConverter;
import com.budjb.httprequests.converter.EntityConverterManager;
import com.budjb.httprequests.exception.UnsupportedConversionException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpEntityConverters extends EntityConverterManager {

    /**
     * Logger.
     */
    private final Logger log = LoggerFactory.getLogger(EntityConverterManager.class);

    public HttpEntityConverters(List<EntityConverter> converters) {
        super(converters);
    }

    /**
     * Reads an object from the given entity {@link InputStream}.
     *
     * @param type   Object type to attempt conversion to.
     * @param entity Entity input stream.
     * @param <T>    Generic type of the method call.
     * @return The converted object.
     * @throws UnsupportedConversionException when there are no entity writers that support the object type.
     * @throws IOException                    When an IO exception occurs.
     */
    @SuppressWarnings("unchecked")
    public <T> T read(Class<?> type, HttpEntity entity) throws UnsupportedConversionException, IOException {
        for (HttpEntityReader reader : this.getHttpEntityReader()) {
            if (reader.canRead(type, entity.getContentType())) {
                return (T) reader.read(type, entity);
            }
        }

        return super.read(type, entity);
    }

    /**
     * Returns the list of all registered entity readers.
     *
     * @return The list of all registered entity readers.
     */
    public List<HttpEntityReader> getHttpEntityReader() {
        return this.getAll().stream()
                .filter(HttpEntityReader.class::isInstance)
                .map(HttpEntityReader.class::cast)
                .collect(Collectors.toList());
    }
}
