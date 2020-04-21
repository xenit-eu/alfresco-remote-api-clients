package eu.xenit.alfresco.webscripts.client.ditto.model;

import static eu.xenit.testing.ditto.api.model.Namespace.createNamespace;
import static eu.xenit.testing.ditto.api.model.QName.createQName;

import eu.xenit.testing.ditto.api.model.Namespace;
import eu.xenit.testing.ditto.api.model.QName;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ModelHelper {

    private List<ModelInfo> defaultModelInfos = new ArrayList<>();

    private Map<String, Namespace> prefixToNamespace = new HashMap<String, Namespace>() {{
        put("cm", createNamespace("http://www.alfresco.org/model/content/1.0", "cm"));
        put("app", createNamespace("http://www.alfresco.org/model/application/1.0", "app"));
        put("sys", createNamespace("http://www.alfresco.org/model/system/1.0", "sys"));
        put("d", createNamespace("http://www.alfresco.org/model/dictionary/1.0", "d"));
        put("st", createNamespace("http://www.alfresco.org/model/site/1.0", "st"));
        put("ver2", createNamespace("http://www.alfresco.org/model/versionstore/2.0", "ver2"));
        put("usr", createNamespace("http://www.alfresco.org/model/user/1.0", "usr"));
    }};

    private Map<String, Function<Serializable, String>> typeToDeserializer = new HashMap<String, Function<Serializable, String>>() {{
        put("d:text", serializable -> (String) serializable);
        put("d:noderef", serializable -> (String) serializable);
        put("d:mltext", Object::toString);
        put("d:date", serializable -> (String) serializable);
        put("d:datetime", serializable -> (String) serializable);
        put("d:long", String::valueOf);
        put("d:int", String::valueOf);
        put("d:qname", Object::toString);
        put("d:locale", serializable -> (String) serializable); // TODO correct?
        put("d:content", Object::toString);
        put("d:boolean", serializable -> Boolean.toString((Boolean) serializable));
    }};


    public ModelHelper() {
        this.initializeDefaults();
    }

    private void initializeDefaults() {
        Namespace cm = getPrefixToNamespaceMap().get("cm");
        Namespace app = getPrefixToNamespaceMap().get("app");
        Namespace sys = getPrefixToNamespaceMap().get("sys");
        Namespace d = getPrefixToNamespaceMap().get("d");
        Namespace st = getPrefixToNamespaceMap().get("st");
        Namespace ver2 = getPrefixToNamespaceMap().get("ver2");
        Namespace usr = getPrefixToNamespaceMap().get("usr");

        setDefaultModelInfos(Arrays.asList(
                createModel(createQName(cm, "description"), createQName(d, "mltext")),
                createModel(createQName(cm, "name"), createQName(d, "text")),
                createModel(createQName(cm, "title"), createQName(d, "mltext")),
                createModel(createQName(cm, "creator"), createQName(d, "text")),
                createModel(createQName(cm, "created"), createQName(d, "datetime")),
                createModel(createQName(cm, "modifier"), createQName(d, "text")),
                createModel(createQName(cm, "modified"), createQName(d, "datetime")),
                createModel(createQName(cm, "content"), createQName(d, "content")),
                createModel(createQName(cm, "versionLabel"), createQName(d, "text")),
                createModel(createQName(cm, "versionType"), createQName(d, "text")),
                createModel(createQName(cm, "initialVersion"), createQName(d, "boolean")),
                createModel(createQName(cm, "autoVersion"), createQName(d, "boolean")),
                createModel(createQName(cm, "autoVersionOnUpdateProps"), createQName(d, "boolean")),
                createModel(createQName(cm, "lockOwner"), createQName(d, "text")),
                createModel(createQName(cm, "lockType"), createQName(d, "text")),
                createModel(createQName(cm, "lockLifetime"), createQName(d, "text")),
                createModel(createQName(cm, "expiryDate"), createQName(d, "date")),
                createModel(createQName(cm, "lockIsDeep"), createQName(d, "boolean")),
                createModel(createQName(cm, "lockAdditionalInfo"), createQName(d, "text")),

                createModel(createQName(app, "icon"), createQName(d, "text")),
                createModel(createQName(sys, "node-dbid"), createQName(d, "long")),
                createModel(createQName(sys, "node-uuid"), createQName(d, "text")),
                createModel(createQName(sys, "store-protocol"), createQName(d, "text")),
                createModel(createQName(sys, "store-identifier"), createQName(d, "text")),
                createModel(createQName(sys, "locale"), createQName(d, "locale")),

                createModel(createQName(st, "sitePreset"), createQName(d, "text")),
                createModel(createQName(st, "siteVisibility"), createQName(d, "text")),
                createModel(createQName(st, "componentId"), createQName(d, "text")),
                createModel(createQName(st, "additionalInformation"), createQName(d, "text")),

                createModel(createQName(ver2, "versionedNodeId"), createQName(d, "text")),
                createModel(createQName(ver2, "assocDbId"), createQName(d, "long")),
                createModel(createQName(ver2, "targetVersionRef"), createQName(d, "noderef")),
                createModel(createQName(ver2, "versionNumber"), createQName(d, "int")),
                createModel(createQName(ver2, "versionLabel"), createQName(d, "text")),
                createModel(createQName(ver2, "description"), createQName(d, "text")),
                createModel(createQName(ver2, "frozenNodeType"), createQName(d, "qname")),
                createModel(createQName(ver2, "frozenAspects"), createQName(d, "qname")),
                createModel(createQName(ver2, "frozenNodeStoreProtocol"), createQName(d, "text")),
                createModel(createQName(ver2, "frozenNodeId"), createQName(d, "text")),
                createModel(createQName(ver2, "frozenNodeDbId"), createQName(d, "long")),
                createModel(createQName(ver2, "frozenCreated"), createQName(d, "datetime")),
                createModel(createQName(ver2, "frozenCreator"), createQName(d, "text")),
                createModel(createQName(ver2, "frozenModified"), createQName(d, "datetime")),
                createModel(createQName(ver2, "frozenModifier"), createQName(d, "text")),
                createModel(createQName(ver2, "frozenAccessed"), createQName(d, "datetime")),
                createModel(createQName(ver2, "versionDescription"), createQName(d, "text"), true),
                createModel(createQName(ver2, "frozenNodeRef"), createQName(d, "text"), true),

                createModel(createQName(usr, "username"), createQName(d, "text")),
                createModel(createQName(usr, "password"), createQName(d, "text")),
                createModel(createQName(usr, "password2"), createQName(d, "text")),
                createModel(createQName(usr, "enabled"), createQName(d, "boolean")),
                createModel(createQName(usr, "accountExpires"), createQName(d, "boolean")),
                createModel(createQName(usr, "accountExpiryDate"), createQName(d, "datetime")),
                createModel(createQName(usr, "credentialsExpire"), createQName(d, "boolean")),
                createModel(createQName(usr, "credentialsExpiryDate"), createQName(d, "datetime")),
                createModel(createQName(usr, "accountLocked"), createQName(d, "boolean")),
                createModel(createQName(usr, "salt"), createQName(d, "text"))
        ));
    }

    private ModelInfo createModel(QName qName, QName type) {
        return createModel(qName, type, false);
    }

    //TODO MV prop support still in progress
    private ModelInfo createModel(QName qName, QName type, boolean isResidual) {
        Objects.requireNonNull(qName);
        Objects.requireNonNull(type);
        Function<Serializable, String> deserializer = typeToDeserializer.get(type.toPrefixString());
        if (deserializer == null) {
            throw new IllegalStateException("No deserializer available for type: " + type.toPrefixString());
        }
        boolean isContent = "d:content".equals(type.toPrefixString());
        boolean isNodeRef = "d:noderef".equals(type.toPrefixString());
        return new ModelInfo(qName, type, type.toString(), deserializer, isContent, isNodeRef, isResidual);

    }

    public List<ModelInfo> getDefaultModelInfos() {
        return defaultModelInfos;
    }

    public void addDefaultModelInfo(ModelInfo modelInfo) {
        defaultModelInfos.add(modelInfo);
    }

    public void setDefaultModelInfos(List<ModelInfo> modelInfos) {
        this.defaultModelInfos = modelInfos;
    }

    public ModelInfo getModelInfoByQName(QName qName) {
        List<ModelInfo> models = getDefaultModelInfos().stream()
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

    public void setPrefixToNamespaceMap(Map<String, Namespace> map) {
        this.prefixToNamespace = map;
    }

    public Map<String, Function<Serializable, String>> getTypeToDeserializerMap() {
        return typeToDeserializer;
    }

    public void setTypeToDeserializerMap(Map<String, Function<Serializable, String>> typeToDeserializer) {
        this.typeToDeserializer = typeToDeserializer;
    }

}
