package com.hackzurichthewall.graffitiwall.networking.tasks;


import java.io.File;
import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;

import com.hackzurichthewall.graffitiwall.networking.RestClient;
import com.hackzurichthewall.model.PictureComment;

public class UploadImageTask extends AsyncTask<String, Void, Void>{

	private AsyncResponse delegate = null;
	
	public UploadImageTask(AsyncResponse delegate) {
		this.delegate = delegate;
	}
	
	@Override
	protected Void doInBackground(String... params) {
		
		if (params == null || params.length == 0) {
			throw new IllegalArgumentException("There was no bitmap to upload.");
		}
		// instantiates httpclient to make request
	    DefaultHttpClient httpclient = new DefaultHttpClient();
		
		HttpPost post = new HttpPost(RestClient.URL + "/photos" + RestClient.API_KEY);
		post.setHeader("Authorization", "Basic cGhpbG9ybGFuZG85MkB5YWhvby5kZToxMjM0");
		post.setHeader("Accept", "application/json");

		MultipartEntityBuilder builder = MultipartEntityBuilder.create();
		builder.addBinaryBody("photo", new File(params[0]), ContentType.APPLICATION_OCTET_STREAM, "file.ext");
		HttpEntity multipart = builder.build();

		post.setEntity(multipart);

		//Handles what is returned from the page 
	    ResponseHandler responseHandler = new BasicResponseHandler() {

			@Override
			public String handleResponse(HttpResponse response)
					throws HttpResponseException, IOException {
					JSONObject respObject = null;
					try {
						respObject = new JSONObject(EntityUtils.toString(response.getEntity()));
					} catch (ParseException | JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					if(respObject != null){
			            try {
			            	String imageUrl = respObject.getString("url");
			            	if (imageUrl != null) {
			            		
			            		PictureComment pComment = new PictureComment();
			            		pComment.setmImageUrl(imageUrl);
			            		JSONObject pCommentJSON = pComment.toJSON();
			            		
			            		if (delegate != null) {
			            			delegate.uploadPost(pCommentJSON);
			            		}
			            		
			            	}
			            } catch (ParseException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} 
				}
				return respObject.toString();
			}
	    	
	    };
		
	    try {
			httpclient.execute(post, responseHandler);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		    
	    return null;
	}
	
	public interface AsyncResponse {
		public void uploadPost(JSONObject post);
	}
	

}
