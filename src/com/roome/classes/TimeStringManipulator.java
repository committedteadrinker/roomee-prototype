package com.roome.classes;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import android.util.Log;

import com.roome.constants.Constants;

/**
 * Class to handle with timestamp, time, and string manipulation
 * 
 * @author Jesica Hadiwiryo
 * 
 */
public class TimeStringManipulator {

	// constants
	private static long ONE_SECOND_IN_MILLISECOND = 1000;
	private static long ONE_MINUTE_IN_MILLISECOND = 60 * ONE_SECOND_IN_MILLISECOND;
	private static long ONE_HOUR_IN_MINUTE = 60;

	/**
	 * get hour and minute in one sentence of string format
	 * 
	 * @param timeStamp
	 * @return String of 'an hour and a minute'
	 */
	public static String getHourMinuteTimeString(Timestamp timeStamp) {
		String timeInString = "";
		Date date = new Date();

		timeInString = getTimeDifference(timeStamp,
				new Timestamp(date.getTime()));

		return timeInString;
	}

	/**
	 * Get the time difference in string format
	 * 
	 * @param startTime
	 * @param endTime
	 * @return String of time difference
	 */
	public static String getTimeDifference(Timestamp startTime,
			Timestamp endTime) {
		String timeInString = "";
		long start = startTime.getTime();
		long end = endTime.getTime();

		long minutesDifference = Math.abs(start - end)
				/ ONE_MINUTE_IN_MILLISECOND;
		if (minutesDifference >= ONE_HOUR_IN_MINUTE) {
			timeInString = getHourMinuteString(minutesDifference);
		} else {
			timeInString = getMinuteString(minutesDifference);
		}

		return timeInString;
	}

	/**
	 * Get string of timestamp in period format
	 * 
	 * @param timeStamp
	 * @return string of time in period format
	 */
	public static String getPeriodFormat(Timestamp timeStamp) {
		String timeInString = "";

		timeInString = new SimpleDateFormat("hh:mma", Locale.US)
				.format(timeStamp);

		return timeInString;
	}

	/**
	 * separate hour to get the integer value of it
	 * 
	 * @param time
	 * @return integer hour
	 */
	public static int getHourInt(String time) {
		String hourString = time.substring(0, 2);
		return Integer.parseInt(hourString);
	}

	/**
	 * separate minute to get the integer value of it
	 * 
	 * @param time
	 * @return integer minute
	 */
	public static int getMinuteInt(String time) {
		String minuteString = time.substring(3, time.length());
		return Integer.parseInt(minuteString);
	}

	/**
	 * Get hours and minutes difference in string format based on the
	 * differences
	 * 
	 * @param minutesDifference
	 * @return String of time difference
	 */
	private static String getHourMinuteString(long minutesDifference) {
		String hourMinute = "";

		long hour = (int) (minutesDifference / ONE_HOUR_IN_MINUTE);

		long minute = (int) (minutesDifference % ONE_HOUR_IN_MINUTE);
		if (minute == 0)
			hourMinute = hour + " hours";
		else
			hourMinute = hour + " hours and " + minute + " minutes";

		return hourMinute;
	}

	/**
	 * get minute in string
	 * 
	 * @param minutesDifference
	 * @return Example : '5 minutes'
	 */
	private static String getMinuteString(long minutesDifference) {
		return String.valueOf(minutesDifference) + " minutes";
	}

	/**
	 * remove last occurence of ':' in RFC3339Format that is received from the
	 * server
	 * 
	 * @param time
	 * @return string of parseable timestamp. Example : '-1100'
	 */
	private static String removeLastOccurence(String time) {
		int index = time.lastIndexOf(":");
		if (index != -1) {
			time = new StringBuilder(time).replace(index, index + 1, "")
					.toString();
		}
		return time;
	}

	/**
	 * Get timestamp format from a string
	 * 
	 * @param timeInString
	 * @return timestamp
	 * @throws ParseException
	 */
	public static Timestamp getTimestampFromString(String timeInString)
			throws ParseException {
		SimpleDateFormat dateFormat = new SimpleDateFormat(
				"yyyy-MM-dd'T'HH:mm:ssZ", Locale.US);
		long timeFormatted = 0;
		TimeStringManipulator.removeLastOccurence(timeInString);
		timeFormatted = dateFormat.parse(timeInString).getTime();

		Timestamp time = new Timestamp(timeFormatted);
		return time;
	}

	/**
	 * get current time in 24hr format
	 * 
	 * @return String of time
	 */
	public static String getCurrentTime24HrFormat() {
		String currentHour = getTimeWithZero(getCurrentHour());
		String currentMinute = getTimeWithZero(getCurrentMinute());
		return currentHour + ":" + currentMinute;
	}

	/**
	 * get one hour after in 24 hr format
	 * 
	 * @return String of 1 hour after
	 */
	public static String getOneHourAfter24HrFormat() {
		String oneHourAfterHour = getTimeWithZero(getCurrentHour() + 1);
		String currentMinute = getTimeWithZero(getCurrentMinute());
		return oneHourAfterHour + ":" + currentMinute;
	}

	/**
	 * get one hour after in period format
	 * 
	 * @return String of 1 hour after
	 */
	public static String getOneHourAfterPeriodFormat() {
		int currentHour = getCurrentHour() + 1;
		int currentMinute = getCurrentMinute();
		return getPeriodFormat(currentHour, currentMinute);
	}

	/**
	 * get current time in period format
	 * 
	 * @return String of time
	 */
	public static String getCurrentPeriodFormat() {
		int currentHour = getCurrentHour();
		int currentMinute = getCurrentMinute();
		return getPeriodFormat(currentHour, currentMinute);
	}

