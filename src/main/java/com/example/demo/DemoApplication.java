package com.example.demo;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.entity.StringEntity;
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

	@Value("${DEVELOPMENT_SLOT}")
	private String devslot;
	@Value("${MULEAPI_ENDPOINT}")
	private String muleapiUrl;

	Logger logger = LogManager.getLogger(DemoApplication.class);
	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}

	@RequestMapping("/")
	String sayHello() {
		return "Read Azure application setting <br> muleapiurl: "
 				+ muleapiUrl
				+ "<br>Development Slot: " + devslot
				+ "<br><a href='/mule'>click here to test</a>"
				;
	}
	@RequestMapping("/mule")
	public String callHealtCheckAPI() throws Exception {
		logger.info("/mule");
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

			SSLContext sc = SSLContext.getInstance("SSL");
			sc.init(null, trustAllCerts, new SecureRandom());

		//	HttpGet createHttpGet = new HttpGet(muleapiUrl);
			HttpPost httpPost = new HttpPost(muleapiUrl);
			httpPost.addHeader(oauthHeader);
			httpPost.addHeader(prettyPrintHeader);
			httpPost.setEntity(new StringEntity("{\n" +
					"    \"action\": \"SELECT\",\n" +
					"    \"parameter\": {\n" +
					"        \"pps\": \"000582142\",\n" +
					"        \"start\": 0,\n" +
					"        \"size\": 100\n" +
					"    }\n" +
					"}", "UTF-8"));
			httpPost.setHeader("Accept", "application/json");
			httpPost.setHeader("Content-type", "application/json; charset=UTF-8");

			//httpClient = HttpClientBuilder.create().build();

			CloseableHttpClient httpClient = HttpClients.custom().setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE).setSSLContext(sc).build();


			HttpResponse upsertResponse = httpClient.execute(httpPost);
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
