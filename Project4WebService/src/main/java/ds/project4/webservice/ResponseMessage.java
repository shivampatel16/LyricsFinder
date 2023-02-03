/**
 * Authors: Shivam Patel and Gautam Naik
 * Andrew IDs: shpatel, gnaik
 * Emails: shpatel@cmu.edu, gnaik@andrew.cmu.edu
 * Last Modified: November 21, 2022
 * File: ResponseMessage.java
 * Part Of: Project4WebService
 *
 * This Java file defines and helps create a JSON message (normal message and
 * not an error message) that would be sent from the server to the client (Android
 * App). The normal response message (without an error) sends the name of the
 * artist, name of the song and the fetched lyrics using the API.
 *
 * Link to 3rd Party API Documentation: https://api.vagalume.com.br/docs/letras/
 * Link to dashboard: https://still-reaches-60603.herokuapp.com/
 */

// Defines the package for the Java file
package ds.project4.webservice;

// Imports necessary for Gson
import com.google.gson.Gson;

public class ResponseMessage {

    // Stores the name of the artist
    protected String artistName;

    // Stores the name of the song
    protected String songName;

    // Stores the lyrics for the songName by artistName
    protected String lyrics;

    // Constructor to initialize the values of the instance variables
    ResponseMessage(String artistName, String songName, String lyrics) {
        this.artistName = artistName;
        this.songName = songName;
        this.lyrics = lyrics;
    }

    /***
     * The method converts the ResponseMessage object to a JSON String using Gson.
     * @return JSON String representation of the ResponseMessage object
     */
    public String toString() {

        // Create a Gson object
        Gson gson = new Gson();

        // Serialize to JSON
        return gson.toJson(this);
    }
}
