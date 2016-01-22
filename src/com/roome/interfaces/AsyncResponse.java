package com.roome.interfaces;

import org.json.JSONObject;

/**
 * Interface to handle and pass async result to context
 * @author Jesica Hadiwiryo
 *
 */
public interface AsyncResponse {
	public void onAsyncFinished(JSONObject jObject);
	
}
