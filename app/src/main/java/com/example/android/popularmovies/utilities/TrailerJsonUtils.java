package com.example.android.popularmovies.utilities;

import com.example.android.popularmovies.data.Trailer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class TrailerJsonUtils {

    /**
     * This method parses JSON from a web response and returns an array of Strings
     * describing the viedo.
     *
     * @param trailerJsonStr JSON response from server
     * @return Array of Strings describing trailer data
     * @throws JSONException If JSON data cannot be properly parsed
     */
    public static List<Trailer> getTrailerListFromJson(String trailerJsonStr)
            throws JSONException {

        /* All movies are objects in the array of results */
        final String TRAILER_RESULTS = "results";

        /* Name object */
        final String TRAILER_NAME = "name";

        /* key object*/
        final String TRAILER_KEY = "key";

        /* type object */
        final String TRAILER_TYPE = "type";

        JSONObject trailerJson = new JSONObject(trailerJsonStr);

        JSONArray trailerArray = trailerJson.getJSONArray(TRAILER_RESULTS);

        List<Trailer> trailer = new ArrayList<>();

        //Iterate through all array objects and grep the video data
        for (int i = 0; i < trailerArray.length(); i++) {
            String name;
            String key;
            String type;

            /* Get the JSON object representing one video */
            JSONObject trailerObject = trailerArray.getJSONObject(i);

            /* Get the name */
            name = trailerObject.getString(TRAILER_NAME);

            /* Get the key */
            key = trailerObject.getString(TRAILER_KEY);

            /* Get the video type */
            type = trailerObject.getString(TRAILER_TYPE);

            // Add the movie to the Movies List
            trailer.add(new Trailer(name, key, type));
        }
        return trailer;
    }
}