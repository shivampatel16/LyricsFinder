/**
 * Authors: Shivam Patel and Gautam Naik
 * Andrew IDs: shpatel, gnaik
 * Emails: shpatel@cmu.edu, gnaik@andrew.cmu.edu
 * Last Modified: November 21, 2022
 * File: LyricsFinderMongoDBModel.java
 * Part Of: Project4WebService
 * <p>
 * This Java file acts as the Model for the web server's interactions with MongoDB database collections.
 * The model defines the following functions:
 * connectToMongoDB() - To connect to the MongoDB database
 * mobileInsertOne() - To insert one document (of logs collected by the request and response
 * to the Android App) into the MOBILE collection in the MongoDB database
 * apiInsertOne() - To insert one document (of logs collected by the request and response
 * by the third party API) into the API collection in the MongoDB database
 * getMobileData() - To get all the logs stored in the MOBILE collection in the MongoDB database
 * getAPIData() - To get all the logs stored in the API collection in the MongoDB database
 * getTopTenSongs() - To get the top ten songs queried by the user of the Android application
 * getTopTenArtists() - To get the top ten artists queried by the user of the Android application
 * getAverageLatency() - To get the average latency between the request and response from
 * (a) the Android app; (b) the third party API
 * getTopFivePhones() - To get the top five phones that send a request to the server
 * calculateLatency() - To find the time taken in milliseconds between the request and response
 * time stamps.
 * <p>
 * Link to 3rd Party API Documentation: https://api.vagalume.com.br/docs/letras/
 * Link to dashboard: https://still-reaches-60603.herokuapp.com/
 */

// Defines the package for the Java file
package ds.project4.webservice;

// Imports required for reading data from and writing data to MongoDB database collections.

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.ServerApi;
import com.mongodb.ServerApiVersion;
import com.mongodb.client.*;
import com.mongodb.client.model.Accumulators;
import com.mongodb.client.model.Sorts;
import org.bson.Document;
import org.json.JSONObject;
import org.slf4j.LoggerFactory;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.TimeZone;
import static com.mongodb.client.model.Aggregates.*;
import static com.mongodb.client.model.Projections.exclude;
import static com.mongodb.client.model.Projections.fields;

public class LyricsFinderMongoDBModel {
    /**
     * Function to connect to the MongoDB database
     * @return Instance of MongoClient that was created in the MongoDB database
     */
    public MongoClient connectToMongoDB() {
        ((LoggerContext) LoggerFactory.getILoggerFactory()).getLogger("org.mongodb.driver").setLevel(Level.ERROR);
        ConnectionString connectionString = new ConnectionString("mongodb://ShivamGautamDSProject4:ShivamGautamDSProject4@ac-d1nwlm4-shard-00-00.tsumdav.mongodb.net:27017, ac-d1nwlm4-shard-00-01.tsumdav.mongodb.net:27017,ac-d1nwlm4-shard-00-02.tsumdav.mongodb.net:27017/?w=majority&retryWrites=true&tls=true&authMechanism=SCRAM-SHA-1");

        // Defining the default settings for the MongoClient
        MongoClientSettings settings = MongoClientSettings.builder()
                .applyConnectionString(connectionString)
                .serverApi(ServerApi.builder()
                        .version(ServerApiVersion.V1)
                        .build())
                .build();

        // Create the MongoClient with the stated settings
        MongoClient mongoClient = MongoClients.create(settings);

        // Instance of the MongoClient that was created
        return mongoClient;
    }

    /**
     * Function to insert one document (of logs collected by the request and response
     * to the Android App) into the MOBILE collection in the MongoDB database
     * @param mobile Object of Mobile class containing the logs to be entered to the MOBILE collection
     * @param mobileCollection MongoDB's collection into which the logs are to be stored
     */
    public void mobileInsertOne(Mobile mobile, MongoCollection<Document> mobileCollection) {
        // Define a new MongoDB Document into which the data is to be stored
        Document mobileDoc = new Document();

        // Append lyrics, mobile brand, mobile model, latency, status code, request artist name,
        // request song name, request time stamp, response time stamp, response artist name and
        // response song name to the newly created document for MOBILE collection
        mobileDoc.append("lyrics", mobile.lyrics);
        mobileDoc.append("mobile_brand", mobile.mobile_brand);
        mobileDoc.append("mobile_model", mobile.mobile_model);
        mobileDoc.append("latency", mobile.latency);
        mobileDoc.append("status_code", mobile.status_code);
        mobileDoc.append("request_artist_name", mobile.request_artist_name);
        mobileDoc.append("request_song_name", mobile.request_song_name);
        mobileDoc.append("request_timestamp", mobile.request_timestamp);
        mobileDoc.append("response_timestamp", mobile.response_timestamp);
        mobileDoc.append("response_artist_name", mobile.response_artist_name);
        mobileDoc.append("response_song_name", mobile.response_song_name);

        // Insert the document into the MOBILE collection
        mobileCollection.insertOne(mobileDoc);
    }


