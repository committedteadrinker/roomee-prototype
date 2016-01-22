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
import android.util.Log;

import com.prototype.roomephone.R;
import com.roome.classes.ConnectionDetector;
import com.roome.classes.Meeting;
import com.roome.classes.TimeStringManipulator;
import com.roome.constants.Constants;
import com.roome.interfaces.AsyncResponse;
import com.roome.parsers.JSONParser;

/**
 * Service to handle posting new meeting details
 * 
 * @author Jesica Hadiwiryo
 * 
 */
public class NewMeetingService extends AsyncService implements AsyncResponse {

	// constants
	public static final String BROADCAST_FILTER_NAME = "com.roome.service.submitnewmeeting";
	private final static int NO_ROOM = -1;

	// class members
	private int mRoomId;
	private String mMeetingSummary;
	private String mMeetingDescription;
	private String mMeetingStartTime;
	private String mMeetingEndTime;
	private String mMeetingDate;
	private Meeting[] meetings;

	@Override
	public void onCreate() {

	}

	@Override
	/**
	 * Method to handle on start service
	 */
	public void onStart(Intent intent, int startId) {
		ConnectionDetector connectionDetector = new ConnectionDetector(
				NewMeetingService.this);
		if (connectionDetector.isConnectingToInternet()) {
			mRoomId = intent.getIntExtra(getString(R.string.room_id_tag),
					NO_ROOM);
			mMeetingSummary = intent
					.getStringExtra(getString(R.string.meeting_summary_tag));
			mMeetingDescription = intent
					.getStringExtra(getString(R.string.meeting_description_tag));
			mMeetingStartTime = intent
					.getStringExtra(getString(R.string.meeting_start_tag));
			mMeetingEndTime = intent
					.getStringExtra(getString(R.string.meeting_end_tag));
			mMeetingDate = intent
					.getStringExtra(getString(R.string.meeting_date_tag));
			startMeetingListService(mRoomId);

		} else {
			broadcastErrorMessage(BROADCAST_FILTER_NAME,
					getString(R.string.no_internet_connection_error_text));
		}
	}

	@Override
	public IBinder onBind(Intent arg0) {

		return null;
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

	private NameValuePair getRoomIdValuePair() {
		NameValuePair roomIdValuePair = new BasicNameValuePair(
				Constants.ROOM_ID_POST_TAG, Integer.toString(mRoomId));
		return roomIdValuePair;
	}

	private NameValuePair getSummaryValuePair() {
		NameValuePair meetingSummaryValuePair = new BasicNameValuePair(
				Constants.MEETING_SUMMARY_POST_TAG, mMeetingSummary);
		return meetingSummaryValuePair;
	}

	private NameValuePair getDescriptionValuePair() {
		NameValuePair meetingDescriptionValuePair = new BasicNameValuePair(
				Constants.MEETING_DESCRIPTION_POST_TAG, mMeetingDescription);
		return meetingDescriptionValuePair;
	}

	private NameValuePair getStartValuePair(String startTimeInRFC3339Format) {
		NameValuePair meetingStartValuePair = new BasicNameValuePair(
				Constants.MEETING_START_POST_TAG, startTimeInRFC3339Format);
		return meetingStartValuePair;
	}

	private NameValuePair getEndValuePair(String endTimeInRFC3339Format) {
		NameValuePair meetingEndValuePair = new BasicNameValuePair(
				Constants.MEETING_END_POST_TAG, endTimeInRFC3339Format);
		return meetingEndValuePair;
	}

	/**
	 * Method to check whether selected time is clashing with the existing
	 * meetings or not
	 * 
	 * @param startTimeInRFC3339Format
	 * @param endTimeInRFC3339Format
	 * @return boolean true if clashes
	 * @throws ParseException
	 */
	private boolean isClashing(String startTimeInRFC3339Format,
			String endTimeInRFC3339Format) throws ParseException {
		Timestamp startTimestamp = TimeStringManipulator
				.getTimestampFromString(startTimeInRFC3339Format);
		Timestamp endTimestamp = TimeStringManipulator
				.getTimestampFromString(endTimeInRFC3339Format);

		for (int number = 0; number < meetings.length; number++) {
			Timestamp currentMeetingStart = meetings[number].getStartTime();
			Timestamp currentMeetingEnd = meetings[number].getEndTime();
			if ((startTimestamp.before(currentMeetingEnd) && startTimestamp
					.after(currentMeetingStart))
					|| (endTimestamp.before(currentMeetingEnd) && endTimestamp
							.after(currentMeetingStart))
					|| ((startTimestamp.before(currentMeetingStart) || startTimestamp
							.equals(currentMeetingStart)) && (endTimestamp
							.after(currentMeetingEnd) || endTimestamp
							.equals(currentMeetingEnd)))) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Method to start meeting list service to get the meetings
	 * 
	 * @param roomId
	 */
	private void startMeetingListService(int roomId) {
		Intent meetingListService = new Intent(NewMeetingService.this,
				MeetingListService.class);
		meetingListService.putExtra(getString(R.string.room_id_tag), roomId);
		startService(meetingListService);
		registerReceiver(broadcastReceiver, new IntentFilter(
				MeetingListService.BROADCAST_FILTER_NAME));
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
					startNewMeetingAsync();

				} else {
					String errorMessage = intent
							.getStringExtra(Constants.BROADCAST_ERROR_MESSAGE_TAG);
					broadcastErrorMessage(BROADCAST_FILTER_NAME, errorMessage);
				}
			}
		}
	};

	/**
	 * Method to post new meeting
	 */
	private void startNewMeetingAsync() {
		try {

			String startTimeInRFC3339Format = TimeStringManipulator
					.getRFC3339Format(mMeetingStartTime, mMeetingDate);
			String endTimeInRFC3339Format = TimeStringManipulator
					.getRFC3339Format(mMeetingEndTime, mMeetingDate);

			if (!isClashing(startTimeInRFC3339Format, endTimeInRFC3339Format)) {

				SharedPreferences oauthPreference = getSharedPreferences(
						Constants.PREF_TAG, Context.MODE_PRIVATE);
				String dummyOauth = oauthPreference.getString(
						Constants.OAUTH_TAG, Constants.OAUTH_DUMMY);
				NameValuePair oauthValuePair = new BasicNameValuePair(
						Constants.OAUTH_TAG, dummyOauth);

				Log.d(Constants.LOG_TAG, dummyOauth);
				JSONParser jsonParser = new JSONParser(
						Constants.NEW_MEETING_URL, Constants.METHOD_POST);

				jsonParser.setResponse(NewMeetingService.this);
				jsonParser.execute(oauthValuePair, getRoomIdValuePair(),
						getSummaryValuePair(), getDescriptionValuePair(),
						getStartValuePair(startTimeInRFC3339Format),
						getEndValuePair(endTimeInRFC3339Format));
			} else {
				broadcastErrorMessage(BROADCAST_FILTER_NAME,
						getString(R.string.time_clashes_error_message));
			}
		} catch (Exception e) {
			broadcastErrorMessage(BROADCAST_FILTER_NAME, e.getMessage());
		}

	}
}