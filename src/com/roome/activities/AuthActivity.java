package com.roome.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import com.prototype.roomephone.R;
import com.roome.constants.Constants;
import com.roome.services.AuthService;

/**
 * Activity to handle authentication call to the server
 * 
 * @author Jesica Hadiwiryo
 * 
 */
public class AuthActivity extends Activity {

	// class members
	private EditText mAPIKeyEditText;
	private ProgressDialog mProgressDialog;
	private Intent mAuthService;

	@Override
	/**
	 * Method to handle view when activity is created
	 */
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_auth);

		mAPIKeyEditText = (EditText) findViewById(R.id.apiKeyEditText);
		mAPIKeyEditText.setText(Constants.API_KEY);
	}

	@Override
	/**
	 * Method to handle when options are created
	 */
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.auth, menu);
		return true;
	}

	@Override
	/**
	 * Method to handle when a menu item got selected
	 */
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_submit_key:
			mProgressDialog = new ProgressDialog(AuthActivity.this);
			mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			mProgressDialog.setMessage(getString(R.string.authenticating_));
			mProgressDialog.show();

			String APIKey = mAPIKeyEditText.getText().toString();

			mAuthService = new Intent(AuthActivity.this, AuthService.class);
			mAuthService.putExtra(Constants.API_KEY_TAG, APIKey);
			startService(mAuthService);
			registerReceiver(broadcastReceiver, new IntentFilter(
					AuthService.BROADCAST_FILTER_NAME));
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	@Override
	/**
	 * Mehod to handle configuration changed
	 * in this case, it is orientation change
	 */
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}

	// Broadcast receiver to handle with Async services
	private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {

		@Override
		/**
		 * Method to handle when broadcast received
		 */
		public void onReceive(Context context, Intent intent) {
			mProgressDialog.dismiss();
			
			// check async transtaction status
			boolean broadcastStatus = intent.getBooleanExtra(
					Constants.BROADCAST_ERROR_STATUS_TAG,
					Constants.BROADCAST_ERROR);
			if (broadcastStatus) {
				Intent roomList = new Intent(AuthActivity.this,
						RoomListActivity.class);
				startActivity(roomList);
			} else {
				String errorMessage = intent
						.getStringExtra(Constants.BROADCAST_ERROR_MESSAGE_TAG);

				new AlertDialog.Builder(AuthActivity.this)
						.setTitle(R.string.error).setMessage(errorMessage)
						.show();
			}
			stopService(mAuthService);
			unregisterReceiver(broadcastReceiver);
		}

	};

}
