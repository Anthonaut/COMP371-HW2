package com.example.hw_2;

import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class BeerAdapter extends RecyclerView.Adapter<BeerAdapter.ViewHolder> {
    // create the basic adapter extending from RecyclerView.Adapter
        // create an inner/helper class that specify the custom ViewHolder,
        // which gives us access to our views

    // list of the beers to be populated, done with an instance variable
    private List<Beer> beers;

    // to notify a list of changes, create another list to keep track of the changes
    private List<Beer> selectedBeers; // Everytime the favorite icon is clicked
    // I want to add the beer item that is with the icon to the list
    // Then I must notify the adapter of this change (image click) with this list
    private View.OnClickListener imageListener;

    // pass this list into the constructor of the adapter
    public BeerAdapter(List<Beer> beers, View.OnClickListener imageListener) { // I don't know if the imageListener parameter is needed, but I assume it does
        this.beers = beers;
        this.selectedBeers = new ArrayList<>();
        this.imageListener = imageListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // used to inflate a layout from xml and return the ViewHolder
        // standard template code to inflate layout
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        // inflate the custom layout
        View beerView = inflater.inflate(R.layout.item_beer, parent, false);
        // return the ViewHolder
        ViewHolder viewHolder = new ViewHolder(beerView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // populate date into the item through holder

        // grab the beer data (i.e beer List)
        Beer beer = beers.get(position);

        // set the view based on the data nad the view names
        holder.textView_beerName.setText(beer.getBeerName());
        holder.textView_beerDescription.setText(beer.getBeerDescription());
        // Load in the beer image using Picasso
        Picasso.get().load(beer.getBeerImageUrl()).into(holder.imageView_beerImage); // Picasso method is used because it handles downloading an image url for us to set in the ImageView
        // Set the favIcon to an empty star icon
        if (selectedBeers.contains(beer)) {
            holder.imageView_favoriteIcon.setImageResource(R.drawable.mariostar); // setImageResource is used because I decided to use images in assets folder, which were imported to drawables
        }
        else {
            holder.imageView_favoriteIcon.setImageResource(R.drawable.emptystar);
        }


    }

    @Override
    public int getItemCount() {
        // return the total number of items in the list
        return beers.size();
    }


    // provide a direct reference to each of the views within the data item
    // used to cache the views within the item layout for fast access

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{ // We go with the adapter method of changing the favorite icon, since it's not part of the beer data model
        // all the views that should be set as the row is rendered
        // beer name, beer description, beer image
        TextView textView_beerName;
        TextView textView_beerDescription;
        // Beer image view
        ImageView imageView_beerImage;
        // Favorite icon image
        ImageView imageView_favoriteIcon;

        // create constructor to set these views
        public ViewHolder(View itemView) {
            // itemView -> represents the entire view of each row
            super(itemView);
            // look up each views from the custom layout
            textView_beerName = itemView.findViewById(R.id.textView_beerName);
            textView_beerDescription = itemView.findViewById(R.id.textView_beerDesciption);
            imageView_beerImage = itemView.findViewById(R.id.imageView_beerImage);
            imageView_beerImage.setOnClickListener(this);
            imageView_favoriteIcon = itemView.findViewById(R.id.imageView_favoriteIcon); // Set the favoriteIcon to the favorite icon ImageView I set
            imageView_favoriteIcon.setOnClickListener(this); // Because the favorite icon must change when clicked, this be set onClickListener
        }

        @Override
        public void onClick(View v) {
            if (v.getId() == imageView_beerImage.getId()) { // This logical statement is what separates if the beer image or favorite icon is clicked - compare by their ID
                imageListener.onClick(v); // From what I can gather, when the beer image is clicked we are setting it as something to be clicked on, or that it allows something to happen in the activity when clicked i.e NEEDED
            }
            else {
                // Grab the beer item at the position, add to the selectedBeers ArrayList
                int selected = getAdapterPosition();
                Beer selectedB = beers.get(selected);
                // if list already contains beer item, I must remove it from selectedBeers list
                if (selectedBeers.contains(selectedB)) {
                    selectedBeers.remove(selectedB);
                } else {
                    selectedBeers.add(selectedB);
                }
                notifyDataSetChanged(); // because we have a list tracking changes we use notify adapter with "DataSet" and NOT "Item"
            }
        }



    }


}
