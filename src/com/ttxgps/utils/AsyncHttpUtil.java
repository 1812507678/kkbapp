
package com.ttxgps.utils;

import java.io.File;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.AbstractHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.BinaryHttpResponseHandler;
import com.loopj.android.http.BuildConfig;
import com.loopj.android.http.FileAsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.ResponseHandlerInterface;
import com.loopj.android.http.TextHttpResponseHandler;

/**
 * @ClassName: AsyncHttpUtil
 * @Description: Http璇锋眰绫伙紝鍖呮嫭POST鍜孏ET璇锋眰鍙婃彁浜ゆ暟鎹�
 * @author: li.xy
 * @date: 2014-4-28 涓婂崍11:02:35
 */
public class AsyncHttpUtil {
	private static String TAG = AsyncHttpUtil.class.getSimpleName();

	private static final int TIME_OUT =600 * 1000;

	private static AsyncHttpClient client = new AsyncHttpClient();


	static {
		// 璁剧疆閾炬帴瓒呮椂锛屽鏋滀笉璁剧疆锛岄粯璁や负10s
		client.setTimeout(TIME_OUT);
		// 璁剧疆閲嶈繛娆℃暟鍙婁袱娆￠噸杩炵殑鏃堕棿闂撮殧
		//client.setMaxRetriesAndTimeout(1, Integer.MAX_VALUE);
		//璁剧疆header
		//client.addHeader("", "");
	}


	private AsyncHttpUtil() {
	}


	/**
	 * @Description: 鍚屾session鍒皐ebview
	 * @param context
	 * @param remoteAddress
	 * @return: void
	 */
	public static void syncCookie(Context context, String remoteAddress) {
		HttpClient httpClient = client.getHttpClient();

		CookieStore cookieStore =
				((AbstractHttpClient)httpClient).getCookieStore();
		Cookie cookie = null;
		List<Cookie> cookies = cookieStore.getCookies();
		if (!cookies.isEmpty()) {
			for (int i = 0; i < cookies.size(); i++) {
				cookie = cookies.get(i);
			}
		}
		CookieSyncManager.createInstance(context);
		CookieManager cookieManager = CookieManager.getInstance();

		if (cookie != null) {
			cookieManager.removeSessionCookie();
			try {
				// webview绗竴娆″姞杞介渶瑕�10姣鐨勫欢鏃�
				Thread.sleep(10);
			} catch (InterruptedException e) {
			}
			String cookieString =
					cookie.getName() + "=" + cookie.getValue() + "; domain="
							+ cookie.getDomain();
			cookieManager.setCookie(remoteAddress, cookieString);
			CookieSyncManager.getInstance().sync();
		}
	}

	/******************************************************************************************
	 * ===================================浠ヤ笅涓篜OST鏂瑰紡璇锋眰=======================================
	 ******************************************************************************************/

	/**
	 * @Description: POST璇锋眰
	 * @param url url鍦板潃
	 * @param params 鍙傛暟
	 * @param handler TextHttpResponseHandler鍥炶皟锛堣繑鍥濻tring鏁版嵁锛�
	 * @return: void
	 */
	public static void post(String url, RequestParams params,			TextHttpResponseHandler handler) {
		httpPost(url, params, handler);
	}

	/**
	 * @Description: POST璇锋眰
	 * @param url url鍦板潃
	 * @param params 鍙傛暟
	 * @param handler JsonHttpResponseHandler鍥炶皟锛堣繑鍥瀓son鏁版嵁锛�
	 * @return: void
	 */
	public static void post(String url, RequestParams params,
			JsonHttpResponseHandler handler) {
		httpPost(url, params, handler);
	}

	/**
	 * @Description: POST璇锋眰
	 * @param url url鍦板潃
	 * @param params 鍙傛暟
	 * @param handler JsonHttpHandler鍥炶皟锛堣繑鍥瀓son鏁版嵁锛�
	 * @return: void
	 */
	public static void post(String url, RequestParams params,
			JsonHttpHandler handler) {
		httpPost(url, params, handler);
	}

	/**
	 * @Description: POST璇锋眰
	 * @param url url鍦板潃
	 * @param params 鍙傛暟
	 * @param handler TextHttpHandler鍥炶皟锛堣繑鍥瀞tring鏁版嵁锛�
	 * @return: void
	 */
	public static void post(String url, RequestParams params,
			TextHttpHandler handler) {
		httpPost(url, params, handler);
	}

	/**
	 * @Description: POST璇锋眰
	 * @param url url鍦板潃
	 * @param params 鍙傛暟
	 * @param handler FileHttpHandler鍥炶皟锛堣繑鍥濬ile鏁版嵁锛�
	 * @return: void
	 */
	public static void post(String url, RequestParams params,
			FileHttpHandler handler) {
		httpPost(url, params, handler);
	}

