package com.roome.adapters;

import java.sql.Timestamp;
import java.util.List;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.prototype.roomephone.R;
import com.roome.classes.Meeting;
import com.roome.classes.TimeStringManipulator;
import com.roome.constants.Constants;
import com.roome.holders.MeetingListItemHolder;

/**
 * Custom adapter class to display a list of meetings
 * 
 * @author Jesica Hadiwiryo
 * 
 */
public class MeetingListAdapter extends ArrayAdapter<Meeting> {

	// constants
	private final static String MEETING_GAP_TEXT = "Free between ";

	// class variables
	private Context context;
	private List<Meeting> meetings;

	/**
	 * Constructor to create the adapter
	 * 
	 * @param context
	 *            current activity where the list belongs to
	 * @param meetingList
	 *            a list of Meeting
	 */
	public MeetingListAdapter(Context context, List<Meeting> meetingList) {
		super(context, R.layout.meeting_list_row, meetingList);
		this.context = context;
		this.meetings = meetingList;
	}

	@Override
	/**
	 * Method called when inflating list view
	 */
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = null;
		// check the oldview passed by listview to recycle

		Meeting currentMeeting = meetings.get(position);
		Meeting nextMeeting = getNextMeeting(position);
		if (convertView == null) {
			// inflate new view
			LayoutInflater inflator = (LayoutInflater) this.context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = inflator.inflate(R.layout.meeting_list_row, null);

			if (nextMeeting != null
					&& hasTimeGap(currentMeeting.getEndTime(),
							nextMeeting.getStartTime())) {

				Log.d(Constants.LOG_TAG, currentMeeting.getEndTime().toString());
				Log.d(Constants.LOG_TAG, nextMeeting.getStartTime().toString());

				RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
						ViewGroup.LayoutParams.MATCH_PARENT,
						ViewGroup.LayoutParams.MATCH_PARENT);

				layoutParams.addRule(RelativeLayout.BELOW,
						R.id.meetingCreatorTimeTextView);

				TextView meetingGapTextView = (TextView) inflator.inflate(
						R.layout.meeting_gap_text_view, null);
				meetingGapTextView.setLayoutParams(layoutParams);
				meetingGapTextView
						.setText(getMeetingGapText(currentMeeting.getEndTime(),
								nextMeeting.getStartTime()));
				((ViewGroup) view).addView(meetingGapTextView);
			}
			// initialize member of views to its view and save it in viewholder
			final MeetingListItemHolder viewHolder = new MeetingListItemHolder();
			setView(view, viewHolder);
		} else {
			// update the old view passed
			view = convertView;
		}

		// get current view
		MeetingListItemHolder holder = (MeetingListItemHolder) view.getTag();

		String summary = meetings.get(position).getSummary();
		String creator = meetings.get(position).getCreator();
		Timestamp startTime = meetings.get(position).getStartTime();
		Timestamp endTime = meetings.get(position).getEndTime();

		String creatorAndTime = getCreatorAndTimeInString(creator, startTime,
				endTime);

		holder.getMeetingSummaryTextView().setText(summary);
		holder.getOwnerAndTimeTextView().setText(creatorAndTime);

		// return the view to be re-used
		return view;
	}

	/**
	 * Set view on to the viewholder that contains member of class
	 * 
	 * @param view
	 *            that contains list row
	 * @param viewHolder
	 *            container to contain the view members
	 */
	private void setView(View view, MeetingListItemHolder viewHolder) {

		TextView meetingSummaryTextView = (TextView) view
				.findViewById(R.id.meetingSummaryTextView);
		TextView meetingCreatorTimeTextView = (TextView) view
				.findViewById(R.id.meetingCreatorTimeTextView);

		viewHolder.setMeetingSummaryTextView(meetingSummaryTextView);
		viewHolder.setOwnerAndTimeTextView(meetingCreatorTimeTextView);
		view.setTag(viewHolder);
	}

	/**
	 * Get string of both creator and the meeting times
	 * 
	 * @param creator
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	private String getCreatorAndTimeInString(String creator,
			Timestamp startTime, Timestamp endTime) {
		String startTimeInString = TimeStringManipulator
				.getPeriodFormat(startTime);
		String endTimeInString = TimeStringManipulator.getPeriodFormat(endTime);

		String creatorAndTime = creator + ". " + startTimeInString + " to "
				+ endTimeInString + ".";

		return creatorAndTime;
	}

	/**
	 * Get string of meeting gap
	 * 
	 * @param currentMeetingEndTime
	 * @param nextMeetingStartTime
	 * @return
	 */
	private String getMeetingGapText(Timestamp currentMeetingEndTime,
			Timestamp nextMeetingStartTime) {
		String currentEndTimeString = TimeStringManipulator
				.getPeriodFormat(currentMeetingEndTime);
		String nextStartTimeString = TimeStringManipulator
				.getPeriodFormat(nextMeetingStartTime);

		return MEETING_GAP_TEXT + currentEndTimeString + " and "
				+ nextStartTimeString + ".";
	}

	/**
	 * Get next meeting available
	 * 
	 * @param currentPosition
	 * @return null if end of meetings
	 */
	private Meeting getNextMeeting(int currentPosition) {
		int nextPosition = currentPosition + 1;
		if (currentPosition < 0 || nextPosition == meetings.size())
			return null;
		return meetings.get(nextPosition);
	}

	/**
	 * Check whether current meeting has gap after it
	 * 
	 * @param currentMeetingEndTime
	 * @param nextMeetingStartTime
	 * @return
	 */
	private boolean hasTimeGap(Timestamp currentMeetingEndTime,
			Timestamp nextMeetingStartTime) {
		if (currentMeetingEndTime.equals(nextMeetingStartTime)
				|| currentMeetingEndTime.after(nextMeetingStartTime)) {
			return false;
		}
		return true;
	}

}
