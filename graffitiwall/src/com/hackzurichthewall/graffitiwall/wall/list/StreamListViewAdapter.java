package com.hackzurichthewall.graffitiwall.wall.list;

import java.util.List;
import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.hackzurichthewall.graffitiwall.R;
import com.hackzurichthewall.model.AbstractContent;



/**
 * Adapter connecting the main list for comments and pictures with the actual data.
 * 
 * @author Johannes Glöckle
 */
@SuppressLint("InflateParams")
public class StreamListViewAdapter extends ArrayAdapter<AbstractContent>  {
	
	/**
	 * Defines the different supported types of content. 
	 * NOTICE: "ABSTRACT" is just for {@link AbstractContent} which is abstract.
	 * 
	 * @author Johannes Glöckle
	 */
	public enum ContentType {
		TEXT_COMMENT,
		PICTURE_COMMENT,
		ABSTRACT
	}

	private LayoutInflater mInflater;
	
	private List<AbstractContent> items;
	
	
	/**
	 * Creates a new adapter with given list of items and layout resource to use.
	 * @param context current context
	 * @param resource resource to use
	 * @param items list of items to display
	 */
	public StreamListViewAdapter(Context context, int resource,
			List<AbstractContent> items) {
		super(context, resource, items);
	
		this.mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.items = items;
	}


	@Override
	public AbstractContent getItem(int position) {
		
		if (position < 0 || position > items.size()) { // checking argument validity
			throw new IllegalArgumentException("Position has to be between zero and number of items.");
		}
		
		return items.get(position);
	}
	
	@Override
	public int getItemViewType(int position) {
		switch (items.get(position).getmType()) {
		case ABSTRACT: // actually should never happen since "ABSTRACT" is only for AbstractContent
			return -1;
		case PICTURE_COMMENT:
			return 1;
		case TEXT_COMMENT:
			return 0;
		default:
			return -1;
		
		}
	}


	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;

        if (convertView == null) { // happens if there is no old item to recycle
            holder = new ViewHolder(); // creating new holder
            switch (getItemViewType(position)) {
                case 0: // usual text comment
                    convertView = mInflater.inflate(R.layout.list_item_comment, null);
                    holder.setmCommentTxt((TextView)convertView.findViewById(R.id.tv_comment));
                    break;
                case 1: // picture comment
                    convertView = mInflater.inflate(R.layout.list_item_picture, null);
                    holder.setmPicture((ImageView)convertView.findViewById(R.id.iv_picture));
                    break;
            }
            convertView.setTag(holder);
        } else { // old list item is going to be reused
            holder = (ViewHolder)convertView.getTag();
        }
        
        getItem(position).fillViewHolder(holder); // fills the given view holder with information
        return convertView;
	}
	
	
	/**
	 * Holds the view elements for list entries.
	 * 
	 * @author Johannes Glöckle
	 */
	public static class ViewHolder {
		
		private TextView mCommentTxt; // holding the comment text
		
		private ImageView mPicture; // holding the image if available

		/**
		 * @return the mCommentTxt
		 */
		public TextView getmCommentTxt() {
			return mCommentTxt;
		}

		/**
		 * @param mCommentTxt the mCommentTxt to set
		 */
		public void setmCommentTxt(TextView mCommentTxt) {
			this.mCommentTxt = mCommentTxt;
		}

		/**
		 * @return the mPicture
		 */
		public ImageView getmPicture() {
			return mPicture;
		}

		/**
		 * @param mPicture the mPicture to set
		 */
		public void setmPicture(ImageView mPicture) {
			this.mPicture = mPicture;
		}
		
		
	}

	
	

}
