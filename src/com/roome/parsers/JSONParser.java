package com.roome.parsers;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.util.Log;

import com.roome.constants.Constants;
import com.roome.interfaces.AsyncResponse;

/**
 * Class to handle getting and posting data to server
 * 
 * @author Jesica Hadiwiryo
 * 
 */
public class JSONParser extends AsyncTask<NameValuePair, String, JSONObject> {

	// variables
	private AsyncResponse response;
	private InputStream is = null;
	private JSONObject jsonObject = null;
	private String url;
	private String json;
	private String method;

	/**
	 * Constructor
	 * 
	 * @param url
	 * @param method
	 *            Constants.METHOD_GET or Constants.METHOD_POST
	 */
	public JSONParser(String url, String method) {
		super();
		this.url = url;
		this.method = method;
	}

	@Override
	/**
	 * Method to handle pre executing the background code
	 */
	protected void onPreExecute() {

	}

	@Override
	/**
	 * Method to start new thread and connect with internet to get JSONObject
	 */
	protected JSONObject doInBackground(NameValuePair... params) {

		ArrayList<NameValuePair> parameters = new ArrayList<NameValuePair>();
		for (int objectNumber = 0; objectNumber < params.length; objectNumber++) {
			parameters.add(params[objectNumber]);
			Log.d(Constants.LOG_TAG, params[objectNumber].getValue());
		}

		DefaultHttpClient httpClient = new DefaultHttpClient();
		try {
			if (method.equals(Constants.METHOD_POST)) {
				setPostStream(parameters, httpClient);
			} else if (method.equals(Constants.METHOD_GET)) {
				setGetStream(parameters, httpClient);
			}

			setJSONObject();

		} catch (JSONException e) {
			Log.e(Constants.LOG_TAG, "Error parsing data " + e.toString());
		} catch (Exception e) {
			Log.d(Constants.LOG_TAG, e.getMessage());
		}

		// return JSON String
		return jsonObject;
	}

	@Override
	/**
	 * After finished background task, pass the result to context using AsyncResponse interface
	 */
	protected void onPostExecute(JSONObject jObject) {
		response.onAsyncFinished(jObject);
	}

	/**
	 * Method to set stream content using POST
	 * 
	 * @param parameters
	 * @param httpClient
	 * @throws Exception
	 */
	private void setPostStream(List<NameValuePair> parameters,
			DefaultHttpClient httpClient) throws Exception {
		HttpPost httpPost = new HttpPost(url);
		httpPost.setEntity(new UrlEncodedFormEntity(parameters));

		HttpResponse httpResponse = httpClient.execute(httpPost);
		HttpEntity httpEntity = httpResponse.getEntity();
		is = httpEntity.getContent();
	}

	/**
	 * Method to set stream content using GET
	 * 
	 * @param parameters
	 * @param httpClient
	 * @throws Exception
	 */
	private void setGetStream(List<NameValuePair> parameters,
			DefaultHttpClient httpClient) throws Exception {
		String paramString = URLEncodedUtils.format(parameters, "utf-8");
		url += "?" + paramString;
		HttpGet httpGet = new HttpGet(url);

		HttpResponse httpResponse = httpClient.execute(httpGet);
		HttpEntity httpEntity = httpResponse.getEntity();
		is = httpEntity.getContent();
	}

	/**
	 * Method to get AsyncResponse
	 * 
	 * @return interface of AsyncResponse
	 */
	public AsyncResponse getResponse() {
		return response;
	}

	/**
	 * Method to set response object
	 * 
	 * @param response
	 */
	public void setResponse(AsyncResponse response) {
		this.response = response;
	}

	/**
	 * Method to get JSON object from the stream content
	 * @throws Exception
	 */
	private void setJSONObject() throws Exception {
		BufferedReader reader = new BufferedReader(new InputStreamReader(is,
				"iso-8859-1"), 8);
		StringBuilder sb = new StringBuilder();
		String line = null;
		while ((line = reader.readLine()) != null) {
			sb.append(line + "\n");
		}
		is.close();
		json = sb.toString();
		Log.d(Constants.LOG_TAG, json);

		jsonObject = new JSONObject(json);
	}
}
