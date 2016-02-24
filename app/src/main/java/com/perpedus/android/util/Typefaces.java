package com.perpedus.android.util;


import android.graphics.Typeface;
import android.util.Log;

import com.perpedus.android.PerpedusApplication;

import java.util.Hashtable;


/**
 * Helper class for loading each font only once.
 */
public class Typefaces {

	private static final String TAG		= Typefaces.class.getSimpleName();

	private static final Hashtable<String, Typeface> CACHE	= new Hashtable<String, Typeface>();


	/**
	 * return Typeface loaded in the asset.
	 * 
	 * @param assetName
	 * @return Typeface loaded in the asset.
	 */
	public static Typeface get(String assetName) {
		/* hold each typeface reference in a hashtable for efficiency */
		synchronized (CACHE) {
			if (!CACHE.containsKey(assetName)) {
				try {
					Typeface t = Typeface.createFromAsset(PerpedusApplication.getInstance().getApplicationContext()
							.getAssets(), "fonts/" + assetName);
					CACHE.put(assetName, t);
				} catch (Exception e) {
					Log.e(TAG, "Could not get typeface '" + assetName + "' because " + e.getMessage());
					return null;
				}
			}
			return CACHE.get(assetName);
		}
	}
}
