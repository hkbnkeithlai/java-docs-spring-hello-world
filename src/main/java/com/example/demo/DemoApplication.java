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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.IOException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;

@SpringBootApplication
@RestController
public class DemoApplication {

	@Value("${MULE_BASIC_TOKEN}")
	private String token;

	@Value("${MULEAPI_ENDPOINT}")
	private String muleapiUrl;
	Logger logger = LogManager.getLogger(DemoApplication.class);
	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}

	@RequestMapping("/")
	String sayHello() {
		return "Hello World!";
	}
	@RequestMapping("/mule")
	public String callHealtCheckAPI() throws Exception {

		String response = "";
		String result = "";
		//	HttpClient httpClient ;
		Header oauthHeader = new BasicHeader("Authorization", "Basic " + token);
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
			HttpGet createHttpGet = new HttpGet(muleapiUrl);
			createHttpGet.addHeader(oauthHeader);
			createHttpGet.addHeader(prettyPrintHeader);
			//httpClient = HttpClientBuilder.create().build();
			SSLContext sc = SSLContext.getInstance("SSL");
			sc.init(null, trustAllCerts, new SecureRandom());
			CloseableHttpClient httpClient = HttpClients.custom().setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE).setSSLContext(sc).build();

			HttpResponse upsertResponse = httpClient.execute(createHttpGet);
			response = EntityUtils.toString(upsertResponse.getEntity());
			result = response ;
			//result = response + " " + properties.getMessage();
			//	logger.info(properties.getMessage());
			logger.info(response);
		} catch (IOException e) {
			result = e.getMessage();
			e.printStackTrace();
		}
		return result ;
	}
}
