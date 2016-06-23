package com.ttxgps.gpslocation.view;
  
import com.ttxgps.utils.DensityUtil;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;

public class CircleProgressBar extends View{
	private final int endAngel;
	private int marxArcStorkeWidth;
	private int maxProgress;
	RectF oval;
	Paint paint;
	private int progress;
	private int progressStrokeWidth;
	private final int startAngel;

	public CircleProgressBar(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.maxProgress = 100;
		this.progress = 0;
		this.progressStrokeWidth = 6;
		this.marxArcStorkeWidth = 12;
		this.startAngel = -42;
		this.endAngel = 260;
		this.oval = new RectF();
		this.paint = new Paint();
		this.progressStrokeWidth = DensityUtil.dip2px(context, 4.0f);
		this.marxArcStorkeWidth = DensityUtil.dip2px(context, 8.0f);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		int width = getWidth();
		int height = getHeight();
		if (width > height) {
			width = height;
		}
		if (width <= height) {
			height = width;
		}
		this.paint.setAntiAlias(true);
		this.paint.setColor(-1);
		canvas.drawColor(0);
		this.paint.setStrokeWidth(this.progressStrokeWidth);
		this.paint.setStyle(Style.STROKE);
		this.oval.left = (this.marxArcStorkeWidth / 2);
		this.oval.top = (this.marxArcStorkeWidth / 2);
		this.oval.right = (width - (this.marxArcStorkeWidth / 2));
		this.oval.bottom = (height - (this.marxArcStorkeWidth / 2));
		canvas.drawArc(this.oval, this.startAngel, this.endAngel, false, this.paint);
		this.paint.setColor(Color.rgb(255, 0, 0));
		this.paint.setStrokeWidth(this.marxArcStorkeWidth);
		Canvas canvas2 = canvas;
		canvas2.drawArc(this.oval, this.startAngel, this.endAngel * (((float) this.progress) / ((float) this.maxProgress)), false, this.paint);
	}

	public int getMaxProgress() {
		return this.maxProgress;
	}

	public void setMaxProgress(int maxProgress) {
		this.maxProgress = maxProgress;
	}

	public void setProgress(int progress, View view) {
		this.progress = progress;
		if (view != null) {
			view.setAnimation(pointRotationAnima(0.0f, ((int) ((((float) this.endAngel) / ((float) this.maxProgress)) * progress))));
		}
		invalidate();
	}

	public void setProgress(int progress) {
		setProgress(progress, null);
	}

	public void setProgressNotInUiThread(int progress, View view) {
		if (progress > getMaxProgress()) {
			this.progress = getMaxProgress();
		} else {
			this.progress = progress;
		}
		view.setAnimation(pointRotationAnima(0.0f, ((int) ((((float) this.endAngel) / ((float) this.maxProgress)) * progress))));
		postInvalidate();
	}

	private Animation pointRotationAnima(float fromDegrees, float toDegrees) {
		RotateAnimation animation = new RotateAnimation(fromDegrees, 306 + toDegrees, 1, 0.5f, 1, 0.5f);
		animation.setDuration(1);
		animation.setRepeatCount(1);
		animation.setFillAfter(true);
		return animation;
	}

}
