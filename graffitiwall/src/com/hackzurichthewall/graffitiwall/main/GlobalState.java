package com.hackzurichthewall.graffitiwall.main;

import android.app.Application;
import android.app.NotificationManager;
import com.estimote.sdk.BeaconManager;

/**
 * 
 * Class which is used to manage global states
 * which should not live also if the activities where
 * shut down.
 * 
 * @author orlando
 *
 */
public class GlobalState extends Application {
	
	
	public static BeaconManager beaconManager;

	public static NotificationManager notifier;

}
