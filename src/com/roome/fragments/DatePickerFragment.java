package com.roome.fragments;

import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.DatePicker;

/**
 * Class to create date picker dialog fragment
 * 
 * @author Jesica Hadiwiryo
 * 
 */
public class DatePickerFragment extends DialogFragment implements
		OnDateSetListener {

	// constants
	private final static String BUNDLE_KEY = "date_picker_bundle_key";
	private final static String YEAR_KEY = "date_picker_year_key";
	private final static String MONTH_KEY = "date_picker_month_key";
	private final static String DAY_KEY = "date_picker_day_key";
	private final static int MONTH_JAVA_DIFFERENCE = 1;
	
	//class members
	private int mId;
	private DatePickerDialogListener mListener;

	/**
	 * New instance of dialog fragment 
	 * @param id
	 * @param year
	 * @param monthOfYear
	 * @param DayOfMonth
	 * @return
	 */
	public static DatePickerFragment newInstance(int id, int year,
			int monthOfYear, int DayOfMonth) {
		Bundle args = new Bundle();
		args.putInt(BUNDLE_KEY, id);
		args.putInt(YEAR_KEY, year);
		args.putInt(MONTH_KEY, monthOfYear);
		args.putInt(DAY_KEY, DayOfMonth);
		DatePickerFragment fragment = new DatePickerFragment();
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	/**
	 * Handling on dialog fragment created
	 */
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		int date = getArguments().getInt(DAY_KEY);
		int month = getArguments().getInt(MONTH_KEY) - MONTH_JAVA_DIFFERENCE;
		int year = getArguments().getInt(YEAR_KEY);

		mId = getArguments().getInt(BUNDLE_KEY);
		mListener = getActivity() instanceof DatePickerDialogListener ? (DatePickerDialogListener) getActivity()
				: null;

		return new DatePickerDialog(getActivity(), this, year, month, date);
	}

	@Override
	/**
	 * Handling when date is set
	 */
	public void onDateSet(DatePicker view, int year, int monthOfYear,
			int dayOfMonth) {
		if (mListener != null)
			mListener.onDateSet(mId, view, year, monthOfYear
					+ MONTH_JAVA_DIFFERENCE, dayOfMonth);
	}

	/**
	 * interface to pass listener result
	 */
	public static interface DatePickerDialogListener {
		public void onDateSet(int id, DatePicker view, int year,
				int monthOfYear, int dayOfMonth);
	}

}
