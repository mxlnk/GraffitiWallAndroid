package com.hackzurichthewall.graffitiwall.wall;

import com.hackzurichthewall.graffitiwall.R;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;



/**
 * Dialog that provides a simple form to upload form for comments.
 * 
 * @author Johannes Glöckle
 */
@SuppressLint("InflateParams")
public class UploadCommentFragment extends DialogFragment {


	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
	    // Get the layout inflater
	    LayoutInflater inflater = getActivity().getLayoutInflater();

	    // Inflate and set the layout for the dialog
	    // Pass null as the parent view because its going in the dialog layout
	    builder.setView(inflater.inflate(R.layout.alert_dialog_layout, null))
	    // Add action buttons
	           .setPositiveButton(R.string.upload_comment, new DialogInterface.OnClickListener() {
	               @Override
	               public void onClick(DialogInterface dialog, int id) {
	                   // TODO upload comment
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
