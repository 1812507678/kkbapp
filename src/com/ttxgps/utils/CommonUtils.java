package com.ttxgps.utils;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.ttxgps.gpslocation.view.MyProgressDialog;
import com.ttxgps.utils.AsyncImageLoader.ImageCallback;
import com.xtst.gps.R;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

public class CommonUtils {

	private static MyProgressDialog mProgress;

	private static DisplayMetrics displayMetrics = new DisplayMetrics();

	public static String COMPRESS_TEMP_FILE_NAME = "temp_";


	/**
	 * ������Ļ��(px)
	 */
	public static int getScreenWidth(Activity activity) {
		activity.getWindowManager().getDefaultDisplay()
		.getMetrics(displayMetrics);
		return displayMetrics.widthPixels;
	}

	/**
	 * ������Ļ��(px)
	 */
	public static int getScreenHeight(Activity activity) {
		activity.getWindowManager().getDefaultDisplay()
		.getMetrics(displayMetrics);
		return displayMetrics.heightPixels;
	}

	/**
	 * ����Ƿ����SDCARD
	 */
	public static boolean checkSDCard() {
		if (android.os.Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED)) {
			return true;
		} else {
			return false;
		}

	}


	/** ��ȡ�ֻ����� */
	public static String getPhoneNumber(Context context) {
		TelephonyManager tm = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		return tm.getLine1Number();
	}

	// ����������ӻ���
	public static boolean getAPNType(Context context) {
		ConnectivityManager connMgr = (ConnectivityManager) context
				.getSystemService(context.CONNECTIVITY_SERVICE);
		NetworkInfo networkinfo = connMgr.getActiveNetworkInfo();
		if (networkinfo != null) {// ����������
			return true;
		}
		return false;
	}

	// ȥ���������
	public static String ReplaceBadCharFileName(String str) {
		for (int i = 0; i < str.length(); i++) {
			str = str.replace("\\", "");
			str = str.replace(":", "");
			str = str.replace("/", "");
			str = str.replace("*", "");
			str = str.replace("?", "");
			str = str.replace("<", "");
			str = str.replace(">", "");
			str = str.replace("|", "");
			str = str.replace("http://", "");

		}
		return str;
	}

	//ȥ��׺
	public static String ReplaceBadCharString(String str){
		if(str != null)
			str = str.substring(0, str.length()-2);
		return str;

	}

	public static void downloadphoto(Context context,String url,final ImageView view){
		AsyncImageLoader imageLoader = new AsyncImageLoader();
		Drawable cachedImage = null;
		cachedImage = imageLoader.loadDrawable(url, context,
				true, new ImageCallback()
		{

			@Override
			public void imageLoaded(Drawable imageDrawable,
					String imageUrl)
			{
				if(null == view)
					return;
				if (imageDrawable != null)
				{
					view.setImageDrawable(imageDrawable);
				}
				else
				{
					view.setImageResource(R.drawable.home_icon_head);
				}
			}
		});
		if(cachedImage!=null){
			if(null == view)
				return;
			view.setImageDrawable(cachedImage);
		}
	}

	public static void showProgress(Activity paramActivity, String paramCharSequence)
	{
		showProgress(paramActivity, paramCharSequence, true, null);
	}

	public static void showProgress(Activity paramActivity, String paramCharSequence, DialogInterface.OnCancelListener paramOnCancelListener)
	{
		showProgress(paramActivity, paramCharSequence, true, paramOnCancelListener);
	}

	public static void showProgress(Activity context, String text, boolean isCancel, DialogInterface.OnCancelListener paramOnCancelListener)
	{
		if (context.isFinishing())
		{
			closeProgress();
			return;
		}
		if (mProgress == null)
			mProgress = MyProgressDialog.createDialog(context);
		mProgress.setMessage(text);
		mProgress.setCancelable(isCancel);
		mProgress.setOnCancelListener(paramOnCancelListener);
		mProgress.show();
	}
	public static void closeProgress()
	{
		try
		{
			if (mProgress != null)
			{
				mProgress.dismiss();
				mProgress = null;
			}
			return;
		}
		catch (Exception localException)
		{
			localException.printStackTrace();
		}
	}

	/**
	 * �ļ�ת��Ϊ�ֽ�����
	 * @EditTime 2007-8-13 ����11:45:28
	 */
	public static byte[] getBytesFromFile(File f) {
		if (f == null) {
			return null;
		}
		try {
			FileInputStream stream = new FileInputStream(f);
			ByteArrayOutputStream out = new ByteArrayOutputStream(1000);
			byte[] b = new byte[1000];
			int n;
			while ((n = stream.read(b)) != -1) {
				out.write(b, 0, n);
			}
			stream.close();
			out.close();
			return out.toByteArray();
		} catch (IOException e) {
		}
		return null;
	}

	public static double toDouble(String string) {
		double value = 0.0d;
		if (TextUtils.isEmpty(string)) {
			return value;
		}
		try {
			value = Double.parseDouble(string);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return value;
	}

	public static int getWeatherImage(String weather) {
		if ("��".equals(weather)) {
			return R.drawable.w0;
		}
		if ("����".equals(weather)) {
			return R.drawable.w1;
		}
		if ("��ת����".equals(weather)) {
			return R.drawable.w1;
		}
		if ("��".equals(weather)) {
			return R.drawable.w2;
		}
		if ("����".equals(weather)) {
			return R.drawable.w3;
		}
		if ("С��ת��".equals(weather)) {
			return R.drawable.w7;
		}
		if ("С��".equals(weather)) {
			return R.drawable.w8;
		}
		if ("����".equals(weather)) {
			return R.drawable.w9;
		}
		if ("����".equals(weather)) {
			return R.drawable.w10;
		}
		if ("Сѩ".equals(weather)) {
			return R.drawable.w14;
		}
		if ("��ѩ".equals(weather)) {
			return R.drawable.w15;
		}
		if ("��ѩ".equals(weather)) {
			return R.drawable.w16;
		}
		if ("���ѩ".equals(weather)) {
			return R.drawable.w6;
		}
		if ("��".equals(weather)) {
			return R.drawable.w18;
		}
		return R.drawable.w0;
	}

	public static Bitmap toRoundBitmap(Bitmap bitmap) {
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		float roundPx;
		float left, top, right, bottom, dst_left, dst_top, dst_right, dst_bottom;
		if (width <= height) {
			roundPx = width / 2;
			left = 0;
			top = 0;
			right = width;
			bottom = width;
			height = width;
			dst_left = 0;
			dst_top = 0;
			dst_right = width;
			dst_bottom = width;
		} else {
			roundPx = height / 2;
			float clip = (width - height) / 2;
			left = clip;
			right = width - clip;
			top = 0;
			bottom = height;
			width = height;
			dst_left = 0;
			dst_top = 0;
			dst_right = height;
			dst_bottom = height;
		}

		Bitmap output = Bitmap.createBitmap(width, height, Config.ARGB_8888);
		Canvas canvas = new Canvas(output);

		final int color = 0xff424242;
		final Paint paint = new Paint();
		final Rect src = new Rect((int) left, (int) top, (int) right, (int) bottom);
		final Rect dst = new Rect((int) dst_left, (int) dst_top, (int) dst_right, (int) dst_bottom);
		final RectF rectF = new RectF(dst);

		paint.setAntiAlias(true);// ���û����޾��

		canvas.drawARGB(0, 0, 0, 0); // �������Canvas
		paint.setColor(color);

		// ���������ַ�����Բ,drawRounRect��drawCircle
		// canvas.drawRoundRect(rectF, roundPx, roundPx, paint);// ��Բ�Ǿ��Σ���һ������Ϊͼ����ʾ���򣬵ڶ��������͵����������ֱ���ˮƽԲ�ǰ뾶�ʹ�ֱԲ�ǰ뾶��
		canvas.drawCircle(roundPx, roundPx, roundPx, paint);

		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));// ��������ͼƬ�ཻʱ��ģʽ,�ο�http://trylovecatch.iteye.com/blog/1189452
		canvas.drawBitmap(bitmap, src, dst, paint); //��Mode.SRC_INģʽ�ϲ�bitmap���Ѿ�draw�˵�Circle

		return output;
	}

	public static String getCacheVoiceFileName(String imageUrl)
	{
		if (imageUrl != null)
		{
			return Constants.HEADER_DIR + CommonUtils.ReplaceBadCharFileName(imageUrl);
		}
		return null;

	}

	/**
	 * �õ�amr��ʱ��
	 * 
	 * @param file
	 * @return
	 * @throws IOException
	 */
	public static long getAmrDuration(File file) throws IOException {
		long duration = -1;
		int[] packedSize = { 12, 13, 15, 17, 19, 20, 26, 31, 5, 0, 0, 0, 0, 0, 0, 0 };
		RandomAccessFile randomAccessFile = null;
		try {
			randomAccessFile = new RandomAccessFile(file, "rw");
			long length = file.length();//�ļ��ĳ���
			int pos = 6;//���ó�ʼλ��
			int frameCount = 0;//��ʼ֡��
			int packedPos = -1;
			/////////////////////////////////////////////////////
			byte[] datas = new byte[1];//��ʼ����ֵ
			while (pos <= length) {
				randomAccessFile.seek(pos);
				if (randomAccessFile.read(datas, 0, 1) != 1) {
					duration = length > 0 ? ((length - 6) / 650) : 0;
					break;
				}
				packedPos = (datas[0] >> 3) & 0x0F;
				pos += packedSize[packedPos] + 1;
				frameCount++;
			}
			/////////////////////////////////////////////////////
			duration += frameCount * 20;//֡��*20
		} finally {
			if (randomAccessFile != null) {
				randomAccessFile.close();
			}
		}
		return duration;
	}

	/**
	 * ��ȡϵͳ��ǰʱ��
	 * @return
	 */
	@SuppressLint("SimpleDateFormat")
	public static String getCurrenttime() {
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");//�������ڸ�ʽ
		return df.format(new Date());
	}

	public static String getPicPathByUri(Activity context, Uri selectedImage) {
		Cursor cursor = context.getContentResolver().query(selectedImage, null, null, null, null);
		if (cursor != null) {
			cursor.moveToFirst();
			String picturePath = cursor.getString(cursor.getColumnIndex("_data"));
			cursor.close();
			if (picturePath == null || picturePath.equals("null")) {
				return null;
			}
			return picturePath;
		}
		File file = new File(selectedImage.getPath());
		if (file.exists()) {
			return file.getAbsolutePath();
		}
		return null;
	}


	public static void deleteTempFile(String path) {
		File file = new File(path);
		if (file.exists()) {
			file.delete();
		}
	}
	public static Bitmap getSmallBitmap(String filePath) {
		Options options = new Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(filePath, options);
		options.inSampleSize = calculateInSampleSize(options, 240, 320);
		options.inJustDecodeBounds = false;
		return BitmapFactory.decodeFile(filePath, options);
	}

	public static String compressLocalPic(Activity context, String filePath) {
		String tempFilePath = null;
		try {
			File f = new File(filePath);
			Bitmap bm = getSmallBitmap(filePath);
			tempFilePath = Constants.HEADER_DIR + COMPRESS_TEMP_FILE_NAME + f.getName();
			bm.compress(CompressFormat.JPEG, 60, new FileOutputStream(new File(tempFilePath)));
			return tempFilePath;
		} catch (Exception e) {
			e.printStackTrace();
			return tempFilePath;
		}
	}


	public static int calculateInSampleSize(Options options, int reqWidth, int reqHeight) {
		int height = options.outHeight;
		int width = options.outWidth;
		if (height <= reqHeight && width <= reqWidth) {
			return 1;
		}
		int heightRatio = Math.round(((float) height) / ((float) reqHeight));
		int widthRatio = Math.round(((float) width) / ((float) reqWidth));
		if (heightRatio < widthRatio) {
			return heightRatio;
		}
		return widthRatio;
	}



}
