package com.hackzurichthewall.graffitiwall.networking.tasks;

import java.io.InputStream;

import com.hackzurichthewall.model.PictureComment;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

public class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
    ImageView bmImage;
    PictureComment bmp;

    public DownloadImageTask(ImageView bmImage) {
        this.bmImage = bmImage;
    }
    
    public DownloadImageTask(PictureComment bmp) {
    	this.bmp = bmp;
    }

    protected Bitmap doInBackground(String... urls) {
        String urldisplay = urls[0];
        Bitmap mIcon11 = null;
        try {
            InputStream in = new java.net.URL(urldisplay).openStream();
            mIcon11 = BitmapFactory.decodeStream(in);
        } catch (Exception e) {
            Log.e("Error", e.getMessage());
            e.printStackTrace();
        }
        if (bmp != null) {
        	bmp.setmPicture(mIcon11.copy(mIcon11.getConfig(), false));
        }
        return mIcon11;
    }

}