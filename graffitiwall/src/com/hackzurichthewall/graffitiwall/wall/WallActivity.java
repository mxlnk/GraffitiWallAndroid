package com.hackzurichthewall.graffitiwall.wall;

import java.util.List;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.estimote.sdk.BeaconManager;
import com.hackzurichthewall.graffitiwall.R;
import com.hackzurichthewall.graffitiwall.main.GlobalState;
import com.hackzurichthewall.graffitiwall.networking.services.BeaconScannerService;
import com.hackzurichthewall.graffitiwall.networking.tasks.ChallengeTask;
import com.hackzurichthewall.graffitiwall.networking.tasks.DownloadImageTask;
import com.hackzurichthewall.graffitiwall.networking.tasks.GetStreamTask;
import com.hackzurichthewall.graffitiwall.wall.dialogs.UploadCommentFragment;
import com.hackzurichthewall.graffitiwall.wall.list.StreamListViewAdapter;
import com.hackzurichthewall.graffitiwall.wall.list.StreamListViewAdapter.ContentType;
import com.hackzurichthewall.images.ImageActivity;
import com.hackzurichthewall.model.AbstractContent;
import com.hackzurichthewall.model.PictureComment;
import com.hackzurichthewall.utils.FontFactory;


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
	private Button mTakePicture;
	private Button mWriteComment;
	private List<AbstractContent> items;
	
	private ProgressDialog mDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// setting up fullscreen mode
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
		     WindowManager.LayoutParams.FLAG_FULLSCREEN);
		this.getWindow().getDecorView()
		    .setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
				
		// setting the current stream from given extra
		if (this.getIntent() != null) {
			STREAM = this.getIntent().getIntExtra(STREAM_ID, 731);
		}

		Log.i(TAG, "created wall with Stream: " + STREAM);
		
		// customized action bar
		getActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
		getActionBar().setCustomView(R.layout.layout_actionbar);
		TextView actionBarTitleTv = (TextView)  findViewById(R.id.tv_actionbar_header);
		actionBarTitleTv.setTypeface(FontFactory.getTypeface_NexaRustScriptL0(this));
		
		setContentView(R.layout.activity_wall); // setting content view to

		// click listener for refresh button
		// users got the possibility to see the newest posts
		findViewById(R.id.ib_actionbar_refresh).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// refresh
				updateList();
				
			}
		});
		
		checkBluetoothConnection();

		Log.d(TAG, "started");

		// initialize
		GlobalState.beaconManager = new BeaconManager(this);
		GlobalState.notifier = (NotificationManager) this
				.getSystemService(Context.NOTIFICATION_SERVICE);

		this.startService(new Intent(this, BeaconScannerService.class));

		this.mCommentList = (ListView) findViewById(R.id.lv_wall_list);

		
		updateList();

		
		// setting up the buttons
		this.mTakePicture = (Button) findViewById(R.id.ib_take_picture);
		this.mTakePicture.setTypeface(FontFactory
				.getTypeface_NexaRustScriptL0(this));
		this.mTakePicture.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				openCamera();
			}
		});

		this.mWriteComment = (Button) findViewById(R.id.ib_write_comment);
		this.mWriteComment.setTypeface(FontFactory
				.getTypeface_NexaRustScriptL0(this));
		this.mWriteComment.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				showCommentDialog();

			}
		});
		
		ChallengeTask.getChallengeAsync();

	}

	
	/**
	 * Updates the list with posts. Therefore fetches them (again) from server. Images will be loaded
	 * asynchronously in background.
	 */
	private void updateList() {
		
		// only showing the progress dialog if there are no items, yet
		if (this.items == null) {
			// setting up progress dialog to show loading
			this.mDialog = new ProgressDialog(this);
			this.mDialog.setMessage(getString(R.string.dialog_wait_for_stream));
			this.mDialog.show();
		}
		
		// using the usual task but override the onPostExecute method to set the list after thread completion
		new GetStreamTask() {

			@Override
			protected void onPostExecute(List<AbstractContent> result) {
				
				// checking if download was successful
				if (isTimeoutExceeded()) {
					Toast.makeText(getApplicationContext(), getString(R.string.error_timeout_exceeded), 
								Toast.LENGTH_LONG).show();
					mDialog.dismiss();
					return;
				}
				
				items = result;
				if (result != null) { // checking if there is a list
					mListAdapter = new StreamListViewAdapter(getApplicationContext(), R.layout.list_item_comment, items);
					mCommentList.setAdapter(mListAdapter);

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
				}
				mDialog.dismiss();
			}
		}.execute(STREAM); // executing with the current stream ID
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
		DialogFragment dialog = new UploadCommentFragment(STREAM);
		dialog.show(getFragmentManager(), "UploadCommentDialog");
	}

	/**
	 * Checks if bluetooth is enabled.
	 */
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
