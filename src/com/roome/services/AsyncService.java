package com.roome.services;

import com.roome.constants.Constants;

import android.app.Service;
import android.content.Intent;

/**
 * Class to be inherited by services that has similar functions
 * 
 * @author Jesica Hadiwiryo
 * 
 */
public abstract class AsyncService extends Service {

	protected void broadcastErrorMessage(String broadcastFilterName,
			String message) {
		Intent intentExtra = new Intent(broadcastFilterName);
		intentExtra.putExtra(Constants.BROADCAST_ERROR_STATUS_TAG,
				Constants.BROADCAST_ERROR);
		intentExtra.putExtra(Constants.BROADCAST_ERROR_MESSAGE_TAG, message);
		sendBroadcast(intentExtra);
	}
}
