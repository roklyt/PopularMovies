package com.example.android.popularmovies.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.popularmovies.R;
import com.example.android.popularmovies.data.Reviews;

import java.util.List;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewAdapterViewHolder> {

    /* List for all reviews*/
    private List<Reviews> ReviewsList;

    /* Interface for the on click handler */
    public interface ReviewAdapterOnClickHandler {
        void onClick(Reviews currentReview);
    }

    public ReviewAdapter(List<Reviews> reviewsList) {
        ReviewsList = reviewsList;
    }

    public class ReviewAdapterViewHolder extends RecyclerView.ViewHolder{
        TextView AuthorTextView;
        TextView ContentTextView;

        public ReviewAdapterViewHolder(View view) {
            super(view);
            AuthorTextView = view.findViewById(R.id.tv_author);
            ContentTextView = view.findViewById(R.id.tv_content);
        }
    }

    @Override
    public ReviewAdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.review_list_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, viewGroup, shouldAttachToParentImmediately);
        return new ReviewAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ReviewAdapterViewHolder forecastAdapterViewHolder, int position) {
        /*Get the current movie to find the correct poster path */
        Reviews reviews = ReviewsList.get(position);

        forecastAdapterViewHolder.AuthorTextView.setText(reviews.getAuthor());
        forecastAdapterViewHolder.ContentTextView.setText(reviews.getContent());
    }

    @Override
    public int getItemCount() {
        return ReviewsList.size();
    }

    /* Set the new Movies list to the adapter */
    public void setReviewData(List<Reviews> reviewsData) {
        ReviewsList = reviewsData;
        notifyDataSetChanged();
    }
}