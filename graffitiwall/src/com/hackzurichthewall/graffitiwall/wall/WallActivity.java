package com.hackzurichthewall.graffitiwall.wall;

import java.util.List;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;

import com.estimote.sdk.Beacon;
import com.estimote.sdk.BeaconManager;
import com.estimote.sdk.BeaconManager.MonitoringListener;
import com.estimote.sdk.Region;
import com.hackzurichthewall.graffitiwall.R;
import com.hackzurichthewall.graffitiwall.main.BeaconConstants;
import com.hackzurichthewall.graffitiwall.main.GlobalState;
import com.hackzurichthewall.graffitiwall.wall.list.StreamListViewAdapter;
import com.hackzurichthewall.images.ImageActivity;


/**
 * This is the main activity. Basically contains a list with the last posted objects ordered
 * chronologically. Furthermore provides buttons to upload images or comments.
 * 
 * @author Johannes Glöckle vorbildlich der mann
 */
public class WallActivity extends Activity {

	public static final String TAG = "WALL_ACTIVITY";
	
	
	
	
	private ListView mCommentList;
	private StreamListViewAdapter mListAdapter;
	private ImageButton mTakePicture;
	
	
	private NotificationManager notificationManager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_wall); // setting content view to default layout
		
		// initialize
		GlobalState.beaconManager = new BeaconManager(this);
		this.notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		 
		this.mCommentList = (ListView) findViewById(R.id.lv_wall_list);
	
		this.mTakePicture = (ImageButton) findViewById(R.id.ib_take_picture);
		this.mTakePicture.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				openCamera();
			}
		});
		
		// ranging: find out distance to device
		GlobalState.beaconManager.setRangingListener(new BeaconManager.RangingListener() {
		  @Override public void onBeaconsDiscovered(Region region, List<Beacon> beacons) {
		    Log.d(TAG, "Ranged beacons: " + beacons);
		  }
		});
		
		// monitoring: scan for beacons periodically, change default scan intervall for more responsiveness
		GlobalState.beaconManager.setMonitoringListener(new MonitoringListener() {

			@Override
			public void onEnteredRegion(Region region, List<Beacon> beacons) {
								
			}

			@Override
			public void onExitedRegion(Region region) {
								
			}
		});
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		
		// Should be invoked in #onStart.
		GlobalState.beaconManager.connect(new BeaconManager.ServiceReadyCallback() {
		  @Override public void onServiceReady() {
		    try {
		    	GlobalState.beaconManager.startRanging(BeaconConstants.ALL_ESTIMOTE_BEACONS);
		    } catch (RemoteException e) {
		      Log.e(TAG, "Cannot start ranging", e);
		    }
		  }
		});
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		
		// Should be invoked in #onStop.
		try {
			GlobalState.beaconManager.stopRanging(BeaconConstants.ALL_ESTIMOTE_BEACONS);
		} catch (RemoteException e) {
		  Log.e("foo", "Cannot stop but it does not matter now", e);
		}
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		GlobalState.beaconManager.disconnect();
	}
	
	/**
	 * Opens the camera.
	 */
	private void openCamera() {
		Intent intent = new Intent(this, ImageActivity.class);
		startActivity(intent);
	}
}