    /**
     * Function to insert one document (of logs collected by the request and response
     * by the third party API) into the API collection in the MongoDB database
     * @param api Object of API class containing the logs to be entered to the API collection
     * @param apiCollection MongoDB's collection into which the logs are to be stored
     */
    public void apiInsertOne(API api, MongoCollection<Document> apiCollection) {
        // Define a new MongoDB Document into which the data is to be stored
        Document apiDoc = new Document();

        // Append lyrics, latency, status code, request artist name, request song name,
        // request time stamp, response time stamp, response artist name and
        // response song name to the newly created document for API collection
        apiDoc.append("lyrics", api.lyrics);
        apiDoc.append("request_artist_name", api.request_artist_name);
        apiDoc.append("request_song_name", api.request_song_name);
        apiDoc.append("request_timestamp", api.request_timestamp);
        apiDoc.append("status_code", api.status_code);
        apiDoc.append("response_timestamp", api.response_timestamp);
        apiDoc.append("response_artist_name", api.response_artist_name);
        apiDoc.append("response_song_name", api.response_song_name);
        apiDoc.append("latency", api.latency);

        // Insert the document into the API collection
        apiCollection.insertOne(apiDoc);
    }

    /**
     * Function to get all the logs stored in the MOBILE collection in the MongoDB database
     * @param mobileCollection MOBILE collection in the MongoDB database
     * @return JSONObject of all the logs in the MOBILE collection of the MongoDB database
     */
    public JSONObject getMobileData(MongoCollection<Document> mobileCollection) {

        // Find all the documents stored in the MOBILE collection in the MongoDB database
        FindIterable<Document> mobileDocs = mobileCollection.find();

        // Create a JSONObject to store all the logs in the MOBILE collection of the MongoDB database
        JSONObject allData = new JSONObject();
        int i = 1;

        // Iterate over each document in the MOBILE collection
        for (Document document : mobileDocs) {

            // Create a JSONObject to store the log of one document in the MOBILE collection
            JSONObject documentObject = new JSONObject();

            // Add lyrics, mobile brand, mobile model, latency, status code, request artist name,
            // request song name, request time stamp, response time stamp, response artist name and
            // response song name to documentObject (JSONObject)
            documentObject.put("lyrics", document.getString("lyrics"));
            documentObject.put("mobile_brand", document.getString("mobile_brand"));
            documentObject.put("mobile_model", document.getString("mobile_model"));
            documentObject.put("latency", document.getDouble("latency"));
            documentObject.put("status_code", document.getInteger("status_code"));
            documentObject.put("request_artist_name", document.getString("request_artist_name"));
            documentObject.put("request_song_name", document.getString("request_song_name"));
            documentObject.put("request_timestamp", document.getString("request_timestamp"));
            documentObject.put("response_timestamp", document.getString("response_timestamp"));
            documentObject.put("response_artist_name", document.getString("response_artist_name"));
            documentObject.put("response_song_name", document.getString("response_song_name"));

            // Add documentObject (JSONObject) to allData (JSONObject)
            allData.put(String.valueOf(i), documentObject);
            i++;
        }

        // Return JSONObject of all logs in the MOBILE collection
        return allData;
    }

