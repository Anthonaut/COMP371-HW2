package com.example.hw_2;

import android.widget.ImageView;

public class Beer {
    // make all the instance variables private
    private String beerName;
    private String beerDescription;
    private String beerImageUrl;

    // constructor
    public Beer(String beerName, String beerDescription, String beerImageUrl) {
        this.beerName = beerName;
        this.beerDescription = beerDescription;
        this.beerImageUrl = beerImageUrl;
    }

    // make all the getters and setters to access the instance variables
    public String getBeerName() {
        return beerName;
    }

    public void setBeerName(String beerName) {
        this.beerName = beerName;
    }

    public String getBeerDescription() {
        return beerDescription;
    }

    public void setBeerDescription(String beerDescription) {
        this.beerDescription = beerDescription;
    }

    public String getBeerImageUrl() {
        return beerImageUrl;
    }

    public void setBeerImageUrl(String beerImageUrl) {
        this.beerImageUrl = beerImageUrl;
    }
}
