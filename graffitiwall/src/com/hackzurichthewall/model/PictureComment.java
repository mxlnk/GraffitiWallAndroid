package com.hackzurichthewall.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.Bitmap;
import android.widget.ImageView;

import com.hackzurichthewall.graffitiwall.wall.list.StreamListViewAdapter.ContentType;
import com.hackzurichthewall.graffitiwall.wall.list.StreamListViewAdapter.ViewHolder;


/**
 * Models an comment containing an image. 
 * 
 * @author Johannes Glöckle
 */
public class PictureComment extends AbstractContent {

	private Bitmap mPicture;
	private String mImageUrl;
	
	
	/**
	 * Creates a new {@link PictureComment}. Simply sets up it's type to avoid wrong behavior.
	 */
	public PictureComment() {
		this.setmType(ContentType.PICTURE_COMMENT);
	}
	
	
	public PictureComment(JSONObject json) {
		this.setmType(ContentType.PICTURE_COMMENT);
		
		if (json != null) {
			try {
				this.mImageUrl = json.getString("photo");
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	
	@Override
	public void fillViewHolder(ViewHolder holder) {
		ImageView imageView = holder.getmPicture();
		imageView.setImageBitmap(mPicture);

	}
	
	@Override
	public JSONObject toJSON() {
		JSONObject result = new JSONObject();
		
		try {
			JSONObject photo = new JSONObject();
			photo.put("url", mImageUrl);
		
			JSONArray photos = new JSONArray();
			photos.put(photo);
			result.put("text", ".");
			result.put("title", ".");
		
			result.put("photos", photos);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return result;

	}

	
	/**
	 * @return the mPicture
	 */
	public Bitmap getmPicture() {
		return mPicture;
	}


	/**
	 * @param mPicture the mPicture to set
	 */
	public void setmPicture(Bitmap mPicture) {
		this.mPicture = mPicture;
	}


	/**
	 * @return the mImageUrl
	 */
	public String getmImageUrl() {
		return mImageUrl;
	}


	/**
	 * @param mImageUrl the mImageUrl to set
	 */
	public void setmImageUrl(String mImageUrl) {
		this.mImageUrl = mImageUrl;
	}



	
}
