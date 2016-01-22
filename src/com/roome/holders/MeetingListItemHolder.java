package com.roome.holders;

import android.widget.TextView;

/**
 * Class holder to hold Meeting list item view
 * 
 * @author Jesica Hadiwiryo
 * 
 */
public class MeetingListItemHolder {

	// class members
	private TextView meetingSummaryTextView;
	private TextView ownerAndTimeTextView;

	/**
	 * parameter constructor
	 * 
	 * @param meetingSummaryTextView
	 * @param ownerAndTimeTextView
	 */
	public MeetingListItemHolder(TextView meetingSummaryTextView,
			TextView ownerAndTimeTextView) {
		super();
		this.meetingSummaryTextView = meetingSummaryTextView;
		this.ownerAndTimeTextView = ownerAndTimeTextView;
	}

	/**
	 * Default constructor
	 */
	public MeetingListItemHolder() {
		super();
	}

	/**
	 * Method to get textview
	 * 
	 * @return summary text view
	 */
	public TextView getMeetingSummaryTextView() {
		return meetingSummaryTextView;
	}

	/**
	 * Method to set summary textview
	 * 
	 * @param meetingSummaryTextView
	 */
	public void setMeetingSummaryTextView(TextView meetingSummaryTextView) {
		this.meetingSummaryTextView = meetingSummaryTextView;
	}

	/**
	 * Method to get owner and time text view
	 * 
	 * @return owner and time text view
	 */
	public TextView getOwnerAndTimeTextView() {
		return ownerAndTimeTextView;
	}

	/**
	 * Method to set owner and time text view
	 * 
	 * @param ownerAndTimeTextView
	 */
	public void setOwnerAndTimeTextView(TextView ownerAndTimeTextView) {
		this.ownerAndTimeTextView = ownerAndTimeTextView;
	}
}
