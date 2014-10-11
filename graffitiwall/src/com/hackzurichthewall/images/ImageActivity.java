package com.hackzurichthewall.images;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import android.app.Activity;
import android.app.AlertDialog;
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
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.hackzurichthewall.graffitiwall.R;

/**
 * Activity that allows taking pictures with the camera or loading one from gallery.
 * 
 * @author Johannes Glöckle
 */
public class ImageActivity extends Activity {

	private final int REQUEST_CAMERA = 200;
	private final int SELECT_FILE = 100;
	
	private boolean mChooserVisible = false;
	
	
	private ImageButton mRefresh;
	private ImageButton mUpload;
	private ImageView mPreview;
	
	private Bitmap mCurrentImage = null;
	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_image);
		
		this.mRefresh = (ImageButton) findViewById(R.id.ib_refresh);
		this.mUpload = (ImageButton) findViewById(R.id.ib_upload);
		this.mPreview = (ImageView) findViewById(R.id.iv_image_preview);
		
		// setting click listeners
		mRefresh.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mCurrentImage = null;
				takePicture();
			}
		});
		
		
		mUpload.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
			}
		});
		
		if (mCurrentImage == null && savedInstanceState == null) {
			takePicture();
		}
	}


	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		mChooserVisible = false;
		
		if (resultCode == RESULT_OK) {
			if (requestCode == REQUEST_CAMERA) {
				File f = new File(Environment.getExternalStorageDirectory()
						.toString());
				for (File temp : f.listFiles()) {
					if (temp.getName().equals("temp.jpg")) {
						f = temp;
						break;
					}
				}
				try {
					Bitmap bm;
					BitmapFactory.Options btmapOptions = new BitmapFactory.Options();

					int rotation = getImageOrientation(f.getAbsolutePath());
					bm = BitmapFactory.decodeFile(f.getAbsolutePath(),
							btmapOptions);
					if (rotation != 0) {
						bm = rotateBitmap(bm, rotation);
					}

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
			} else if (requestCode == SELECT_FILE) {
				Uri selectedImageUri = data.getData();

				String tempPath = getPath(selectedImageUri, ImageActivity.this);
				Bitmap bm;
				BitmapFactory.Options btmapOptions = new BitmapFactory.Options();
				int rotation = getImageOrientation(tempPath);
				bm = BitmapFactory.decodeFile(tempPath, btmapOptions);
				if (rotation != 0) {
					bm = rotateBitmap(bm, rotation);
				}
				this.mCurrentImage = bm;
				mPreview.setImageBitmap(bm);
			}
		}
		
	}
	
	
	/**
	 * Starts an intent chooser to select the source of the image to get.
	 */
	private void takePicture() {
		final CharSequence[] items = { "Take Photo", "Choose from Library",
		"Cancel" };

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
		// setting boolean to be able to return to last activity on back pressed
		mChooserVisible = true;
		builder.show();
	}


	/* (non-Javadoc)
	 * @see android.app.Activity#onBackPressed()
	 */
	@Override
	public void onBackPressed() {
		if (mChooserVisible) 
			finish();
		
		super.onBackPressed();
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
	 
	 
	 private Bitmap rotateBitmap(Bitmap bmp, int rotation) {
		 Matrix matrix = new Matrix();
		 matrix.postRotate(rotation);
		 Bitmap rotatedBitmap = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(),
				 									bmp.getHeight(), matrix, true);
		 bmp.recycle();
		 return rotatedBitmap;
	 }
	

}
