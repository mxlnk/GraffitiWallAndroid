package com.hackzurichthewall.images;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.MediaStore.MediaColumns;
import android.support.v7.app.ActionBar;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.hackzurichthewall.graffitiwall.R;
import com.hackzurichthewall.graffitiwall.networking.tasks.CreatePostTask;
import com.hackzurichthewall.graffitiwall.networking.tasks.UploadImageTask;
import com.hackzurichthewall.graffitiwall.wall.WallActivity;
import com.hackzurichthewall.utils.FontFactory;

/**
 * Activity that allows taking pictures with the camera or loading one from gallery.
 * 
 * @author Johannes Glöckle
 */
public class ImageActivity extends Activity implements UploadImageTask.AsyncResponse {

	private final int REQUEST_CAMERA = 200;
	private final int SELECT_FILE = 100;
	
	
	private Button mRefresh;
	private Button mUpload;
	private ImageView mPreview;
	
	private Bitmap mCurrentImage = null;
	private String mCurrentPath = null;
	
	
	ProgressDialog mDialog;
	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// setting fullscreen up
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
		     WindowManager.LayoutParams.FLAG_FULLSCREEN);
		this.getWindow().getDecorView()
		    .setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
		
		// using customized action bar
		getActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
		getActionBar().setCustomView(R.layout.layout_actionbar);
		TextView actionBarTitleTv = (TextView)  findViewById(R.id.tv_actionbar_header);
		actionBarTitleTv.setTypeface(FontFactory.getTypeface_NexaRustScriptL0(this));
		
		
		setContentView(R.layout.activity_image);
		
		// setting up the dialog for uploading
		this.mDialog = new ProgressDialog(this);
		this.mDialog.setMessage(getString(R.string.dialog_image_upload));
		this.mDialog.setCancelable(false);
		
		this.mRefresh = (Button) findViewById(R.id.ib_refresh);
		this.mRefresh.setTypeface(FontFactory
				.getTypeface_NexaRustScriptL0(this));
		this.mUpload = (Button) findViewById(R.id.ib_upload);
		this.mUpload.setTypeface(FontFactory
				.getTypeface_NexaRustScriptL0(this));
		this.mPreview = (ImageView) findViewById(R.id.iv_image_preview);
		
		// setting click listeners
		mRefresh.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mCurrentImage = null;
				mCurrentPath = null;
				takePicture();
			}
		});
		
		
		// listener for upload
		mUpload.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (mCurrentPath == null) {
					Toast.makeText(getApplicationContext(), getString(R.string.error_no_image_path), 
											Toast.LENGTH_LONG).show();
				} else {
					mDialog.show();
					uploadImage();
				}
				
			}
		});
		
		if (mCurrentImage == null && savedInstanceState == null) {
			takePicture();
		}
	}


	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		if (resultCode == RESULT_OK) { // everything ok?
			if (requestCode == REQUEST_CAMERA) { // data from camera
				File f = new File(Environment.getExternalStorageDirectory()
						.toString());
				for (File temp : f.listFiles()) {
					if (temp.getName().equals("temp.jpg")) {
						f = temp;
						break;
					}
				}
				try {
					
					// getting the bitmap
					Bitmap bm;
					BitmapFactory.Options btmapOptions = new BitmapFactory.Options();

					int rotation = getImageOrientation(f.getAbsolutePath());
					bm = BitmapFactory.decodeFile(f.getAbsolutePath(),
							btmapOptions);
					if (rotation != 0) {
						bm = rotateBitmap(bm, rotation); // rotating if necessary
					}
					
					// setting the file path to be able tu upload the image
					this.mCurrentPath = f.getAbsolutePath();

					// bm = Bitmap.createScaledBitmap(bm, 70, 70, true);
					mPreview.setImageBitmap(bm);

					String path = android.os.Environment
							.getExternalStorageDirectory()
							+ File.separator
							+ "Phoenix" + File.separator + "default";
					f.delete();
					OutputStream fOut = null;
					File file = new File(path, String.valueOf(System
							.currentTimeMillis()) + ".jpg");
					try {
						fOut = new FileOutputStream(file);
						bm.compress(Bitmap.CompressFormat.JPEG, 85, fOut);
						fOut.flush();
						fOut.close();
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					} catch (Exception e) {
						e.printStackTrace();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else if (requestCode == SELECT_FILE) { // if data from gallery
				Uri selectedImageUri = data.getData();

				String tempPath = getPath(selectedImageUri, ImageActivity.this);
				Bitmap bm; // getting the bitmap
				BitmapFactory.Options btmapOptions = new BitmapFactory.Options();
				int rotation = getImageOrientation(tempPath);
				bm = BitmapFactory.decodeFile(tempPath, btmapOptions);
				if (rotation != 0) {
					bm = rotateBitmap(bm, rotation); // rotating if necessary
				}
				
				// saving the image's path
				this.mCurrentPath = tempPath;
				
				
				this.mCurrentImage = bm;
				mPreview.setImageBitmap(bm); // showing the bitmap
			}
		}
		
	}
	
	
	/**
	 * Starts an intent chooser to select the source of the image to get.
	 */
	private void takePicture() {
		final CharSequence[] items = { "Take Photo", "Choose from Library",
		"Cancel" };

		// creates a dialog containing the list of possible intents
		AlertDialog.Builder builder = new AlertDialog.Builder(ImageActivity.this);
		builder.setTitle("Add Photo!");
		builder.setItems(items, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int item) {
				if (items[item].equals("Take Photo")) {
					Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
					File f = new File(android.os.Environment
							.getExternalStorageDirectory(), "temp.jpg");
					intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
					startActivityForResult(intent, REQUEST_CAMERA);
				} else if (items[item].equals("Choose from Library")) {
					Intent intent = new Intent(
							Intent.ACTION_PICK,
							android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
					intent.setType("image/*");
					startActivityForResult(
							Intent.createChooser(intent, "Select File"),
							SELECT_FILE);
				} else if (items[item].equals("Cancel")) {
					dialog.dismiss();
				}
			}
		});
		builder.show();
	}



	@Override
	public void onBackPressed() {
		finish();
	}
	
	
	/**
	 * Gets the file's path.
	 * @param uri file identifier
	 * @param activity 
	 * @return the file's path
	 */
	@SuppressWarnings("deprecation")
	public String getPath(Uri uri, Activity activity) {
		String[] projection = { MediaColumns.DATA };
		Cursor cursor = activity
				.managedQuery(uri, projection, null, null, null);
		int column_index = cursor.getColumnIndexOrThrow(MediaColumns.DATA);
		cursor.moveToFirst();
		return cursor.getString(column_index);
	}
	
	/**
	 * Gets the orientation of an image. Therefore reads exif data.
	 * @param imagePath path of image
	 * @return angle in degrees
	 */
	private int getImageOrientation(String imagePath){
	    int rotate = 0;
	    try {
	    	File imageFile = new File(imagePath);
	    	ExifInterface exif = new ExifInterface(
    			imageFile.getAbsolutePath());
	        int orientation = exif.getAttributeInt(
                ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_NORMAL);

	        switch (orientation) {
	        case ExifInterface.ORIENTATION_ROTATE_270:
        		rotate = 270;
    			break;
	        case ExifInterface.ORIENTATION_ROTATE_180:
        		rotate = 180;
        		break;
	        case ExifInterface.ORIENTATION_ROTATE_90:
	        	rotate = 90;
	        	break;
	         }
	     } catch (IOException e) {
	         e.printStackTrace();
	     }
	    return rotate;
	 }
	 
	 
	/**
	 * Rotates a given bitmap. Rotation is given in degree.
	 * @param bmp bitmap to rotate
	 * @param rotation degrees
	 * @return rotated bitmap
	 */
	private Bitmap rotateBitmap(Bitmap bmp, int rotation) {
		Matrix matrix = new Matrix();
		matrix.postRotate(rotation);
		Bitmap rotatedBitmap = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(),
				 									bmp.getHeight(), matrix, true);
		bmp.recycle();
		return rotatedBitmap;
	}


	/**
	 * Uploads an file with path stored in mCurrentPath.
	 */
	private void uploadImage() {
		new UploadImageTask(this).execute(mCurrentPath);
	}
	 
	 
	 @Override
	 public void uploadPost(JSONObject post) {
		 CreatePostTask task = new CreatePostTask() {

			@Override
			protected void onPostExecute(Void result) {
				super.onPostExecute(result);
				
				// dismissing the dialog...
				mDialog.dismiss();
				// ... and finishing the activity.
				finish();
			}
			 
		 };
		
		 task.setmStreamId(WallActivity.STREAM);
		
		 task.execute(post);
	 }
	

}
