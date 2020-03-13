package eu.xenit.alfresco.solrapi.client.spring.http;

import eu.xenit.alfresco.solrapi.client.spring.model.SolrSslProperties;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.Objects;
import javax.net.ssl.SSLContext;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.springframework.core.io.ClassPathResource;
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
                && StringUtils.hasText(truststorePath)) {
            setHttpClient(HttpClients.custom()
                    .setSSLContext(sslContext(keyStoreType, keyStorePath, keystorePassword, truststorePath,
                            truststorePassword))
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
                        getResourceURL(truststorePath),
                        truststorePassword.toCharArray())
                .build();
    }

    private static KeyStore loadKeyStore(String keystoreType, String file, char[] password)
            throws KeyStoreException, IOException, CertificateException, NoSuchAlgorithmException {

        KeyStore keyStore = KeyStore.getInstance(keystoreType);
        try (InputStream in = getResourceURL(file).openStream()) {
            keyStore.load(in, password);
        }
        return keyStore;
    }

    private static URL getResourceURL(String resource) {
        Objects.requireNonNull(resource, "Argument 'resource' is required");

        // This should resolve the issue where the embedded keystore & truststore are
        // not found on the classpath using ResourceUtils.getFile(), because the current thread has a different classloader

        if (resource.startsWith("classpath:")) {
            String path = resource.substring("classpath:".length());
            ClassPathResource cpResource = new ClassPathResource(path, SolrRequestFactory.class.getClassLoader());
            if (cpResource.exists()) {
                try {
                    return cpResource.getURL();
                } catch (IOException e) {
                    throw new UncheckedIOException(e);
                }
            }
        }

        // Fallback to normal ResourceUtils.getUrl(resource)
        try {
            return ResourceUtils.getURL(resource);
        } catch (FileNotFoundException e) {
            throw new UncheckedIOException(e);
        }
    }
}
