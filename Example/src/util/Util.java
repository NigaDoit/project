package util;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.params.SyncBasicHttpParams;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;

public class Util {

	public static String requestUrl(String url,
			ArrayList<Hashtable<String, String>> params, boolean isPost) {
		InputStream instream = null;
		try {
			HttpClient client = new DefaultHttpClient();
			String postURL = url;
			HttpResponse response;

			if (isPost == true) {
				HttpPost post = new HttpPost(postURL);
				MultipartEntity reqEntity = new MultipartEntity(
						HttpMultipartMode.BROWSER_COMPATIBLE);

				for (int i = 0; i < params.size(); i++) {
					reqEntity.addPart(
							params.get(i).get("name"),
							new StringBody(params.get(i).get("value"), Charset
									.forName("UTF-8")));
				}
				post.setEntity(reqEntity);
				response = client.execute(post);

			} else {

				HttpGet get = new HttpGet(postURL);
				HttpParams hParams = new SyncBasicHttpParams();
				for (int i = 0; i < params.size(); i++) {
					hParams.setParameter(params.get(i).get("name"),
							params.get(i).get("value"));
				}
				get.setParams(hParams);
				response = client.execute(get);
			}

			instream = response.getEntity().getContent();
			String responseStr = new String(Util.readBytes(instream));

			client.getConnectionManager().shutdown();
			if (responseStr.indexOf("false") == -1) {
				return responseStr;
			} else {
				return "false";
			}
		} catch (Exception e) {
			e.printStackTrace();
			return "false";
		}
	}

	public static String requestUrl(String domain, boolean isPost) {

		URL url;
		try {
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
			http.setRequestProperty("content-type",
					"application/x-www-form-urlencoded");

			instream = http.getInputStream();
			instream = new BufferedInputStream(instream);
			headers = http.getHeaderFields();

			String text = new String(Util.readBytes(instream));
			return text;
		} catch (MalformedURLException e) {
			e.printStackTrace();
			return "false";
		} catch (IOException e) {
			e.printStackTrace();
			return "false";
		}
	}

	final static HostnameVerifier DO_NOT_VERIFY = new HostnameVerifier() {
		public boolean verify(String hostname, SSLSession session) {
			return true;
		}
	};

