package com.hackzurichthewall.graffitiwall.main;

import java.util.List;

import android.util.Log;

import com.estimote.sdk.Beacon;
import com.estimote.sdk.Region;
import com.estimote.sdk.Utils;

public class BeaconConstants {

	private static final String ESTIMOTE_PROXIMITY_UUID = "B9407F30-F5F8-466E-AFF9-25556B57FE6D";
	public static final Region ALL_ESTIMOTE_BEACONS_33333 = new Region("bWallEstimote", ESTIMOTE_PROXIMITY_UUID, 33333, null);
	
	private static final String IPAD_PROXIMITY_UUID = "8492e75f-4fd6-469d-b132-043fe94921d8";
	public static final Region IPAD_BEACON = new Region("bWalliPad", IPAD_PROXIMITY_UUID, 2926, 4048);
	
	private static final String TAG = "BeaconConstants";
	
	/**
	 * Returns the nearest beacon of the given beacons.
	 * 
	 * For a restricition to beacons only in a certain proximity, like
	 * nearer then e.g. 5m pass a minDistance parameter.
	 * @param beaconsInRange the beacons of which you want the nearest
	 * @return null if none found, closest beacon of the given otherwise
	 */
	public static Beacon closestBeacon(List<Beacon> beaconsInRange) {
		return closestBeacon(beaconsInRange, Double.MAX_VALUE);
	}
	
	/**
	 * Returns the closest beacon with the given minimum distance
	 * or null if the beacons are too far away.
	 * @param beaconsInRange
	 * @param maxDistance
	 * 		indicates the maximum distance a beacons should have
	 * 		otherwise it is too far away and will not be considered
	 * @return
	 */
	public static Beacon closestBeacon(List<Beacon> beaconsInRange, double maxDistance) { 
		Beacon closestBeacon = null;
		double currentClosestDistance = Double.MAX_VALUE;
		if (beaconsInRange != null && !beaconsInRange.isEmpty()) {
			closestBeacon = beaconsInRange.get(0);
			for (Beacon beacon : beaconsInRange) {
				double currentDistance = Utils.computeAccuracy(beacon);
				Log.i(TAG, "Beacon found with distance: " + currentDistance);
				Log.i(TAG, "and proximity: " + Utils.computeProximity(beacon));
				if (currentDistance < currentClosestDistance) {
					closestBeacon = beacon;
					currentClosestDistance = currentDistance;
				}
			}			
		}
		
		if (currentClosestDistance <= maxDistance) {
			return closestBeacon;
		} else {
			return null;
		}
	}
}
