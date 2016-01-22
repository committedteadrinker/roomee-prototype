package com.roome.services;

import java.sql.Timestamp;
import java.text.ParseException;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.Parcelable;

import com.prototype.roomephone.R;
import com.roome.classes.ConnectionDetector;
import com.roome.classes.Meeting;
import com.roome.classes.Room;
import com.roome.classes.TimeStringManipulator;
import com.roome.constants.Constants;
import com.roome.interfaces.AsyncResponse;
import com.roome.parsers.JSONParser;

/**
 * Service to get room detail data
 * 
 * @author Jesica Hadiwiryo
 * 
 */
public class RoomDetailService extends AsyncService implements AsyncResponse {

	// broadcast filter name
	public static final String BROADCAST_FILTER_NAME = "com.roome.service.roomdetails";

	// variables
	private Room room;
	private Meeting[] meetings;

	@Override
	public void onCreate() {
		super.onCreate();
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	/**
	 * Method to handle on start service
	 */
	public void onStart(Intent intent, int startId) {
		ConnectionDetector connectionDetector = new ConnectionDetector(
				RoomDetailService.this);
		if (connectionDetector.isConnectingToInternet()) {
			String roomId = intent
					.getStringExtra(getString(R.string.room_id_tag));
			String roomUrl = Constants.ROOM_DETAIL_URL + "/" + roomId;

			SharedPreferences oauthPreference = getSharedPreferences(
					Constants.PREF_TAG, Context.MODE_PRIVATE);
			String dummyOauth = oauthPreference.getString(Constants.OAUTH_TAG,
					Constants.OAUTH_DUMMY);

			NameValuePair oauthValuePair = new BasicNameValuePair(
					Constants.OAUTH_TAG, dummyOauth);
			JSONParser jsonParser = new JSONParser(roomUrl,
					Constants.METHOD_GET);

			jsonParser.setResponse(this);
			jsonParser.execute(oauthValuePair);
		} else {
			broadcastErrorMessage(BROADCAST_FILTER_NAME,
					getString(R.string.no_internet_connection_error_text));
		}

	}

	@Override
	/**
	 * Method to handle after asynctask has been finished
	 */
	public void onAsyncFinished(JSONObject jObject) {
		if (jObject != null) {
			room = getRoomFromJSON(jObject);
			Intent meetingListService = new Intent(RoomDetailService.this,
					MeetingListService.class);
			meetingListService.putExtra(getString(R.string.room_id_tag),
					room.getId());
			startService(meetingListService);
			registerReceiver(broadcastReceiver, new IntentFilter(
					MeetingListService.BROADCAST_FILTER_NAME));
		} else {
			broadcastErrorMessage(BROADCAST_FILTER_NAME,
					getString(R.string.server_cannot_be_reached_error_text));
		}
	}

	/**
	 * Method to get room from JSONObject
	 * @param jObject 
	 * @return room
	 */
	private Room getRoomFromJSON(JSONObject jObject) {
		Room room = null;
		try {
			String jStatus = jObject.getString(getString(R.string.status));
			if (jStatus.equalsIgnoreCase(getString(R.string.success))) {
				JSONObject jRoom = jObject.getJSONObject(
						getString(R.string.data)).getJSONObject(
						getString(R.string.room_object_tag));

				int id = jRoom.getInt(getString(R.string.room_id_tag));
				String name = jRoom
						.getString(getString(R.string.room_name_tag));
				int freebusy = jRoom
						.getInt(getString(R.string.room_freebusy_tag));
				Timestamp time = TimeStringManipulator
						.getTimestampFromString(jRoom
								.getString(getString(R.string.room_time_tag)));
				room = new Room(id, name, freebusy, time);
			} else {
				String message = jObject.getString(getString(R.string.message));
				throw new JSONException(message);
			}
		} catch (JSONException e) {
			Intent intentExtra = new Intent(BROADCAST_FILTER_NAME);
			intentExtra.putExtra(Constants.BROADCAST_ERROR_STATUS_TAG,
					Constants.BROADCAST_ERROR);
			intentExtra.putExtra(Constants.BROADCAST_ERROR_MESSAGE_TAG,
					e.getMessage());
			sendBroadcast(intentExtra);
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return room;
	}

	// broadcast receiver to handle with the list of existing meetings
	private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {

		@Override
		/**
		 * Method to handle the received broadcast intent
		 */
		public void onReceive(Context context, Intent intent) {
			unregisterReceiver(broadcastReceiver);
			boolean broadcastStatus = intent.getBooleanExtra(
					Constants.BROADCAST_ERROR_STATUS_TAG,
					Constants.BROADCAST_ERROR);
			if (broadcastStatus) {
				Parcelable[] parcels = intent
						.getParcelableArrayExtra(getString(R.string.meetings_array_tag));
				if (parcels != null) {
					meetings = new Meeting[parcels.length];
					System.arraycopy(parcels, 0, meetings, 0, parcels.length);
					Intent intentExtra = new Intent(BROADCAST_FILTER_NAME);
					intentExtra.putExtra(Constants.BROADCAST_ROOM_DETAIL_TAG,
							room);
					intentExtra.putExtra(Constants.BROADCAST_MEETING_LIST_TAG,
							meetings);
					intentExtra.putExtra(Constants.BROADCAST_ERROR_STATUS_TAG,
							Constants.BROADCAST_NO_ERROR);
					sendBroadcast(intentExtra);
				}

			} else {
				String errorMessage = intent
						.getStringExtra(Constants.BROADCAST_ERROR_MESSAGE_TAG);
				broadcastErrorMessage(BROADCAST_FILTER_NAME, errorMessage);
			}
		}
	};
}
