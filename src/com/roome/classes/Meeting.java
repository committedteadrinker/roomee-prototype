package com.roome.classes;

import java.sql.Timestamp;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Class to contain Meeting model
 * 
 * @author Jesica Hadiwiryo
 * 
 */
public class Meeting implements Parcelable {

	// attributes
	private String summary;
	private String creator;
	private Timestamp startTime;
	private Timestamp endTime;

	/**
	 * super class constructor
	 */
	public Meeting() {
		super();
	}

	/**
	 * parameter class contructor
	 * 
	 * @param summary
	 * @param creator
	 * @param startTime
	 * @param endTime
	 */
	public Meeting(String summary, String creator, Timestamp startTime,
			Timestamp endTime) {
		super();
		this.summary = summary;
		this.creator = creator;
		this.startTime = startTime;
		this.endTime = endTime;
	}

	/**
	 * parcel constructor
	 * 
	 * @param parcel
	 */
	public Meeting(Parcel parcel) {
		summary = parcel.readString();
		creator = parcel.readString();
		startTime = (Timestamp) parcel.readValue(Timestamp.class
				.getClassLoader());
		endTime = (Timestamp) parcel
				.readValue(Timestamp.class.getClassLoader());
	}

	/**
	 * Method to get summary
	 * 
	 * @return string of summary
	 */
	public String getSummary() {
		return summary;
	}

	/**
	 * Method to set summary
	 * 
	 * @param summary
	 */
	public void setSummary(String summary) {
		this.summary = summary;
	}

	/**
	 * Method to get creator
	 * 
	 * @return string of creator
	 */
	public String getCreator() {
		return creator;
	}

	/**
	 * Method to set creator
	 * 
	 * @param creator
	 */
	public void setCreator(String creator) {
		this.creator = creator;
	}

	/**
	 * Method to get start time
	 * 
	 * @return timestamp of start time
	 */
	public Timestamp getStartTime() {
		return startTime;
	}

	/**
	 * Method to set start time
	 * 
	 * @param startTime
	 */
	public void setStartTime(Timestamp startTime) {
		this.startTime = startTime;
	}

	/**
	 * Method to get end time
	 * 
	 * @return timestamp of end time
	 */
	public Timestamp getEndTime() {
		return endTime;
	}

	/**
	 * Method to set end time
	 * 
	 * @param endTime
	 */
	public void setEndTime(Timestamp endTime) {
		this.endTime = endTime;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(summary);
		dest.writeString(creator);
		dest.writeValue(startTime);
		dest.writeValue(endTime);
	}

	public static Creator<Meeting> CREATOR = new Creator<Meeting>() {

		@Override
		public Meeting createFromParcel(Parcel parcel) {
			return new Meeting(parcel);
		}

		@Override
		public Meeting[] newArray(int size) {
			return new Meeting[size];
		}
	};
}
