package com.hackzurichthewall.graffitiwall.networking.tasks;


import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.os.AsyncTask;

import com.hackzurichthewall.graffitiwall.networking.RestClient;

public class UploadImageTask extends AsyncTask<String, Void, String>{

	@Override
	protected String doInBackground(String... params) {
		
		if (params == null || params.length == 0) {
			throw new IllegalArgumentException("There was no bitmap to upload.");
		}
		HttpURLConnection connection = null;
		DataOutputStream outputStream = null;
		DataInputStream inputStream = null;
		String pathToOurFile = params[0];
		String urlServer = RestClient.URL + "/photos" + RestClient.API_KEY;
		String lineEnd = "\r\n";
		String twoHyphens = "--";
		String boundary =  "*****";
		 
		int bytesRead, bytesAvailable, bufferSize;
		byte[] buffer;
		int maxBufferSize = 1*1024*1024;
		int serverResponseCode;
		String serverResponseMessage = null;
		try
		{
		    FileInputStream fileInputStream = new FileInputStream(new File(pathToOurFile) );
		 
		    URL url = new URL(urlServer);
		    connection = (HttpURLConnection) url.openConnection();
		 
		    // Allow Inputs &amp; Outputs.
		    connection.setDoInput(true);
		    connection.setDoOutput(true);
		    connection.setUseCaches(false);
		 
		    // Set HTTP method to POST.
		    connection.setRequestMethod("POST");
		 
		    connection.setRequestProperty("Connection", "Keep-Alive");
		    connection.setRequestProperty("Content-Type", "multipart/form-data;boundary="+boundary);
		    connection.setRequestProperty("Authorization", "Basic cGhpbG9ybGFuZG85MkB5YWhvby5kZToxMjM0");
		 
		    outputStream = new DataOutputStream( connection.getOutputStream() );
		    outputStream.writeBytes(twoHyphens + boundary + lineEnd);
		    outputStream.writeBytes("Content-Disposition: form-data; name=\"uploadedfile\";filename=\"" + pathToOurFile +"\"" + lineEnd);
		    outputStream.writeBytes(lineEnd);
		 
		    bytesAvailable = fileInputStream.available();
		    bufferSize = Math.min(bytesAvailable, maxBufferSize);
		    buffer = new byte[bufferSize];
		 
		    // Read file
		    bytesRead = fileInputStream.read(buffer, 0, bufferSize);
		 
		    while (bytesRead > 0)
		    {
		        outputStream.write(buffer, 0, bufferSize);
		        bytesAvailable = fileInputStream.available();
		        bufferSize = Math.min(bytesAvailable, maxBufferSize);
		        bytesRead = fileInputStream.read(buffer, 0, bufferSize);
		    }
		 
		    outputStream.writeBytes(lineEnd);
		    outputStream.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
		 
		    // Responses from the server (code and message)
		    serverResponseCode = connection.getResponseCode();
		    serverResponseMessage = connection.getResponseMessage();
		 
		    fileInputStream.close();
		    outputStream.flush();
		    outputStream.close();
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		    //Exception handling
		}
		return serverResponseMessage;
	}

}
