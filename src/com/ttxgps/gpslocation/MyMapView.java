package com.ttxgps.gpslocation;
import java.util.ArrayList;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;

import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.Projection;
import com.ttxgps.entity.MyMarker;

public class MyMapView extends MapView {
	private ArrayList<MyMarker> markers;
	Paint paint = new Paint();
	Paint paint_1 = new Paint();
	Paint paint_2 = new Paint();
	Paint paint_3 = new Paint();
	int c = 1;
	Handler map_draw = new Handler() {
		public void handleMessage(Message msg) {
			c++;
			if (c == 10) {
				c = 1;
			}
			if (map_draw != null)
				map_draw.sendEmptyMessageDelayed(1, 100);
		};
	};

	public MyMapView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		paint.setColor(Color.BLUE);
		paint.setDither(true);
		paint.setStyle(Paint.Style.STROKE);
		paint.setStrokeJoin(Paint.Join.ROUND);
		paint.setStrokeCap(Paint.Cap.ROUND);
		paint.setSubpixelText(true);
		paint.setAntiAlias(true);
		paint.setStrokeWidth(4);
	}

	@Override
	public void draw(Canvas canvas) {
		// TODO Auto-generated method stub
		System.out.println("----1");
		super.draw(canvas);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.onDraw(canvas);
		System.out.println("----------------");
		if (markers != null) {
			Projection projection = this.getMap().getProjection();
			for (MyMarker mli : markers) {
				// String[] tps = mli.a_type.split(":");
				// if (RegularUtils.getNumber(tps[tps.length - 1])) {
				Point p = projection.toScreenLocation(mli.getLatlng());
				canvas.drawCircle(p.x, p.y, (c * 3 / 4) * 100, paint);
				canvas.drawCircle(p.x, p.y, (c * 2 / 4) * 100, paint);
				canvas.drawCircle(p.x, p.y, c * 100, paint);
				// }
			}
		}

	}
}
