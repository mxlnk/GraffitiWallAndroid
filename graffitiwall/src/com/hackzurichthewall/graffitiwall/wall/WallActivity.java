package com.hackzurichthewall.graffitiwall.wall;

import java.util.ArrayList;
import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import com.estimote.sdk.BeaconManager;
import com.hackzurichthewall.graffitiwall.R;
import com.hackzurichthewall.graffitiwall.main.BeaconScannerService;
import com.hackzurichthewall.graffitiwall.main.GlobalState;
import com.hackzurichthewall.graffitiwall.wall.list.StreamListViewAdapter;
import com.hackzurichthewall.model.AbstractContent;
import com.hackzurichthewall.model.PictureComment;
import com.hackzurichthewall.model.TextComment;


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
		
		
		// TODO erase if working.
		ArrayList<AbstractContent> comments = new ArrayList<AbstractContent>(4);
		for (int i = 0; i < 4; i++) {
			TextComment comment = new TextComment();
			comment.setComment("Comment " + i);
			comments.add(comment);
			PictureComment pComment = new PictureComment();
			Bitmap bmp = BitmapFactory.decodeResource(this.getResources(),
                    R.drawable.ic_launcher);
			pComment.setmPicture(bmp);
			comments.add(pComment);
		}
		
		// setting the list adapter and connect it with the list
		this.mListAdapter = new StreamListViewAdapter(this, R.layout.list_item_comment, comments);
		this.mCommentList.setAdapter(mListAdapter);
	}
		
	// TODO stop service probably here
	@Override
	protected void onDestroy() {
		GlobalState.beaconManager.disconnect();
		
		super.onDestroy();

	}

}
