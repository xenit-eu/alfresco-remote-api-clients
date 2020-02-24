package eu.xenit.alfresco.webscripts.client.spi.model;

import static eu.xenit.testing.ditto.api.model.Namespace.createNamespace;
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

public class ModelHelper {

    private final List<ModelInfo> defaults = new ArrayList<>();

    private final Map<String, Namespace> prefixToNamespace = new HashMap<String, Namespace>() {{
        put("cm", createNamespace("http://www.alfresco.org/model/content/1.0", "cm"));
        put("app", createNamespace("http://www.alfresco.org/model/application/1.0", "app"));
        put("sys", createNamespace("http://www.alfresco.org/model/system/1.0", "sys"));
        put("d",   createNamespace("http://www.alfresco.org/model/dictionary/1.0", "d"));
    }};

    private final Map<String, Function<Serializable, String>> typeToDeserializer = new HashMap<String, Function<Serializable, String>>() {{
        put("d:text", serializable -> (String) serializable);
        put("d:mltext", Object::toString);
        put("d:datetime", serializable -> (String) serializable);
        put("d:long", String::valueOf);
        put("d:locale", serializable -> (String) serializable); // TODO correct?
    }};


    public ModelHelper() {
        this.initializeDefaults();
    }

    private void initializeDefaults() {
        Namespace cm = getPrefixToNamespaceMap().get("cm");
        Namespace app = getPrefixToNamespaceMap().get("app");
        Namespace sys = getPrefixToNamespaceMap().get("sys");
        Namespace d = getPrefixToNamespaceMap().get("d");

        defaults.add(createModel(createQName(cm, "description"), createQName(d, "mltext")));
        defaults.add(createModel(createQName(cm, "name"), createQName(d, "text")));
        defaults.add(createModel(createQName(cm, "title"), createQName(d, "mltext")));
        defaults.add(createModel(createQName(cm, "creator"), createQName(d, "text")));
        defaults.add(createModel(createQName(cm, "created"), createQName(d, "datetime")));
        defaults.add(createModel(createQName(cm, "modifier"), createQName(d, "text")));
        defaults.add(createModel(createQName(cm, "modified"), createQName(d, "datetime")));
        defaults.add(createModel(createQName(app, "icon"), createQName(d, "text")));
        defaults.add(createModel(createQName(sys, "node-dbid"), createQName(d, "long")));
        defaults.add(createModel(createQName(sys, "node-uuid"), createQName(d, "text")));
        defaults.add(createModel(createQName(sys, "store-protocol"), createQName(d, "text")));
        defaults.add(createModel(createQName(sys, "store-identifier"), createQName(d, "text")));
        defaults.add(createModel(createQName(sys, "locale"), createQName(d, "locale")));
    }

    private ModelInfo createModel(QName qName, QName type) {
        return createModel(qName, type, false);
    }

    //TODO MV prop support still in progress
    private ModelInfo createModel(QName qName, QName type, boolean isResidual) {
        Objects.requireNonNull(qName);
        Objects.requireNonNull(type);
        Function<Serializable, String> deserializer = typeToDeserializer.get(type.toPrefixString());
        Objects.requireNonNull(deserializer);
        boolean isContent = "d:content".equals(type.toPrefixString());
        boolean isNodeRef = "d:noderef".equals(type.toPrefixString());
        return new ModelInfo(qName, type, type.toString(), deserializer, isContent, isNodeRef, isResidual);

    }

    public List<ModelInfo> getDefaults() {
        return defaults;
    }

    public ModelInfo getByQName(QName qName) {
        List<ModelInfo> models = defaults.stream()
                .filter(modelInfo -> hasQName(modelInfo, qName))
                .collect(Collectors.toList());
        if (models.size() < 1) {
            throw new IllegalStateException("Found no PropertyModel matching '" + qName + "'");
        }
        if (models.size() > 1) {
            throw new IllegalStateException("Found more than 1 PropertyModel matching '" + qName + "'");
        }
        return models.get(0);
    }

    private boolean hasQName(ModelInfo modelInfo, QName qName) {
        return modelInfo.getQName().toPrefixString().equals(qName.toPrefixString()) &&
                modelInfo.getQName().toString().equals(qName.toString());
    }

    public Map<String, Namespace> getPrefixToNamespaceMap() {
        return prefixToNamespace;
    }

}
