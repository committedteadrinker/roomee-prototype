package com.roome.adapters;

import java.sql.Timestamp;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.prototype.roomephone.R;
import com.roome.classes.Room;
import com.roome.classes.TimeStringManipulator;
import com.roome.constants.Constants;
import com.roome.holders.RoomListItemHolder;

/**
 * Custom adapter class to display a list of rooms
 * 
 * @author Jesica Hadiwiryo
 * 
 */
public class RoomListAdapter extends ArrayAdapter<Room> {
	// class attributes
	private final List<Room> rooms;
	private final Activity context;

	// constructor of class: initialize all class attributes
	public RoomListAdapter(Activity context, List<Room> list) {
		super(context, R.layout.room_list_row, list);
		this.context = context;
		this.rooms = list;
	}

	@Override
	/**
	 * Method called when inflating list view
	 */
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = null;
		// check the oldview passed by listview to recycle
		if (convertView == null) {
			// inflate new view
			LayoutInflater inflator = (LayoutInflater) this.context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = inflator.inflate(R.layout.room_list_row, null);

			// initialize member of views to its view and save it in viewholder
			final RoomListItemHolder viewHolder = new RoomListItemHolder();
			setView(view, viewHolder);
		} else {
			// update the old view passed
			view = convertView;
		}

		// get current view
		RoomListItemHolder holder = (RoomListItemHolder) view.getTag();

		int id = rooms.get(position).getId();
		int freeBusy = rooms.get(position).getFreebusy();
		String roomName = rooms.get(position).getName();
		Timestamp time = rooms.get(position).getTime();

		holder.setId(id);
		holder.getRoomStatusImageView().setImageResource(
				getImageResource(freeBusy));
		holder.getRoomNameTextView().setText(roomName);
		holder.getRoomTimeTextView().setText(
				Html.fromHtml(getTimeString(time, freeBusy)));

		// return the view to be re-used
		return view;
	}

	/**
	 * Set view on to the viewholder that contains member of class
	 * 
	 * @param view
	 *            that contains list row
	 * @param viewHolder
	 *            container to contain the view members
	 */
	private void setView(View view, RoomListItemHolder viewHolder) {

		ImageView roomStatusImageView = (ImageView) view
				.findViewById(R.id.roomStatusListIconImageView);
		TextView roomNameTextView = (TextView) view
				.findViewById(R.id.roomStatusListNameTextView);
		TextView roomTimeTextView = (TextView) view
				.findViewById(R.id.roomStatusListTimeTextView);

		viewHolder.setRoomStatusImageView(roomStatusImageView);
		viewHolder.setRoomNameTextView(roomNameTextView);
		viewHolder.setRoomTimeTextView(roomTimeTextView);
		view.setTag(viewHolder);
	}

	/**
	 * Get time in string together with its font color in HTML format
	 * 
	 * @param time
	 * @param roomStatus
	 * @return HTML format of time in strings
	 */
	private String getTimeString(Timestamp time, int roomStatus) {
		String timeDetailsSentence = "";
		String timeDetail = TimeStringManipulator.getHourMinuteTimeString(time);
		switch (roomStatus) {
		case Constants.ROOM_FREE:
			return "<font color=#c0c0c0>" + Constants.FREE_TEXT
					+ "</font><font color=#35C286>" + timeDetail + "</font>";
		case Constants.ROOM_OCCUPIED:
			return "<font color=#c0c0c0>" + Constants.BUSY_TEXT
					+ "</font><font color=#EB4344>" + timeDetail + "</font>";
		}
		return timeDetailsSentence;
	}

	/**
	 * get icon based on room status
	 * 
	 * @param roomStatus
	 * @return image resource id
	 */
	private int getImageResource(int roomStatus) {
		switch (roomStatus) {
		case Constants.ROOM_FREE:
			return R.drawable.ok_icon;
		case Constants.ROOM_OCCUPIED:
			return R.drawable.not_ok_icon;
		}
		return -1;
	}
}
