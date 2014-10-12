package com.hackzurichthewall.graffitiwall.networking.tasks;

import java.io.IOException;
import java.net.URL;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import com.hackzurichthewall.graffitiwall.networking.RestClient;

import android.os.AsyncTask;
import android.util.Log;

public class GetChallengeTask extends AsyncTask<Integer, Void, String> {

	private static final String TAG = "ChallengeTask";
	
	private static final String URL_TO_MAIN_STREAM = "https://thewall.beekeeper.io/api/2/streams/";
	
	
	@Override
	protected String doInBackground(Integer... params) {
		
		if (params == null || params.length == 0) {
			throw new IllegalArgumentException("You have to provide a stream ID.");
		}
		String streamDescription = null;
		try {
		    JSONObject json =  getStreamObject(params[0]);
		    if (json.has("name")) {
		    	streamDescription = json.getString("name");
		    }
		    Log.i(TAG, "Stream description is: " + streamDescription);
		  } catch (Exception e) {
		    Log.e("[GET REQUEST]", "Network exception", e);
		  }
		
		return streamDescription;
	}
	
	
	private static JSONObject getStreamObject(Integer i) throws IOException, JSONException {
		HttpClient httpclient = new DefaultHttpClient();
		
	    HttpGet httpGet = new HttpGet(new URL(URL_TO_MAIN_STREAM + i.toString() + RestClient.API_KEY).toString());
	    httpGet.setHeader("Accept", "application/json");   
	  
	    
	    httpGet.setHeader("Authorization", "Basic cGhpbG9ybGFuZG85MkB5YWhvby5kZToxMjM0");
	    HttpResponse response = httpclient.execute(httpGet);
	    return new JSONObject(EntityUtils.toString(response.getEntity()));
	}

}
