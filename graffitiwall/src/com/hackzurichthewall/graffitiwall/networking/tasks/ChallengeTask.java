package com.hackzurichthewall.graffitiwall.networking.tasks;

import java.io.InputStream;
import java.net.URL;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
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
				
				 InputStream content = null;
				  try {
				    HttpClient httpclient = new DefaultHttpClient();
				    
				    HttpGet httpGet = new HttpGet(new URL(URL_TO_MAIN_STREAM).toString());
				    httpGet.setHeader("Accept", "application/json");
				    httpGet.setHeader("Authorization", "Basic cGhpbG9ybGFuZG85MkB5YWhvby5kZToxMjm0");
				    HttpResponse response = httpclient.execute(httpGet);
				    
				    
				    JSONObject json = new JSONObject(EntityUtils.toString(response.getEntity()));
				    String streamDescription = json.getString("description");
				    Log.i(TAG, "Stream description is: " + streamDescription);
				  } catch (Exception e) {
				    Log.e("[GET REQUEST]", "Network exception", e);
				  }

				
				return null;
			}
			
		}.execute();
	}

}
