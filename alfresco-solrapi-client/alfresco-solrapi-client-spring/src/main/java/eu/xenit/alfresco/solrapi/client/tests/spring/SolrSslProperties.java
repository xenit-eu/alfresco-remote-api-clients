package eu.xenit.alfresco.solrapi.client.tests.spring;


import lombok.Data;

@Data
public class SolrSslProperties {

    private String keystoreType = "JCEKS";
    private String keystorePath = "classpath:ssl.repo.client.keystore";
    private String keystorePassword = "kT9X6oe68t";
    private String truststorePath = "classpath:ssl.repo.client.truststore";
    private String truststorePassword = "kT9X6oe68t";

}
