package com.example.demo;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.IOException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;

@RestController
public class MuleRestController {
    private final MessageProperties properties;
    Logger logger = LogManager.getLogger(DemoApplication.class);

    public MuleRestController(MessageProperties properties) {
        this.properties = properties;
    }

    @GetMapping
    public String getMessage() {
        return "Message: " + properties.getMessage();
    }



    @RequestMapping("/mule")
    public String callHealtCheckAPI() throws Exception {

        String response = "";
        String result = "";
        //	HttpClient httpClient ;
        Header oauthHeader = new BasicHeader("Authorization", "Basic YTNiOGM1YWE4NDNhNDdmMzk3MDU4YzI0ZGE5NWU0YTk6RjU3YWU4NjQ0YzllNDZlNjlhMjA2RUNlODhkMjA4NzM=");
        Header prettyPrintHeader = new BasicHeader("X-PrettyPrint", "1");
        try {

            TrustManager[] trustAllCerts = new TrustManager[] {
                    new X509TrustManager() {
                        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                            return null;
                        }
                        public void checkClientTrusted(X509Certificate[] certs, String authType) {  }

                        public void checkServerTrusted(X509Certificate[] certs, String authType) {  }
                    }
            };
            HttpGet createHttpGet = new HttpGet("https://192.168.237.80/api/sys/healthcheck/v1/healthcheck");
            createHttpGet.addHeader(oauthHeader);
            createHttpGet.addHeader(prettyPrintHeader);
            //httpClient = HttpClientBuilder.create().build();
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new SecureRandom());
            CloseableHttpClient httpClient = HttpClients.custom().setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE).setSSLContext(sc).build();

            HttpResponse upsertResponse = httpClient.execute(createHttpGet);
            response = EntityUtils.toString(upsertResponse.getEntity());
            result = response + " " + properties.getMessage();
            logger.info(properties.getMessage());
            logger.info(response);
        } catch (IOException e) {
            result = e.getMessage();
            e.printStackTrace();
        }
        return result ;
    }
}