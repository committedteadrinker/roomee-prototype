package com.roome.fragments;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import uk.co.senab.actionbarpulltorefresh.library.ActionBarPullToRefresh;
import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshLayout;
import uk.co.senab.actionbarpulltorefresh.library.listeners.OnRefreshListener;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.prototype.roomephone.R;
import com.roome.activities.RoomDetailActivity;
import com.roome.activities.RoomListActivity;
import com.roome.adapters.RoomListAdapter;
import com.roome.classes.Room;
import com.roome.constants.Constants;
import com.roome.holders.RoomListItemHolder;
import com.roome.services.RoomListService;

/**
 * Fragment to handle list of rooms contained in a pager
 * 
 * @author Jesica Hadiwiryo
 * 
 */
public class RoomListFragment extends ListFragment implements OnRefreshListener {
	// constants
	public static final String ARG_SECTION_NUMBER = "section_number";

	// variables
	protected int listFilterNumber;
	private PullToRefreshLayout mPullToRefreshLayout;

	/**
	 * Empty constructor
	 */
	public RoomListFragment() {

	}

	@Override
	/**
	 * Handling when view has just been createds
	 */
	public void onViewCreated(View view, Bundle savedInstanceState) {
		ViewGroup viewGroup = (ViewGroup) view;
		mPullToRefreshLayout = new PullToRefreshLayout(viewGroup.getContext());
		ActionBarPullToRefresh
				.from(getActivity())
				.insertLayoutInto(viewGroup)
				.theseChildrenArePullable(android.R.id.list, android.R.id.empty)
				.listener(this).setup(mPullToRefreshLayout);
	}

	@Override
	/**
	 * Handling when the fragment is created
	 */
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		listFilterNumber = getArguments().getInt(ARG_SECTION_NUMBER);
	}

	@Override
	/**
	 * Handling when the configuration is changed
	 * in this case its orientation
	 */
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}

	@Override
	/**
	 * Handling when the fragment view is being created
	 */
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = super.onCreateView(inflater, container,
				savedInstanceState);
		ListView listView = (ListView) rootView.findViewById(android.R.id.list);
		ViewGroup parent = (ViewGroup) listView.getParent();

		int listViewIndex = parent.indexOfChild(listView);
		parent.removeViewAt(listViewIndex);

		PullToRefreshLayout mPullToRefreshLayout = (PullToRefreshLayout) inflater
				.inflate(R.layout.fragment_list_room, container, false);
		parent.addView(mPullToRefreshLayout, listViewIndex,
				listView.getLayoutParams());

		getActivity().registerReceiver(broadcastReceiver,
				new IntentFilter(RoomListService.BROADCAST_FILTER_NAME));

		return rootView;
	}

	// Broadcast receiver to handle with Async services
	private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {

		@Override
		/**
		 * Method to handle when broadcast received
		 */
		public void onReceive(Context context, Intent intent) {
			boolean broadcastStatus = intent.getBooleanExtra(
					Constants.BROADCAST_ERROR_STATUS_TAG,
					Constants.BROADCAST_ERROR);
			if (broadcastStatus) {
				Parcelable[] parcelArray = intent
						.getParcelableArrayExtra(Constants.BROADCAST_ROOM_LIST_TAG);
				Room[] roomArray = new Room[parcelArray.length];
				System.arraycopy(parcelArray, 0, roomArray, 0,
						parcelArray.length);
				List<Room> rooms = Arrays.asList(roomArray);
				rooms = filterList(rooms, listFilterNumber);
				if (rooms.size() != 0) {
					// Use adapter to show room list in list view
					RoomListAdapter adapter = new RoomListAdapter(
							getActivity(), rooms);
					setListAdapter(adapter);
					setListShown(true);
				} else {
					setEmptyText(getString(R.string.no_room_list));
				}
			} else {
				String errorMessage = intent
						.getStringExtra(Constants.BROADCAST_ERROR_MESSAGE_TAG);

				setListShown(true);
				new AlertDialog.Builder(getActivity()).setTitle(R.string.error)
						.setMessage(errorMessage).show();
			}
			mPullToRefreshLayout.setRefreshComplete();
		}

	};

	/**
	 * Method to filter the list item that is going to be displayed
	 * 
	 * @param rooms
	 * @param filterNumber
	 * @return
	 */
	private List<Room> filterList(List<Room> rooms, int filterNumber) {
		switch (filterNumber) {
		case Constants.FILTER_STATUS_ALL:
			return rooms;
		case Constants.FILTER_STATUS_FREE:
			List<Room> freeRooms = new LinkedList<Room>();
			for (Room room : rooms) {
				if (room.getFreebusy() == Constants.ROOM_FREE) {
					freeRooms.add(room);
				}
			}
			return freeRooms;
		}

		return rooms;
	}

	@Override
	/**
	 * Method to handle list item clicking
	 */
	public void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);

		RoomListItemHolder selectedView = (RoomListItemHolder) v.getTag();
		int roomId = selectedView.getId();

		Intent roomDetailIntent = new Intent(getActivity(),
				RoomDetailActivity.class);
		roomDetailIntent.putExtra(getString(R.string.room_id_tag), roomId);

		startActivity(roomDetailIntent);
	}

	/**
	 * Method to refresh current fragment by calling the activity refresh
	 * service
	 */
	public void startRefresh() {
		mPullToRefreshLayout.setRefreshing(true);
		RoomListActivity listActivity = (RoomListActivity) getActivity();
		listActivity.startListService();
	}

	@Override
	/**
	 * Method to handle when the refresh pull bar has been pulled to refresh data
	 */
	public void onRefreshStarted(View view) {
		startRefresh();
	}
}