	/**
	 * get current hour
	 * 
	 * @return int of hour
	 */
	private static int getCurrentHour() {
		final Calendar currentTime = Calendar.getInstance();
		int currentHour = currentTime.get(Calendar.HOUR_OF_DAY);
		return currentHour;
	}

	/**
	 * Example time = 3. Result should be '03'
	 * 
	 * @param time
	 * @return
	 */
	private static String getTimeWithZero(int time) {
		if (time < 10) {
			return "0" + time;
		} else {
			return "" + time;
		}
	}

	/**
	 * get current minute
	 * 
	 * @return integer of minute
	 */
	private static int getCurrentMinute() {
		final Calendar currentTime = Calendar.getInstance();
		int currentMinute = currentTime.get(Calendar.MINUTE);
		return currentMinute;
	}

	/**
	 * Get period based on the hour of the day
	 * 
	 * @param hourOfDay
	 * @return String of period. Example 'AM'
	 */
	private static String getPeriod(int hourOfDay) {
		String period;
		if (hourOfDay < 12) {
			period = Constants.AM_IN_TEXT;
		} else if (hourOfDay == 12) {
			period = Constants.PM_IN_TEXT;
		} else if (hourOfDay == 24) {
			period = Constants.AM_IN_TEXT;
		} else {
			period = Constants.PM_IN_TEXT;
		}
		return period;
	}

	/**
	 * Get string of time in period format
	 * 
	 * @param hourOfDay
	 * @param minute
	 * @return String of time in period format. Example: '09:00 am'
	 */
	public static String getPeriodFormat(int hourOfDay, int minute) {
		String hourInString = getTimeWithZero(hourOfDay);
		String minuteInString = getTimeWithZero(minute);
		String period = getPeriod(hourOfDay);
		if (hourOfDay == 24) {
			hourInString = "00";
		} else if (hourOfDay > 12) {
			hourInString = getTimeWithZero(hourOfDay - 12);
		} else {
			hourInString = getTimeWithZero(hourOfDay);
		}

		return hourInString + ":" + minuteInString + " " + period;
	}

	/**
	 * Get current date in string
	 * 
	 * @return string of current date. Example '17-07-2013'
	 */
	public static String getCurrentDateInString() {
		DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
		Date date = new Date();

		return dateFormat.format(date);
	}

	/**
	 * Get current date in RFC3339 format
	 * 
	 * @return string of RFC3339formatted date. Example '2013-07-17'
	 */
	public static String getCurrentDateInRFC3339Format() {
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
		Date date = new Date();

		return dateFormat.format(date);
	}

	/**
	 * Build date using integers
	 * 
	 * @param year
	 * @param monthOfYear
	 * @param dayOfMonth
	 * @return String of date
	 */
	public static String getDateInString(int year, int monthOfYear,
			int dayOfMonth) {

		return getDateWithZero(dayOfMonth) + "-" + getDateWithZero(monthOfYear)
				+ "-" + year;
	}

	/**
	 * Get date with zero Example date = 8 result should be '08'
	 * 
	 * @param date
	 * @return
	 */
	private static String getDateWithZero(int date) {
		if (date < 10) {
			return "0" + date;
		} else {
			return "" + date;
		}
	}

	/**
	 * Get current timezone
	 * 
	 * @return string of timezone
	 */
	private static String getCurrentTimeZone() {
		String timeZoneString = new SimpleDateFormat("Z", Locale.US)
				.format(Calendar.getInstance().getTime());
		return timeZoneString.substring(0, 3) + ":"
				+ timeZoneString.substring(3, timeZoneString.length());
	}

	/**
	 * build RFC3339 format timestamp
	 * 
	 * @param time
	 * @param date
	 * @return String of RFC3330 timestamp
	 */
	public static String getRFC3339Format(String time, String date) {
		String RFC3339Time = date + "T" + time + ":00" + getCurrentTimeZone();
		return RFC3339Time;
	}

	/**
	 * get RFC3339 format of time
	 * 
	 * @param hourOfDay
	 * @param minute
	 * @return
	 */
	public static String getRFC3339Time(int hourOfDay, int minute) {
		return getTimeWithZero(hourOfDay) + ":" + getTimeWithZero(minute);
	}

	/**
	 * Build RFC3339 format date
	 * 
	 * @param year
	 * @param monthOfYear
	 * @param dayOfMonth
	 * @return
	 */
	public static String getRFC3339Date(int year, int monthOfYear,
			int dayOfMonth) {
		return year + "-" + getDateWithZero(monthOfYear) + "-"
				+ getDateWithZero(dayOfMonth);
	}

	/**
	 * Get year from RFC3339 format date string
	 * 
	 * @param RFC3339Date
	 * @return
	 */
	public static int getRFC3339Year(String RFC3339Date) {
		String yearString = RFC3339Date.substring(0, 4);
		return Integer.parseInt(yearString);
	}

	/**
	 * Get month from RFC3339 format date string
	 * 
	 * @param RFC3339Date
	 * @return
	 */
	public static int getRFC3339Month(String RFC3339Date) {
		String monthString = RFC3339Date.substring(5, 7);
		return Integer.parseInt(monthString);
	}

	/**
	 * Get day from RFC3339 format date string
	 * 
	 * @param RFC3339Date
	 * @return
	 */
	public static int getRFC3339Day(String RFC3339Date) {
		String dayString = RFC3339Date.substring(8, RFC3339Date.length());
		Log.d(Constants.LOG_TAG, dayString);
		return Integer.parseInt(dayString);
	}
}
