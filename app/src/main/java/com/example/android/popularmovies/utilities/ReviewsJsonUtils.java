package com.example.android.popularmovies.utilities;

import com.example.android.popularmovies.data.Movies;
import com.example.android.popularmovies.data.Reviews;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class ReviewsJsonUtils {

    /**
     * This method parses JSON from a web response and returns an array of Strings
     * describing the review.
     *
     * @param reviewJsonStr JSON response from server
     * @return Array of Strings describing weather data
     * @throws JSONException If JSON data cannot be properly parsed
     */
    public static List<Reviews> getReviewListFromJson(String reviewJsonStr)
            throws JSONException {

        /* All movies are objects in the array of results */
        final String REVIEW_RESULTS = "results";

        /* Author object */
        final String REVIEW_AUTHOR = "author";

        /* Review content */
        final String REVIEW_COTENT = "content";

        JSONObject reviewJson = new JSONObject(reviewJsonStr);

        JSONArray reviewArray = reviewJson.getJSONArray(REVIEW_RESULTS);

        List<Reviews> reviews = new ArrayList<>();

        //Iterate through all array objects and grep the movie data
        for (int i = 0; i < reviewArray.length(); i++) {
            String author;
            String content;

            /* Get the JSON object representing one movie */
            JSONObject reviewObject = reviewArray.getJSONObject(i);

            /* Get the author */
            author = reviewObject.getString(REVIEW_AUTHOR);

            /* Get the content */
            content = reviewObject.getString(REVIEW_COTENT);

            // Add the movie to the Movies List
            reviews.add(new Reviews(author, content));
        }
        return reviews;
    }
}