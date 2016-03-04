package com.perpedus.android.dom;

import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Object data that maps the response from Places API
 */
public class PlacesResponse implements Serializable {

    public class Location implements Serializable {

        @SerializedName("lat")
        public double latitude;

        @SerializedName("lng")
        public double longitude;
    }

    public class Geometry implements Serializable {

        @SerializedName("location")
        public Location location;
    }

    public class Result implements Serializable {

        @SerializedName("geometry")
        public Geometry geometry;

        @SerializedName("name")
        public String name;

        @SerializedName("types")
        public String[] types;

        @SerializedName("vicinity")
        public String address;

        @SerializedName("reference")
        public String photoReference;

        @SerializedName("place_id")
        public String placeId;

    }

    @SerializedName("results")
    public Result[] results;

    @SerializedName("status")
    public String status;

}
