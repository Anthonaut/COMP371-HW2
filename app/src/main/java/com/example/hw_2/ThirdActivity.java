package com.example.hw_2;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Adapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ThirdActivity extends AppCompatActivity {

    private ArrayList<Beer> beers;
    private RecyclerView recyclerView;
    private TextView resultsTitle;
    private int beerJSONArrayLength;
    private View.OnClickListener imageListener;
    private JSONArray entireBeersArray; // Will be helpful for getting the JSON object of beer clicked for the info of the beer profile

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_third);

        //look up the recycler view in the main activity xml
        recyclerView = findViewById(R.id.recyclerView_beers);
        // Look up the results title
        resultsTitle = findViewById(R.id.textView_resultsTitle);
        // create a new ArrayList for the beers
        beers = new ArrayList<>();
        // retrieve the intent from Second Activity with JSONArray of beers
        Intent intent = getIntent();
        // convert string in intent into the JSON Array
        try {
            JSONArray beersArray = new JSONArray(intent.getStringExtra("Beers"));
            for (int i = 0; i < beersArray.length(); i++) {
                JSONObject beerObject = beersArray.getJSONObject(i); // The elements in array are beer JSONObjects
                Beer beer = new Beer(beerObject.getString("name"), beerObject.getString("description"), beerObject.getString("image_url")); // For each beerObject, get the name, description and image url
                //Log.d("Beer Image url:", beerObject.getString("image_url"));
                // add the beer to the arrayList
                beers.add(beer);
            }
            entireBeersArray = beersArray;
            beerJSONArrayLength = beersArray.length();
            resultsTitle.setText("We found " + Integer.toString(beerJSONArrayLength) + " results");
            launchBeerProfile();
            // create the beer adapter to pass in the data
            BeerAdapter adapter = new BeerAdapter(beers, imageListener);
            // attach the adapter to recyclerView to populate
            recyclerView.setAdapter(adapter);
            // Layout manager
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            // Must set layout manager for recyclerView to show
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void launchBeerProfile() {
        imageListener = new View.OnClickListener() { // This is needed to launch the Beer profile when beer image is clicked
            @Override
            public void onClick(View v) {
                int position = getPosition(v);
                try {
                    JSONObject selectedBeer = entireBeersArray.getJSONObject(position); // Get the JSONObject of the associated beer
                    Log.d("Beer position", selectedBeer.toString());
                    Intent intent = new Intent(ThirdActivity.this, FourthActivity.class);
                    intent.putExtra("selected beer", selectedBeer.toString()); // Send intent (JSONObject String) to Fourth Activity
                    startActivity(intent);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        };
    }

    // I am trying to receive the position of the ITEM VIEW that the IMAGE VIEW is CONTAINED IN - NOT THE POSITION OF THE IMAGE VIEW (it doesn't make sense for the image view to have a position in the recyclerView)
    public int getPosition(View view) {
        int position = recyclerView.getChildLayoutPosition(recyclerView.findContainingItemView(view));
        return position;
    }


}
