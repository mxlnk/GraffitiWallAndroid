package com.hackzurichthewall.graffitiwall.wall;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;
import android.widget.ListView;

import com.estimote.sdk.Beacon;
import com.estimote.sdk.BeaconManager;
import com.estimote.sdk.BeaconManager.MonitoringListener;
import com.estimote.sdk.Region;
import com.hackzurichthewall.graffitiwall.R;
import com.hackzurichthewall.graffitiwall.wall.list.StreamListViewAdapter;
import com.hackzurichthewall.model.AbstractContent;
import com.hackzurichthewall.model.TextComment;


/**
 * This is the main activity. Basically contains a list with the last posted objects ordered
 * chronologically. Furthermore provides buttons to upload images or comments.
 * 
 * @author Johannes Glöckle vorbildlich der mann
 */
public class WallActivity extends Activity {

	public static final String TAG = "WALL_ACTIVITY";
	
	//not sure if needed..
	private static final String ESTIMOTE_PROXIMITY_UUID = "B9407F30-F5F8-466E-AFF9-25556B57FE6D";
	private static final Region ALL_ESTIMOTE_BEACONS = new Region("regionId", ESTIMOTE_PROXIMITY_UUID, null, null);
	
	private ListView mCommentList;
	private StreamListViewAdapter mListAdapter;
	
	private BeaconManager beaconManager;
	private NotificationManager notificationManager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_wall); // setting content view to default layout
		
		// initialize
		this.beaconManager = new BeaconManager(this);
		this.notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		 
		this.mCommentList = (ListView) findViewById(R.id.lv_wall_list);
		
		
		// TODO erase if working.
		ArrayList<AbstractContent> comments = new ArrayList<AbstractContent>(4);
		for (int i = 0; i < 4; i++) {
			TextComment comment = new TextComment();
			comment.setComment("Comment " + i);
			comments.add(comment);
		}
		
		this.mListAdapter = new StreamListViewAdapter(this, R.layout.list_item_comment, comments);
		this.mCommentList.setAdapter(mListAdapter);
		
		// ranging: find out distance to device
		beaconManager.setRangingListener(new BeaconManager.RangingListener() {
		  @Override public void onBeaconsDiscovered(Region region, List<Beacon> beacons) {
		    Log.d(TAG, "Ranged beacons: " + beacons);
		  }
		});
		
		// monitoring: scan for beacons periodically, change default scan intervall for more responsiveness
		beaconManager.setMonitoringListener(new MonitoringListener() {

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
		beaconManager.connect(new BeaconManager.ServiceReadyCallback() {
		  @Override public void onServiceReady() {
		    try {
		      beaconManager.startRanging(ALL_ESTIMOTE_BEACONS);
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
		  beaconManager.stopRanging(ALL_ESTIMOTE_BEACONS);
		} catch (RemoteException e) {
		  Log.e("foo", "Cannot stop but it does not matter now", e);
		}
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		this.beaconManager.disconnect();
	}

}
