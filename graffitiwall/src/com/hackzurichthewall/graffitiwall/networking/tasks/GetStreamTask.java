package com.hackzurichthewall.graffitiwall.networking.tasks;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;

import com.hackzurichthewall.graffitiwall.networking.RestClient;
import com.hackzurichthewall.model.AbstractContent;
import com.hackzurichthewall.model.PictureComment;
import com.hackzurichthewall.model.TextComment;


/**
 * Task that tries to get an stream from the REST-API and returns it as JSONArray.
 * Returns null if stream does not exist or response can not be cast to JSON.
 * 
 * @author Johannes Glöckle
 */
public class GetStreamTask extends AsyncTask<Integer, Void, List<AbstractContent>> {

	private String mPath = null;
	
	private final int limit = 100;
	
	private boolean mTimeoutExceeded = false;
	
	
	@Override
	protected List<AbstractContent> doInBackground(Integer... params) {
		
		if (params == null || params.length == 0) { // checking if stream ID is given
			throw new IllegalArgumentException("At least a stream ID has to be provided.");
		}
		
		// creating the path to make GET-request
		mPath = RestClient.URL + "/streams/" + params[0].toString() + "/posts" + RestClient.API_KEY + "&limit=" + limit;
		
		
		// request method is GET
        DefaultHttpClient httpClient = new DefaultHttpClient();
        HttpGet httpGet = new HttpGet(mPath);
        httpGet.setHeader("Accept", "application/json");

        HttpResponse httpResponse = null;
		try {
			httpResponse = httpClient.execute(httpGet);
		} catch (ClientProtocolException e) {
			mTimeoutExceeded = true;
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			mTimeoutExceeded = true;
			e.printStackTrace();
			return null;
		}
        HttpEntity httpEntity = httpResponse.getEntity();
        
        
        JSONArray respObject = null;
        if(httpEntity != null){
            try {
            	respObject = new JSONArray(EntityUtils.toString(httpEntity));
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            
          }
        
        
        // creating the list with objects
        ArrayList<AbstractContent> items = new ArrayList<AbstractContent>();
        
        for (int i = 0; i < respObject.length(); i++) {
        	try {
        		JSONObject current = respObject.getJSONObject(i);
				if (current.has("photo")) {
					items.add(new PictureComment(current));
				} else {
					items.add(new TextComment(current));
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
        
		return items;
	}

	
	public boolean isTimeoutExceeded() {
		return this.mTimeoutExceeded;
	}
}