	/**
	 * @Description: POST璇锋眰
	 * @param url url鍦板潃
	 * @param params 鍙傛暟
	 * @param handler AsyHttpHandler鍥炶皟锛堣繑鍥瀞tring鏁版嵁锛�
	 * @return: void
	 */
	public static void post(String url, RequestParams params,
			AsyHttpHandler handler) {
		httpPost(url, params, handler);
	}

	/**
	 * @Description: POST璇锋眰
	 * @param url url鍦板潃
	 * @param params 鍙傛暟
	 * @param handler AsyncHttpResponseHandler鍥炶皟锛堣繑鍥濻tring鏁版嵁锛�
	 * @return: void
	 */
	public static void post(String url, RequestParams params,
			AsyncHttpResponseHandler handler) {
		httpPost(url, params, handler);
	}

	/**
	 * @Description: POST璇锋眰
	 * @param url url鍦板潃
	 * @param params 鍙傛暟
	 * @param handler FileAsyncHttpResponseHandler鍥炶皟锛堣繑鍥濬ile鏁版嵁锛�
	 * @return: void
	 */
	public static void post(String url, RequestParams params,
			FileAsyncHttpResponseHandler handler) {
		httpPost(url, params, handler);
	}

	/**
	 * @Description: POST璇锋眰
	 * @param url url鍦板潃
	 * @param params 鍙傛暟
	 * @param handler BinaryHttpResponseHandler鍥炶皟锛堣繑鍥瀊yte[]鏁版嵁锛�
	 * @return: void
	 */
	public static void post(String url, RequestParams params,
			BinaryHttpResponseHandler handler) {
		httpPost(url, params, handler);
	}

	public static void httpPost(String url, RequestParams params,
			ResponseHandlerInterface handler) {

		if (params == null) {
			//        	Logger.i(TAG, "request post url=" + url);
			client.post(url, handler);
		} else {
			//        	Logger.i(TAG, "request post url=" + url +"?"+params.toString());
			client.post(url, params, handler);
		}
	}

	/******************************************************************************************
	 * ===================================浠ヤ笅涓篏ET鏂瑰紡璇锋眰========================================
	 ******************************************************************************************/

	/**
	 * @Description: GET璇锋眰
	 * @param url url鍦板潃
	 * @param params 鍙傛暟
	 * @param asyncHttpResponseHandler TextHttpResponseHandler鍥炶皟锛堣繑鍥濻tring鏁版嵁锛�
	 * @return: void
	 */
	//	public static void get(String url, RequestParams params,
	//			AsyncHttpResponseHandler asyncHttpResponseHandler) {
	//		httpGet(url, params, asyncHttpResponseHandler);
	//	}

	/**
	 * @Description: GET璇锋眰
	 * @param url url鍦板潃
	 * @param params 鍙傛暟
	 * @param handler AsyHttpHandler鍥炶皟锛堣繑鍥濻tring鏁版嵁锛�
	 * @return: void
	 */
	public static void get(String url, RequestParams params,
			AsyHttpHandler handler) {
		httpGet(url, params, handler);
	}

	/**
	 * @Description: GET璇锋眰
	 * @param url url鍦板潃
	 * @param params 鍙傛暟
	 * @param handler TextHttpResponseHandler鍥炶皟锛堣繑鍥濻tring鏁版嵁锛�
	 * @return: void
	 */
	public static void get(String url, RequestParams params,
			TextHttpResponseHandler handler) {
		httpGet(url, params, handler);
	}

	/**
	 * @Description: GET璇锋眰
	 * @param url url鍦板潃
	 * @param params 鍙傛暟
	 * @param handler TextHttpHandler鍥炶皟锛堣繑鍥濻tring鏁版嵁锛�
	 * @return: void
	 */
	public static void get(String url, RequestParams params,
			TextHttpHandler handler) {
		httpGet(url, params, handler);
	}


	/**
	 * @Description: GET璇锋眰
	 * @param url url鍦板潃
	 * @param params 鍙傛暟
	 * @param handler JsonHttpResponseHandler鍥炶皟锛堣繑鍥瀓son鏁版嵁锛�
	 * @return: void
	 */
	public static void get(String url, RequestParams params,
			JsonHttpResponseHandler handler) {
		httpGet(url, params, handler);
	}

	/**
	 * @Description: GET璇锋眰
	 * @param url url鍦板潃
	 * @param params 鍙傛暟
	 * @param handler JsonHttpHandler鍥炶皟锛堣繑鍥瀓son鏁版嵁锛�
	 * @return: void
	 */
	public static void get(String url, RequestParams params,
			JsonHttpHandler handler) {
		httpGet(url, params, handler);
	}

	/**
	 * @Description: GET璇锋眰
	 * @param url url鍦板潃
	 * @param params 鍙傛暟
	 * @param handler FileAsyncHttpResponseHandler鍥炶皟锛堣繑鍥濬ile鏁版嵁锛�
	 * @return: void
	 */
	public static void get(String url, RequestParams params,
			FileAsyncHttpResponseHandler handler) {
		httpGet(url, params, handler);
	}

