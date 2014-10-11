package com.hackzurichthewall.graffitiwall.main;

import java.util.List;

import com.estimote.sdk.Beacon;
import com.estimote.sdk.Region;
import com.estimote.sdk.Utils;

public class BeaconConstants {

	private static final String ESTIMOTE_PROXIMITY_UUID = "B9407F30-F5F8-466E-AFF9-25556B57FE6D";
	// only looking for UUID (atm all estimote beacons)
	public static final Region ALL_ESTIMOTE_BEACONS = new Region("OurAppName", ESTIMOTE_PROXIMITY_UUID, null, null);
	
	/**
	 * 
	 * @param beaconsInRange the beacons of which you want the nearest
	 * @return null if none found, closest beacon of the given otherwise
	 */
	public static Beacon closestBeacon(List<Beacon> beaconsInRange) {
		Beacon closestBeacon = null;
		
		if (beaconsInRange != null && !beaconsInRange.isEmpty()) {
			closestBeacon = beaconsInRange.get(0);
			for (Beacon beacon : beaconsInRange)
				if (Utils.computeAccuracy(beacon) < Utils.computeAccuracy(closestBeacon))
					closestBeacon = beacon;
		}
		
		return closestBeacon;
	}
}
