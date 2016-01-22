package com.roome.activities;

import java.util.Arrays;
import java.util.List;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.prototype.roomephone.R;
import com.roome.adapters.MeetingListAdapter;
import com.roome.classes.Meeting;
import com.roome.classes.Room;
import com.roome.classes.TimeStringManipulator;
import com.roome.constants.Constants;
import com.roome.services.RoomDetailService;

/**
 * Class to handle room detail activity
 * 
 * @author Jesica Hadiwiryo
 * 
 */
public class RoomDetailActivity extends ListActivity {
	// constants
	private final static int errorId = -1;

	// members of class
	private TextView mRoomStatusTextView;
	private TextView mRoomTimeTextView;
	private LinearLayout mRoomStatusRelativeLayout;
	private ProgressDialog mProgressDialog;
	private Intent roomDetailService;
	private Room mRoom;
	private Meeting[] mMeetingList;

	@Override
	/**
	 * Method to handle view when activity is created
	 */
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_detail_room);

		mRoomStatusRelativeLayout = (LinearLayout) findViewById(R.id.roomStatusRelativeLayout);
		mRoomStatusTextView = (TextView) findViewById(R.id.roomStatusTextView);
		mRoomTimeTextView = (TextView) findViewById(R.id.roomTimeTextView);

		getActionBar().hide();
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setHomeButtonEnabled(true);
		Intent intent = getIntent();

		String roomId = String.valueOf(intent.getIntExtra(
				getString(R.string.room_id_tag), errorId));

		mProgressDialog = new ProgressDialog(RoomDetailActivity.this);
		mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		mProgressDialog.setMessage(getString(R.string.getting_room_details_));
		mProgressDialog.show();

		roomDetailService = new Intent(RoomDetailActivity.this,
				RoomDetailService.class);
		roomDetailService.putExtra(getString(R.string.room_id_tag), roomId);
		startService(roomDetailService);
		registerReceiver(broadcastReceiver, new IntentFilter(
				RoomDetailService.BROADCAST_FILTER_NAME));
	}

	@Override
	/**
	 * Method to handle when a menu item got selected
	 */
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			this.finish();
			return true;
		case R.id.action_new_meeting:
			Intent newMeetingIntent = new Intent(RoomDetailActivity.this,
					NewMeetingActivity.class);
			newMeetingIntent.putExtra(getString(R.string.room_object_tag),
					mRoom);
			newMeetingIntent.putExtra(getString(R.string.meetings_array_tag),
					mMeetingList);
			startActivity(newMeetingIntent);

			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	@Override
	/**
	 * Mehod to handle configuration changed
	 * in this case, it is orientation change
	 */
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}

	@Override
	/**
	 * Method to handle when options are created
	 */
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.detail_room, menu);
		return true;
	}

	// Broadcast receiver to handle with Async services
	private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {

		@Override
		/**
		 * Method to handle when broadcast received
		 */
		public void onReceive(Context context, Intent intent) {
			mProgressDialog.dismiss();
			stopService(roomDetailService);
			unregisterReceiver(broadcastReceiver);

			boolean broadcastStatus = intent.getBooleanExtra(
					Constants.BROADCAST_ERROR_STATUS_TAG,
					Constants.BROADCAST_ERROR);
			if (broadcastStatus) {
				setActionBar(intent);
				setList(intent);
			} else {
				String errorMessage = intent
						.getStringExtra(Constants.BROADCAST_ERROR_MESSAGE_TAG);

				new AlertDialog.Builder(RoomDetailActivity.this)
						.setTitle(R.string.error).setMessage(errorMessage)
						.show();
			}
		}

	};

	/**
	 * Set action bar background, title, and other background color
	 * 
	 * @param intent
	 *            intent that contains room selected
	 */
	private void setActionBar(Intent intent) {
		mRoom = intent.getParcelableExtra(Constants.BROADCAST_ROOM_DETAIL_TAG);

		String statusText;
		ColorDrawable actionBarColor;
		ColorDrawable roomStatusColor;
		String timeString = TimeStringManipulator.getHourMinuteTimeString(mRoom
				.getTime());

		if (mRoom.getFreebusy() == Constants.ROOM_FREE) {
			actionBarColor = new ColorDrawable(getResources().getColor(
					R.color.roome_darker_green));
			roomStatusColor = new ColorDrawable(getResources().getColor(
					R.color.roome_green));
			statusText = Constants.FREE_TEXT;
		} else {
			actionBarColor = new ColorDrawable(getResources().getColor(
					R.color.roome_darker_red));
			roomStatusColor = new ColorDrawable(getResources().getColor(
					R.color.roome_red));
			statusText = Constants.BUSY_TEXT;
		}
		getActionBar().setBackgroundDrawable(actionBarColor);
		mRoomStatusRelativeLayout.setBackground(roomStatusColor);
		mRoomStatusTextView.setText(statusText);
		mRoomTimeTextView.setText(timeString);
		getActionBar().setTitle(mRoom.getName());
		getActionBar().show();
	}

	/**
	 * Set meeting list using custom adapter
	 * 
	 * @param intent
	 *            intent that contains meeting list
	 */
	private void setList(Intent intent) {

		Parcelable[] parcelArray = intent
				.getParcelableArrayExtra(Constants.BROADCAST_MEETING_LIST_TAG);
		mMeetingList = new Meeting[parcelArray.length];
		System.arraycopy(parcelArray, 0, mMeetingList, 0, parcelArray.length);
		List<Meeting> meetings = Arrays.asList(mMeetingList);

		if (meetings.size() != 0) {
			// Use adapter to show room list in list view
			MeetingListAdapter adapter = new MeetingListAdapter(
					RoomDetailActivity.this, meetings);
			setListAdapter(adapter);
		} else {
			TextView emptyTextView = (TextView) findViewById(R.id.emptyMeetingListTextView);
			emptyTextView.setText(R.string.no_room_list);
		}
	}
}
