/**
 * Authors: Shivam Patel and Gautam Naik
 * Andrew IDs: shpatel, gnaik
 * Emails: shpatel@cmu.edu, gnaik@andrew.cmu.edu
 * Last Modified: November 21, 2022
 * File: LyricsFinderServlet.java
 * Part Of: Project4WebService
 * <p>
 * This Java file acts as the Servlet for the web server.
 * The servlet defines two URL endpoints. One is for getting the lyrics using the API
 * and the other endpoint is used to get the dashboard. The servlet gets the request from
 * the mobile app which sends an HTTP request. After extracting the contents from the
 * request query, it forms the third party URL to fetch. It then makes the HTTP request
 * to the 3rd party API which in turn returns JSON data in the HTTP response. The servlet
 * then sends back a JSON HTTP response to the mobile application that made the request.
 * The logs of all interactions with the mobile app and the third party api are stored
 * in a MongoDB database and are displayed on a dashboard. The dashboard also contains
 * a few important operation analytics on the logs that were stored in the MongoDB
 * database.
 * <p>
 * Link to 3rd Party API Documentation: https://api.vagalume.com.br/docs/letras/
 * Link to dashboard: https://still-reaches-60603.herokuapp.com/
 */

// Defines the package for the Java file
package ds.project4.webservice;

// Imports required for performing read and write to MongoDB, Date, JSONArray
// JSONObject, IO operations

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.bson.Document;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;
import java.util.TimeZone;

// Servlet handles two url endpoints
@WebServlet(name = "lyricsFinderServlet",
        urlPatterns = {"/getLyrics", "/getDashboard"})

public class LyricsFinderServlet extends HttpServlet {

    // Create an instance of the Android model of the server
    LyricsFinderAndroidModel lyricsFinderAndroidModel = new LyricsFinderAndroidModel();

    // Create an instance of the API model of the server
    LyricsFinderAPIModel lyricsFinderAPIModel = new LyricsFinderAPIModel();

    // Create an instance of the Dashboard model of the server
    LyricsFinderDashboardModel lyricsFinderDashboardModel = new LyricsFinderDashboardModel();

    // Create an instance of the MongoDB model of the server
    LyricsFinderMongoDBModel lyricsFinderMongoDBModel = new LyricsFinderMongoDBModel();

