package com.example.demo;

import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.microsoft.applicationinsights.core.dependencies.google.gson.Gson;
import com.microsoft.applicationinsights.core.dependencies.google.gson.GsonBuilder;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.IOException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.Map;

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
				+ "<br>timestamp: 1"
				+ "<br><a href='/mule'>click here to test</a>"
				+ "<br><form method=\"post\" action=\"/unsubscribe/hkbnes\">"
				+"<label for=\"firstname\">First name:</label>"
				+"<input type=\"text\" name=\"firstname\" /><br />"
				+" <label for=\"lastname\">Last name:</label>"
				+"<input type=\"text\" name=\"lastname\" /><br />"
				+"<input type=\"submit\" />"
				+"</form>"
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



	@RestController
	public class UnsubscribeController {

		@PostMapping("/unsubscribe/hkbnes")
		public void unsubscribe(@RequestBody String payload) {
			logger.info("Received payload: " + payload);
			System.out.println("Received payload: " + payload);
		}
	}

	@Autowired
	private ObjectMapper objectMapper;

	@GetMapping("/t")
	public String test() throws Exception {

		class RestResp {

			public ResponseEntity<?> data = null;
		}


		String result = "{\"name\":\"Alex\"}";
		ObjectMapper mapper = new ObjectMapper();
		JsonNode node = mapper.readTree(result);

		RestResp response = new RestResp();
		response.data = ResponseEntity.ok().body(node);
		Gson gson = new GsonBuilder().setPrettyPrinting().serializeNulls().create();
		String jsonobject = gson.toJson(node);



		return jsonobject;

	}


	@GetMapping("/web2case")
	public String web2case() throws Exception {

		return "\n" +
			"<META HTTP-EQUIV=\"Content-type\" CONTENT=\"text/html; charset=UTF-8\">\n" +
			"<script src=\"https://www.google.com/recaptcha/api.js\"></script>\n" +
			"<script>\n" +
			" function timestamp() { var response = document.getElementById(\"g-recaptcha-response\"); if (response == null || response.value.trim() == \"\") {var elems = JSON.parse(document.getElementsByName(\"captcha_settings\")[0].value);elems[\"ts\"] = JSON.stringify(new Date().getTime());document.getElementsByName(\"captcha_settings\")[0].value = JSON.stringify(elems); } } setInterval(timestamp, 500); \n" +
			"</script>\n" +
			"\n" +
			"<!--  ----------------------------------------------------------------------  -->\n" +
			"<!--  NOTE: Please add the following <FORM> element to your page.             -->\n" +
			"<!--  ----------------------------------------------------------------------  -->\n" +
			"\n" +
			"<form action=\"https://hkbn--escrmuat.sandbox.my.salesforce.com/servlet/servlet.WebToCase?encoding=UTF-8&orgId=00D0k000000EJLl\" method=\"POST\">\n" +
			"\n" +
			"<input type=hidden name='captcha_settings' value='{\"keyname\":\"googlerecaptchakey_esaio\",\"fallback\":\"true\",\"orgId\":\"00D0k000000EJLl\",\"ts\":\"\"}'>\n" +
			"<input type=hidden name=\"orgid\" value=\"00D0k000000EJLl\">\n" +
			"<input type=hidden name=\"retURL\" value=\"http://demoapp.itpoc.dev\">\n" +
			"\n" +
			"<label for=\"name\">Contact Name</label><input  id=\"name\" maxlength=\"80\" name=\"name\" size=\"20\" type=\"text\" /><br>\n" +
			"\n" +
			"<label for=\"email\">Email</label><input  id=\"email\" maxlength=\"80\" name=\"email\" size=\"20\" type=\"text\" /><br>\n" +
			"\n" +
			"<label for=\"phone\">Phone</label><input  id=\"phone\" maxlength=\"40\" name=\"phone\" size=\"20\" type=\"text\" /><br>\n" +
			"\n" +
			"<label for=\"subject\">Subject</label><input  id=\"subject\" maxlength=\"80\" name=\"subject\" size=\"20\" type=\"text\" /><br>\n" +
			"\n" +
			"<label for=\"description\">Description</label><textarea name=\"description\"></textarea><br>\n" +
			"\n" +
			"<input type=\"hidden\"  id=\"external\" name=\"external\" value=\"1\" /><br>\n" +
			"\n" +
			"<div class=\"g-recaptcha\" data-sitekey=\"6Ldlv6QpAAAAAERvh9vPWR9js9RZu-5NwUgZeLvg\"></div><br>\n" +
			"<input type=\"submit\" name=\"submit\">\n" +
			"\n" +
			"</form>\n";


	
	}
	
	@GetMapping("/presales/{hkbnes_presales_id}")
	public String getPreSalesRequest(@PathVariable Long hkbnes_presales_id) throws StreamReadException, DatabindException, IOException {
		String json = "{\r\n" +
				"    \"htcl_presales_id\": \"20230119N\",\r\n" +
				"    \"htcl_sales_email\": \"sales@example.com\",\r\n" +
				"    \"htcl_sales_name\": \"John Doe\",\r\n" +
				"    \"htcl_sales_contact\": \"12345678\",\r\n" +
				"    \"address_code\": \"123456\",\r\n" +
				"    \"flat_room_unit\": \"Unit 123\",\r\n" +
				"    \"floor\": \"5\",\r\n" +
				"    \"block\": \"A\",\r\n" +
				"    \"building_name\": \"ABC Building\",\r\n" +
				"    \"street\": \"Main Street\",\r\n" +
				"    \"area\": \"Central\",\r\n" +
				"    \"district\": \"Hong Kong\",\r\n" +
				"    \"service\": \"Broadband\",\r\n" +
				"    \"plan_code\": \"BB1GDIP\",\r\n" +
				"    \"plan_desc\": \"1000Mbps (1 Dynamic IP)\",\r\n" +
				"    \"end_user_name\": \"XYZ Company\",\r\n" +
				"    \"site_contact_person\": \"Jane Smith\",\r\n" +
				"    \"site_contact_number\": \"98765432\",\r\n" +
				"    \"site_visit_date\": \"2023-10-20\",\r\n" +
				"    \"site_visit_time_slot\": \"AM (09:00-12:30)\",\r\n" +
				"    \"htcl_remarks\": \"Special request for additional equipment\",\r\n" +
				"    \"htcl_request_status\": \"Submitted\",\r\n" +
				"    \"hkbnes_address_code\": \"123456\",\r\n" +
				"    \"hkbnes_building_name\": \"ABC Building\",\r\n" +
				"    \"hkbnes_street\": \"Main Street\",\r\n" +
				"    \"hkbnes_area\": \"Central\",\r\n" +
				"    \"hkbnes_district\": \"Hong Kong\",\r\n" +
				"    \"hkbnes_remark\": \"Additional notes from HKBN\",\r\n" +
				"    \"hkbnes_under_coverage\": true,\r\n" +
				"    \"hkbnes_site_visit\": false,\r\n" +
				"    \"hkbnes_additional_charge\": null,\r\n" +
				"    \"hkbnes_status\": \"Open\",\r\n" +
				"    \"create_datetime\": \"2023-10-19 10:00:00\",\r\n" +
				"    \"last_modified_datetime\": \"2023-10-19 14:30:00\"\r\n" +
				"}";

		Gson gson = new GsonBuilder().setPrettyPrinting().serializeNulls().create();
		String jsonobject = gson.toJson(objectMapper.readValue(json, Map.class));


		return jsonobject;
		//return ResponseEntity.ok().body(RestResponse.success(formService.getExistingLineDetail(cusPlanCode)));
	}


}
