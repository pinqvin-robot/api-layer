package org.zowe.apiml.gateway.security.login.zosmf;

import io.restassured.config.RestAssuredConfig;
import io.restassured.config.SSLConfig;
import io.restassured.specification.RequestSpecification;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.util.ResourceUtils;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.SSLContext;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.*;
import java.security.cert.CertificateException;

import static io.restassured.RestAssured.given;

@ExtendWith(SpringExtension.class)
public class DummyTest {

    private static RestTemplate restTemplate;
    private static RequestSpecification requestSpecification;

    @BeforeAll
    static void initialSetUp() throws KeyManagementException, NoSuchAlgorithmException, KeyStoreException, CertificateException, UnrecoverableKeyException {
        // get/hard-code client keyPassword
        char[] password = "password".toCharArray();

        // get keystore instance
        KeyStore keystore = KeyStore.getInstance("PKSC12"); // choose appropriate type strore.
        try (FileInputStream in = new FileInputStream(ResourceUtils.getFile("path to client cert"))) {
            // load cert in keystore
            keystore.load(in, password);
        } catch (IOException e) {
            e.printStackTrace();
        }

        final SSLContextBuilder sslContextBuilder = new SSLContextBuilder()
            .setKeyStoreType("PKCS12") // get from client configuration;
            .setProtocol("TLSv1.2") // get from client configuration;
            .loadKeyMaterial(keystore, password);

        // Build the sslContetx and create a custom restTemplate from it.
        final SSLContext sslContext = sslContextBuilder.build();
        final HttpClient client = HttpClients.custom().setSSLContext(sslContext).build();
        final RestTemplateBuilder restTemplateBuilder = new RestTemplateBuilder();
        final ClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory(client);

        // Approach 1
        restTemplate = restTemplateBuilder
            .requestFactory(() -> requestFactory)
            .build();

        /////////////// Approach 2 - use rest Assured. ////////////////
        requestSpecification = given()
            .config(RestAssuredConfig
                .newConfig()
                .sslConfig(new SSLConfig().keyStore("path_to_client_jks_cert", "pwd")));
    }


    /**
     * With a local client certificate.
     */
    // @Test // Commenting these as I don't want test to fail
    public void trustedClientWithX509Certificate() throws URISyntaxException {

        /////////////////////////approach 1//////////////////////////////////
        HttpEntity<?> httpEntity = new HttpEntity<>(null, new HttpHeaders());
        final ResponseEntity<String> exchange = restTemplate
            .exchange(new URI("/gateway/login"), HttpMethod.POST, httpEntity, String.class);
        exchange.getStatusCode();// assert this.
        final String body = exchange.getBody(); // assert body has jwt token.


        //////////////////////////approach 2//////////////////////////////////
        String returnedJWT = requestSpecification
            .auth()
            .certificate("present under resources", "password")
            .post("/login")
            .then()
            .statusCode(HttpStatus.OK.value())
            .extract()
            .asString();

        //assert on returnedJwt
    }

}
