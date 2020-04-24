package eu.xenit.alfresco.client.httprequest.converter.jackson;

import com.budjb.httprequests.HttpEntity;
import com.budjb.httprequests.converter.EntityWriter;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import eu.xenit.alfresco.client.httprequest.converter.HttpEntityReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.concurrent.atomic.AtomicReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JacksonEntityConverter implements HttpEntityReader, EntityWriter {

    private final Logger logger = LoggerFactory.getLogger(JacksonEntityConverter.class);

    /**
     * Jackson object mapper.
     */
    private final ObjectMapper objectMapper;

    /**
     * Constructor.
     *
     * @param objectMapper Jackson object mapper.
     */
    public JacksonEntityConverter(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean canRead(Class<?> clazz, String contentType) {
        // TODO check content-type
        // currently contextClass is ignored: if we run into blocking issues with generic types,
        // we should look at SpringFramework's `GenericTypeResolver.resolveType(type, contextClass)`
        TypeFactory typeFactory = this.objectMapper.getTypeFactory();
        JavaType javaType = typeFactory.constructType(clazz);
        AtomicReference<Throwable> causeRef = new AtomicReference<>();
        if (this.objectMapper.canDeserialize(javaType, causeRef)) {
            return true;
        }

        logReason(javaType, false, causeRef.get());

        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public <T> T read(Class<? extends T> clazz, HttpEntity entity) throws IOException {
        TypeFactory typeFactory = this.objectMapper.getTypeFactory();
        JavaType javaType = typeFactory.constructType(clazz);
        return (T) this.objectMapper.readValue(entity.getInputStream(), javaType);
    }

    /**
     * Determine whether to log the given exception coming from a {@link ObjectMapper#canDeserialize} / {@link
     * ObjectMapper#canSerialize} check.
     *
     * @param type the class that Jackson tested for (de-)serializability
     * @param serialize true when trying to serialize an object, false when trying to deserialize
     * @param cause the Jackson-thrown exception to evaluate (typically a {@link JsonMappingException})
     */
    protected void logReason(Type type, boolean serialize, Throwable cause) {

        logger.debug("Jackson cannot {} type {}, reason: {}",
                serialize ? "serialize" : "deserialize",
                type,
                cause != null ? cause.toString() : "unknown");
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public String getContentType() {
        return "application/json";
    }

    @Override
    public boolean supports(Class<?> type) {
        // if the target mime-type is already set, we would like to check we're supporting that at all ...
        AtomicReference<Throwable> causeRef = new AtomicReference<>();
        if (this.objectMapper.canSerialize(type, causeRef)) {
            return true;
        }

        logReason(type, true, causeRef.get());

        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public InputStream write(Object entity, String characterSet) throws Exception {
        return new ByteArrayInputStream(objectMapper.writeValueAsBytes(entity));
    }
}
