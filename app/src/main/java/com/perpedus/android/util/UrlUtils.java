package com.perpedus.android.util;

import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

/**
 * Util class that gets data and handles urls
 */
public class UrlUtils {

    /**
     * Retrieves a Json string from the given url
     *
     * @param url
     * @return
     */
    public static String getJsonFromUrl(final String url) {

        InputStream stream = null;
        String jsonString = "";

        try {
            // defaultHttpClient
            final DefaultHttpClient httpClient = new DefaultHttpClient();
            final HttpGet httpGet = new HttpGet(url);

            final HttpResponse httpResponse = httpClient.execute(httpGet);
            final HttpEntity httpEntity = httpResponse.getEntity();
            stream = httpEntity.getContent();

            final BufferedReader reader = new BufferedReader(new InputStreamReader(stream, "utf-8"), 8);
            final StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            stream.close();
            jsonString = sb.toString();

        } catch (final UnsupportedEncodingException e) {
            Log.e("ProductRetriever", "Error retrieving data from URL: " + e.toString());
        } catch (final ClientProtocolException e) {
            Log.e("ProductRetriever", "Error retrieving data from URL: " + e.toString());
        } catch (final IOException e) {
            Log.e("ProductRetriever", "Error retrieving data from URL: " + e.toString());
        }

        return jsonString;

    }

    /**
     * Builds the get places url based on some parameters
     *
     * @param keyword
     * @param latitude
     * @param longitude
     * @param radius
     * @param language
     * @param type
     * @return
     */
    public static String buildPlacesLink(String keyword, float latitude, float longitude, String radius, String language, String type) {

        StringBuilder builder = new StringBuilder();

        // add latitude and longitude
        builder.append("?location=" + latitude + "," + longitude);

        // add radius
        builder.append("&radius=" + radius);

        // add language
        builder.append("&language=" + language);

        // add keyword
        if (keyword != null && !keyword.isEmpty()) {
            builder.append("&keyword=");
            try {
                builder.append(URLEncoder.encode(keyword, "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                builder.append(keyword);
            }
        }

        // add type
        if (type != null) {
            builder.append("&type=" + type);
        }

        // add API key
        builder.append("&key=" + Constants.PLACES_KEY);

        Log.w("link", Constants.PLACES_SEARCH_URL + builder.toString());

        return Constants.PLACES_SEARCH_URL + builder.toString();
    }

    public static String buildPlaceDetailsLink(String placeId, String language) {

        StringBuilder builder = new StringBuilder();

        // add place id
        builder.append("?placeid=" + placeId);

        // add language
        builder.append("&language=" + language);

        // add API key
        builder.append("&key=" + Constants.PLACES_KEY);

        Log.w("link", Constants.PLACE_DETAILS_SEARCH_URL + builder.toString());

        return Constants.PLACE_DETAILS_SEARCH_URL + builder.toString();
    }

    public static String buildPlacePhotoLink(String reference, int screenWidth) {

        StringBuilder builder = new StringBuilder();

        // add max width
        builder.append("?maxwidth=" + screenWidth);

        // add language
        builder.append("&photoreference=" + reference);

        // add API key
        builder.append("&key=" + Constants.PLACES_KEY);

        Log.w("link", Constants.PLACE_PHOTO_URL + builder.toString());

        return Constants.PLACE_PHOTO_URL + builder.toString();
    }

}
