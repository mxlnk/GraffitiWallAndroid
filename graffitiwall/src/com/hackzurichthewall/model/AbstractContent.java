package com.hackzurichthewall.model;

import org.json.JSONObject;

import com.hackzurichthewall.graffitiwall.wall.list.StreamListViewAdapter;
import com.hackzurichthewall.graffitiwall.wall.list.StreamListViewAdapter.ContentType;


/**
 * Models a simple content that is inherited by several different types of content.
 *  
 * @author Johannes Glöckle
 */
public abstract class AbstractContent {
	
	private Long mTimeStamp;
	private StreamListViewAdapter.ContentType mType = ContentType.ABSTRACT;
	
	
	// add here further main information here like author...
	
	
	/**
	 * Forces all subclasses to implement a method that fills the given view holder.
	 * @param holder given holder
	 */
	public abstract void fillViewHolder(StreamListViewAdapter.ViewHolder holder);
	
	
	/**
	 * Classes have to provide a method to convert them to a JSON object.
	 * @return the JSON object
	 */
	public abstract JSONObject toJSON();
	
	
	public Long getmTimeStamp() {
		return mTimeStamp;
	}

	public void setmTimeStamp(Long mTimeStamp) {
		this.mTimeStamp = mTimeStamp;
	}

	
	
	/**
	 * @return the mType
	 */
	public StreamListViewAdapter.ContentType getmType() {
		return mType;
	}

	/**
	 * @param mType the mType to set
	 */
	protected void setmType(StreamListViewAdapter.ContentType mType) {
		this.mType = mType;
	}
	
}
