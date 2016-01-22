package com.roome.services;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.IBinder;

import com.prototype.roomephone.R;
import com.roome.classes.ConnectionDetector;
import com.roome.constants.Constants;
import com.roome.interfaces.AsyncResponse;
import com.roome.parsers.JSONParser;

/**
 * Service to handle authentications
 * 
 * @author Jesica Hadiwiryo
 * 
 */
public class AuthService extends AsyncService implements AsyncResponse {

	//broadcast filter name
	public static final String BROADCAST_FILTER_NAME = "com.roome.service.authenticate";

	@Override
	public void onCreate() {

	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	/**
	 * Method to handle on start service
	 */
	public void onStart(Intent intent, int startId) {
		ConnectionDetector connectionDetector = new ConnectionDetector(
				AuthService.this);
		if (connectionDetector.isConnectingToInternet()) {

			String APIKey = intent.getStringExtra(Constants.API_KEY_TAG);

			NameValuePair auth = new BasicNameValuePair(Constants.API_KEY_TAG,
					APIKey);
			// jsonParser = new JSONParser(Constants.ROOM_LIST_URL, METHOD);
			JSONParser jsonParser = new JSONParser(Constants.AUTH_URL,
					Constants.METHOD_GET);
			jsonParser.setResponse(this);
			jsonParser.execute(auth);
		} else {
			broadcastErrorMessage(BROADCAST_FILTER_NAME,
					getString(R.string.no_internet_connection_error_text));
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	@Override
	/**
	 * Method to handle after asynctask has been finished
	 */
	public void onAsyncFinished(JSONObject jObject) {
		if (jObject != null) {
			try {
				String jStatus = jObject.getString(getString(R.string.status));
				if (jStatus.equalsIgnoreCase(getString(R.string.success))) {
					JSONObject jData = jObject
							.getJSONObject(getString(R.string.data));
					String oauth_token = jData.getString(Constants.OAUTH_TAG);
					SharedPreferences oauthPreference = getSharedPreferences(
							Constants.PREF_TAG, Context.MODE_PRIVATE);
					Editor editor = oauthPreference.edit();
					editor.putString(Constants.OAUTH_TAG, oauth_token);
					editor.commit();

					Intent intentExtra = new Intent(BROADCAST_FILTER_NAME);
					intentExtra.putExtra(Constants.BROADCAST_ERROR_STATUS_TAG,
							Constants.BROADCAST_NO_ERROR);
					sendBroadcast(intentExtra);
				} else {
					String message = jObject
							.getString(getString(R.string.message));
					throw new JSONException(message);
				}
			} catch (JSONException e) {
				broadcastErrorMessage(BROADCAST_FILTER_NAME, e.getMessage());
				e.printStackTrace();
			}
		} else {
			broadcastErrorMessage(BROADCAST_FILTER_NAME,
					getString(R.string.server_cannot_be_reached_error_text));
		}
	}

}
