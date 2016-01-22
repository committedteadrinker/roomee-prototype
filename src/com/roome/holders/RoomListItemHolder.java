package com.roome.holders;

import android.widget.ImageView;
import android.widget.TextView;

/**
 * Class holder to hold room list item views
 * 
 * @author Jesica Hadiwiryo
 * 
 */
public class RoomListItemHolder {

	// attributes
	private int id;
	private ImageView roomStatusImageView;
	private TextView roomNameTextView;
	private TextView roomTimeTextView;

	/**
	 * Method to get room id
	 * 
	 * @return integer of id
	 */
	public int getId() {
		return id;
	}

	/**
	 * Method to set id in the view
	 * 
	 * @param id
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * Method to get image view
	 * 
	 * @return return imageview
	 */
	public ImageView getRoomStatusImageView() {
		return roomStatusImageView;
	}

	/**
	 * Method to set imageview
	 * 
	 * @param roomStatusImageView
	 */
	public void setRoomStatusImageView(ImageView roomStatusImageView) {
		this.roomStatusImageView = roomStatusImageView;
	}

	/**
	 * Method to get room name text view
	 * 
	 * @return textview of room name
	 */
	public TextView getRoomNameTextView() {
		return roomNameTextView;
	}

	/**
	 * Method to set room name textview
	 * 
	 * @param roomNameTextView
	 */
	public void setRoomNameTextView(TextView roomNameTextView) {
		this.roomNameTextView = roomNameTextView;
	}

	/**
	 * Method to get room time text view
	 * 
	 * @return room time text view
	 */
	public TextView getRoomTimeTextView() {
		return roomTimeTextView;
	}

	/**
	 * Method to set room time text view
	 * 
	 * @param roomTimeTextView
	 */
	public void setRoomTimeTextView(TextView roomTimeTextView) {
		this.roomTimeTextView = roomTimeTextView;
	}
}
