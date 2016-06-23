package com.ttxgps.gpslocation.view;
import com.xtst.gps.R;
 
import android.app.Dialog;
import android.content.Context;
import android.view.Window;
import android.widget.TextView;

public class MyProgressDialog extends Dialog
{
	private static MyProgressDialog customProgressDialog = null;
	private Context context = null;

	public MyProgressDialog(Context paramContext)
	{
		super(paramContext);
		this.context = paramContext;
	}

	public MyProgressDialog(Context paramContext, int paramInt)
	{
		super(paramContext, paramInt);
	}

	public static MyProgressDialog createDialog(Context paramContext)
	{
		customProgressDialog = new MyProgressDialog(paramContext,R.style.ImhereProgressDialog);
		customProgressDialog.setContentView(R.layout.progress_bg);
		customProgressDialog.getWindow().getAttributes().gravity = 17;
		return customProgressDialog;
	}

	@Override
	public void onWindowFocusChanged(boolean paramBoolean)
	{
		if (customProgressDialog == null);
	}

	public MyProgressDialog setMessage(CharSequence paramCharSequence)
	{
		TextView localTextView = (TextView)customProgressDialog.findViewById(R.id.id_tv_loadingmsg);
		if (localTextView != null)
			localTextView.setText(paramCharSequence);
		return customProgressDialog;
	}

	public MyProgressDialog setTitile(String paramString)
	{
		return customProgressDialog;
	}
}

/* Location:           E:\test-tools\dex2jar-2.0\HYWatch20150824-dex2jar.jar
 * Qualified Name:     com.hy.hywatch.view.MyProgressDialog
 * JD-Core Version:    0.6.0
 */