    /***
     * This servlet will reply to HTTP GET requests via this doGet method
     * urlPatterns = {"/getLyrics", "/getDashboard"}
     * @param request Represents the request from HttpServletRequest
     * @param response Represents the response from HttpServletResponse
     */
    public void doGet(HttpServletRequest request, HttpServletResponse response) {

        // Source for compatability of different devices: CMU 95702 Fall 2022 Lab2-InterestingPicture Code
        // Used for compatability of the code on different devices

        // Determine what type of device our user is
        String ua = request.getHeader("User-Agent");

        // prepare the appropriate DOCTYPE for the view pages
        if (ua != null && ((ua.indexOf("Android") != -1) || (ua.indexOf("iPhone") != -1))) {
            /*
             * This is the latest XHTML Mobile doctype. To see the difference it
             * makes, comment it out so that a default desktop doctype is used
             * and view on an Android or iPhone.
             */
            request.setAttribute("doctype", "<!DOCTYPE html PUBLIC \"-//WAPFORUM//DTD XHTML Mobile 1.2//EN\" \"http://www.openmobilealliance.org/tech/DTD/xhtml-mobile12.dtd\">");
        } else {
            request.setAttribute("doctype", "<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\" \"http://www.w3.org/TR/html4/loose.dtd\">");
        }

        // Connect to MongoDB database and create a MongoDB client
        MongoClient mongoClient = lyricsFinderMongoDBModel.connectToMongoDB();

        // Get the required database from the MongoDB client
        MongoDatabase LyricsFinderDB = mongoClient.getDatabase("LyricsFinderDB");

        // Get the MOBILE collection from the MongoDB database
        MongoCollection<Document> mobileCollection = LyricsFinderDB.getCollection("MOBILE");

        // Get the API collection from the MongoDB database
        MongoCollection<Document> apiCollection = LyricsFinderDB.getCollection("API");


        // Check the request URL endpoint and make the appropriate method calls
        // Use this endpoint to get the lyrics from the 3rd party API
        if (request.getServletPath().equals("/getLyrics")) {

            // Stores the request and response time stamps of the mobile app and the 3rd party API
            String mobile_request_timestamp = "", mobile_response_timestamp = "", api_request_timestamp = "", api_response_timestamp = "";

            // Stores the artist name and song name got from the response from the 3rd party API
            // The response song and artist names might be different that the ones in the request
            // as we are also handling cases where the user of the mobile app did not enter full
            // names and did some minor mistake in entering the names.
            String response_artistName = "", response_songName = "";

            // Stores the latency in milliseconds for the time it took between the request and
            // response from the 3rd party API
            double latencyAPI;

            // Stores the latency in milliseconds for the time it took between the request and
            // response from the mobile Android application
            double latencyApp;

            // Stores the lyrics found from the 3rd party API
            String lyrics = "";

            // Defines an object of the API class to store the logs from the request
            // response interactions with the API.
            API api;

            // Defines an object of the MOBILE class to store the logs from the request
            // response interactions with the MOBILE.
            Mobile mobile;

            // Source for formatting dates: https://www.baeldung.com/java-simple-date-format
            // Define time zone
            TimeZone tz = TimeZone.getTimeZone("UTC");

            // Set date format
            SimpleDateFormat sdf
                    = new SimpleDateFormat(
                    "dd-MM-yyyy HH:mm:ss.SSS");

            // Set time zone
            sdf.setTimeZone(tz);

            // Get the request timestamp for the mobile
            mobile_request_timestamp = sdf.format(new Date());

            // Extract artist name from JSON HTTP request from the mobile app
            String request_artistName = request.getParameter("artistName");

            // Extract song name from JSON HTTP request from the mobile app
            String request_songName = request.getParameter("songName");

            // Extract phone brand from JSON HTTP request from the mobile app
            String phoneBrand = request.getParameter("phoneBrand");

            // Extract phone device from JSON HTTP request from the mobile app
            String phoneDevice = request.getParameter("phoneDevice");

            // Extract api key from JSON HTTP request from the mobile app
            // Our API key is "4794d71e3256361ba03a768912556bc9"
            String api_key = request.getParameter("apiKey");

            // Source to set response content type: https://www.baeldung.com/servlet-json-response
            PrintWriter out = null;
            try {
                out = response.getWriter();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");

            // Forms the URL of the 3rd party API
            String apiURLString = "https://api.vagalume.com.br/search.php?art="
                    + request_artistName + "&mus="
                    + request_songName + "&apikey="
                    + api_key;

            // Get the request timestamp for the api
            api_request_timestamp = sdf.format(new Date());

            // Get the JSON reply (in String format) from the API
            String jsonAPIReply = lyricsFinderAPIModel.fetchAPI(apiURLString);

            // Get the response timestamp for the api
            api_response_timestamp = sdf.format(new Date());

            // Create a JSONObject from the String (in JSON format) reply from the API
            JSONObject jsonObject = new JSONObject(jsonAPIReply);

            // Calculate the latency for API
            latencyAPI = lyricsFinderMongoDBModel.calculateLatency(api_request_timestamp, api_response_timestamp);

            // If the artist name or song name were entered empty
            if (Objects.equals(request_artistName, "") || Objects.equals(request_songName, "")) {

                // Create an object of the ErrorResponseMessage class with the appropriate error message
                ErrorResponseMessage errorResponseMessage = new ErrorResponseMessage("Song name and artist name cannot be empty!");

                // Initiate the object of the API class to store the logs from the request-response interactions with the API.
                api = new API("Empty input", request_songName, request_artistName, "Empty input", "Empty input", "Empty input", "Empty input", 422, 0.0);

                // Get the response timestamp for mobile
                mobile_response_timestamp = sdf.format(new Date());

                // Calculate the latency for mobile
                latencyApp = lyricsFinderMongoDBModel.calculateLatency(mobile_request_timestamp, mobile_response_timestamp);

                // Initiate the object of the Mobile class to store the logs from the request-response interactions with the mobile application.
                mobile = new Mobile(phoneDevice, phoneBrand, mobile_request_timestamp, request_songName, request_artistName, "Empty input", "Empty input", "Empty input", mobile_response_timestamp, latencyApp, 422);

                // Set the response status
                response.setStatus(422);

                // Send the response
                out.print(errorResponseMessage);
            }

            // If the song name entered is syntactically incorrect
            else if (!lyricsFinderAndroidModel.verifySongName(request_songName)) {

                // Create an object of the ErrorResponseMessage class with the appropriate error message
                ErrorResponseMessage errorResponseMessage = new ErrorResponseMessage("Invalid song name!");

                // Initiate the object of the API class to store the logs from the request-response interactions with the API.
                api = new API("Invalid input", request_songName, request_artistName, "Invalid input", "Invalid input", "Invalid input", "Invalid input", 422, 0.0);

                // Get the response timestamp for mobile
                mobile_response_timestamp = sdf.format(new Date());

                // Calculate the latency for mobile
                latencyApp = lyricsFinderMongoDBModel.calculateLatency(mobile_request_timestamp, mobile_response_timestamp);

                // Initiate the object of the Mobile class to store the logs from the request-response interactions with the mobile application.
                mobile = new Mobile(phoneDevice, phoneBrand, mobile_request_timestamp, request_songName, request_artistName, "Invalid input", "Invalid input", "Invalid input", mobile_response_timestamp, latencyApp, 422);

                // Set the response status
                response.setStatus(422);

                // Send the response
                out.print(errorResponseMessage);
            }

            // If the artist name entered is syntactically incorrect
            else if (!lyricsFinderAndroidModel.verifyArtistName(request_artistName)) {

                // Create an object of the ErrorResponseMessage class with the appropriate error message
                ErrorResponseMessage errorResponseMessage = new ErrorResponseMessage("Invalid artist name!");

                // Initiate the object of the API class to store the logs from the request-response interactions with the API.
                api = new API("Invalid input", request_songName, request_artistName, "Invalid input", "Invalid input", "Invalid input", "Invalid input", 422, 0.0);

                // Get the response timestamp for mobile
                mobile_response_timestamp = sdf.format(new Date());

                // Calculate the latency for mobile
                latencyApp = lyricsFinderMongoDBModel.calculateLatency(mobile_request_timestamp, mobile_response_timestamp);

                // Initiate the object of the Mobile class to store the logs from the request-response interactions with the mobile application.
                mobile = new Mobile(phoneDevice, phoneBrand, mobile_request_timestamp, request_songName, request_artistName, "Invalid input", "Invalid input", "Invalid input", mobile_response_timestamp, latencyApp, 422);

                // Set the response status
                response.setStatus(422);

                // Send the response
                out.print(errorResponseMessage);
            }

            // If the API is currently unavailable
            else if (lyricsFinderAPIModel.fetchAPIStatusCode(apiURLString) == 503) {

                // Create an object of the ErrorResponseMessage class with the appropriate error message
                ErrorResponseMessage errorResponseMessage = new ErrorResponseMessage("API is currently unavailable!");

                // Initiate the object of the API class to store the logs from the request-response interactions with the API.
                api = new API("API not available", request_songName, request_artistName, "API not available", "API not available", "API not available", "API not available", 503, 0.0);

                // Get the response timestamp for mobile
                mobile_response_timestamp = sdf.format(new Date());

                // Calculate the latency for mobile
                latencyApp = lyricsFinderMongoDBModel.calculateLatency(mobile_request_timestamp, mobile_response_timestamp);

                // Initiate the object of the Mobile class to store the logs from the request-response interactions with the mobile application.
                mobile = new Mobile(phoneDevice, phoneBrand, mobile_request_timestamp, request_songName, request_artistName, "API not available", "API not available", "API not available", mobile_response_timestamp, latencyApp, 503);

                // Set the response status
                response.setStatus(503);

                // Send the response
                out.print(errorResponseMessage);
            }

            // If the authentication was unsuccessful
            else if (lyricsFinderAPIModel.fetchAPIStatusCode(apiURLString) == 403) {

                // Create an object of the ErrorResponseMessage class with the appropriate error message
                ErrorResponseMessage errorResponseMessage = new ErrorResponseMessage("Authentication unsuccessful. Invalid API Key!");

                // Initiate the object of the API class to store the logs from the request-response interactions with the API.
                api = new API("Authentication unsuccessful", request_songName, request_artistName, "Authentication unsuccessful", "Authentication unsuccessful", "Authentication unsuccessful", "Authentication unsuccessful", 403, 0.0);

                // Get the response timestamp for mobile
                mobile_response_timestamp = sdf.format(new Date());

                // Calculate the latency for mobile
                latencyApp = lyricsFinderMongoDBModel.calculateLatency(mobile_request_timestamp, mobile_response_timestamp);

                // Initiate the object of the Mobile class to store the logs from the request-response interactions with the mobile application.
                mobile = new Mobile(phoneDevice, phoneBrand, mobile_request_timestamp, request_songName, request_artistName, "Authentication unsuccessful", "Authentication unsuccessful", "Authentication unsuccessful", mobile_response_timestamp, latencyApp, 403);

                // Set the response status
                response.setStatus(403);

                // Send the response
                out.print(errorResponseMessage);
            }

            // If the song name was syntactically correct, but it was not found in the API's database
            else if (Objects.equals(jsonObject.getString("type"), "song_notfound")) {

                // Create an object of the ErrorResponseMessage class with the appropriate error message
                ErrorResponseMessage errorResponseMessage = new ErrorResponseMessage("Song not found!");

                // Initiate the object of the API class to store the logs from the request-response interactions with the API.
                api = new API(api_request_timestamp, request_songName, request_artistName, "Data not found", "Data not found", "Data not found", api_response_timestamp, 422, latencyAPI);

                // Get the response timestamp for mobile
                mobile_response_timestamp = sdf.format(new Date());

                // Calculate the latency for mobile
                latencyApp = lyricsFinderMongoDBModel.calculateLatency(mobile_request_timestamp, mobile_response_timestamp);

                // Initiate the object of the Mobile class to store the logs from the request-response interactions with the mobile application.
                mobile = new Mobile(phoneDevice, phoneBrand, mobile_request_timestamp, request_songName, request_artistName, "Data not found", "Data not found", "Data not found", mobile_response_timestamp, latencyApp, 422);

                // Set the response status
                response.setStatus(422);

                // Send the response
                out.print(errorResponseMessage);
            }

            // If the artist name was syntactically correct, but it was not found in the API's database
            else if (Objects.equals(jsonObject.getString("type"), "notfound")) {

                // Create an object of the ErrorResponseMessage class with the appropriate error message
                ErrorResponseMessage errorResponseMessage = new ErrorResponseMessage("Artist not found!");

                // Initiate the object of the API class to store the logs from the request-response interactions with the API.
                api = new API(api_request_timestamp, request_songName, request_artistName, "Data not found", "Data not found", "Data not found", api_response_timestamp, 422, latencyAPI);

                // Get the response timestamp for mobile
                mobile_response_timestamp = sdf.format(new Date());

                // Calculate the latency for mobile
                latencyApp = lyricsFinderMongoDBModel.calculateLatency(mobile_request_timestamp, mobile_response_timestamp);

                // Initiate the object of the Mobile class to store the logs from the request-response interactions with the mobile application.
                mobile = new Mobile(phoneDevice, phoneBrand, mobile_request_timestamp, request_songName, request_artistName, "Data not found", "Data not found", "Data not found", mobile_response_timestamp, latencyApp, 422);

                // Set the response status
                response.setStatus(422);

                // Send the response
                out.print(errorResponseMessage);
            }

            // If the song and artist name inputs were correct and the lyrics were found by the 3rd party API
            else {

                // Extract particular JSONObject from the JSON reply from the 3rd party API
                JSONObject artistInfo = jsonObject.getJSONObject("art");

                // Extract artist name from the artistInfo (JSONObject)
                response_artistName = artistInfo.getString("name");

                // Parse JSONObject to get the song name and lyrics
                // Source for parsing JSONObject: https://stackoverflow.com/questions/5015844/parsing-json-object-in-java
                JSONArray jsonArray = jsonObject.getJSONArray("mus");
                for (int i = 0; i < jsonArray.length(); i++) {
                    String lyrics_line = jsonArray.getJSONObject(i).getString("text");
                    lyrics = lyrics + lyrics_line;
                    response_songName = jsonArray.getJSONObject(i).getString("name");
                }

                // Initiate the object of the API class to store the logs from the request-response interactions with the API.
                api = new API(api_request_timestamp, request_songName, request_artistName, response_songName, response_artistName, lyrics, api_response_timestamp, 200, latencyAPI);

                // Create an object of the ResponseMessage class, initializing its instance
                // variables with the artist name, song name and the lyrics
                ResponseMessage responseMessage = new ResponseMessage(response_artistName, response_songName, lyrics);

                // Get the response timestamp for mobile
                mobile_response_timestamp = sdf.format(new Date());

                // Calculate the latency for mobile
                latencyApp = lyricsFinderMongoDBModel.calculateLatency(mobile_request_timestamp, mobile_response_timestamp);

                // Initiate the object of the Mobile class to store the logs from the request-response interactions with the mobile application.
                mobile = new Mobile(phoneDevice, phoneBrand, mobile_request_timestamp, request_songName, request_artistName, response_songName, response_artistName, lyrics, mobile_response_timestamp, latencyApp, 200);

                // Send the response
                out.print(responseMessage);
            }

            // Insert one document (of API interaction logs) into the API collection in the MongoDB database
            lyricsFinderMongoDBModel.apiInsertOne(api, apiCollection);

            // Insert one document (of mobile app interaction logs) into the API collection in the MongoDB database
            lyricsFinderMongoDBModel.mobileInsertOne(mobile, mobileCollection);

            // Flush the response
            out.flush();
        }

        // Use this endpoint to get the dashboard with all the logs stored in the MongoDB database
        // and display the operation analytics
        else if (request.getServletPath().equals("/getDashboard")) {
            //call the method to get all operation analytics data from MongoDB and forward the data to the JSP to display
            lyricsFinderDashboardModel.buildDashboard(mobileCollection, apiCollection, request, response);
        }

        // Close the MongoDB connection
        mongoClient.close();
    }
}