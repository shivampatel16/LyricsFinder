/**
 * Authors: Shivam Patel and Gautam Naik
 * Andrew IDs: shpatel, gnaik
 * Emails: shpatel@cmu.edu, gnaik@andrew.cmu.edu
 * Last Modified: November 21, 2022
 * File: LyricsFinderAPIModel.java
 * Part Of: Project4WebService
 * <p>
 * This Java file acts as the Model for the web server's interactions with third-party API.
 * The model defines the following functions:
 * fetchAPI() - To get the lyrics from the API
 * fetchAPIStatusCode() - To find the status code of the fetch operation to the API
 * <p>
 * Link to 3rd Party API Documentation: https://api.vagalume.com.br/docs/letras/
 * Link to dashboard: https://still-reaches-60603.herokuapp.com/
 */

// Defines the package for the Java file
package ds.project4.webservice;

// Imports required for making HTTP request to third-party API
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class LyricsFinderAPIModel {
    /**
     * Function to make an HTTP request to the given URL and fetch the lyrics from the API
     *
     * @param urlString URL to which the HTTP request is to be made
     * @return JSON reply from the API
     */
    public String fetchAPI(String urlString) {

        // Stores the JSON reply from the API
        String response = "";
        try {

            // Source: CMU 95702 Fall 2022 Lab2-InterestingPicture Code
            // Create a new URL object
            URL url = new URL(urlString);

            /*
             * Create an HttpURLConnection.  This is useful for setting headers
             * and for getting the path of the resource that is returned (which
             * may be different from the URL above if redirected).
             * HttpsURLConnection (with an "s") can be used if required by the site.
             */
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            // Read all the text returned by the server
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8));
            String str;
            // Read each line of "in" until done, adding each to "response"
            while ((str = in.readLine()) != null) {
                // str is one line of text readLine() strips newline characters
                response += str;
            }
            in.close();
        }
        // catch IO exception
        catch (IOException e) {
            System.out.println("An IO Exception has occurred.");
        }
        // return JSON response from the API
        return response;
    }

    /**
     * Function to find the status code of the fetch operation to the API
     *
     * @param urlString URL to which the HTTP request is made and the status is to be found
     * @return Status code of the fetch operation to the API
     */
    public int fetchAPIStatusCode(String urlString) {

        // Stores the JSON reply from the API
        // Default is set to 200 (would be updated as per the response)
        int apiFetchStatusCode = 200;
        try {

            // Source: CMU 95702 Fall 2022 Lab2-InterestingPicture Code
            // Create a new URL object
            URL url = new URL(urlString);

            /*
             * Create an HttpURLConnection.  This is useful for setting headers
             * and for getting the path of the resource that is returned (which
             * may be different from the URL above if redirected).
             * HttpsURLConnection (with an "s") can be used if required by the site.
             */
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            // Get the response code of the HTTP URL connection
            apiFetchStatusCode = connection.getResponseCode();
        }
        // catch IO exception
        catch (IOException e) {
            System.out.println("An IO Exception has occurred.");
        }

        // returns the response code of the HTTP URL connection
        return apiFetchStatusCode;
    }
}
