package com.ttxgps.gpslocation;
//package com.car.carlocation;
//import android.content.Context;
//import android.graphics.Canvas;
//import android.view.GestureDetector;
//import android.view.GestureDetector.OnDoubleTapListener;
//import android.view.GestureDetector.OnGestureListener;
//import android.view.MotionEvent;
//
//import com.google.android.gms.maps.MapView;
//import com.google.android.maps.GeoPoint;
//import com.google.android.maps.Overlay;
//
///**
// * �̳�ItemizedOverlay<OverlayItem> ����Overlay���������
// * 
// * ��� ֻ��Ū��������Ч���� ����˵ ��ͼ�Ϻܶ�ȷ���ĵ㣬���ĳһ�� ������Ӧ�Ķ������ǾͲ����ж�����һ�����ˡ� ��֪���µ�api�Ƿ�������
// * 
// * @author zeng
// * 
// */
//public class LongClickOverlay extends Overlay implements OnDoubleTapListener,
//		OnGestureListener {
//	private GestureDetector gestureScanner = new GestureDetector(this);
//	public static final int LONGPRESSINTERVAL = 1000;
//	private Context mContext;
//	float x = 0;
//	float y = 0;
//	// down - up ����ֵ
//	protected long time;
//	// �Ƿ�longpress
//	protected boolean isLongPress = false;
//
//	public LongClickOverlay(Context context) {
//		this.mContext = context;
//	}
//
//	@Override
//	public boolean onTouchEvent(MotionEvent arg0, MapView mapView) {
//		x = arg0.getX();
//		y = arg0.getY();
//		switch (arg0.getAction()) {
//		case MotionEvent.ACTION_DOWN:
//			time = System.currentTimeMillis();
//			isLongPress = false;
//			break;
//
//		case MotionEvent.ACTION_UP:
//			if (System.currentTimeMillis() - time > LONGPRESSINTERVAL) {
//				isLongPress = true;
//			}
//			break;
//		default:
//			break;
//		}
//		return gestureScanner.onTouchEvent(arg0);
//	}
//
//	@Override
//	public void onLongPress(MotionEvent e) {
//		// x = e.getX();
//		// y = e.getY();
//		// if (mContext instanceof Main) {
//		// ((Main) mContext).showPopupWindow((int) x, (int) y);
//		// }
//		// isLongPress = false;
//	}
//
//	@Override
//	public boolean onTap(GeoPoint arg0, MapView mapView) {
//		if (!isLongPress)
//			return super.onTap(arg0, mapView);
//		return true;
//	}
//
//	@Override
//	public boolean onDown(MotionEvent e) {
//		// TODO Auto-generated method stub
//		return false;
//	}
//
//	@Override
//	public void onShowPress(MotionEvent e) {
//		// TODO Auto-generated method stub
//
//	}
//
//	@Override
//	public boolean onSingleTapUp(MotionEvent e) {
//		// TODO Auto-generated method stub
//		return false;
//	}
//
//	@Override
//	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
//			float distanceY) {
//		// TODO Auto-generated method stub
//		return false;
//	}
//
//	@Override
//	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
//			float velocityY) {
//		// TODO Auto-generated method stub
//		return false;
//	}
//
//	@Override
//	public boolean onSingleTapConfirmed(MotionEvent e) {
//		// TODO Auto-generated method stub
//		return false;
//	}
//
//	@Override
//	public boolean onDoubleTap(MotionEvent e) {
//		// TODO Auto-generated method stub
//		return false;
//	}
//
//	@Override
//	public boolean onDoubleTapEvent(MotionEvent e) {
//		// TODO Auto-generated method stub
//		return false;
//	}
//
// }
