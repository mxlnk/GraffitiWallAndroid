package com.hackzurichthewall.graffitiwall.wall;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DialogFragment;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;

import com.estimote.sdk.BeaconManager;
import com.hackzurichthewall.graffitiwall.R;
import com.hackzurichthewall.graffitiwall.main.BeaconScannerService;
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

	public static final String TAG = WallActivity.class.getSimpleName();
	
	public static final String STREAM_ID = "streamID";
	
	private ListView mCommentList;
	private StreamListViewAdapter mListAdapter;
	private ImageButton mTakePicture;
	private ImageButton mWriteComment;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_wall); // setting content view to default layout
		
		Log.d(TAG, "started");
		
		// initialize
		GlobalState.beaconManager = new BeaconManager(this);
		GlobalState.notifier = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);

		// TODO probably want this on startup, not onCreate
		this.startService(new Intent(this, BeaconScannerService.class));
		
		this.mCommentList = (ListView) findViewById(R.id.lv_wall_list);
	
		this.mTakePicture = (ImageButton) findViewById(R.id.ib_take_picture);
		this.mTakePicture.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				openCamera();
			}
		});
		
		this.mWriteComment = (ImageButton)findViewById(R.id.ib_write_comment);
		this.mWriteComment.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				showCommentDialog();
				
			}
		});
		
		
	}
		
	// TODO stop service probably here
	@Override
	protected void onDestroy() {
		GlobalState.beaconManager.disconnect();
		
		super.onDestroy();

	}
	
	/**
	 * Opens the camera.
	 */
	private void openCamera() {
		Intent intent = new Intent(this, ImageActivity.class);
		startActivity(intent);
	}
	
	
	/**
	 * Shows the "Comment-Dialog" that allows to upload comments.
	 */
	private void showCommentDialog() {
		DialogFragment dialog = new UploadCommentFragment();
		dialog.show(getFragmentManager(), "UploadCommentDialog");
	}
}
