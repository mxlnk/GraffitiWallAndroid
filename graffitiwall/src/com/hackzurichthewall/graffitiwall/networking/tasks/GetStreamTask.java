package com.hackzurichthewall.graffitiwall.networking.tasks;

import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;

import android.os.AsyncTask;

import com.hackzurichthewall.graffitiwall.networking.RestClient;


/**
 * Task that tries to get an stream from the REST-API and returns it as JSONArray.
 * Returns null if stream does not exist or response can not be cast to JSON.
 * 
 * @author Johannes Glöckle
 */
public class GetStreamTask extends AsyncTask<Integer, Void, JSONArray> {

	private String mPath = null;
	
	
	@Override
	protected JSONArray doInBackground(Integer... params) {
		
		if (params == null || params.length == 0) { // checking if stream ID is given
			throw new IllegalArgumentException("At least a stream ID has to be provided.");
		}
		
		// creating the path to make GET-request
		mPath = RestClient.URL + "/streams/" + params[0].toString() + "/posts" + RestClient.API_KEY;
		
		
		// request method is GET
        DefaultHttpClient httpClient = new DefaultHttpClient();
        HttpGet httpGet = new HttpGet(mPath);
        httpGet.setHeader("Accept", "application/json");

        HttpResponse httpResponse = null;
		try {
			httpResponse = httpClient.execute(httpGet);
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			// TODO Auto-generated catch block
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
		return respObject;
	}

}
