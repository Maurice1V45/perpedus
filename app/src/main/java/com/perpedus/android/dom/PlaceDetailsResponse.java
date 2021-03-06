package com.perpedus.android.dom;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Object data that maps the response from Places API
 */
public class PlaceDetailsResponse implements Serializable{


    public class Photo implements Serializable{

        @SerializedName("photo_reference")
        public String reference;

    }

    public class Review implements Serializable{

        @SerializedName("author_name")
        public String author;

        @SerializedName("text")
        public String comment;

        @SerializedName("rating")
        public int rating;
    }

    public class Result implements Serializable{

        @SerializedName("geometry")
        public Geometry geometry;

        @SerializedName("name")
        public String name;

        @SerializedName("formatted_address")
        public String address;

        @SerializedName("formatted_phone_number")
        public String formattedPhone;

        @SerializedName("international_phone_number")
        public String phone;

        @SerializedName("place_id")
        public String placeId;

        @SerializedName("photos")
        public Photo[] photos;

        @SerializedName("reviews")
        public Review[] reviews;

    }

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

    @SerializedName("result")
    public Result result;

    @SerializedName("status")
    public String status;

}
