package util;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

public class json {
	private ConnectToServer inputStream;
	private static int index;
	static json instance;

	public json() {
		init();
	}

	public static json getInstance() {
		instance = new json();
		return instance;
	}

	public void init() {
		index = 0;
		inputStream = new ConnectToServer();
	}

	public static JSONArray JsonRequest(String domain, boolean isPost, Context context) {

		Log.i("URL ", "URL :" + domain);
		try {
			index = 0;
			String[] temp_arr = null;
			String postParameterData = null;
			boolean flag = false;

			if (domain.indexOf("?") != -1) {
				temp_arr = domain.split("\\?");
				domain = temp_arr[0];
				postParameterData = temp_arr[1];
				flag = true;
			}

			URL url;
			url = new URL(domain);
			Log.i("domain", "domain: " + domain);
			InputStream instream = null;
			Map<String, List<String>> headers = null;

			HttpURLConnection http = null;

			if (url.getProtocol().toLowerCase().equals("https")) {
				trustAllHosts();
				HttpsURLConnection https = (HttpsURLConnection) url
						.openConnection();
				https.setHostnameVerifier(DO_NOT_VERIFY);
				http = https;
			} else {
				http = (HttpURLConnection) url.openConnection();
			}

			http.setDefaultUseCaches(false);
			http.setDoInput(true);
			http.setDoOutput(true);

			if (isPost) {
				http.setRequestMethod("POST");
			} else {
				http.setRequestMethod("GET");
			}
			http.setConnectTimeout(15000);
			
			SharedPreferences cache = null;
			cache = context.getSharedPreferences("cache",
						android.content.ContextWrapper.MODE_PRIVATE);

			http.setRequestProperty ("User-agent", cache.getString("UserAgent", "error"));

			http.setRequestProperty("content-type",
					"application/x-www-form-urlencoded");

			if (flag) {
				OutputStream outputStream = http.getOutputStream();
				outputStream.write(postParameterData.getBytes("UTF-8"));
				outputStream.flush();
				outputStream.close();
			}
			instream = http.getInputStream();
			instream = new BufferedInputStream(instream);

			headers = http.getHeaderFields();

			String text = new String(Util.readBytes(instream));

			JSONParser parser = new JSONParser();
			Log.i("total_http_text", "total_http_text" + text);
			Object obj = parser.parse(text);
			JSONArray array;
			array = (JSONArray) obj;

			for (int i = 0; i < array.size(); i++) {
				Log.i("json ", "json" + array.get(i));
				JSONParser parser2 = new JSONParser();
				JSONObject obj2 = (JSONObject) array.get(i);
				Iterator temp2 = obj2.keySet().iterator();

				while (temp2.hasNext()) {
					Log.i("json2 " + i, "값확??" + obj2.get(temp2.next()));
				}
			}

			if (headers.get("Set-Cookie") != null) {
				List<String> values1 = headers.get("Set-Cookie");
				String cookieValue = null;
				for (Iterator<String> iter = values1.iterator(); iter.hasNext();) {
					String v = String.valueOf(iter.next());
					if (cookieValue == null)
						cookieValue = v;
					else
						cookieValue = cookieValue + ";" + v;
				}
				String b[] = cookieValue.split(";");
			}

			return array;

		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (org.json.simple.parser.ParseException e) {
			e.printStackTrace();
		}

		return null;
	}

	public static Hashtable<String, String> changeJsonHt(JSONObject obj) {
		Hashtable<String, String> hashObj = new Hashtable<String, String>();
		Iterator<String> iterator = obj.keySet().iterator();
		while (iterator.hasNext()) {
			String key = (String) iterator.next();
			hashObj.put(key, String.valueOf(obj.get(key)));
		}

		return hashObj;
	}

	// always verify the host - dont check for certificate
	final static HostnameVerifier DO_NOT_VERIFY = new HostnameVerifier() {
		public boolean verify(String hostname, SSLSession session) {
			return true;
		}
	};

	/**
	 * Trust every server - dont check for any certificate
	 */
	private static void trustAllHosts() {
		// Create a trust manager that does not validate certificate chains
		TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
			public java.security.cert.X509Certificate[] getAcceptedIssuers() {
				return new java.security.cert.X509Certificate[] {};
			}

			public void checkClientTrusted(X509Certificate[] chain,
					String authType) throws CertificateException {
			}

			public void checkServerTrusted(X509Certificate[] chain,
					String authType) throws CertificateException {
			}
		} };

		// Install the all-trusting trust manager
		try {
			SSLContext sc = SSLContext.getInstance("TLS");
			sc.init(null, trustAllCerts, new java.security.SecureRandom());
			HttpsURLConnection
					.setDefaultSSLSocketFactory(sc.getSocketFactory());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
