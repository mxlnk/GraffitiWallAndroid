package com.hackzurichthewall.graffitiwall.networking.tasks;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import android.os.AsyncTask;

import com.hackzurichthewall.graffitiwall.networking.RestClient;

public class CreatePostTask extends AsyncTask<JSONObject, Void, Void> {

	private int mStreamId = -1;
	private String mPath = null;
	
	@Override
	protected Void doInBackground(JSONObject... params) {
		
		if (mStreamId == -1 || params == null 
				|| params.length == 0) { // checking if stream id was set
			throw new IllegalStateException("Stream ID was not set.");
		}
		
		// creating the path
		mPath = RestClient.URL + "/streams/" + mStreamId + "/posts" + RestClient.API_KEY;
		
		// instantiates httpclient to make request
	    DefaultHttpClient httpclient = new DefaultHttpClient();

	    // url with the post data
	    HttpPost httpost = new HttpPost(mPath);
	    
	    //sets a request header so the page receving the request
	    //will know what to do with it
	    httpost.setHeader("Accept", "application/json");
	    httpost.setHeader("Content-Type", "application/json");
	    
	    // adding authorization, second part is Base64 encoding of login data
	    httpost.setHeader("Authorization", "Basic cGhpbG9ybGFuZG85MkB5YWhvby5kZToxMjM0");
	    // passes the results to a string builder/entity
	    StringEntity se = null;
		try {
			se = new StringEntity(params[0].toString());
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return null;
		}

	    //sets the post request as the resulting string
	    httpost.setEntity(se);
	    
	    

	    //Handles what is returned from the page 
	    ResponseHandler<?> responseHandler = new BasicResponseHandler();
	    try {
			httpclient.execute(httpost, responseHandler);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	   
	    return null;
	}


	/**
	 * @param mStreamId the mStreamId to set
	 */
	public void setmStreamId(int mStreamId) {
		this.mStreamId = mStreamId;
	}

}