    /**
     * Function to get all the logs stored in the API collection in the MongoDB database
     * @param apiCollection API collection in the MongoDB database
     * @return JSONObject of all the logs in the API collection of the MongoDB database
     */
    public JSONObject getAPIData(MongoCollection<Document> apiCollection) {

        // Find all the documents stored in the API collection in the MongoDB database
        FindIterable<Document> apiDocs = apiCollection.find();

        // Create a JSONObject to store all the logs in the API collection of the MongoDB database
        JSONObject allData = new JSONObject();
        int i = 1;

        // Iterate over each document in the API collection
        for (Document document : apiDocs) {

            // Create a JSONObject to store the log of one document in the API collection
            JSONObject documentObject = new JSONObject();

            // Add lyrics, latency, status code, request artist name, request song name,
            // request time stamp, response time stamp, response artist name and
            // response song name to documentObject (JSONObject)
            documentObject.put("lyrics", document.getString("lyrics"));
            documentObject.put("request_timestamp", document.getString("request_timestamp"));
            documentObject.put("response_timestamp", document.getString("response_timestamp"));
            documentObject.put("response_artist_name", document.getString("response_artist_name"));
            documentObject.put("response_song_name", document.getString("response_song_name"));
            documentObject.put("latency", document.getDouble("latency"));
            documentObject.put("status_code", document.getInteger("status_code"));
            documentObject.put("request_artist_name", document.getString("request_artist_name"));
            documentObject.put("request_song_name", document.getString("request_song_name"));

            // Add documentObject (JSONObject) to allData (JSONObject)
            allData.put(String.valueOf(i), documentObject);
            i++;
        }
        // Return JSONObject of all logs in the API collection
        return allData;
    }

    /**
     * Function to get the top ten songs queried by the user of the Android application
     * @param mobileCollection MOBILE collection in the MongoDB database
     * @return JSONObject of the top ten songs queried by the user of the Android application
     */
    public JSONObject getTopTenSongs(MongoCollection<Document> mobileCollection) {

        // MongoDB query to get the top ten songs queried by the user of the Android application
        // along with their count and the songs being sorted by the descending order of the count.
        // The query returns the documents for the top ten songs.

        // Source: https://www.baeldung.com/java-mongodb-aggregations
        AggregateIterable<Document> topTenSongsDocs = mobileCollection.aggregate(Arrays.asList(match(new Document("response_song_name",
                new Document("$nin", Arrays.asList("Invalid input", "Authentication unsuccessful", "API not available", "Data not found", "Empty input")))),
                group("$response_song_name", Accumulators.sum("count", 1)),
                sort(Sorts.descending("count")), limit(10)));

        // Create a JSONObject to store the top ten songs
        JSONObject allData = new JSONObject();
        int i = 1;

        // Iterate over each document for the top 10 songs
        for (Document document : topTenSongsDocs) {

            // Create a JSONObject to store data related to one of the top 10 songs
            JSONObject documentObject = new JSONObject();

            // Add song name and number of searches to documentObject (JSONObject)
            documentObject.put("song_name", document.getString("_id"));
            documentObject.put("number_of_searches", document.getInteger("count").toString());

            // Add documentObject (JSONObject) to allData (JSONObject)
            allData.put(String.valueOf(i), documentObject);
            i++;
        }
        // Return JSONObject of top 10 songs queried by the user of the Android application
        return allData;
    }

    /**
     * Function to get the top ten artists queried by the user of the Android application
     * @param mobileCollection MOBILE collection in the MongoDB database
     * @return JSONObject of the top ten artists queried by the user of the Android application
     */
    public JSONObject getTopTenArtists(MongoCollection<Document> mobileCollection) {

        // MongoDB query to get the top ten artists queried by the user of the Android application
        // along with their count and the artists being sorted by the descending order of the count.
        // The query returns the documents for the top ten artists.

        // Source: https://www.baeldung.com/java-mongodb-aggregations
        AggregateIterable<Document> topTenArtistsDocs = mobileCollection.aggregate(Arrays.asList(match(new Document("response_artist_name",
                        new Document("$nin", Arrays.asList("Invalid input", "Authentication unsuccessful", "API not available", "Data not found", "Empty input")))),
                group("$response_artist_name", Accumulators.sum("count", 1)),
                sort(Sorts.descending("count")), limit(10)));

        // Create a JSONObject to store the top ten artists
        JSONObject allData = new JSONObject();
        int i = 1;

        // Iterate over each document for the top 10 artists
        for (Document document : topTenArtistsDocs) {

            // Create a JSONObject to store data related to one of the top 10 artists
            JSONObject documentObject = new JSONObject();

            // Add artist name and number of searches to documentObject (JSONObject)
            documentObject.put("artist_name", document.getString("_id"));
            documentObject.put("number_of_searches", document.getInteger("count").toString());

            // Add documentObject (JSONObject) to allData (JSONObject)
            allData.put(String.valueOf(i), documentObject);
            i++;
        }
        // Return JSONObject of top 10 artist names queried by the user of the Android application
        return allData;
    }

