package com.hackzurichthewall.model;

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
	
	
	/**
	 * Creates a new {@link PictureComment}. Simply sets up it's type to avoid wrong behavior.
	 */
	public PictureComment() {
		this.setmType(ContentType.PICTURE_COMMENT);
	}
	
	
	@Override
	public void fillViewHolder(ViewHolder holder) {
		ImageView imageView = holder.getmPicture();
		imageView.setImageBitmap(mPicture);

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
	
}
