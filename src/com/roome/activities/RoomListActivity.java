package com.roome.activities;

import java.util.Locale;

import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.ListFragment;
import android.support.v4.view.ViewPager;
import android.util.SparseArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;

import com.prototype.roomephone.R;
import com.roome.constants.Constants;
import com.roome.fragments.RoomListFragment;
import com.roome.services.RoomListService;

/**
 * Class handling 2 view pagers and its list fragments
 * 
 * @author Jesica Hadiwiryo
 * 
 */
public class RoomListActivity extends FragmentActivity implements
		ActionBar.TabListener {

	// constants
	private final static int NUMBER_OF_TABS = 2;

	// class members
	private ProgressDialog mProgressDialog;
	private Intent mRoomListService;

	// pager adapter
	SectionsPagerAdapter mSectionsPagerAdapter;

	// pager view
	ViewPager mViewPager;

	@Override
	/**
	 * Method to handle view when activity is created
	 */
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_list_room);

		// Set up the action bar.
		final ActionBar actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		// Create the adapter that will return a fragment for each of the three
		// primary sections of the app.
		mSectionsPagerAdapter = new SectionsPagerAdapter(
				getSupportFragmentManager());

		// Set up the ViewPager with the sections adapter.
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mSectionsPagerAdapter);
		mViewPager
				.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
					@Override
					public void onPageSelected(int position) {
						actionBar.setSelectedNavigationItem(position);
					}
				});

		// For each of the sections in the app, add a tab to the action bar.
		for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
			actionBar.addTab(actionBar.newTab()
					.setText(mSectionsPagerAdapter.getPageTitle(i))
					.setTabListener(this));
		}

		mProgressDialog = new ProgressDialog(RoomListActivity.this);
		mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		mProgressDialog.setMessage(getString(R.string.getting_room_list));
		mProgressDialog.show();

		mRoomListService = new Intent(RoomListActivity.this,
				RoomListService.class);
		startRefreshService();

		registerReceiver(broadcastReceiver, new IntentFilter(
				RoomListService.BROADCAST_FILTER_NAME));
	}

	@Override
	/**
	 * Method to handle when the activity is on pause
	 */
	protected void onPause() {
		if (mRoomListService != null) {
			stopService(mRoomListService);
		}
		super.onPause();
	}

	@Override
	/**
	 * Method to handle when the activity is resumed
	 */
	protected void onResume() {
		if (mRoomListService != null) {
			startRefreshService();
		}
		super.onResume();
	}

	// broadcast receiver
	private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {

		@Override
		/**
		 * Method to handle when broadcast received
		 */
		public void onReceive(Context arg0, Intent arg1) {
			mProgressDialog.dismiss();
			unregisterReceiver(broadcastReceiver);
		}

	};

	/**
	 * Public method to enable list fragments acces to start service through the
	 * activity
	 */
	public void startListService() {
		startService(mRoomListService);
	}

	/**
	 * Method to handle refresh list relating to PullToRefresh action bar
	 */
	private void startRefreshService() {
		RoomListFragment roomListFragment = (RoomListFragment) mSectionsPagerAdapter
				.getRegisteredFragment(mViewPager.getCurrentItem());
		if (roomListFragment != null) {
			roomListFragment.startRefresh();
		} else {
			startListService();
		}
	}

	@Override
	/**
	 * Method to handle when options are created
	 */
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.list_room, menu);
		return true;
	}

	@Override
	/**
	 * Method to handle when a menu item got selected
	 */
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_refresh:
			startRefreshService();
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	@Override
	/**
	 * Method to handle when a tab is selected
	 */
	public void onTabSelected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
		// When the given tab is selected, switch to the corresponding page in
		// the ViewPager.
		mViewPager.setCurrentItem(tab.getPosition());
	}

	@Override
	/**
	 * Method to handle when a tab is not selected
	 */
	public void onTabUnselected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
	}

	@Override
	/**
	 * Method to handle when a method is re-selected
	 */
	public void onTabReselected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
	}

	/**
	 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
	 * one of the sections/tabs/pages.
	 */
	public class SectionsPagerAdapter extends FragmentStatePagerAdapter {

		// registered fragments
		SparseArray<Fragment> registeredFragments = new SparseArray<Fragment>();

		public SectionsPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		/**
		 * Get the fragment when selected based on tab position
		 */
		public Fragment getItem(int position) {
			ListFragment fragment = new RoomListFragment();

			Bundle args = new Bundle();
			args.putInt(RoomListFragment.ARG_SECTION_NUMBER, position);
			fragment.setArguments(args);
			return fragment;
		}

		@Override
		/**
		 * get total number of tabs
		 */
		public int getCount() {
			// Show 2 total pages.
			return NUMBER_OF_TABS;
		}

		@Override
		/**
		 * Put newly registered fragments based on position of tab
		 */
		public Object instantiateItem(ViewGroup container, int position) {
			Fragment fragment = (Fragment) super.instantiateItem(container,
					position);
			registeredFragments.put(position, fragment);
			return fragment;
		}

		@Override
		/**
		 * Destroy created item from registered fragments
		 */
		public void destroyItem(ViewGroup container, int position, Object object) {
			registeredFragments.remove(position);
			super.destroyItem(container, position, object);
		}

		/**
		 * Get registered fragment
		 * 
		 * @param position
		 *            tab position
		 * @return Fragment
		 */
		public Fragment getRegisteredFragment(int position) {
			return registeredFragments.get(position);
		}

		@Override
		/**
		 * get Page title based on tab position
		 */
		public CharSequence getPageTitle(int position) {
			Locale l = Locale.getDefault();
			switch (position) {
			case Constants.FILTER_STATUS_ALL:
				return getString(R.string.title_list_all_room).toUpperCase(l);
			case Constants.FILTER_STATUS_FREE:
				return getString(R.string.title_list_free_room).toUpperCase(l);
			}
			return null;
		}
	}

}
