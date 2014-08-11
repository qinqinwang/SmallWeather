package com.weather.util;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.content.Context;
import android.provider.OpenableColumns;
import android.util.Log;

public class HttpUtil {
	private final static String FILE_NAME = "weather.txt";
	private static Context context;

	public HttpUtil(Context context) {
		this.context = context;
	}

	public static String getJsonContent(String urlStr) {
		try {
			URL url = new URL(urlStr);
			HttpURLConnection httpConn = (HttpURLConnection) url
					.openConnection();
			httpConn.setConnectTimeout(30000);
			httpConn.setReadTimeout(30000);
			httpConn.setDoInput(true);
			httpConn.setRequestMethod("GET");
			int respCode = httpConn.getResponseCode();
			if (respCode == 200) {
				return ConvertStream2Json(httpConn.getInputStream());
			}
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";
	}

	private static String ConvertStream2Json(InputStream inputStream) {
		String jsonStr = "";
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		int len = 0;
		try {
			while ((len = inputStream.read(buffer, 0, buffer.length)) != -1) {
				out.write(buffer, 0, len);
			}
			jsonStr = new String(out.toByteArray());
			write(jsonStr);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return jsonStr;
	}

	private static void write(String name) {
		try {
			FileOutputStream outputStream = context.openFileOutput(FILE_NAME,
					Context.MODE_APPEND);
			outputStream.write(name.getBytes());
			outputStream.close();
		} catch (Exception e) {
			// TODO: handle exception
		}

	}

	public String getName() throws Exception {
		FileInputStream inputStream = context.openFileInput(FILE_NAME);
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		int len = 0;
		while ((len = inputStream.read(buffer)) != -1) {
			outStream.write(buffer, 0, len);
		}
		outStream.close();
		byte[] data = outStream.toByteArray();
		String name = new String(data);
		return name;
	}

}
