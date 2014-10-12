package com.hackzurichthewall.graffitiwall.main;

import java.util.List;

import android.app.Notification.Builder;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.estimote.sdk.Beacon;
import com.estimote.sdk.BeaconManager;
import com.estimote.sdk.BeaconManager.MonitoringListener;
import com.estimote.sdk.Region;
import com.estimote.sdk.Utils;
import com.estimote.sdk.utils.L;
import com.hackzurichthewall.graffitiwall.R;
import com.hackzurichthewall.graffitiwall.wall.WallActivity;

public class BeaconScannerService  extends Service {

	private static final String TAG = BeaconScannerService.class.getSimpleName();
	
	public static final int searchTime = 15000; 
	
	 @Override
	  public int onStartCommand(Intent intent, int flags, int startId) {
	      startMonitoring();
	      return START_STICKY;
	  }
	 
	private void startMonitoring() {
		GlobalState.beaconManager = new BeaconManager(this);

        // Configure verbose debug logging.
        L.enableDebugLogging(true);

        /**
         * We want monitoring of a region
         */
        Log.d(TAG, "startingToMonitor");
		        
        GlobalState.beaconManager.setBackgroundScanPeriod(searchTime, 0);
        GlobalState.beaconManager.setMonitoringListener(new MonitoringListener() {
        	// we are in proximity to one of our beacons
        	@Override
        	public void onEnteredRegion(Region region, List<Beacon> beacons) {
        		// TODO check if user is already hooked up, was vernünftiges für ids/cancellation einfallen lassen
				// find closest one
				Beacon closest = BeaconConstants.closestBeacon(beacons, 200);
						
				if (closest != null) {
					Log.i(TAG, "entered region: " + region);
					Log.i(TAG, "we found " + beacons.size() + " beacons: " + beacons);
					Log.i(TAG, "closest one: " + closest);
					Log.i(TAG, "distance closest: " + Utils.computeAccuracy(closest));
							
					showNotification(closest);
				} else {
					Log.e(TAG, "Nearest beacon is not near enough.");
					/*try {
						Thread.sleep(2000);
					} catch (InterruptedException e) {
						Log.e(TAG, "could not sleep", e);
					}*/
					startMonitoring();
				}	
			}

			// we aren't (anymore?) in proximity to one of our beacons
			// Problem! Exited Region event is caused by a beacon which is not
			// the nearest one
			@Override
			public void onExitedRegion(Region region) {
				//TODO if notification present, revoke
				Log.d(TAG, "left region: " + region);
					if (GlobalState.notifier != null) {
						GlobalState.notifier.cancel(0);
					}
				}
	        });
		        
        GlobalState.beaconManager.connect(new BeaconManager.ServiceReadyCallback() {
        	@Override
        	public void onServiceReady() {
				 try {
					 GlobalState.beaconManager.startMonitoring(BeaconConstants.ALL_ESTIMOTE_BEACONS_33333);
					 GlobalState.beaconManager.startMonitoring(BeaconConstants.IPAD_BEACON);
				 } catch (RemoteException e) {
					 Log.d(TAG, "Error while starting monitoring");
				 }
			 }
		});
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		// no binding desired in this case
		return null;
	}
	
	/**
	 * Shows a notification.
	 * 
	 * @param closestBeacon 
	 * 		the button which is the closest of all detected beacons
	 */
	private void showNotification(Beacon closestBeacon) {
		// construct notification
		Builder builder = new Builder(BeaconScannerService.this)
		.setContentTitle("much notification")
		.setContentText(closestBeacon.getMajor() + " " + closestBeacon.getMinor())
		.setSmallIcon(R.drawable.notification)
		.setAutoCancel(true)
		.setVibrate(new long[] { 7, 2, 7 , 2}); // TODO imagine fancy pattern and add sound
		
		// build intent for notification
		Intent resultIntent = new Intent(BeaconScannerService.this, WallActivity.class);
		
		//hardcoded for presentation with iPad as iBeacon
		if (closestBeacon.getProximityUUID().equals(BeaconConstants.IPAD_BEACON.getProximityUUID())
			&& closestBeacon.getMajor() == BeaconConstants.IPAD_BEACON.getMajor()
			&& closestBeacon.getMinor() == BeaconConstants.IPAD_BEACON.getMinor())
			resultIntent.putExtra(WallActivity.STREAM_ID, 737);
		else
			resultIntent.putExtra(WallActivity.STREAM_ID, closestBeacon.getMinor());
		
		
		// ensures that you can get back to where you accepted the notification
		TaskStackBuilder stackBuilder = TaskStackBuilder.create(BeaconScannerService.this);
		stackBuilder.addParentStack(WallActivity.class);
		stackBuilder.addNextIntent(resultIntent);
		PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
		builder.setContentIntent(resultPendingIntent);

		if (GlobalState.notifier == null)
			GlobalState.notifier  =  (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
			
		GlobalState.notifier.notify(0, builder.build());
		
		Log.i(TAG, "Notification showed");
	}
}