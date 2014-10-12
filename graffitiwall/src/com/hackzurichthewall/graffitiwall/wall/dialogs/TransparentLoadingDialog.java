package com.hackzurichthewall.graffitiwall.wall.dialogs;

import android.app.ActionBar.LayoutParams;
import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.hackzurichthewall.graffitiwall.R;


/**
 * Simple dialog that just animates an given image. While shown the image will
 * rotate.
 * 
 * @author Johannes Glöckle
 */
public class TransparentLoadingDialog extends Dialog {

	private ImageView iv;
	
	/**
	 * Setting up the dialog with given image resource as spinner.
	 * @param context the dialogs context
	 * @param resource drawable resource
	 */
	public TransparentLoadingDialog(Context context, int resource) {
		super(context, R.style.TransparentLoadingDialog);
		
    	WindowManager.LayoutParams wlmp = getWindow().getAttributes();
    	wlmp.gravity = Gravity.CENTER_HORIZONTAL;
    	getWindow().setAttributes(wlmp);
		setTitle(null);
		setCancelable(false);
		setOnCancelListener(null);
		
		// creating the dialogs layout
		LinearLayout layout = new LinearLayout(context);
		layout.setOrientation(LinearLayout.VERTICAL);
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		iv = new ImageView(context);
		iv.setImageResource(resource);
		layout.addView(iv, params);
		addContentView(layout, params);
	}
		
	@Override
	public void show() {
		super.show();
		// simple rotation animation
		RotateAnimation anim = new RotateAnimation(0.0f, 360.0f , Animation.RELATIVE_TO_SELF, .5f, Animation.RELATIVE_TO_SELF, .5f);
		anim.setInterpolator(new LinearInterpolator());
		anim.setRepeatCount(Animation.INFINITE);
		anim.setDuration(3000);
		iv.setAnimation(anim);
		iv.startAnimation(anim);
	}
}
