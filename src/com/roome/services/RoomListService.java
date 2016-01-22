package com.roome.services;

import java.sql.Timestamp;
import java.text.ParseException;
import java.util.LinkedList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.util.Log;

import com.prototype.roomephone.R;
import com.roome.classes.ConnectionDetector;
import com.roome.classes.Room;
import com.roome.classes.TimeStringManipulator;
import com.roome.constants.Constants;
import com.roome.interfaces.AsyncResponse;
import com.roome.parsers.JSONParser;

public class RoomListService extends AsyncService implements AsyncResponse {

	// broadcast filter name
	public final static String BROADCAST_FILTER_NAME = "com.roome.service.displayroomlist";
	private JSONParser jParser;

	@Override
	public void onCreate() {
		super.onCreate();
	}

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	/**
	 * Method to handle on start service
	 */
	public void onStart(Intent intent, int startId) {
		ConnectionDetector connectionDetector = new ConnectionDetector(
				RoomListService.this);
		if (connectionDetector.isConnectingToInternet()) {
			SharedPreferences oauthPreference = getSharedPreferences(
					Constants.PREF_TAG, Context.MODE_PRIVATE);
			String dummyOauth = oauthPreference.getString(Constants.OAUTH_TAG,
					Constants.OAUTH_DUMMY);
			NameValuePair oauthValuePair = new BasicNameValuePair(
					Constants.OAUTH_TAG, dummyOauth);

			jParser = new JSONParser(Constants.ROOM_LIST_URL,
					Constants.METHOD_GET);
			jParser.setResponse(this);
			jParser.execute(oauthValuePair);
		} else {
			broadcastErrorMessage(BROADCAST_FILTER_NAME,
					getString(R.string.no_internet_connection_error_text));
		}

	}

	/**
	 * Method to get list of rooms from JSONObject
	 * @param jObject 
	 * @return array of rooms
	 */
	private Room[] getRoomsFromJSON(JSONObject jObject) {
		List<Room> rooms = new LinkedList<Room>();
		JSONArray jRooms;
		try {
			jRooms = jObject.getJSONObject("data").getJSONArray("rooms");

			for (int index = 0; index < jRooms.length(); index++) {
				JSONObject aRoom = jRooms.getJSONObject(index);
				int id = aRoom.getInt(getString(R.string.room_id_tag));
				String name = aRoom
						.getString(getString(R.string.room_name_tag));
				int freebusy = aRoom
						.getInt(getString(R.string.room_freebusy_tag));

				String timeInString = aRoom
						.getString(getString(R.string.room_time_tag));
				Log.d(Constants.LOG_TAG, timeInString);
				Timestamp time = TimeStringManipulator
						.getTimestampFromString(timeInString);
				Log.d(Constants.LOG_TAG, name);
				rooms.add(new Room(id, name, freebusy, time));
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}

		Room[] arrayRooms = rooms.toArray(new Room[rooms.size()]);
		return arrayRooms;
	}

	@Override
	/**
	 * Method to handle after asynctask has been finished
	 */
	public void onAsyncFinished(JSONObject jObject) {
		if (jObject != null) {
			Intent intentExtra = new Intent(BROADCAST_FILTER_NAME);
			intentExtra.putExtra(Constants.BROADCAST_ROOM_LIST_TAG,
					getRoomsFromJSON(jObject));
			intentExtra.putExtra(Constants.BROADCAST_ERROR_STATUS_TAG,
					Constants.BROADCAST_NO_ERROR);
			sendBroadcast(intentExtra);
		} else {
			broadcastErrorMessage(BROADCAST_FILTER_NAME,
					getString(R.string.server_cannot_be_reached_error_text));
		}
	}
}
