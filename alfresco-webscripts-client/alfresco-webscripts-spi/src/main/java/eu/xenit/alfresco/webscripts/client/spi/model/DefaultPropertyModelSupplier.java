package eu.xenit.alfresco.webscripts.client.spi.model;

import static eu.xenit.testing.ditto.api.model.QName.createQName;

import eu.xenit.testing.ditto.api.model.Namespace;
import eu.xenit.testing.ditto.api.model.QName;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

public class DefaultPropertyModelSupplier {

    private static final Map<String, Function<Serializable, String>> typeToDeserializer = new HashMap<String, Function<Serializable, String>>() {{
        put("d:text", serializable ->  (String) serializable);
        put("d:mltext", Object::toString);
        put("d:datetime", serializable -> (String) serializable);
        put("d:long", String::valueOf);
        put("d:locale", serializable -> (String) serializable); // TODO correct?
    }};

    private List<PropertyModel> defaults = new ArrayList<>();

    public DefaultPropertyModelSupplier() {
        this.initializeDefaults();
    }

    private void initializeDefaults() {
        Namespace cm = Namespace.createNamespace("http://www.alfresco.org/model/content/1.0", "cm");
        Namespace app = Namespace.createNamespace("http://www.alfresco.org/model/application/1.0", "app");
        Namespace sys = Namespace.createNamespace("http://www.alfresco.org/model/system/1.0", "sys");
        Namespace d = Namespace.createNamespace("http://www.alfresco.org/model/dictionary/1.0", "d");

        defaults.add(createModel(createQName(cm, "description"),createQName(d,"mltext")));
        defaults.add(createModel(createQName(cm, "name"),createQName(d,"text")));
        defaults.add(createModel(createQName(cm, "title"),createQName(d,"mltext")));
        defaults.add(createModel(createQName(cm, "creator"),createQName(d,"text")));
        defaults.add(createModel(createQName(cm, "created"),createQName(d,"datetime")));
        defaults.add(createModel(createQName(cm, "modifier"),createQName(d,"text")));
        defaults.add(createModel(createQName(cm, "modified"),createQName(d,"datetime")));
        defaults.add(createModel(createQName(app, "icon"),createQName(d,"text")));
        defaults.add(createModel(createQName(sys, "node-dbid"),createQName(d,"long")));
        defaults.add(createModel(createQName(sys, "node-uuid"),createQName(d,"text")));
        defaults.add(createModel(createQName(sys, "store-protocol"),createQName(d,"text")));
        defaults.add(createModel(createQName(sys, "store-identifier"),createQName(d,"text")));
        defaults.add(createModel(createQName(sys, "locale"),createQName(d,"locale")));
    }

    //TODO booleans
    private PropertyModel createModel(QName qName, QName type) {
        Function<Serializable, String> deserializer = typeToDeserializer.get(type.toPrefixString());
        Objects.requireNonNull(deserializer);
        return new PropertyModel(qName,type,type.toString(),deserializer,false,false, false, false);

    }

    public List<PropertyModel> getDefaults() {
        return defaults;
    }

    public PropertyModel getByQName(QName qName) {
        List<PropertyModel> models = defaults.stream()
                .filter(propertyModel -> hasQName(propertyModel, qName))
                .collect(Collectors.toList());
        if (models.size() < 1) {
            throw new IllegalStateException("Found no PropertyModel matching '" + qName + "'");
        }
        if (models.size() > 1) {
            throw new IllegalStateException("Found more than 1 PropertyModel matching '" + qName + "'");
        }
        return models.get(0);
    }

    private boolean hasQName(PropertyModel propertyModel, QName qName) {
        return propertyModel.getQName().toPrefixString().equals(qName.toPrefixString()) &&
                propertyModel.getQName().toString().equals(qName.toString());
    }
}
