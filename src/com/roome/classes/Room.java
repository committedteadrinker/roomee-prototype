package com.roome.classes;

import java.sql.Timestamp;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Method to contain Room model
 * 
 * @author Jesica Hadiwiryo
 * 
 */
public class Room implements Parcelable {

	// attributes
	private int id;
	private String name;
	private int freeBusy;
	private Timestamp timeStamp;

	/**
	 * parameter class contructor
	 * 
	 * @param summary
	 * @param creator
	 * @param startTime
	 * @param endTime
	 */
	public Room(int id, String name, int freeBusy, Timestamp time) {
		super();
		this.id = id;
		this.name = name;
		this.freeBusy = freeBusy;
		this.timeStamp = time;
	}

	/**
	 * super class constructor
	 */
	public Room() {
		super();
	}

	/**
	 * parcel constructor
	 * 
	 * @param parcel
	 */
	public Room(Parcel parcel) {
		id = parcel.readInt();
		name = parcel.readString();
		freeBusy = parcel.readInt();
		timeStamp = (Timestamp) parcel.readValue(Timestamp.class
				.getClassLoader());
	}

	/**
	 * Method to get id
	 * 
	 * @return integer of id
	 */
	public int getId() {
		return id;
	}

	/**
	 * Method to set id
	 * 
	 * @param id
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * Method to get name
	 * 
	 * @return string of name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Method to set name
	 * 
	 * @param name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Method to get free busy status
	 * 
	 * @return boolean of room status
	 */
	public int getFreebusy() {
		return freeBusy;
	}

	/**
	 * Method to set freebusy status
	 * 
	 * @param freeBusy
	 */
	public void setFreebusy(int freeBusy) {
		this.freeBusy = freeBusy;
	}

	/**
	 * Method to get time
	 * 
	 * @return timestamp of time
	 */
	public Timestamp getTime() {
		return timeStamp;
	}

	/**
	 * Method to set time
	 * 
	 * @param time
	 */
	public void setTime(Timestamp time) {
		this.timeStamp = time;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(id);
		dest.writeString(name);
		dest.writeInt(freeBusy);
		dest.writeValue(timeStamp);
	}

	public static Creator<Room> CREATOR = new Creator<Room>() {

		@Override
		public Room createFromParcel(Parcel parcel) {
			return new Room(parcel);
		}

		@Override
		public Room[] newArray(int size) {
			return new Room[size];
		}
	};
}
