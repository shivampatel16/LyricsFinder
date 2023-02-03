/**
 * Authors: Shivam Patel and Gautam Naik
 * Andrew IDs: shpatel, gnaik
 * Emails: shpatel@cmu.edu, gnaik@andrew.cmu.edu
 * Last Modified: November 21, 2022
 * File: LyricsFinderDashboardModel.java
 * Part Of: Project4WebService
 * <p>
 * This Java file acts as the Model for the web service operations to create a dashboard.
 * The model defines the following functions:
 * getTopTenSongs() - To get the top ten songs queried by the user of the Android application
 * getTopTenArtists() - To get the top ten artists queried by the user of the Android application
 * getAverageLatency() - To get the average latency between the request and response from
 * (a) the Android app; (b) the third party API
 * getTopFivePhones() - To get the top five phones that send a request to the server
 * <p>
 * Link to 3rd Party API Documentation: https://api.vagalume.com.br/docs/letras/
 * Link to dashboard: https://still-reaches-60603.herokuapp.com/
 */

// Defines the package for the Java file
package ds.project4.webservice;

// Imports required for querying MongoDB database collections to get operations analytics.

import com.mongodb.client.MongoCollection;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.bson.Document;
import org.json.JSONObject;
import java.io.IOException;

public class LyricsFinderDashboardModel {

    // Create an instance of the mongoDB model of the server
    LyricsFinderMongoDBModel lyricsFinderMongoDBModel = new LyricsFinderMongoDBModel();

    /**
     * Fetches operations analytics data using MongoDB queries
     *
     * @param mobileCollection Represents the Mobile collection MongoDB instance
     * @param apiCollection    Represents the API collection MongoDB instance
     * @param request          Represents the request from HttpServletRequest
     * @param response         Represents the response from HttpServletResponse
     */
    public void buildDashboard(MongoCollection<Document> mobileCollection, MongoCollection<Document> apiCollection, HttpServletRequest request, HttpServletResponse response) {
        // Get all mobile logs from the MOBILE collection in the MongoDB database
        JSONObject mobileLogs = lyricsFinderMongoDBModel.getMobileData(mobileCollection);

        // Set mobileLogs attribute for dashboard.jsp file
        request.setAttribute("mobileLogs", mobileLogs);

        // Get all api logs from the API collection in the MongoDB database
        JSONObject apiLogs = lyricsFinderMongoDBModel.getAPIData(apiCollection);

        // Set apiLogs attribute for dashboard.jsp file
        request.setAttribute("apiLogs", apiLogs);

        // Get the top ten songs queried by the mobile application from
        // the MOBILE collection in the MongoDB database
        JSONObject topTenSongs = lyricsFinderMongoDBModel.getTopTenSongs(mobileCollection);

        // Set topTenSongs attribute for dashboard.jsp file
        request.setAttribute("topTenSongs", topTenSongs);

        // Get the top ten artists queried by the mobile application from
        // the MOBILE collection in the MongoDB database
        JSONObject topTenArtists = lyricsFinderMongoDBModel.getTopTenArtists(mobileCollection);

        // Set topTenArtists attribute for dashboard.jsp file
        request.setAttribute("topTenArtists", topTenArtists);

        // Get the average latency of the request-response interactions from the Android app
        double avgLatencyAndroidApp = lyricsFinderMongoDBModel.getAverageLatency(mobileCollection);

        // Set avgLatencyAndroidApp attribute for dashboard.jsp file
        request.setAttribute("avgLatencyAndroidApp", avgLatencyAndroidApp);

        // Get the average latency of the request-response interactions from the 3rd party API
        double avgLatencyAPI = lyricsFinderMongoDBModel.getAverageLatency(apiCollection);

        // Set avgLatencyAPI attribute for dashboard.jsp file
        request.setAttribute("avgLatencyAPI", avgLatencyAPI);

        // Get the top five phones that were used to send requests to the server using
        // the MOBILE collection in the MongoDB database
        JSONObject topFivePhones = lyricsFinderMongoDBModel.getTopFivePhones(mobileCollection);

        // Set topFivePhones attribute for dashboard.jsp file
        request.setAttribute("topFivePhones", topFivePhones);

        // Transfers control to the view "dashboard.jsp"
        RequestDispatcher view = request.getRequestDispatcher("dashboard.jsp");

        // Forwards HttpServletRequest request, HttpServletResponse response to the View
        try {
            view.forward(request, response);
        } catch (ServletException | IOException e) {
            throw new RuntimeException(e);
        }

    }
}
