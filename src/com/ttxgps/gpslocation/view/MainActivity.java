package com.ttxgps.gpslocation.view;
  
import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.xtst.gps.R;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.palmtrends.loadimage.Utils;

public class MainActivity extends FragmentActivity {
	private ViewGroup infoWindow;
	private TextView infoTitle;
	private TextView infoSnippet;
	private TextView infoButton;
	private TextView infoButton1;
	private OnInfoWindowElemTouchListener infoButtonListener;
	MapView mMapView = null;
	GoogleMap map;
	private Circle circle;
	Timer timer = new Timer();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_map_google);
		final MapWrapperLayout mapWrapperLayout = (MapWrapperLayout) findViewById(R.id.map_relative_layout);
		mMapView = (MapView) mapWrapperLayout.findViewById(R.id.googlemap);
		mMapView.onCreate(savedInstanceState);
		try {
			MapsInitializer.initialize(getApplication());
		} catch (GooglePlayServicesNotAvailableException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// MapWrapperLayout initialization
		// 39 - default marker height
		// 20 - offset between the default InfoWindow bottom edge and it's
		// content bottom edge
		map = mMapView.getMap();
		mapWrapperLayout.init(map, getPixelsFromDp(MainActivity.this, 39 + 20));
		// We want to reuse the info window for all the markers,
		// so let's create only one class member instance
		infoWindow = (ViewGroup) getLayoutInflater().inflate(R.layout.popview_b,
				null);
		infoTitle = (TextView) infoWindow.findViewById(R.id.pop_title);
		infoSnippet = (TextView) infoWindow.findViewById(R.id.pop_address);

		// Let's add a couple of markers
		map.addMarker(new MarkerOptions().title("Prague")
				.snippet("Czech Republic").position(new LatLng(50.08, 14.43)));

		map.addMarker(new MarkerOptions().title("Paris").snippet("France")
				.position(new LatLng(48.86, 2.33)));

		map.addMarker(new MarkerOptions().title("London")
				.snippet("United Kingdom").position(new LatLng(51.51, -0.1)));
		circle = map.addCircle(new CircleOptions()
		.center(new LatLng(51.51, -0.1)).radius(1000000).strokeWidth(3)
		.strokeColor(Color.BLACK).zIndex(3));
		// Setting custom OnTouchListener which deals with the
		// pressed state
		// so it shows up
		infoButtonListener = new OnInfoWindowElemTouchListener(infoButton,
				getResources().getDrawable(R.drawable.d_list_bg),
				getResources().getDrawable(R.drawable.d_list_selector_bg)) {
			@Override
			protected void onClickConfirmed(View v, Marker marker) {
				// Here we can perform some action triggered
				// after clicking the
				// button
				Toast.makeText(MainActivity.this,
						marker.getTitle() + "'s button clicked!",
						Toast.LENGTH_SHORT).show();
			}
		};
		infoButton.setOnTouchListener(infoButtonListener);

		infoButtonListener = new OnInfoWindowElemTouchListener(infoButton1,
				getResources().getDrawable(R.drawable.d_list_bg),
				getResources().getDrawable(R.drawable.d_list_selector_bg)) {
			@Override
			protected void onClickConfirmed(View v, Marker marker) {
				// Here we can perform some action triggered
				// after clicking the
				// button
				Toast.makeText(MainActivity.this,
						marker.getTitle() + "'s button clicked!",
						Toast.LENGTH_SHORT).show();
			}
		};
		infoButton1.setOnTouchListener(infoButtonListener);
		map.setInfoWindowAdapter(new InfoWindowAdapter() {
			@Override
			public View getInfoWindow(Marker marker) {
				return null;
			}

			@Override
			public View getInfoContents(Marker marker) {
				// Setting up the infoWindow with current's
				// marker info
				infoTitle.setText(marker.getTitle());
				infoSnippet.setText(marker.getSnippet());
				infoButtonListener.setMarker(marker);

				// We must call this to set the current marker
				// and infoWindow
				// references
				// to the MapWrapperLayout
				mapWrapperLayout.setMarkerWithInfoWindow(marker, infoWindow);
				return infoWindow;
			}
		});
		timer.schedule(new TimerTask() {

			@Override
			public void run() {
				// TODO Auto-generated method stub

				Utils.h.post(new Runnable() {

					@Override
					public void run() {
						double i = 1000000 ;
						// i = circle.getRadius() + i;
						// if (i > 1000000)
						// i = 0;
						circle.setRadius(i);
					}
				});

			}
		}, 1000, 500);
		addCircleToMap();
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		System.out.println("=====");
		return super.onTouchEvent(event);
	}

	private void addCircleToMap() {

		// circle settings
		int radiusM = 100000;// your radius in meters
		LatLng ll = new LatLng(50.08, 14.43);
		// draw circle
		int d = 500; // diameter
		Bitmap bm = Bitmap.createBitmap(d, d, Config.ARGB_8888);
		Canvas c = new Canvas(bm);
		Paint p = new Paint();
		p.setColor(Color.BLACK);
		c.drawCircle(d / 2, d / 2, d / 2, p);

		// generate BitmapDescriptor from circle Bitmap
		BitmapDescriptor bmD = BitmapDescriptorFactory.fromBitmap(bm);

		// mapView is the GoogleMap
		map.addGroundOverlay(new GroundOverlayOptions().image(bmD)
				.position(ll, radiusM * 2, radiusM * 2).transparency(0.4f));
	}

	@Override
	protected void onResume() {
		super.onResume();
		mMapView.onResume();

	}

	@Override
	protected void onPause() {
		mMapView.onPause();
		super.onPause();
	}

	@Override
	protected void onDestroy() {
		mMapView.onDestroy();
		super.onDestroy();
	}

	@Override
	public void onLowMemory() {
		super.onLowMemory();
		mMapView.onLowMemory();
	}

	public void things(View view) {
		System.out.println("====");
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		mMapView.onSaveInstanceState(outState);
	}

	public static int getPixelsFromDp(Context context, float dp) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dp * scale + 0.5f);
	}

}