	/**
	 * @Description: GET璇锋眰
	 * @param url url鍦板潃
	 * @param params 鍙傛暟
	 * @param handler FileHttpHandler鍥炶皟锛堣繑鍥瀓son鏁版嵁锛�
	 * @return: void
	 */
	public static void get(String url, RequestParams params,
			FileHttpHandler handler) {
		httpGet(url, params, handler);
	}

	/**
	 * @Description: GET璇锋眰
	 * @param url url鍦板潃
	 * @param params 鍙傛暟
	 * @param handler BinaryHttpResponseHandler鍥炶皟锛堣繑鍥瀊yte[]鏁版嵁锛�
	 * @return: void
	 */
	public static void get(String url, RequestParams params,
			BinaryHttpResponseHandler handler) {
		httpGet(url, params, handler);
	}

	public static void httpGet(String url, RequestParams params,
			ResponseHandlerInterface handler) {
		if (BuildConfig.DEBUG) {
			Log.i(TAG, "request get url=" + url);
		}

		if (params == null) {
			client.get(url, handler);
		} else {
			Log.i(TAG, "request get url=" + url+"?"+params.toString());
			client.get(url, params, handler);
		}
	}


	/******************************************************************************************
	 * ===================================浠ヤ笅涓鸿姹俬andler====================================
	 ******************************************************************************************/

	/**
	 * @ClassName: TextHttpHandler
	 * @Description: 杩斿洖string
	 * @author li.xy
	 * @date 2014-6-18 涓嬪崍2:07:18
	 */
	public static abstract class TextHttpHandler extends TextHttpResponseHandler{
		public abstract void start();
		public abstract void finish();
		public abstract void success(String content);
		public abstract void fail(Throwable error, String content);
		@Override
		public void onStart() {
			start();
		}
		@Override
		public void onFinish() {
			finish();
		}

		@Override
		public void onSuccess(int arg0, Header[] arg1, String arg2) {
			success(arg2);
		}

		@Override
		public void onFailure(int arg0, Header[] arg1, String arg2,
				Throwable arg3) {
			fail(arg3, arg2);
		}
	}

	/**
	 * @ClassName: JsonHttpHandler
	 * @Description: 杩斿洖json
	 * @author li.xy
	 * @date 2014-6-18 涓嬪崍2:07:18
	 */
	public static abstract class JsonHttpHandler extends JsonHttpResponseHandler{
		public abstract void start();
		public abstract void finish();
		public abstract void success(JSONObject jsonObject);
		public abstract void success(JSONArray jsonArray);
		public abstract void fail(Throwable error);
		@Override
		public void onStart() {
			start();
		}
		@Override
		public void onFinish() {
			finish();
		}

		@Override
		public void onSuccess(int statusCode, Header[] headers,
				JSONArray response) {
			success(response);
			super.onSuccess(statusCode, headers, response);
		}

		@Override
		public void onSuccess(int statusCode, Header[] headers,
				JSONObject response) {
			success(response);
			super.onSuccess(statusCode, headers, response);
		}

		@Override
		public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
			fail(throwable);
			super.onFailure(statusCode, headers, throwable, errorResponse);
		}

	}

	/**
	 * @ClassName: FileHttpHandler
	 * @Description: 杩斿洖file
	 * @author li.xy
	 * @date 2014-6-18 涓嬪崍2:07:18
	 */
	public static abstract class FileHttpHandler extends FileAsyncHttpResponseHandler{
		public FileHttpHandler(File file) {
			super(file);
		}
		public abstract void start();
		public abstract void finish();
		public abstract void success(File file);
		public abstract void progress(int bytesWritten, int totalSize);
		public abstract void fail(Throwable error);
		@Override
		public void onStart() {
			start();
		}
		@Override
		public void onFinish() {
			finish();
		}


		public void onProgress(int bytesWritten, int totalSize) {
			progress(bytesWritten, totalSize);
		}

		@Override
		public void onSuccess(int arg0, Header[] arg1, File arg2) {
			success(arg2);
		}

		@Override
		public void onFailure(int arg0, Header[] arg1, Throwable arg2, File arg3) {
			fail(arg2);
		}
	}

	/**
	 * @ClassName: AsyHttpHandler
	 * @Description: 杩斿洖string
	 * @author li.xy
	 * @date 2014-6-18 涓嬪崍2:07:18
	 */
	public static abstract class AsyHttpHandler extends AsyncHttpResponseHandler{
		public abstract void start();
		public abstract void finish();
		public abstract void success(String content);
		public abstract void fail(Throwable error);
		@Override
		public void onStart() {
			start();
		}
		@Override
		public void onFinish() {
			finish();
		}

		@Override
		public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
			success(new String(arg2));
		}

		@Override
		public void onFailure(int arg0, Header[] arg1, byte[] arg2,
				Throwable arg3) {
			fail(arg3);
		}
	}
}
