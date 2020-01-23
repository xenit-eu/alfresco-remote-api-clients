package eu.xenit.alfresco.solrapi.client.tests.spring;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import javax.net.ssl.SSLContext;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.util.ResourceUtils;
import org.springframework.util.StringUtils;

public class SolrRequestFactory extends HttpComponentsClientHttpRequestFactory implements ClientHttpRequestFactory {

    public SolrRequestFactory(SolrSslProperties properties) throws GeneralSecurityException, IOException {
        this(properties.getKeystoreType(), properties.getKeystorePath(), properties.getKeystorePassword(),
                properties.getTruststorePath(), properties.getTruststorePassword());
    }

    public SolrRequestFactory(String keyStoreType, String keyStorePath, String keystorePassword, String truststorePath,
            String truststorePassword)
            throws GeneralSecurityException, IOException {

        if (StringUtils.hasText(keyStoreType)
                && StringUtils.hasText(keyStorePath)
                && StringUtils.hasText(truststorePath))
        {
            setHttpClient(HttpClients.custom()
                    .setSSLContext(sslContext(keyStoreType, keyStorePath, keystorePassword, truststorePath, truststorePassword))
                    .setSSLHostnameVerifier(new NoopHostnameVerifier())
                    .build());
        }
    }

    private SSLContext sslContext(String keyStoreType, String keyStorePath, String keystorePassword,
            String truststorePath, String truststorePassword)
            throws CertificateException, NoSuchAlgorithmException, KeyStoreException,
            IOException, UnrecoverableKeyException, KeyManagementException {
        return SSLContextBuilder
                .create()
                .setKeyStoreType(keyStoreType)
                .loadKeyMaterial(
                        loadKeyStore(
                                keyStoreType,
                                keyStorePath,
                                keystorePassword.toCharArray()),
                        keystorePassword.toCharArray())
                .loadTrustMaterial(
                        ResourceUtils
                                .getFile(truststorePath),
                        truststorePassword.toCharArray())
                .build();
    }

    private static KeyStore loadKeyStore(String keystoreType, String file, char[] password)
            throws KeyStoreException, IOException, CertificateException, NoSuchAlgorithmException {

        KeyStore keyStore = KeyStore.getInstance(keystoreType);
        try (InputStream in = new FileInputStream(ResourceUtils.getFile(file))) {
            keyStore.load(in, password);
        }
        return keyStore;
    }
}
