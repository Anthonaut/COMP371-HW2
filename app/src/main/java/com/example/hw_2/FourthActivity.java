package com.example.hw_2;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

public class FourthActivity extends AppCompatActivity {

    private ImageView beerImage;
    private TextView beerName;
    private TextView beerABV;
    private TextView beerBrewDate;
    private TextView beerDescription;
    private TextView beerFoodPairings;
    private TextView beerBrewsterTips;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fourth);
        // Look up the views of the beer info we want displayed
        beerImage = findViewById(R.id.imageView_selectedBeerImage);
        beerName = findViewById(R.id.textView_selectedBeerName);
        beerABV = findViewById(R.id.textView_selectedBeerABV);
        beerBrewDate = findViewById(R.id.textView_selectedBeerBrewDate);
        beerDescription = findViewById(R.id.textView_selectedBeerDescription);
        beerFoodPairings = findViewById(R.id.textView_selectedBeerFoodPairs);
        beerBrewsterTips = findViewById(R.id.textView_selectedBeerBrewTips);

        // Retrieve the intent from Third Activity
        Intent intent = getIntent();
        try {
            JSONObject beer = new JSONObject(intent.getStringExtra("selected beer"));
            beerName.setText(beer.getString("name")); // Set the name of the beer
            beerABV.setText("ABV: " + beer.getString("abv")); // Set the ABV value
            beerBrewDate.setText("First Brewed: " + beer.getString("first_brewed")); // Set when the beer was first brewed
            Picasso.get().load(beer.getString("image_url")).into(beerImage); // Load the beer image with Picasso method
            beerDescription.setText("Description: " + beer.getString("description")); // Set the beer description
            beerFoodPairings.setText("Food pairings: "); // Set the food pairings
            for (int i = 0; i < beer.getJSONArray("food_pairing").length(); i++) { // Because the Food pairings is a JSONArray, we must use a loop to properly list them, separated by commas
                if (i == beer.getJSONArray("food_pairing").length() - 1) {
                    beerFoodPairings.append(beer.getJSONArray("food_pairing").get(i).toString());
                    continue;
                }
                beerFoodPairings.append(beer.getJSONArray("food_pairing").get(i).toString() + ", ");
            }
            beerBrewsterTips.setText("Brewster Tips: " + beer.getString("brewers_tips")); // Set the brewster tips
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

}
