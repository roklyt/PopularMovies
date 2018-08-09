package com.example.android.popularmovies.utilities;

import com.example.android.popularmovies.data.Movies;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class MovieJsonUtils {

    /* Status code */
    public final static String MOVIE_STATUS_CODE = "status_code";

    /* Status message */
    public final static String MOVIE_STATUS_MESSGAE = "status_message";

    /**
     * This method parses JSON from a web response and returns an array of Strings
     * describing the movie.
     *
     * @param movieJsonStr JSON response from server
     * @return Array of Strings describing weather data
     * @throws JSONException If JSON data cannot be properly parsed
     */
    public static List<Movies> getMovieListFromJson(String movieJsonStr)
            throws JSONException {

        /* All movies are objects in the array of results */
        final String MOVIE_RESULTS = "results";

        /* Title object */
        final String MOVIE_TITLE = "title";

        /* Movie Id */
        final String MOVIE_ID = "id";

        /* Release date object */
        final String MOVIE_RELEASE_DATE = "release_date";

        /* Path to the movie poster */
        final String MOVIE_POSTER_PATH = "poster_path";

        /* Vote average object */
        final String MOVIE_VOTE_AVERAGE = "vote_average";

        /* Plot synopsis*/
        final String MOVIE_SYNOPSIS = "overview";


        JSONObject movieJson = new JSONObject(movieJsonStr);

        JSONArray movieArray = movieJson.getJSONArray(MOVIE_RESULTS);

        List<Movies> movies = new ArrayList<>();

        //Iterate through all array objects and grep the movie data
        for (int i = 0; i < movieArray.length(); i++) {
            String title;
            String id;
            String releaseDate;
            String posterPath;
            Double average;
            String plotSynopsis;

            /* Get the JSON object representing one movie */
            JSONObject movieObject = movieArray.getJSONObject(i);

            /* Get the title */
            title = movieObject.getString(MOVIE_TITLE);

            /* Get the id */
            id = movieObject.getString(MOVIE_ID);

            /* Get release date*/
            releaseDate = movieObject.getString(MOVIE_RELEASE_DATE);

            /* Get poster path */
            posterPath = movieObject.getString(MOVIE_POSTER_PATH);

            /* Get average */
            average = movieObject.getDouble(MOVIE_VOTE_AVERAGE);

            /* get plot synopsis */
            plotSynopsis = movieObject.getString(MOVIE_SYNOPSIS);

            // Add the movie to the Movies List
            movies.add(new Movies(title, id, posterPath, releaseDate, average, plotSynopsis));
        }
        return movies;
    }
}