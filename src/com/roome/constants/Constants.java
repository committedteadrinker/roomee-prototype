package com.roome.constants;

/**
 * Class to containt all shared constants
 * 
 * @author Jesica Hadiwiryo
 * 
 */
public class Constants {

	public static String URL = "http://api.roomee.chrsptn.com/";
	public static String ROOM_LIST_URL = URL + "rooms";
	public static String MEETING_LIST_URL = URL + "meetings";
	public static String AUTH_URL = URL + "auth";
	public static String ROOM_DETAIL_URL = URL + "room";
	public static String NEW_MEETING_URL = URL + "meetings";

	public static String PREF_TAG = "Roome";
	public static long DEFAULT_LIST_REFRESH_RATE = 60000;
	public static final long SIMULATED_REFRESH_LENGTH = 6000;

	public final static String LOG_TAG = "Roomee Prototype";
	public final static String OAUTH_TAG = "oauth_token";
	public final static String API_KEY_TAG = "api_key";
	public final static String ROOM_ID_POST_TAG = "id";
	public final static String MEETING_SUMMARY_POST_TAG = "summary";
	public final static String MEETING_DESCRIPTION_POST_TAG = "description";
	public final static String MEETING_START_POST_TAG = "startTime";
	public final static String MEETING_END_POST_TAG = "endTime";
	public final static String API_KEY = "lrn6a1n3";
	public final static String OAUTH_DUMMY = "dummy";

	public static final String AM_IN_TEXT = "am";
	public static final String PM_IN_TEXT = "pm";

	public final static int FILTER_STATUS_ALL = 0;
	public final static int FILTER_STATUS_FREE = 1;

	public final static String METHOD_GET = "GET";
	public final static String METHOD_POST = "POST";

	public final static int ROOM_FREE = 0;
	public final static int ROOM_OCCUPIED = 1;

	public static final String BROADCAST_ROOM_LIST_TAG = "room lists";
	public static final String BROADCAST_ROOM_DETAIL_TAG = "room details";
	public static final String BROADCAST_MEETING_LIST_TAG = "meeting lists";
	public static final String BROADCAST_ERROR_STATUS_TAG = "error status";
	public static final String BROADCAST_ERROR_MESSAGE_TAG = "error";
	public static final boolean BROADCAST_NO_ERROR = true;
	public static final boolean BROADCAST_ERROR = false;

	public static final String FREE_TEXT = "Free for ";
	public static final String BUSY_TEXT = "Busy for ";

	public static final int START_TIME_PICKER_ID = 33;
	public static final int END_TIME_PICKER_ID = 34;
	public static final int DATE_PICKER_ID = 80;

}
