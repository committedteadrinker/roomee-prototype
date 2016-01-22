package com.roome.classes;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Class to check current connection availability
 * 
 * @author Jesica Hadiwiryo
 * 
 */
public class ConnectionDetector {

	// context
	private Context _context;

	/**
	 * Constructor
	 * 
	 * @param context
	 */
	public ConnectionDetector(Context context) {
		this._context = context;
	}

	/**
	 * Check whether current context is connected to the internet or not
	 * 
	 * @return true if connected
	 */
	public boolean isConnectingToInternet() {
		ConnectivityManager connectivity = (ConnectivityManager) _context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivity != null) {
			NetworkInfo[] info = connectivity.getAllNetworkInfo();
			if (info != null)
				for (int i = 0; i < info.length; i++)
					if (info[i].getState() == NetworkInfo.State.CONNECTED) {
						return true;
					}

		}
		return false;
	}
}
