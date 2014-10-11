package com.hackzurichthewall.graffitiwall.wall;

import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;

import com.hackzurichthewall.graffitiwall.R;
import com.hackzurichthewall.graffitiwall.wall.list.StreamListViewAdapter;
import com.hackzurichthewall.model.AbstractContent;
import com.hackzurichthewall.model.TextComment;


/**
 * This is the main activity. Basically contains a list with the last posted objects ordered
 * chronologically. Furthermore provides buttons to upload images or comments.
 * 
 * @author Johannes Glöckle
 */
public class WallActivity extends Activity {

	private ListView mCommentList;
	private StreamListViewAdapter mListAdapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_wall); // setting content view to default layout
		
		this.mCommentList = (ListView) findViewById(R.id.lv_wall_list);
		
		// TODO erase if working.
		ArrayList<AbstractContent> comments = new ArrayList<AbstractContent>(4);
		for (int i = 0; i < 4; i++) {
			TextComment comment = new TextComment();
			comment.setComment("Comment " + i);
			comments.add(comment);
		}
		
		this.mListAdapter = new StreamListViewAdapter(this, R.layout.list_item_comment, comments);
		this.mCommentList.setAdapter(mListAdapter);
		
		
	}

}
