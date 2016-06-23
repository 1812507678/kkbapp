package com.ttxgps.utils;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;

//import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;

public class BaiduAPIConverter {

	public static String testPost(String x, String y) throws IOException {
		URL url = new URL(
				"http://api.map.baidu.com/ag/coord/convert?from=0&to=4&x=" + x
						+ "&y=" + y);
		URLConnection connection = url.openConnection();
		/**
		 * 然后把连接设为输出模式。URLConnection通常作为输入来使用，比如下载一个Web页。
		 * 通过把URLConnection设为输出，你可以把数据向你个Web页传送。下面是如何做：
		 */
		connection.setDoOutput(true);
		OutputStreamWriter out = new OutputStreamWriter(
				connection.getOutputStream(), "utf-8");
		// remember to clean up
		out.flush();
		out.close();
		// 一旦发送成功，用以下方法就可以得到服务器的回应：
		String sCurrentLine;
		String sTotalString;
		sCurrentLine = "";
		sTotalString = "";
		InputStream l_urlStream;
		l_urlStream = connection.getInputStream();
		BufferedReader l_reader = new BufferedReader(new InputStreamReader(
				l_urlStream));
		while ((sCurrentLine = l_reader.readLine()) != null) {
			if (!sCurrentLine.equals(""))
				sTotalString += sCurrentLine;
		}
		
		sTotalString = sTotalString.substring(1, sTotalString.length() - 1);
		
		String[] results = sTotalString.split("\\,");
		if (results.length == 3) {
			if (results[0].split("\\:")[1].equals("0")) {
				String mapX = results[1].split("\\:")[1];
				String mapY = results[2].split("\\:")[1];
				mapX = mapX.substring(1, mapX.length() - 1);
				mapY = mapY.substring(1, mapY.length() - 1);
				mapX = new String(Base64.decode(mapX));
				mapY = new String(Base64.decode(mapY));
				// System.out.println(Double.parseDouble(mapX)
				// - Double.parseDouble(x));
				// System.out.println(Double.parseDouble(mapY)
				// - Double.parseDouble(y));
				return mapX + ";" + mapY;
			}
		}
		return 0 + ";" + 0;
	}

	// 0.010899355139997624
	// 0.007150678534998178

	// 0.011052305280003338
	// 0.007018279545000894

	// 0.01101260354000999
	// 0.007077248008002357

	// 0.01101213251000388
	// 0.007078139806999673
	public static void main(String[] args) throws IOException {
		testPost("118.163598", "39.224653");
	}
}