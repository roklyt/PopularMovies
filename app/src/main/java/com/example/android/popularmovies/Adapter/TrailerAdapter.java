package com.example.android.popularmovies.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.popularmovies.R;
import com.example.android.popularmovies.data.Trailer;

import java.util.List;

public class TrailerAdapter extends RecyclerView.Adapter<TrailerAdapter.TrailerAdapterViewHolder> {

    /* List for all Trailer*/
    private List<Trailer> TrailerList;

    private final TrailerAdapterOnClickHandler ClickHandler;

    /* Interface for the on click handler */
    public interface TrailerAdapterOnClickHandler {
        void onClick(Trailer currentTrailer);
    }

    public TrailerAdapter(TrailerAdapterOnClickHandler  clickHandler, List<Trailer> trailerList) {
        ClickHandler = clickHandler;
        TrailerList = trailerList;
    }

    public class TrailerAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView NameTextView;
        TextView TypeTextView;

        public TrailerAdapterViewHolder(View view) {
            super(view);
            NameTextView = view.findViewById(R.id.tv_video_name);
            TypeTextView = view.findViewById(R.id.tv_video_type);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Trailer currentTrailer = TrailerList.get(getAdapterPosition());
            ClickHandler.onClick(currentTrailer);
        }
    }

    @Override
    public TrailerAdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.trailer_list_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, viewGroup, shouldAttachToParentImmediately);
        return new TrailerAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TrailerAdapterViewHolder forecastAdapterViewHolder, int position) {
        /*Get the current movie to find the correct poster path */
        Trailer trailer = TrailerList.get(position);

        forecastAdapterViewHolder.NameTextView.setText(trailer.getName());
        forecastAdapterViewHolder.TypeTextView.setText(trailer.getType());
    }

    @Override
    public int getItemCount() {
        return TrailerList.size();
    }

    /* Set the new Movies list to the adapter */
    public void setTrailerData(List<Trailer> trailerData) {
        TrailerList = trailerData;
        notifyDataSetChanged();
    }
}