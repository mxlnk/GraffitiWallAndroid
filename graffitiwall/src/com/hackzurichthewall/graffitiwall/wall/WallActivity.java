package com.hackzurichthewall.graffitiwall.wall;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.estimote.sdk.BeaconManager;
import com.hackzurichthewall.graffitiwall.R;
import com.hackzurichthewall.graffitiwall.main.BeaconScannerService;
import com.hackzurichthewall.graffitiwall.main.GlobalState;
import com.hackzurichthewall.graffitiwall.networking.tasks.ChallengeTask;
import com.hackzurichthewall.graffitiwall.networking.tasks.DownloadImageTask;
import com.hackzurichthewall.graffitiwall.networking.tasks.GetStreamTask;
import com.hackzurichthewall.graffitiwall.wall.list.StreamListViewAdapter;
import com.hackzurichthewall.graffitiwall.wall.list.StreamListViewAdapter.ContentType;
import com.hackzurichthewall.images.ImageActivity;
import com.hackzurichthewall.model.AbstractContent;
import com.hackzurichthewall.model.PictureComment;
import com.hackzurichthewall.model.TextComment;

/**
 * This is the main activity. Basically contains a list with the last posted
 * objects ordered chronologically. Furthermore provides buttons to upload
 * images or comments.
 * 
 * @author Johannes Glöckle vorbildlich der mann
 */
public class WallActivity extends Activity {

	public static final String TAG = WallActivity.class.getSimpleName();

	public static final String STREAM_ID = "streamID";
	
	// default stream if no other found
	public static int STREAM = 731;
	

	private ListView mCommentList;
	private StreamListViewAdapter mListAdapter;
	private ImageButton mTakePicture;
	private ImageButton mWriteComment;
	private List<AbstractContent> items;
	
	private ProgressDialog mDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (savedInstanceState != null) {
			STREAM = savedInstanceState.getInt(STREAM_ID, 731);
		}
		
		
		setContentView(R.layout.activity_wall); // setting content view to
												// default layout

		checkBluetoothConnection();

		Log.d(TAG, "started");

		// initialize
		GlobalState.beaconManager = new BeaconManager(this);
		GlobalState.notifier = (NotificationManager) this
				.getSystemService(Context.NOTIFICATION_SERVICE);

		// TODO probably want this on startup, not onCreate
		this.startService(new Intent(this, BeaconScannerService.class));

		this.mCommentList = (ListView) findViewById(R.id.lv_wall_list);

		
		// setting up progress dialog to show loading
		this.mDialog = new ProgressDialog(this);
		this.mDialog.setMessage(getString(R.string.dialog_wait_for_stream));
		this.mDialog.show();
		
		updateList();

		this.mTakePicture = (ImageButton) findViewById(R.id.ib_take_picture);
		this.mTakePicture.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				openCamera();
			}
		});

		this.mWriteComment = (ImageButton) findViewById(R.id.ib_write_comment);
		this.mWriteComment.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				showCommentDialog();

			}
		});
		
		ChallengeTask.getChallengeAsync();

	}

	private void updateList() {
		
		new GetStreamTask() {

			@Override
			protected void onPostExecute(List<AbstractContent> result) {
				items = result;
				if (result != null) {
					mListAdapter = new StreamListViewAdapter(getApplicationContext(), R.layout.list_item_comment, items);
					mCommentList.setAdapter(mListAdapter);
				}
				
				for (int i = 0; i < items.size(); i++) {
					AbstractContent item = items.get(i);
					if (item.getmType() == ContentType.PICTURE_COMMENT) {
						PictureComment pComment = (PictureComment) item;
						new DownloadImageTask(pComment) {
							
							@Override
							public void onPostExecute(Bitmap result) {
								mListAdapter.notifyDataSetChanged();
								
							}
							
						}.execute(pComment.getmImageUrl());
					}
				}
				
				mDialog.dismiss();
			}
			
		}.execute(STREAM);
		
		
		
	}

	@Override
	protected void onDestroy() {
		// do not disconnect, because otherwise no scan
		// in background can happen
		// GlobalState.beaconManager.disconnect();

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

	private void checkBluetoothConnection() {
		BluetoothAdapter mBluetoothAdapter = BluetoothAdapter
				.getDefaultAdapter();
		if (mBluetoothAdapter == null) {
			// Device does not support Bluetooth
			Toast.makeText(this,
					"Unfortunatly your device does not support bluetooth.",
					Toast.LENGTH_LONG).show();
		} else {
			if (!mBluetoothAdapter.isEnabled()) {
				// Bluetooth is not enabled
				Toast.makeText(this, "Please enable bluetooth on your phone.",
						Toast.LENGTH_LONG).show();
			}
		}
	}
}
