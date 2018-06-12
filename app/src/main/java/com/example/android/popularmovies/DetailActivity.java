package com.example.android.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.popularmovies.utilities.NetworkUtils;
import com.example.android.popularmovies.data.Movies;
import com.squareup.picasso.Picasso;

public class DetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_activity);

        Intent intent = getIntent();
        if (intent.hasExtra(Movies.PARCELABLE_KEY)) {
            /* Get the current movies data from the intent*/
            Movies currentMovies = intent.getParcelableExtra(Movies.PARCELABLE_KEY);

            /* Publish all data into their views */
            ImageView posterView = findViewById(R.id.poster_image);
            Picasso.with(this)
                    .load(String.valueOf(NetworkUtils.buildPosterUrl(currentMovies.getPosterPath(), NetworkUtils.W_500)))
                    .into(posterView);

            TextView titleView = findViewById(R.id.title_text);
            titleView.setText(currentMovies.getTitle());

            TextView averageView = findViewById(R.id.average_text);
            averageView.setText(currentMovies.getAverage().toString());

            TextView releaseView = findViewById(R.id.date_text);
            releaseView.setText(currentMovies.getPublishedDate());

            TextView synopsisView = findViewById(R.id.synopsis_text);
            synopsisView.setText(currentMovies.getSynopsis());

        } else {
            //If there are no extras show toast and go back to the main activity
            Toast.makeText(this, getString(R.string.error_empty_extra), Toast.LENGTH_SHORT).show();
            finish();
        }
    }
}
