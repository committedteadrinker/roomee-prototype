package com.roome.activities;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;

import com.prototype.roomephone.R;
import com.roome.classes.Keyboard;
import com.roome.classes.Meeting;
import com.roome.classes.Room;
import com.roome.classes.TimeStringManipulator;
import com.roome.constants.Constants;
import com.roome.fragments.DatePickerFragment;
import com.roome.fragments.TimePickerFragment;
import com.roome.services.NewMeetingService;

/**
 * Class to handle New meeting activity
 * 
 * @author Jesica Hadiwiryo
 * 
 */
public class NewMeetingActivity extends FragmentActivity implements
		TimePickerFragment.TimePickerDialogListener,
		DatePickerFragment.DatePickerDialogListener {

	// class members
	private EditText mSummaryEditText;
	private EditText mDescriptionEditText;
	private Button mStartTimeTextView;
	private Button mEndTimeTextView;
	private Button mDateSelectionTextView;
	private String mDateSelected;
	private String mStartTimeSelected;
	private String mEndTimeSelected;
	private Room mRoomSelected;
	private ProgressDialog mProgressDialog;
	private Meeting[] mMeetingList;
	private Intent mNewMeetingService;

	@Override
	/**
	 * Method to handle view when activity is created
	 */
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_new_meeting);

		mRoomSelected = getIntent().getParcelableExtra(
				getString(R.string.room_object_tag));
		Parcelable[] parcelArray = getIntent().getParcelableArrayExtra(
				getString(R.string.meetings_array_tag));
		mMeetingList = new Meeting[parcelArray.length];
		System.arraycopy(parcelArray, 0, mMeetingList, 0, parcelArray.length);

		setView();
		setDefaultTime();
	}

	@Override
	/**
	 * Method to handle when options are created
	 */
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.new_meeting, menu);
		return true;
	}

	@Override
	/**
	 * Method to handle when a menu item got selected
	 */
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_submit:
			Keyboard.hide(NewMeetingActivity.this);
			String summary = mSummaryEditText.getText().toString();
			String description = mDescriptionEditText.getText().toString();

			if (!summary.isEmpty() && !description.isEmpty()) {
				setupProgressDialog();
				setExtras(summary, description);
				startService(mNewMeetingService);
				registerReceiver(broadcastReceiver, new IntentFilter(
						NewMeetingService.BROADCAST_FILTER_NAME));

				return true;
			} else {
				new AlertDialog.Builder(NewMeetingActivity.this)
						.setTitle(R.string.error)
						.setMessage("Please complete the meeting details")
						.show();

				return false;
			}
		case R.id.action_cancel:
			this.finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * setup and show progress dialog
	 */
	private void setupProgressDialog() {
		mProgressDialog = new ProgressDialog(NewMeetingActivity.this);
		mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		mProgressDialog.setMessage(getString(R.string.creating_new_meeting_));
		mProgressDialog.show();
	}

	/**
	 * setup members of class to be submitted
	 * 
	 * @param summary
	 *            String for meeting summary
	 * @param description
	 *            String for meeting description
	 */
	private void setExtras(String summary, String description) {
		mNewMeetingService = new Intent(NewMeetingActivity.this,
				NewMeetingService.class);
		mNewMeetingService.putExtra(getString(R.string.room_id_tag),
				mRoomSelected.getId());
		mNewMeetingService.putExtra(getString(R.string.meeting_summary_tag),
				summary);
		mNewMeetingService.putExtra(
				getString(R.string.meeting_description_tag), description);
		mNewMeetingService.putExtra(getString(R.string.meeting_start_tag),
				mStartTimeSelected);
		mNewMeetingService.putExtra(getString(R.string.meeting_end_tag),
				mEndTimeSelected);
		mNewMeetingService.putExtra(getString(R.string.meeting_date_tag),
				mDateSelected);
		mNewMeetingService.putExtra(getString(R.string.meetings_array_tag),
				mMeetingList);
	}

	/**
	 * setup default time displayed in the time buttons
	 */
	private void setDefaultTime() {
		mStartTimeSelected = TimeStringManipulator.getCurrentTime24HrFormat();
		mEndTimeSelected = TimeStringManipulator.getOneHourAfter24HrFormat();
		mDateSelected = TimeStringManipulator.getCurrentDateInRFC3339Format();

		mStartTimeTextView.setText(TimeStringManipulator
				.getCurrentPeriodFormat());
		mEndTimeTextView.setText(TimeStringManipulator
				.getOneHourAfterPeriodFormat());
		mDateSelectionTextView.setText(TimeStringManipulator
				.getCurrentDateInString());
	}

	/**
	 * set members of class to views from layout
	 */
	private void setView() {
		mSummaryEditText = (EditText) findViewById(R.id.summaryEditText);
		mDescriptionEditText = (EditText) findViewById(R.id.descriptionEditText);
		mStartTimeTextView = (Button) findViewById(R.id.startTimeTextView);
		mEndTimeTextView = (Button) findViewById(R.id.endTimeTextView);
		mDateSelectionTextView = (Button) findViewById(R.id.dateSelectionTextView);
	}

	/**
	 * Method to handle button pressed
	 * 
	 * @param v
	 *            View that is pressed
	 */
	public void timeButtonListener(View v) {
		switch (v.getId()) {
		case R.id.startTimeTextView:
			int startHour = TimeStringManipulator
					.getHourInt(mStartTimeSelected);
			int startMinute = TimeStringManipulator
					.getMinuteInt(mStartTimeSelected);
			DialogFragment startTimeFragment = TimePickerFragment.newInstance(
					Constants.START_TIME_PICKER_ID, startHour, startMinute);
			startTimeFragment.show(getSupportFragmentManager(),
					getString(R.string.timepicker_fragment_tag));
			break;
		case R.id.endTimeTextView:
			int endHour = TimeStringManipulator.getHourInt(mEndTimeSelected);
			int endMinute = TimeStringManipulator
					.getMinuteInt(mEndTimeSelected);
			DialogFragment endTimeFragment = TimePickerFragment.newInstance(
					Constants.END_TIME_PICKER_ID, endHour, endMinute);
			endTimeFragment.show(getSupportFragmentManager(),
					getString(R.string.timepicker_fragment_tag));
			break;
		case R.id.dateSelectionTextView:
			int year = TimeStringManipulator.getRFC3339Year(mDateSelected);
			int month = TimeStringManipulator.getRFC3339Month(mDateSelected);
			int day = TimeStringManipulator.getRFC3339Day(mDateSelected);

			DialogFragment dateSelectionFragment = DatePickerFragment
					.newInstance(Constants.DATE_PICKER_ID, year, month, day);
			dateSelectionFragment.show(getSupportFragmentManager(),
					getString(R.string.datepicker_fragment_tag));
		}
	}

	@Override
	/**
	 * Method to handle when user has chosen a time
	 */
	public void onTimeSet(int id, TimePicker view, int hourOfDay, int minute) {
		String periodTimeInString = TimeStringManipulator.getPeriodFormat(
				hourOfDay, minute);
		String timeSelectedInString = TimeStringManipulator.getRFC3339Time(
				hourOfDay, minute);
		switch (id) {
		case Constants.START_TIME_PICKER_ID:
			mStartTimeTextView.setText(periodTimeInString);
			mStartTimeSelected = timeSelectedInString;
			break;
		case Constants.END_TIME_PICKER_ID:
			mEndTimeTextView.setText(periodTimeInString);
			mEndTimeSelected = timeSelectedInString;
			break;
		}
	}

	@Override
	/**
	 * Method to handle when user has selected date
	 */
	public void onDateSet(int id, DatePicker view, int year, int monthOfYear,
			int dayOfMonth) {

		String dateInString = TimeStringManipulator.getDateInString(year,
				monthOfYear, dayOfMonth);
		String dateSelectedInString = TimeStringManipulator.getRFC3339Date(
				year, monthOfYear, dayOfMonth);
		switch (id) {
		case Constants.DATE_PICKER_ID:
			mDateSelectionTextView.setText(dateInString);
			mDateSelected = dateSelectedInString;
			break;
		}

	}

	// Broadcast receiver to handle with Async services
	private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {

		@Override
		/**
		 * Method to handle when broadcast received
		 */
		public void onReceive(Context context, Intent intent) {
			mProgressDialog.dismiss();
			unregisterReceiver(broadcastReceiver);
			boolean broadcastStatus = intent.getBooleanExtra(
					Constants.BROADCAST_ERROR_STATUS_TAG,
					Constants.BROADCAST_ERROR);
			if (broadcastStatus) {
				Intent roomDetailIntent = new Intent(NewMeetingActivity.this,
						RoomDetailActivity.class);
				roomDetailIntent.putExtra(getString(R.string.room_id_tag),
						mRoomSelected.getId());

				startActivity(roomDetailIntent);
			} else {
				String errorMessage = intent
						.getStringExtra(Constants.BROADCAST_ERROR_MESSAGE_TAG);

				new AlertDialog.Builder(NewMeetingActivity.this)
						.setTitle(R.string.error).setMessage(errorMessage)
						.show();
			}
			stopService(mNewMeetingService);
		}

	};

}
