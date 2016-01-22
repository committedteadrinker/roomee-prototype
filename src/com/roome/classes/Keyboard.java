package com.roome.classes;

import android.app.Activity;
import android.view.inputmethod.InputMethodManager;

/**
 * Class to address related functionality of softkeyboard
 * 
 * @author Jesica Hadiwiryo
 * 
 */
public class Keyboard {

	/**
	 * Toggle the keyboard in the activity
	 * 
	 * @param activity
	 *            current activity for toggling keyboard
	 */
	public static void toggle(Activity activity) {
		InputMethodManager imm = (InputMethodManager) activity
				.getSystemService(Activity.INPUT_METHOD_SERVICE);
		if (imm.isActive()) {
			imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0); // hide
		} else {
			imm.toggleSoftInput(0, InputMethodManager.HIDE_IMPLICIT_ONLY); // show
		}
	}

	/**
	 * Hide keyboard from the activity
	 * 
	 * @param activity
	 */
	public static void hide(Activity activity) {
		InputMethodManager inputMethodManager = (InputMethodManager) activity
				.getSystemService(Activity.INPUT_METHOD_SERVICE);
		inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus()
				.getWindowToken(), 0);
	}
}