package com.hackzurichthewall.graffitiwall.networking.tasks;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.util.Log;



public class ChallengeTask {
	
	private static final String TAG = "ChallengeTask";
	
	private static final String URL_TO_MAIN_STREAM = "https://thewall.beekeeper.io/api/2/streams/731?key=8P7Sy4NF2kAQJUAA";
	
	public static void getChallengeAsync() {
		new AsyncTask<Void, Void, Void>() {

			@Override
			protected Void doInBackground(Void... params) {
				
				 
				  try {
			
				    
				    JSONObject json =  getStreamObject();
				    String streamDescription = json.getString("description");
				    Log.i(TAG, "Stream description is: " + streamDescription);
				  } catch (Exception e) {
				    Log.e("[GET REQUEST]", "Network exception", e);
				  }

				
				return null;
			}
			
		}.execute();
	}
	
	public static void changeChallenge(final String newChallengeDescription) {
		new AsyncTask<Void, Void, Void>() {

			@Override
			protected Void doInBackground(Void... params) {
				try {
				    				    
				    // get the old description object
				    JSONObject json = getStreamObject();
				    
				    // Overwrite stream object with new description
				    json.put("description", "fitnessFirst007");
				    
				    HttpClient httpclient = new DefaultHttpClient();
				    
				    HttpPut httpPut = new HttpPut(new URL(URL_TO_MAIN_STREAM).toString());
				    
				    StringEntity se = null;
					try {
						se = new StringEntity(json.toString());
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
						return null;
					}

				    //sets the post request as the resulting string
					httpPut.setEntity(se);
				    
				    httpPut.setHeader("Accept", "application/json");   
				    httpPut.setHeader("Content-Type", "application/json");
				    
				    httpPut.setHeader("Authorization", "Basic cGhpbG9ybGFuZG85MkB5YWhvby5kZToxMjM0");
				    HttpResponse response = httpclient.execute(httpPut);
				    //JSONObject j = new JSONObject(EntityUtils.toString(response.getEntity()));
				    
				    Log.i(TAG, response.toString());
				  } catch (Exception e) {
				    Log.e("[GET REQUEST]", "Network exception", e);
				  }
				return null;
			}
			
		}.execute();
	}

	private static JSONObject getStreamObject() throws IOException, JSONException {
		HttpClient httpclient = new DefaultHttpClient();
	    
	    HttpGet httpGet = new HttpGet(new URL(URL_TO_MAIN_STREAM).toString());
	    httpGet.setHeader("Accept", "application/json");   
	  
	    
	    httpGet.setHeader("Authorization", "Basic cGhpbG9ybGFuZG85MkB5YWhvby5kZToxMjM0");
	    HttpResponse response = httpclient.execute(httpGet);
	    return new JSONObject(EntityUtils.toString(response.getEntity()));
	}
	
}
