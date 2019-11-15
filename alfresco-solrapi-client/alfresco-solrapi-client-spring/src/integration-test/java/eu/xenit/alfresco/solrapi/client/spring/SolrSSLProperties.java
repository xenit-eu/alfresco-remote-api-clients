package eu.xenit.alfresco.solrapi.client.spring;


import lombok.Data;

@Data
public class SolrSSLProperties {

    private String keystoreType = "JCEKS";
    private String keystorePath;
    private String keystorePassword;
    private String truststorePath;
    private String truststorePassword;



}