    /**
     * Function to get the average latency between the request and response from
     * (a) the Android app; (b) the third party API
     * @param collection MongoDB collection (MOBILE or API) for which the average latency is to be found
     * @return Average latency between the request and response for the requested collection
     */
    public double getAverageLatency(MongoCollection<Document> collection) {

        // MongoDB query to get the average latency between the request and response for
        // the requested collection. The query returns an AggregateIterable Document
        // with the average latency.

        // Source: https://www.baeldung.com/java-mongodb-aggregations
        AggregateIterable<Document> averageLatencyDoc = collection.aggregate(Arrays.asList(match(new Document("latency", new Document("$not", new Document("$eq", 0)))), group(null, Accumulators.avg("latency", "$latency")), project(fields(exclude("_id")))));
        double avgLatency = 0.0;

        // Loop over the document and get average latency
        for (Document document : averageLatencyDoc) {
            avgLatency = document.getDouble("latency");
        }

        // Return average latency
        return avgLatency;
    }

    /**
     * Function to get the top five phones that were used to send a request to the server
     * @param mobileCollection MOBILE collection in the MongoDB database
     * @return JSONObject of the top five phones that were used to send a request to the server
     */
    public JSONObject getTopFivePhones(MongoCollection<Document> mobileCollection) {

        // MongoDB query to get the top five phones that were used to send a request to the server.
        // The query returns the documents of the top five phones that were used to send the request.

        // Source: https://www.baeldung.com/java-mongodb-aggregations
        // Java query generated from MongoDB query by Atlas

        AggregateIterable<Document> topFivePhonesDocs = mobileCollection.aggregate(Arrays.asList(new Document("$group",
                        new Document("_id",
                                new Document("brand", "$mobile_brand")
                                        .append("model", "$mobile_model"))
                                .append("count",
                                        new Document("$sum", 1))),
                new Document("$sort",
                        new Document("count", -1)),
                new Document("$project",
                        new Document("brand", "$_id.brand")
                                .append("model", "$_id.model")
                                .append("_id", 0)
                                .append("count", 1)),
                new Document("$limit", 5)));

        // Create a JSONObject to store the top five phones
        JSONObject allData = new JSONObject();
        int i = 1;

        // Iterate over each document returned by the query
        for (Document document : topFivePhonesDocs) {

            // Create a JSONObject to store data related to one of the top 5 phones
            JSONObject documentObject = new JSONObject();

            // Add mobile brand, mobile model and number of requests to documentObject (JSONObject)
            documentObject.put("mobile_brand", document.getString("brand"));
            documentObject.put("mobile_model", document.getString("model"));
            documentObject.put("number_of_requests", document.getInteger("count").toString());

            // Add documentObject (JSONObject) to allData (JSONObject)
            allData.put(String.valueOf(i), documentObject);
            i++;
        }

        // Return JSONObject of the top 5 phones that were used to send the request.
        return allData;
    }

    /**
     * Function to find the time taken in milliseconds between the request and response
     * time stamps.
     * @param request_timestamp Timestamp of the request
     * @param response_timestamp Timestamp of the response
     * @return Latency - time taken in milliseconds between the request and response time stamps
     */
    public double calculateLatency(String request_timestamp, String response_timestamp) {

        // Source for formatting dates: https://www.baeldung.com/java-simple-date-format
        // Define time zone
        TimeZone tz = TimeZone.getTimeZone("UTC");

        // Set date format
        SimpleDateFormat sdf
                = new SimpleDateFormat(
                "dd-MM-yyyy HH:mm:ss.SSS");

        // Set time zone
        sdf.setTimeZone(tz);

        // Stores the value of latency
        double latency;

        // Calculate latency between the request and response time stamps
        try {
            latency = sdf.parse(response_timestamp).getTime() - sdf.parse(request_timestamp).getTime();
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        // Return latency
        return latency;
    }
}
