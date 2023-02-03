
/**
 * Authors: Shivam Patel and Gautam Naik
 * Andrew IDs: shpatel, gnaik
 * Emails: shpatel@cmu.edu, gnaik@andrew.cmu.edu
 * Last Modified: November 21, 2022
 * File: DisplayActivity.java
 * Part Of: Project4Task2
 * <p>
 * This Java file is the  activity of the android application
 * which shows the lyrics of the song.
 * <p>
 * The file defines the following functions:
 * onCreate() - To create the activity
 */

// Defines the package for the Java file
package edu.cmu.lyricfinder;

// Imports required for the activity

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;


public class DisplayActivity extends AppCompatActivity {
    /**
     * creates new activity
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //creates new activity
        super.onCreate(savedInstanceState);
        //set the layout
        setContentView(R.layout.activity_display);
        //fetch the title view in the UI
        TextView titleView = findViewById(R.id.title);
        //Referred code from https://www.geeksforgeeks.org/how-to-send-data-from-one-activity-to-second-activity-in-android/#:~:text=We%20can%20send%20the%20data,using%20the%20getStringExtra()%20method.
        //get the intent
        Intent i = getIntent();
        //fetch the song name and artist name from the intent
        String titleStr = i.getStringExtra("song") + " by " + i.getStringExtra("artist");
        //set the title
        titleView.setText(titleStr);
        //fetch the lyrics text view
        TextView lyricsView = findViewById(R.id.lyrics);
        //set the text to the view
        lyricsView.setText(i.getStringExtra("lyrics"));
        //fetch the search again button
        Button searchAgainButton = findViewById(R.id.searchAgain);
        //add on click listener to the button to get back to the search screen
        searchAgainButton.setOnClickListener(view -> {
            //get the intent of the search screen
            Intent myIntent = new Intent(getApplicationContext(), SearchActivity.class);
            //start the main(search) activity i.e. open the search screen
            startActivity(myIntent);
        });
    }
}