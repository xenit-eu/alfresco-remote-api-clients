### Instructions for curl

Alfresco protects this API with mTLS. 

This means that if you want to follow along with `curl`, you need to have the TLS-client-certificate in _.pem_ format.

This project contains a keystore with a TLS-client-certificate that a default Alfresco system accepts.

Export the certificate from the Java keystore and import it to a new _PKCS#12 keystore_ format using the Java `keytool` 

```
keytool -importkeystore \
    -srckeystore ./alfresco-solrapi-client-spring/src/main/resources/ssl.repo.client.keystore \
    -srcstorepass kT9X6oe68t -srcstoretype JCEKS -srcalias ssl.repo \
    -destkeystore alfresco-client.p12 -deststoretype pkcs12 \
    -destalias ssl.repo -destkeypass alfresco -deststorepass alfresco
```

Convert the new _PKCS#12_ file (alfresco-client.p12) to PEM using `openssl`

```
openssl pkcs12 -in alfresco-client.p12 -out alfresco-client.pem -nodes -password pass:alfresco
```

Validate if the certificate is valid:

```
curl -k -E alfresco-client.pem  https://localhost:8443/alfresco/service/api/solr/transactions?maxTxnId=2
```