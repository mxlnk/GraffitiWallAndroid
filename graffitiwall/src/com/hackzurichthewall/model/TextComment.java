package com.hackzurichthewall.model;

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

	private String comment;
	
	
	public TextComment() {
		this.setmType(ContentType.TEXT_COMMENT);
	}

	
	@Override
	public void fillViewHolder(ViewHolder holder) {
		TextView txtView = holder.getmCommentTxt();
		txtView.setText(this.comment);
		
	}

	/**
	 * @return the comment
	 */
	public String getComment() {
		return comment;
	}

	/**
	 * @param comment the comment to set
	 */
	public void setComment(String comment) {
		this.comment = comment;
	}


}
