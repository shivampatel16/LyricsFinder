/**
 * Authors: Shivam Patel and Gautam Naik
 * Andrew IDs: shpatel, gnaik
 * Emails: shpatel@cmu.edu, gnaik@andrew.cmu.edu
 * Last Modified: November 21, 2022
 * File: API.java
 * Part Of: Project4WebService
 *
 * This Java file defines the API class to store the logs from the request
 * response interactions with the API. Each object of the API class would contain
 * the logs that were stored during each request-response pair. The major logs that
 * were stored are the request timestamp, request song name, request artist name,
 * response song name, response artist name, lyrics found from the API, response
 * time stamp, response status code, and latency (milliseconds consumed between the time
 * the request was sent to the API and the response was received by the server).
 *
 * Link to 3rd Party API Documentation: https://api.vagalume.com.br/docs/letras/
 * Link to dashboard: https://still-reaches-60603.herokuapp.com/
 */

// Defines the package for the Java file
package ds.project4.webservice;

public class API {

    // Stores the time stamp of the request
    protected String request_timestamp;

    // Stores the song name sent in the request
    protected String request_song_name;

    // Stores the artist name sent in the request
    protected String request_artist_name;

    // Stores the song name got from the API and sent as response
    protected String response_song_name;

    // Stores the artist name got from the API and sent as response
    protected String response_artist_name;

    // Stores the lyrics got from the API and sent as response
    protected String lyrics;

    // Stores the time stamp of the response
    protected String response_timestamp;

    // Stores the HTTP status code stating how the request was handled by the server
    protected int status_code;

    // Stores the milliseconds consumed from the time the request was sent
    // from the mobile to the time the response was received by the mobile app
    protected double latency;

    // Constructor to initialize the values of the instance variables
    public API(String request_timestamp, String request_song_name, String request_artist_name, String response_song_name, String response_artist_name, String lyrics, String response_timestamp, int status_code, double latency) {
        this.request_timestamp = request_timestamp;
        this.request_song_name = request_song_name;
        this.request_artist_name = request_artist_name;
        this.response_song_name = response_song_name;
        this.response_artist_name = response_artist_name;
        this.lyrics = lyrics;
        this.response_timestamp = response_timestamp;
        this.status_code = status_code;
        this.latency = latency;
    }
}
