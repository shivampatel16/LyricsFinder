/**
 * Authors: Shivam Patel and Gautam Naik
 * Andrew IDs: shpatel, gnaik
 * Emails: shpatel@cmu.edu, gnaik@andrew.cmu.edu
 * Last Modified: November 21, 2022
 * File: ErrorResponseMessage.java
 * Part Of: Project4WebService
 *
 * This Java file defines and helps create a JSON message (error message) that
 * would be sent from the server to the client (Android App). The error response
 * message sends only the error message that occurred due to invalid server side
 * input.
 *
 * Link to 3rd Party API Documentation: https://api.vagalume.com.br/docs/letras/
 * Link to dashboard: https://still-reaches-60603.herokuapp.com/
 * */

// Defines the package for the Java file
package ds.project4.webservice;

import com.google.gson.Gson;

public class ErrorResponseMessage {

    // Stores the error message
    protected String errorMessage;

    // Constructor to initialize the values of the instance variables
    ErrorResponseMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    /***
     * The method converts the ErrorResponseMessage object to a JSON String using Gson.
     * @return JSON String representation of the ErrorResponseMessage object
     */
    public String toString() {

        // Create a Gson object
        Gson gson = new Gson();

        // Serialize to JSON
        return gson.toJson(this);
    }
}
