package com.hackzurichthewall.utils;

import android.content.Context;
import android.graphics.Typeface;

public class FontFactory {

	

	public static Typeface getTypeface_NexaRustScriptL0(Context context) {

		return Typeface.createFromAsset(context.getAssets(), 
				"fonts/NexaRustScriptL-0.otf");

	}
}
