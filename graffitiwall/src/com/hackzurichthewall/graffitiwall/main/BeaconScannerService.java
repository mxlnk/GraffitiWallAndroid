package com.hackzurichthewall.graffitiwall.main;

import java.util.List;
import java.util.concurrent.TimeUnit;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.estimote.sdk.Beacon;
import com.estimote.sdk.BeaconManager;
import com.estimote.sdk.BeaconManager.RangingListener;
import com.estimote.sdk.Region;
import com.estimote.sdk.Utils;
import com.estimote.sdk.Utils.Proximity;
import com.estimote.sdk.utils.L;

public class BeaconScannerService  extends Service {

	private static final String TAG =  "BeaconScannerService";
	
	 @Override
	  public int onStartCommand(Intent intent, int flags, int startId) {
	      startMonitoring();
	      return START_STICKY;
	  }
	 
	 private void startMonitoring() {
		    if (GlobalState.beaconManager == null) {
		    	GlobalState.beaconManager = new BeaconManager(this);

		        // Configure verbose debug logging.
		        L.enableDebugLogging(true);

		        /**
		         * Scanning
		         */
		        GlobalState.beaconManager.setBackgroundScanPeriod(TimeUnit.SECONDS.toMillis(1), 1);

		        GlobalState.beaconManager.setRangingListener(new RangingListener() {

		            @Override
		            public void onBeaconsDiscovered(Region paramRegion, List<Beacon> paramList) {
		                if (paramList != null && !paramList.isEmpty()) {
		                    Beacon beacon = paramList.get(0);
		                    Proximity proximity = Utils.computeProximity(beacon);
		                    if (proximity == Proximity.IMMEDIATE) {
		                        Log.d(TAG, "entered in region " + paramRegion.getProximityUUID());
//		                        postNotification(paramRegion);
		                    } else if (proximity == Proximity.FAR) {
		                        Log.d(TAG, "exiting in region " + paramRegion.getProximityUUID());
//		                         removeNotification(paramRegion);
		                    }
		                }
		            }

		        });

		        GlobalState.beaconManager.connect(new BeaconManager.ServiceReadyCallback() {
		            @Override
		            public void onServiceReady() {
		                try {
		                    Log.d(TAG, "connected");		                  
		                    GlobalState.beaconManager.startRanging(BeaconConstants.ALL_ESTIMOTE_BEACONS);
		                    
		                } catch (RemoteException e) {
		                    Log.d("TAG", "Error while starting monitoring");
		                }
		            }
		        });
		    }
		}
	
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	
	

}