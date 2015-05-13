package util;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Iterator;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Log;

public class SdCardManager {

	private static String getExternalRootDir() {
		String result;
		String ext = Environment.getExternalStorageState();
		if (ext.equals(Environment.MEDIA_MOUNTED)) {
			result = Environment.getExternalStorageDirectory()
					.getAbsolutePath();
		}

		else {
			result = Environment.MEDIA_UNMOUNTED;
		}

		return result;
	}

	private static HashMap<String, Bitmap> data = null;
	private static SdCardManager FILEHANDLE = null;
	// public final static String FILEPATH = Environment
	// .getExternalStorageDirectory().getAbsolutePath()
	// + "/NPKI/ISignPlusSSO/";

	public final static String FILEPATH = getExternalRootDir()
			+ "/NPKI/";

	private SdCardManager() {
		createDirectory();
		init();
	}

	public static SdCardManager getInstance() {
		if (FILEHANDLE == null)
			FILEHANDLE = new SdCardManager();
		return FILEHANDLE;
	}


	public static Object readTextFile(String fileName) { // 파일이름만
	// String resultText = "{\"result\":\"false\"}"; // 최초에 스트링에 false가 들어있다.
		JSONArray array = new JSONArray(); // 결과 스트링에
		// BufferedInputStream bis = null;
		BufferedReader br = null;
		try {
			// 한줄에 2차 스트림으로 선언하기
			// bis = new BufferedInputStream(new FileInputStream(FILEPATH
			// + fileName));
			int data = 0;
			br = new BufferedReader(new InputStreamReader(new FileInputStream(
					FILEPATH + fileName)));
			String sessionString = br.readLine();
			Log.i("파일 열기", "파일 열기 결과  " + sessionString);
			if (sessionString == null) { // 파일이 없다.
				return "false";
			}

			// while((data = bis.read()) != -1){
			// System.out.println((char)data);
			// }

			String text = sessionString;
			JSONParser parser = new JSONParser();
			Log.i("total_http_text", "total_http_text" + text);
			Object obj = parser.parse(text);

			array = (JSONArray) obj;
			
			if (array.size() == 0) { // 이상한 스트링이 들어있을때 0으로 보면 된다.
				return "false";
			}
			
			for (int i = 0; i < array.size(); i++) {
				Log.i("json ", "json" + array.get(i));
				JSONParser parser2 = new JSONParser();
				JSONObject obj2 = (JSONObject) array.get(i);
				Iterator temp2 = obj2.keySet().iterator();

				while (temp2.hasNext()) {
					// key1 =String.valueOf(temp2.next());
					Log.i("json2 " + i, "값확인 " + obj2.get(temp2.next()));
				}
			}
			
			return text;

		} catch (FileNotFoundException e) {
			 e.printStackTrace();
			return "false";
		} catch (IOException e) {
			 e.printStackTrace();
			return "false";
		} catch (ParseException e) {
			 e.printStackTrace();
			return "false";
		} finally {
			if (br != null)
				try {
					br.close();
				} catch (IOException e) {
					 e.printStackTrace();
					return "false";
				}

			// if (bis != null)
			// try {
			// bis.close();
			// } catch (IOException e) {
			// // e.printStackTrace();
			// return "false";
			// }
		}
	}

	public static boolean saveFile(String fileName, String text) {
		createDirectory();
		BufferedOutputStream bos = null;
		try {
			bos = new BufferedOutputStream(new FileOutputStream(FILEPATH
					+ fileName, false));
			bos.write(text.getBytes()); // 텍스트를 해당파일에 쓴다.

		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		} finally {
			if (bos != null) {
				try {
					bos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return true;
	}

	public static String[] getList() {
		File dir = new File(FILEPATH);
		String[] files;

		if (dir.isDirectory()) {
			files = dir.list();
		} else {
			return null;
		}
		return files;
	}

	public static void createDirectory() {

		File path2 = new File(getExternalRootDir() + "/NPKI/");
		if (!path2.isDirectory()) {
			path2.mkdir();
		}

		File path = new File(FILEPATH);
		if (!path.isDirectory()) {
			path.mkdir();
		}

	}

	private void init() {
		data = new HashMap<String, Bitmap>();
	}
}
