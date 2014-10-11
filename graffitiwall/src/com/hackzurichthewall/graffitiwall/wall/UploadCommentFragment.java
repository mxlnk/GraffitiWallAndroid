package com.hackzurichthewall.graffitiwall.wall;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.hackzurichthewall.graffitiwall.R;
import com.hackzurichthewall.graffitiwall.networking.tasks.CreatePostTask;
import com.hackzurichthewall.model.PictureComment;
import com.hackzurichthewall.model.TextComment;



/**
 * Dialog that provides a simple form to upload form for comments.
 * 
 * @author Johannes Glöckle
 */
@SuppressLint("InflateParams")
public class UploadCommentFragment extends DialogFragment {

	private EditText etTitle;
	private EditText etComment;

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
	    // Get the layout inflater
	    LayoutInflater inflater = getActivity().getLayoutInflater();
	    
	    View rootView = inflater.inflate(R.layout.alert_dialog_layout, null);
	    etTitle = (EditText) rootView.findViewById(R.id.et_enter_title);
	    etComment = (EditText) rootView.findViewById(R.id.et_enter_comment);
	    
	    // Inflate and set the layout for the dialog
	    // Pass null as the parent view because its going in the dialog layout
	    builder.setView(rootView)
	    // Add action buttons
	           .setPositiveButton(R.string.upload_comment, new DialogInterface.OnClickListener() {
	               @Override
	               public void onClick(DialogInterface dialog, int id) {
	            	   
	            	   TextComment txtComment = new TextComment();
	            	   txtComment.setComment(etComment.getText().toString());
	            	   txtComment.setmTitle(etTitle.getText().toString());
	            	 
	            	   CreatePostTask task = new CreatePostTask();
	            	   task.setmStreamId(731);
	            	   task.execute(txtComment.toJSON());
	                   
	               }
	           })
	           .setNegativeButton(R.string.cancel_comment, new DialogInterface.OnClickListener() {
	               public void onClick(DialogInterface dialog, int id) {
	                   UploadCommentFragment.this.getDialog().cancel(); // just cancels the dialog
	               }
	           });      
	    
	    return builder.create();
	}

	
	
}
