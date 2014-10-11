package com.hackzurichthewall.graffitiwall.wall;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.NotificationManager;
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
import android.widget.TextView;
import android.widget.Toast;

import com.estimote.sdk.BeaconManager;
import com.hackzurichthewall.graffitiwall.R;
import com.hackzurichthewall.graffitiwall.main.BeaconScannerService;
import com.hackzurichthewall.graffitiwall.main.GlobalState;

import com.hackzurichthewall.graffitiwall.networking.tasks.ChallengeTask;



import com.hackzurichthewall.graffitiwall.wall.list.StreamListViewAdapter;
import com.hackzurichthewall.images.ImageActivity;
import com.hackzurichthewall.model.AbstractContent;
import com.hackzurichthewall.model.PictureComment;
import com.hackzurichthewall.model.TextComment;
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

	private ListView mCommentList;
	private StreamListViewAdapter mListAdapter;
	private ImageButton mTakePicture;
	private ImageButton mWriteComment;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

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

		List<AbstractContent> items = new ArrayList<AbstractContent>();
		PictureComment pic0 = new PictureComment();
		PictureComment pic1 = new PictureComment();
		Bitmap icon = BitmapFactory.decodeResource(this.getResources(),
				R.drawable.model02);

		pic0.setmPicture(icon);
		pic1.setmPicture(icon);

		// TextComment
		TextComment txtComment0 = new TextComment();
		txtComment0.setComment("Where is my bacon?");
		
		TextComment txtComment1 = new TextComment();
		txtComment1.setComment("Go to HackZurich they said...\n There will be wifi they said...");

		items.add(pic0);
		items.add(txtComment1);
		items.add(pic1);
		items.add(txtComment0);
		items.add(pic0);

		mListAdapter = new StreamListViewAdapter(this, R.id.lv_wall_list, items);

		mCommentList.setAdapter(mListAdapter);

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
