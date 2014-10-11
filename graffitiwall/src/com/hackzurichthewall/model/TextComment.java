package com.hackzurichthewall.model;

import org.json.JSONException;
import org.json.JSONObject;

import android.widget.TextView;

import com.hackzurichthewall.graffitiwall.wall.list.StreamListViewAdapter.ContentType;
import com.hackzurichthewall.graffitiwall.wall.list.StreamListViewAdapter.ViewHolder;

/**
 * Models a usual and simple text comment.
 * 
 * @author Johannes Glöckle
 *
 */
public class TextComment extends AbstractContent {

	private String mComment;
	private String mTitle;
	
	
	public TextComment() {
		this.setmType(ContentType.TEXT_COMMENT);
	}
	
	/**
	 * Creates an comment from given {@link JSONObject}.
	 * @param obj JSONObject
	 */
	public TextComment(JSONObject obj) {
		this.setmType(ContentType.TEXT_COMMENT);
		
		if (obj != null) {
			try {
				this.mComment = obj.getString("text");
				this.mTitle = obj.getString("title");
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	
	@Override
	public void fillViewHolder(ViewHolder holder) {
		TextView txtView = holder.getmCommentTxt();
		txtView.setText(this.mComment);
		
	}
	
	@Override
	public JSONObject toJSON() {
		JSONObject result = new JSONObject();
		try {
			result.put("text", mComment);
			result.put("title", mTitle);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return result;
	}

	/**
	 * @return the comment
	 */
	public String getComment() {
		return mComment;
	}

	/**
	 * @param comment the comment to set
	 */
	public void setComment(String comment) {
		this.mComment = comment;
	}

	/**
	 * @return the mTitle
	 */
	public String getmTitle() {
		return mTitle;
	}

	/**
	 * @param mTitle the mTitle to set
	 */
	public void setmTitle(String mTitle) {
		this.mTitle = mTitle;
	}


}
