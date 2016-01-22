package com.roome.fragments;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.TimePicker;

/**
 * Class to create time picker dialog fragment
 * 
 * @author Jesica Hadiwiryo
 * 
 */
public class TimePickerFragment extends DialogFragment implements
		OnTimeSetListener {

	// constants
	private final static String BUNDLE_KEY = "time_picker_bundle_key";
	private final static String HOUR_KEY = "time_picker_hour_key";
	private final static String MINUTE_KEY = "time_picker_minute_key";
	
	//class members
	private int mId;
	private TimePickerDialogListener mListener;

	/**
	 * New instance of dialog fragment
	 * @param id
	 * @param hourOfDay
	 * @param minute
	 * @return
	 */
	public static TimePickerFragment newInstance(int id, int hourOfDay, int minute) {
		Bundle args = new Bundle();
		args.putInt(BUNDLE_KEY, id);
		args.putInt(HOUR_KEY, hourOfDay);
		args.putInt(MINUTE_KEY, minute);
		TimePickerFragment fragment = new TimePickerFragment();
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	/**
	 * Handling on dialog fragment created
	 */
	public Dialog onCreateDialog(Bundle savedInstanceState) {

		int hour = getArguments().getInt(HOUR_KEY);
		int minute = getArguments().getInt(MINUTE_KEY);
		mId = getArguments().getInt(BUNDLE_KEY);
		mListener = getActivity() instanceof TimePickerDialogListener ? (TimePickerDialogListener) getActivity()
				: null;
		
		// Create a new instance of TimePickerDialog and return it
		return new TimePickerDialog(getActivity(), this, hour, minute, false);
	}

	@Override
	/**
	 * Handling when time is set
	 */
	public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
		if (mListener != null)
			mListener.onTimeSet(mId, view, hourOfDay, minute);
	}

	/**
	 * interface to pass listener result
	 */
	public static interface TimePickerDialogListener {
		public void onTimeSet(int id, TimePicker view, int hourOfDay, int minute);
	}
}
