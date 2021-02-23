package com.example.hw_2;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class SecondActivity extends AppCompatActivity {

    private Button button_fetchBeerAPI;
    private String beerName;
    private String startDateRange;
    private String endDateRange;
    private String preStartDate; // For validating the date ranges
    private String preEndDate;
    private Boolean HPBeer;
    private static final String api_url = "https://api.punkapi.com/v2/beers";
    private static AsyncHttpClient client  = new AsyncHttpClient();


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        //Look up button by its ID
        button_fetchBeerAPI = findViewById(R.id.button);

        // add event listener for the click
        button_fetchBeerAPI.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchNextActivity(v);
            }
        });

    }


    public void launchNextActivity(View view) {
        client.addHeader("Accept", "*/*");
        RequestParams param = new RequestParams(); // Use RequestParams to have a filtered JSON Array, dependent on user input
        // We are going to have these methods called to check input, and add parameters if input is valid that user enters
        checkBeerName(); // Check that the user put some string for the "Brew name" parameter (i.e must be non-empty or parameter isn't added)
        if (!beerName.equals("")) {
            param.put("beer_name", beerName); // use the "put" method with the correct key (look at API doc) and value pair (user input)
        }
        checkBrewStartDate(); // The brew date params EXCLUDE the date value inputted (i.e brews before/after 09/2007 won't include brews with brew date on 09/2007)
        Log.d("start date", startDateRange);
        if ((!startDateRange.equals("Invalid")) && (!startDateRange.equals(""))) {
            preStartDate = startDateRange;
            Log.d("start date input", startDateRange);
            startBrewDateInclusive(startDateRange);
            param.put("brewed_after", startDateRange); // Add "All brews after a date"
        }
        checkBrewEndDate();
        if (!endDateRange.equals("Invalid") && (!endDateRange.equals(""))) {
            preEndDate = endDateRange;
            Log.d("end date input", endDateRange);
            endBrewDateInclusive(endDateRange);
            param.put("brewed_before", endDateRange); // Add "All brews before a date parameter"
        }
        checkHPBeer(); // Check if High Point criteria is switched on
        if (HPBeer == true) {
            Log.d("HP Beer", "HP Beer was true");
            param.put("abv_gt", 3.99); // Add HP Beer parameter
        }

        //param.put("per_page", 80); // returns the number of results
        //param.put("page", 1); // A "page" is a JSON Array of 25 beers in it. Range is 1-13(1-325 beers by id). Returns only page 1 by default, one page per request

        client.get(api_url, param, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                //Log.d("api response", new String(responseBody));

                try {
                    JSONArray json = new JSONArray(new String(responseBody)); // Note that the API response is a JSONArray because the root encapsulation of the data is "[]", not "{}" (Also API doc says so)
                    /*for (int i = 0; i < json.length(); i++) {
                        Log.d("JSON Object", json.get(i).toString());
                    }*/
                    if ((validDateFormat(startDateRange) == false) || (validDateFormat(endDateRange) == false)) { // 1st Flag - Check if dates entered are proper format (i.e mm/yyyy)
                        //Log.d("Check 1", "Check 1 flagged");
                        invalidDateFormatMessage(view);
                    }
                    // 2nd Flag - If one of the date inputs is empty, but the other is a valid date input then search is valid
                    else if((validDateFormat(startDateRange) == true && endDateRange.equals("")) || (validDateFormat(endDateRange) && startDateRange.equals(""))) {
                        Intent intent = new Intent(SecondActivity.this, ThirdActivity.class);
                        intent.putExtra("Beers", new String(responseBody)); // Send JSONArray as intent to Third Activity
                        startActivity(intent);
                    }

                    else if (validateStartAndEndRange() == false) { // 3rd Flag, Check if the two dates inputted do not follow where Start Date must be earlier than End Date
                        invalidDateRangeMessage(view);
                    }
                    else { // 4th Check - Both dates were inputted, follow proper format, and Start date is earlier than End date OR no dates were inputted
                        Intent intent = new Intent(SecondActivity.this, ThirdActivity.class);
                        intent.putExtra("Beers", new String(responseBody)); // Send JSONArray as intent to Third Activity
                        startActivity(intent);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Log.e("api error", new String(responseBody));
            }
        });

    }

    public void startBrewDateInclusive(String date) {
        String monthStr = date.substring(0, 2);
        String yearStr = date.substring(3);
        if (monthStr.equals("01")) {
            int yearInt = Integer.parseInt(yearStr);
            yearInt = yearInt - 1;
            monthStr = "12";
            yearStr = Integer.toString(yearInt);
            startDateRange = monthStr + "/" + yearStr;
            //Log.d("startBrewDateInclusive", startDateRange);
        }
        else {
            int monthInt = Integer.parseInt(monthStr);
            monthInt = monthInt - 1;
            if (monthInt >= 10) {
                monthStr = Integer.toString(monthInt);
                startDateRange = monthStr + "/" + yearStr;
                //Log.d("startBrewDateInclusive", startDateRange);
            }
            else {
                monthStr = "0" + Integer.toString(monthInt);
                startDateRange = monthStr + "/" + yearStr;
                //Log.d("startBrewDateInclusive", startDateRange);
            }
        }
    }

    public void endBrewDateInclusive(String date) {
        String monthStr = date.substring(0, 2);
        String yearStr = date.substring(3);
        if (monthStr.equals("12")) {
            int yearInt = Integer.parseInt(yearStr);
            yearInt = yearInt + 1;
            monthStr = "01";
            yearStr = Integer.toString(yearInt);
            endDateRange = monthStr + "/" + yearStr;
            //Log.d("endBrewDateInclusive", endDateRange);
        }
        else {
            int monthInt = Integer.parseInt(monthStr);
            monthInt = monthInt + 1;
            if (monthInt >= 10) {
                monthStr = Integer.toString(monthInt);
                endDateRange = monthStr + "/" + yearStr;
                //Log.d("endBrewDateInclusive", endDateRange);
            }
            else {
                monthStr = "0" + Integer.toString(monthInt);
                endDateRange = monthStr + "/" + yearStr;
                //Log.d("endBrewDateInclusive", endDateRange);
            }
        }
    }

    public void checkBeerName() {
        EditText inputFilled = (EditText) findViewById(R.id.editTextBeerName);
        String input = inputFilled.getText().toString().trim();
        if (input.contains(" ")) { // We must fill in all spaces between words with underscores per the API doc
            char[] inputCharArray = input.toCharArray();
            for (int i = 0; i < inputCharArray.length; i++) {
                if (inputCharArray[i] == ' ') {
                    inputCharArray[i] = '_';
                }
                else {
                    continue;
                }
            }
            input = String.valueOf(inputCharArray);
        }
        //Log.d("Beer name", input);
        beerName = input; // Set beerName variable to user input in Beer Name Edit Text field
    }


    public void checkBrewStartDate() {
        EditText inputFilled = (EditText) findViewById(R.id.editText_brewStartDate);
        String input = inputFilled.getText().toString().trim();
        if (input.equals("")) {
            startDateRange = input;
        }
        else {
            if (validDateFormat(input) == false) {
                startDateRange = "Invalid";
                //Log.d("Check start date", startDateRange);
            }
            else {
                startDateRange = input;
            }
        }
    }

    public void checkBrewEndDate() {
        EditText inputFilled = (EditText) findViewById(R.id.editText_brewEndDate);
        String input = inputFilled.getText().toString().trim();
        if (input.equals("")) {
            endDateRange = input;
        }
        else {
            if (validDateFormat(input) == false) {
                endDateRange = "Invalid";
            }
            else {
                endDateRange = input;
            }
        }
    }

    public Boolean validDateFormat(String dateInput) {
        Boolean flag;
        flag = true;
        dateInput = dateInput.trim();
        if (dateInput.equals("")) { // This helps in that an empty date input is valid, so long as it's not added as a parameter in API call
            return flag;
        }

        if (dateInput.length() != 7) { // 1st Check - Since 'mm/yyyy' is 7 characters in length, then it's an easy filter if the input isn't of length 7
            //Log.d("check length", "checking that the length is incorrect");
            flag = false;
            return flag;
        }

        for (int i = 0; i <dateInput.length(); i++) { // We will cycle through the characters to check they are digits, and the slash is at index 2
            char ch1 = dateInput.charAt(i);
            if (i == 2 && ch1 != '/') { // If there is no slash at index 2, then its invalid
                //Log.d("check slash", "checking that there is no slash");
                flag = false;
                return flag;
                //break;
            }

            if (i == 2 && ch1 == '/') // Slash is at index 2, so continue to not trigger the final if statement
                continue;

            if (Character.isDigit(ch1) == false) { // Check if the char is a number
                flag = false;
                //Log.d("check char is digit", "checking a char is not a digit");
                return flag;
                //break;
            }
        }

        String monthSubStr = dateInput.substring(0, 2); // At this point, the first two characters are numbers (i.e 'mm' portion)
        int month = Integer.parseInt(monthSubStr); // Turn month substring into int for operator comparison
        if (month <= 0 || month >= 13) { // If the int month is not from 1-12, then it's invalid
            flag = false;
            return flag;
        }
        // The year doesn't need checking, since the range for years is "indefinite" and its assumed that no year like "999" will be inputted
        return flag;
    }

    public Boolean validateStartAndEndRange() {
        if (startDateRange.equals("") && endDateRange.equals("")) { //As before, if both date inputs are empty then this is still valid, as long as the empty string aren't added as brew date parameters in APi call
            return true;
        }

        String month1 = preStartDate.substring(0, 2); // We are using the dates that are pre-inclusive of the inputs for easier checking of date ranges
        String month2 = preEndDate.substring(0, 2);
        int monthStart = Integer.parseInt(month1);
        int monthEnd = Integer.parseInt(month2);
        String year1 = preStartDate.substring(3);
        String year2 = preEndDate.substring(3);
        int yearStart = Integer.parseInt(year1);
        int yearEnd = Integer.parseInt(year2);
        if (yearStart < yearEnd) { // If the the start year is smaller than the end year, then the date range is valid
            return true;
        }
        else if(yearStart == yearEnd) { // If the years are equal then check the months
            if (monthStart <= monthEnd) { // Because the API brew date parameters is exclusive, the months can't equal - But now i've made them inclusive so its less than OR EQUAL to
                return true;
            }
            return false; // The end month is greater than or equal to start month, which is invalid
        }
        else {
            return false; // The End year is larger than the Start year, which is invalid
        }

    }

    public void checkHPBeer() {
        Switch switchCheck = (Switch) findViewById(R.id.switch1);
        if (switchCheck.isChecked() == true) { // Checking if the switch for HP Beer was switched to green (i.e switch is on if "Checked true"
            HPBeer = true;
        }
        else {
            HPBeer = false;
        }
    }

    public void invalidDateRangeMessage(View view) { // Toast for an invalid Date range input
        Toast invalidDateRangeInput = Toast.makeText(this, "Start date must be earlier than End date", Toast.LENGTH_SHORT);
        invalidDateRangeInput.show();
    }

    public void invalidDateFormatMessage(View view) { // Toast for invalid Date format input
        Toast invalidDateFormatInput = Toast.makeText(this, "The Start or End date doesn't follow 'mm/yyyy' format", Toast.LENGTH_SHORT);
        invalidDateFormatInput.show();
    }

}
