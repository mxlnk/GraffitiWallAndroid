package com.hackzurichthewall.graffitiwall.wall;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.hackzurichthewall.graffitiwall.R;
import com.hackzurichthewall.graffitiwall.networking.tasks.CreatePostTask;
import com.hackzurichthewall.model.TextComment;



/**
 * Dialog that provides a simple form to upload form for comments.
 * 
 * @author Johannes Glöckle
 */
@SuppressLint("InflateParams")
public class UploadCommentFragment extends DialogFragment {

	private static final String TAG = UploadCommentFragment.class.getSimpleName();
	
	//private EditText etTitle;
	private EditText etComment;
	
	private int stream; //default

	public UploadCommentFragment(int stream) {
		super();
		this.stream = stream;
	}
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
	    // Get the layout inflater
	    LayoutInflater inflater = getActivity().getLayoutInflater();
	    
	    View rootView = inflater.inflate(R.layout.alert_dialog_layout, null);
	    //etTitle = (EditText) rootView.findViewById(R.id.et_enter_title);
	    etComment = (EditText) rootView.findViewById(R.id.et_enter_comment);
	    
	    // Inflate and set the layout for the dialog
	    // Pass null as the parent view because its going in the dialog layout
	    builder.setView(rootView)
	    // Add action buttons
	           .setPositiveButton(R.string.upload_comment, new DialogInterface.OnClickListener() {
	               @Override
	               public void onClick(DialogInterface dialog, int id) { // nothing to do becaus will be overwritten
	               }
	           })
	           .setNegativeButton(R.string.cancel_comment, new DialogInterface.OnClickListener() {
	               public void onClick(DialogInterface dialog, int id) {
	                   UploadCommentFragment.this.getDialog().cancel(); // just cancels the dialog
	               }
	           });      
	    
	    return builder.create();
	}


	@Override
	public void onStart() {
		super.onStart();    //super.onStart() is where dialog.show() is actually called on the underlying dialog, so we have to do it after this point
	    AlertDialog d = (AlertDialog)getDialog();
	    if(d != null)
	    {
	        Button positiveButton = (Button) d.getButton(Dialog.BUTTON_POSITIVE);
	        positiveButton.setOnClickListener(new View.OnClickListener()
	                {
	                    @Override
	                    public void onClick(View v)
	                    {
	                    	Log.i(TAG, "should comment now..");
	                    	// title not used
//	                    	// checking the title input field
//	                    	 if (etTitle.getText().toString().length() == 0) {
//                    		 	 etTitle.setError(getString(R.string.error_emtpy));
//	  	            		   	 return;
//							 } else {
//								 etTitle.setError(null);
//							 }
							 
							// checking the comment field   
							 if (etComment.getText().toString().length() == 0) {
								 etComment.setError(getString(R.string.error_emtpy));
								 return;
							 } else {
								 etComment.setError(null);
							 }
							
							 // finally creating the comment
							 TextComment txtComment = new TextComment();
							 txtComment.setComment(etComment.getText().toString());
							 txtComment.setmTitle("."); // hardcoded, because title is not used
							 
							 // and loading it up
							 CreatePostTask task = new CreatePostTask();
							 task.setmStreamId(stream);
							 task.execute(txtComment.toJSON());
							 dismiss();
            			}
	                });
	    }
	}
}
