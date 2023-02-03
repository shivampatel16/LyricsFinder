
/**
 * Authors: Shivam Patel and Gautam Naik
 * Andrew IDs: shpatel, gnaik
 * Emails: shpatel@cmu.edu, gnaik@andrew.cmu.edu
 * Last Modified: November 21, 2022
 * File: SearchActivity.java
 * Part Of: Project4Task2
 * <p>
 * This Java file is the main activity of the android application
 * which is created on startup of application. This application provides lyrics
 * of the song by taking song and artist name as input and making an API call.
 * <p>
 * It accepts the song name and artist name from the user. Then, it makes the
 * HTTP request and gets the song lyrics as the response. Then this activity passes control
 * to the second activity which handles the second screen of the application to show the lyrics.
 * <p>
 * The file defines the following functions:
 * onCreate() - To create the activity
 * searchLyrics() - To make the API call in order to fetch the lyrics for the song
 * isConnected() - To check if app is connected to the internet
 */

// Defines the package for the Java file
package edu.cmu.lyricfinder;

// Imports required for the activity
import androidx.appcompat.app.AppCompatActivity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONException;
import org.json.JSONObject;
import java.nio.charset.StandardCharsets;


public class SearchActivity extends AppCompatActivity {
    /**
     * creates new activity
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Source: CMU 95702 Fall 2022 lab7-Android Code
        //creates new activity
        super.onCreate(savedInstanceState);
        //set the layout
        setContentView(R.layout.activity_search);
        //fetch the search button in the UI
        Button searchButton = findViewById(R.id.searchButton);
        //fetch the song name text field from the UI
        EditText songName = findViewById(R.id.songName);
        //fetch the artist name text field from the UI
        EditText artistName = findViewById(R.id.artistName);

        //Referred code from https://developer.android.com/develop/ui/views/components/dialogs
        //create instance of alert dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(SearchActivity.this);
        //set dialog title
        builder.setTitle(R.string.error_title);
        //add negative(cancel) button
        builder.setNegativeButton(R.string.cancel, (dialog, id) -> {
            //User cancelled the dialog
        });

        //Code referred from https://www.okhelp.cz/android/how-set-focus-on-view-android-development-example/
        //set focus(cursor) on song name input field
        songName.requestFocus();

        //Code referred from https://stackoverflow.com/questions/20824634/android-on-text-change-listener

        //add listener on change of song name input field
        songName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                for (int i = 0; i < s.length(); i++) {
                    //check for invalid input
                    if (!Character.isLetterOrDigit(s.charAt(i)) && s.charAt(i) != ' ' && s.charAt(i) != '!' && s.charAt(i) != '&'
                            && s.charAt(i) != '(' && s.charAt(i) != ')' && s.charAt(i) != '\'' && s.charAt(i) != '\"') {
                        //show error in UI
                        songName.setError("Invalid Song Name");
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
        //add listener on change of artist name input field
        artistName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                for (int i = 0; i < s.length(); i++) {
                    //check for invalid input
                    if (!Character.isLetterOrDigit(s.charAt(i)) && s.charAt(i) != ' ' && s.charAt(i) != '\'') {
                        //show error in UI
                        artistName.setError("Invalid Artist Name");
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
        //on clicking the search button, check for errors and then make api call if no errors
        // Source: CMU 95702 Fall 2022 lab7-Android Code
        searchButton.setOnClickListener(view -> {
            //check length of song name
            if (songName.toString().trim().length() == 0) {
                //show error in UI
                songName.setError("Song Name Cannot Be Empty");
            }
            //check length of artist name
            if (artistName.toString().trim().length() == 0) {
                //show error in UI
                artistName.setError("Artist Name Cannot Be Empty");
            }
            //make api call if data is valid
            if (songName.getError() == null && artistName.getError() == null
                    &&
                    songName.toString().trim().length() != 0 && artistName.toString().trim().length() != 0
            ) {
                //convert inputs to string
                String song = songName.getText().toString().trim();
                String artist = artistName.getText().toString().trim();
                //check for internet connection
                if (isConnected()) {
                    //if app is connected to internet, make the api call
                    searchLyrics(song, artist);
                }
                //else show error
                else {
                    //alert dialog to tell user that there is no internet
                    //Referred code from https://developer.android.com/develop/ui/views/components/dialogs
                    //set error message
                    builder.setMessage(R.string.no_internet);
                    //create the dialog
                    AlertDialog dialog = builder.create();
                    //show the dialog
                    dialog.show();
                }

            }
        });
    }

    /**
     * Searches for lyrics of song
     *
     * @param songName:   Name of song
     * @param artistName: Artist associated with the song
     */
    private void searchLyrics(String songName, String artistName) {
        String apiKey = "4794d71e3256361ba03a768912556bc9";
        //heroku url to make API call to the web service
        String url = "https://still-reaches-60603.herokuapp.com/getLyrics?apiKey=" + apiKey + "&artistName=" + artistName + "&songName=" + songName + "&phoneBrand=" + Build.BRAND + "&phoneDevice=" + Build.DEVICE;

        //Referred code from https://developer.android.com/develop/ui/views/components/dialogs
        //create instance of alert dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(SearchActivity.this);
        //set dialog title
        builder.setTitle(R.string.error_title);
        //add negative(cancel) button
        builder.setNegativeButton(R.string.cancel, (dialog, id) -> {
            //User cancelled the dialog
        });

        //Referred code from https://www.geeksforgeeks.org/json-parsing-in-android-using-volley-library/
        RequestQueue volleyQueue = Volley.newRequestQueue(SearchActivity.this);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                // we are using GET HTTP request method
                Request.Method.GET,
                // url we want to send the HTTP request to
                url,
                // this parameter is used to send a JSON object to the
                // server, since this is not required in our case,
                // we are keeping it `null`
                null,

                // lambda function for handling the case
                // when the HTTP request succeeds
                response -> {
                    // get the lyrics, song name and artist name from the JSON object
                    String lyrics;
                    String song;
                    String artist;
                    try {
                        lyrics = response.getString("lyrics");
                        song = response.getString("songName");
                        artist = response.getString("artistName");

                        //Referred code from https://www.geeksforgeeks.org/how-to-send-data-from-one-activity-to-second-activity-in-android/#:~:text=We%20can%20send%20the%20data,using%20the%20getStringExtra()%20method.
                        //store response data into intent so that it can be accessed from another activity
                        //then, start the next activity to go to the next screen
                        Intent i = new Intent(SearchActivity.this, DisplayActivity.class);
                        i.putExtra("lyrics", lyrics);
                        i.putExtra("song", song);
                        i.putExtra("artist", artist);
                        //start the next activity i.e. move to next screen
                        startActivity(i);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },

                // lambda function for handling the case
                // when the HTTP request fails
                (Response.ErrorListener) error -> {
                    //https://stackoverflow.com/questions/35841118/how-to-get-error-message-description-using-volley
                    try {
                        if (error.networkResponse != null && error.networkResponse.data != null) {
                            //get the response body
                            String responseBody = new String(error.networkResponse.data, StandardCharsets.UTF_8);
                            JSONObject data = new JSONObject(responseBody);
                            //get error message from response
                            String errorMessage = data.getString("errorMessage");
                            //alert dialog to tell user that something is wrong
                            //Referred code from https://developer.android.com/develop/ui/views/components/dialogs
                            //set error message
                            builder.setMessage(errorMessage);
                            //create the dialog
                            AlertDialog dialog = builder.create();
                            //show the dialog
                            dialog.show();
                        } else {
                            //if it is a timeout error, show the appropriate error in UI
                            if (error.toString().equalsIgnoreCase("com.android.volley.TimeoutError")) {
                                //alert dialog to tell user that something is wrong
                                //Referred code from https://developer.android.com/develop/ui/views/components/dialogs
                                builder.setMessage(R.string.service_unavailable);
                                AlertDialog dialog = builder.create();
                                dialog.show();
                            }
                        }
                    } catch (JSONException e) {
                        System.out.println("JSON parsing error " + e);
                    }
                }
        );
        //add to queue
        volleyQueue.add(jsonObjectRequest);
    }

    //Referred code from https://stackoverflow.com/questions/4238921/detect-whether-there-is-an-internet-connection-available-on-android
    //Referred code from https://stackoverflow.com/questions/57277759/getactivenetworkinfo-is-deprecated-in-api-29

    /**
     * Checks for internet and returns true if app is connected to internet. Else, false
     *
     * @return boolean True if connected to internet, else false
     */
    public boolean isConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        Network nw = connectivityManager.getActiveNetwork();
        if (nw == null) return false;
        NetworkCapabilities actNw = connectivityManager.getNetworkCapabilities(nw);
        //check for internet
        return actNw != null && (actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) || actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) || actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) || actNw.hasTransport(NetworkCapabilities.TRANSPORT_BLUETOOTH));
    }


}