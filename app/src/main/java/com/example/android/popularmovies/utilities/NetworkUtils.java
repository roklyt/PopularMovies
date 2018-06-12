package com.example.android.popularmovies.utilities;

import android.net.Uri;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

/**
 * These utilities will be used to communicate with the weather servers.
 */
public final class NetworkUtils {

    private static final String TAG = NetworkUtils.class.getSimpleName();

    private static final String BASE_MOVIE_URL =
            "http://api.themoviedb.org/3/movie/";

    public static final String BASE_POSTER_URL =
            "http://image.tmdb.org/t/p/";

    public static final String W_185 = "w185";

    public static final String W_342 = "w342";

    public static final String W_500 = "w500";

    final static String API_KEY = "api_key";

    /**
     * Builds the URL used to talk to the movie database server.
     *
     * @param apiKey The api key to get access to the movie database.
     * @return The URL to use to query the weather server.
     */
    public static URL buildUrl(String apiKey, String searchFor) {
        Uri builtUri = Uri.parse(BASE_MOVIE_URL).buildUpon()
                .appendPath(searchFor)
                .appendQueryParameter(API_KEY, apiKey)
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        Log.v(TAG, "Built URI " + url);

        return url;
    }

    public static URL buildPosterUrl(String posterPath, String posterSize){

        Uri builtUri = Uri.parse(BASE_POSTER_URL).buildUpon()
                .appendPath(posterSize)
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString() + posterPath);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        Log.v(TAG, "Built URI " + url);

        return url;
    }

    /**
     * This method returns the entire result from the HTTP response.
     *
     * @param url The URL to fetch the HTTP response from.
     * @return The contents of the HTTP response.
     * @throws IOException Related to network and stream reading
     */
    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in;
            if(urlConnection.getResponseCode() == 200){
               in = urlConnection.getInputStream();
            }else{
                in = urlConnection.getErrorStream();
            }

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }
}