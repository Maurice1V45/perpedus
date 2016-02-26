package com.perpedus.android.dom;

import com.google.gson.annotations.SerializedName;

/**
 * Object data that maps the response from Places API
 */
public class PlaceDetailsResponse {


    public class Photo {

        @SerializedName("photo_reference")
        public String reference;

    }

    public class Review {

        @SerializedName("author_name")
        public String author;

        @SerializedName("text")
        public String comment;

        @SerializedName("rating")
        public int rating;
    }

    public class Result {

        @SerializedName("name")
        public String name;

        @SerializedName("formatted_address")
        public String address;

        @SerializedName("international_phone_number")
        public String phone;

        @SerializedName("place_id")
        public String placeId;

        @SerializedName("photos")
        public Photo[] photos;

        @SerializedName("reviews")
        public Review[] reviews;

    }

    @SerializedName("result")
    public Result result;

    @SerializedName("status")
    public String status;

}
