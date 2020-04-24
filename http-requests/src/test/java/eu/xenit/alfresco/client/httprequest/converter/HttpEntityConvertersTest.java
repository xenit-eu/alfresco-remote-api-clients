package eu.xenit.alfresco.client.httprequest.converter;

import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static org.assertj.core.api.Assertions.assertThat;

import com.budjb.httprequests.HttpEntity;
import com.budjb.httprequests.StreamUtils;
import com.budjb.httprequests.exception.UnsupportedConversionException;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import eu.xenit.alfresco.client.httprequest.converter.jackson.JacksonEntityConverter;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import lombok.Data;
import lombok.experimental.Accessors;
import org.junit.jupiter.api.Test;

class HttpEntityConvertersTest {

    @Test
    public void read() throws IOException, UnsupportedConversionException {
        ObjectMapper objectMapper = new ObjectMapper()
                .configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);

        HttpEntityConverters converters = new HttpEntityConverters(Arrays.asList(
                new JacksonEntityConverter(objectMapper)
        ));

        String json = "{"
                + "  bar: {  value: \"test\" },"
                + "  list: ["
                + "    { value: \"bar\" }"
                + "  ]"
                + "}";
        InputStream stream = new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8));
        HttpEntity httpEntity = new HttpEntity(stream);

        Foo foo = converters.read(Foo.class, httpEntity);
        assertThat(foo.getBar().getValue()).isEqualTo("test");
        assertThat(foo.getList())
                .hasSize(1)
                .hasOnlyOneElementSatisfying(bar -> assertThat(bar.getValue()).isEqualTo("bar"));
    }

    @Test
    public void write() throws IOException, UnsupportedConversionException {
        HttpEntityConverters converters = new HttpEntityConverters(Arrays.asList(
                new JacksonEntityConverter(new ObjectMapper())
        ));

        Foo foo = new Foo()
                .setBar(new Bar().setValue("test"))
                .setList(Collections.singletonList(new Bar().setValue("bar")));

        HttpEntity entity = converters.write(foo);

        String json = new String(StreamUtils.readBytes(entity.getInputStream()));
        assertThatJson(json)
                .isObject()
                .hasEntrySatisfying("bar", bar -> assertThatJson(bar)
                        .isObject()
                        .hasEntrySatisfying("value", v -> assertThat(v).isEqualTo("test")))
                .hasEntrySatisfying("list", list -> {
                    assertThatJson(list).isArray().hasOnlyOneElementSatisfying(element -> {
                        assertThatJson(element)
                                .isObject()
                                .hasEntrySatisfying("value", v -> assertThat(v).isEqualTo("bar"));
                    });
                });
    }

    @Data
    @Accessors(chain = true)
    static class Foo {

        private Bar bar;
        private List<Bar> list;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    @Accessors(chain = true)
    static class Bar {

        private String value;
    }
}