package otp;

import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.HttpClientBuilder;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import amazon.CommonFunctions;

public class OTP extends CommonFunctions implements OTP_Strings {

	public Map<String, String> getNumber(String key) {

		Map<String, String> res = new HashMap<>();

		try {
			HttpResponse response;
			HttpClient client = HttpClientBuilder.create().build();
			String url = getNumber.replace("{key}", key).replace("{service}", "amazon");
			log.debug("Get number URL - " + url);
			HttpUriRequest httpUriRequest = new HttpGet(url);
			response = client.execute(httpUriRequest);
			String JSONString = response.toString();
			log.debug("Get number URL response - " + JSONString);
			JSONObject jsonObject = (JSONObject) JSONValue.parse(JSONString);
			res.put("number", (String) jsonObject.get("mo"));
			res.put("id", (String) jsonObject.get("id"));
		} catch (Exception e) {
			log.error("Failed to get number", e);
			res.put("failure", "Failed to get number");
		}

		return res;
	}

	public void getOTP() {

	}

	public void cancelNumber() {

	}

}
