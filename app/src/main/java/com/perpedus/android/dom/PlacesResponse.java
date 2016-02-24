package com.perpedus.android.dom;

import com.google.gson.annotations.SerializedName;

/**
 * Object data that maps the response from Places API
 */
public class PlacesResponse {

    public class Location {

        @SerializedName("lat")
        public double latitude;

        @SerializedName("lng")
        public double longitude;
    }

    public class Geometry {

        @SerializedName("location")
        public Location location;
    }

    public class Result {

        @SerializedName("geometry")
        public Geometry geometry;

        @SerializedName("name")
        public String name;

        @SerializedName("types")
        public String[] types;

        @SerializedName("vicinity")
        public String address;

        @SerializedName("place_id")
        public String placeId;

    }

    @SerializedName("results")
    public Result[] results;

    @SerializedName("status")
    public String status;

}