	private static void trustAllHosts() {
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

		try {
			SSLContext sc = SSLContext.getInstance("TLS");
			sc.init(null, trustAllCerts, new java.security.SecureRandom());
			HttpsURLConnection
					.setDefaultSSLSocketFactory(sc.getSocketFactory());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static byte[] readBytes(InputStream inputStream) throws IOException {
		ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();

		int bufferSize = 1024;
		byte[] buffer = new byte[bufferSize];

		int len = 0;
		while ((len = inputStream.read(buffer)) != -1) {
			byteBuffer.write(buffer, 0, len);
		}

		return byteBuffer.toByteArray();
	}

	public static byte[] md5(String input) throws NoSuchAlgorithmException {
		MessageDigest messageDigest = MessageDigest.getInstance("MD5");
		return messageDigest.digest(input.getBytes());
	}

	public static byte[] sha1(String input) throws NoSuchAlgorithmException {
		MessageDigest messageDigest = MessageDigest.getInstance("SHA1");
		return messageDigest.digest(input.getBytes());
	}

	public static Drawable getResourceDrawable(Context context, int id) {

		InputStream instream = null;
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inPurgeable = true;
		options.inDither = true;
		Bitmap bitmap = null;
		byte[] bytes;

		instream = context.getResources().openRawResource(id);
		try {
			bytes = new byte[instream.available()];
			BufferedInputStream bin = new BufferedInputStream(instream);
			bin.read(bytes);
			bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length,
					options);
		} catch (IOException e) {
			e.printStackTrace();
		}

		return new BitmapDrawable(bitmap);
	}

	public static String getNowTime() {
		Date date = new Date();
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String strdate = df.format(date);

		return strdate;

	}

	public static String base64Encode(byte[] msg) {
		String result;

		if (msg != null) {
			try {
				//result = IssacWebAPI.Base64Encode(msg, msg.length);
				result = null;
			} catch (Exception e) {
				Log.e("base64Encode", e.getMessage());
				result = null;
			}
		} else {
			Log.i("base64Encode", "input parameter is null!!");
			result = null;
		}

		return result;
	}

	public static byte[] base64Decode(String str) {
		byte[] result;

		if (str != null) {
			try {
				//result = IssacWebAPI.Base64Decode(str);
				result = null;
			} catch (Exception e) {
				Log.e("base64Decode", e.getMessage());
				result = null;
			}
		} else {
			Log.i("base64Decode", "input parameter is null!!");
			result = null;
		}

		return result;
	}

	public static byte[] getRandomNumber(int len) {
		byte[] result;

		if (len > 0) {
			try {
				//result = IssacWebAPI.GenerateRandom(len);
				result = null;
			} catch (Exception e) {
				Log.e("getRandomNumber", e.getMessage());
				result = null;
			}
		} else {
			Log.i("getRandomNumber", "input parameter is negative number!!");
			result = null;
		}
		return result;
	}

	public static byte[] hashValue(byte[] data) {
		byte[] result;

		if (data != null) {
			try {
				//result = IssacWebAPI.GetHashValue(data, data.length, 0);
				result = null;
			} catch (Exception e) {
				result = e.getMessage().getBytes();
			}
		} else
			result = null;
		return result;
	}

	public String getVersion() {
		//return IssacWebAPI.getVersion();
		return "1.0";
	}

	public int deleteDir(String a_path) {

		File file = new File(a_path);
		if (file.exists()) {

			File[] childFileList = file.listFiles();
			for (File childFile : childFileList) {
				if (childFile.isDirectory()) {
					deleteDir(childFile.getAbsolutePath());
				} else {
					childFile.delete();
				}
			}
			file.delete();
			return 0;
		} else {
			return 0;
		}
	}

	public static String escape(String src) {
		int i;
		char j;
		StringBuffer tmp = new StringBuffer();
		tmp.ensureCapacity(src.length() * 6);
		for (i = 0; i < src.length(); i++) {
			j = src.charAt(i);
			if (Character.isDigit(j) || Character.isLowerCase(j)
					|| Character.isUpperCase(j))
				tmp.append(j);
			else if (j < 256) {
				tmp.append("%");
				if (j < 16)
					tmp.append("0");
				tmp.append(Integer.toString(j, 16));
			} else {
				tmp.append("%u");
				tmp.append(Integer.toString(j, 16));
			}
		}
		return tmp.toString();
	}

	public static String unescape(String src) {
		StringBuffer tmp = new StringBuffer();
		tmp.ensureCapacity(src.length());
		int lastPos = 0, pos = 0;
		char ch;
		while (lastPos < src.length()) {
			pos = src.indexOf("%", lastPos);
			if (pos == lastPos) {
				if (src.charAt(pos + 1) == 'u') {
					ch = (char) Integer.parseInt(
							src.substring(pos + 2, pos + 6), 16);
					tmp.append(ch);
					lastPos = pos + 6;
				} else {
					ch = (char) Integer.parseInt(
							src.substring(pos + 1, pos + 3), 16);
					tmp.append(ch);
					lastPos = pos + 3;
				}
			} else {
				if (pos == -1) {
					tmp.append(src.substring(lastPos));
					lastPos = src.length();
				} else {
					tmp.append(src.substring(lastPos, pos));
					lastPos = pos;
				}
			}
		}
		return tmp.toString();
	}
	
	public static String getAppVersion(Context context) {
		try {
			return context.getPackageManager().getPackageInfo(
					context.getPackageName(), PackageManager.GET_META_DATA).versionName;
		} catch (PackageManager.NameNotFoundException e) {
			Log.d("error", "패키지 버전을 가져올 수 없음");
			return "0.0";
		}
	}	
}
