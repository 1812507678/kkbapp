
package com.ttxgps.utils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.lang.ref.SoftReference;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;


public class AsyncImageLoader
{
	private final Map<String, SoftReference<Drawable>> imageCache = new HashMap<String, SoftReference<Drawable>>();

	public Drawable loadDrawable(final String imageUrl, final Context context,
			final boolean isSaveIcon, final ImageCallback callback)
	{
		if (imageCache.containsKey(imageUrl))
		{
			SoftReference<Drawable> softReference = imageCache.get(imageUrl);
			if (softReference.get() != null)
			{
				return softReference.get();
			}
		}
		if (CommonUtils.checkSDCard())
		{
			String str = CommonUtils.ReplaceBadCharFileName(imageUrl);
			File file = new File(Constants.HEADER_DIR + str + ".jpg");
			if (file.exists())
			{
				Bitmap bitmap = BitmapFactory
						.decodeFile(file.getAbsolutePath());
				BitmapDrawable bd = new BitmapDrawable(bitmap);
				return bd;
			}
		}
		final Handler handler = new Handler()
		{
			@Override
			public void handleMessage(Message msg)
			{
				callback.imageLoaded((Drawable) msg.obj, imageUrl);
			}
		};
		new Thread()
		{
			@Override
			public void run()
			{
				Drawable drawable = loadImageFromUrl(imageUrl, context,
						isSaveIcon);
				imageCache.put(imageUrl, new SoftReference<Drawable>(drawable));
				handler.sendMessage(handler.obtainMessage(0, drawable));
			};
		}.start();

		return null;
	}

	protected Drawable loadImageFromUrl(String imageUrl, Context context,
			boolean isSaveIcon)
	{
		try
		{
			if (imageUrl.equals("null") || imageUrl.equals(""))
			{
				return null;
			}
			Drawable drawable = Drawable.createFromStream(
					new URL(imageUrl).openStream(), "src");
			// ÊÇ·ñ±£´æÍ¼±ê
			if (drawable != null && isSaveIcon)
			{
				if (CommonUtils.checkSDCard())
				{
					String str = CommonUtils.ReplaceBadCharFileName(imageUrl);
					File iconFile = new File(Constants.HEADER_DIR + str + ".jpg");
					BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
					Bitmap bitMap = bitmapDrawable.getBitmap();
					BufferedOutputStream stream = new BufferedOutputStream(
							new FileOutputStream(iconFile));
					bitMap.compress(Bitmap.CompressFormat.PNG, 100, stream);
					stream.flush();
					stream.close();
					// bitMap.recycle();
				}
			}
			return drawable;
		}
		catch(MalformedURLException e1){
			e1.printStackTrace();
			return null;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}

		finally
		{

		}
	}

	public interface ImageCallback
	{
		public void imageLoaded(Drawable imageDrawable, String imageUrl);
	}

	public static String getCacheImgFileName(String imageUrl)
	{
		if (imageUrl != null)
		{
			File file = new File(Constants.HEADER_DIR
					+ CommonUtils.ReplaceBadCharFileName(imageUrl) + ".jpg");
			if (file.exists())
				return file.getAbsolutePath();
		}
		return null;

	}
}
