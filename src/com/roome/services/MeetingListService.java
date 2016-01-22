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
import com.roome.classes.Meeting;
import com.roome.classes.TimeStringManipulator;
import com.roome.constants.Constants;
import com.roome.interfaces.AsyncResponse;
import com.roome.parsers.JSONParser;

/**
 * Service to get list of meetings
 * 
 * @author Jesica Hadiwiryo
 * 
 */
public class MeetingListService extends AsyncService implements AsyncResponse {

	// constants
	public static final String BROADCAST_FILTER_NAME = "com.roome.service.meetinglist";

	@Override
	/**
	 * Method to handle onstart of the service
	 */
	public void onStart(Intent intent, int startId) {
		ConnectionDetector connectionDetector = new ConnectionDetector(
				MeetingListService.this);
		if (connectionDetector.isConnectingToInternet()) {
			int roomId = intent.getIntExtra(getString(R.string.room_id_tag), 0);
			String meetingsUrl = Constants.MEETING_LIST_URL + "/" + roomId;

			SharedPreferences oauthPreference = getSharedPreferences(
					Constants.PREF_TAG, Context.MODE_PRIVATE);
			String dummyOauth = oauthPreference.getString(Constants.OAUTH_TAG,
					Constants.OAUTH_DUMMY);

			NameValuePair oauthValuePair = new BasicNameValuePair(
					Constants.OAUTH_TAG, dummyOauth);
			JSONParser jsonParser = new JSONParser(meetingsUrl,
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
			Intent intentExtra = new Intent(BROADCAST_FILTER_NAME);
			intentExtra.putExtra(getString(R.string.meetings_array_tag),
					getMeetingsFromJSON(jObject));
			intentExtra.putExtra(Constants.BROADCAST_ERROR_STATUS_TAG,
					Constants.BROADCAST_NO_ERROR);
			sendBroadcast(intentExtra);
		} else {
			broadcastErrorMessage(BROADCAST_FILTER_NAME,
					getString(R.string.server_cannot_be_reached_error_text));
		}
		stopSelf();
	}

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Method to get an array of meeting
	 * @param jObject
	 * @return array of meetings
	 */
	private Meeting[] getMeetingsFromJSON(JSONObject jObject) {
		List<Meeting> meetings = new LinkedList<Meeting>();

		try {
			String jStatus = jObject.getString(getString(R.string.status));
			if (jStatus.equalsIgnoreCase(getString(R.string.success))) {

				JSONArray jMeeting = jObject.getJSONObject(
						getString(R.string.data)).getJSONArray(
						getString(R.string.meetings_array_tag));
				for (int index = 0; index < jMeeting.length(); index++) {
					JSONObject aMeeting = jMeeting.getJSONObject(index);
					String summary = aMeeting
							.getString(getString(R.string.meeting_summary_tag));
					String creator = aMeeting
							.getString(getString(R.string.meeting_creator_tag));

					String startTimeInString = aMeeting
							.getString(getString(R.string.meeting_start_tag));
					String endTimeInString = aMeeting
							.getString(getString(R.string.meeting_end_tag));

					Timestamp startTime = TimeStringManipulator
							.getTimestampFromString(startTimeInString);
					Timestamp endTime = TimeStringManipulator
							.getTimestampFromString(endTimeInString);

					Log.d(Constants.LOG_TAG, summary);
					meetings.add(new Meeting(summary, creator, startTime,
							endTime));
				}
			}

		} catch (JSONException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}

		Meeting[] arrayMeeting = meetings.toArray(new Meeting[meetings.size()]);
		return arrayMeeting;
	}

}
