<%--
  Authors: Shivam Patel and Gautam Naik
  Andrew IDs: shpatel, gnaik
  Emails: shpatel@cmu.edu, gnaik@andrew.cmu.edu
  Last Modified: November 21, 2022
  File: dashboard.jsp
  Part Of: Project4WebService

  This file defines the HTML content, related CSS and Javascript for the web based
  dashboard for this application. The dashboard, in general, shows the logs stored
  in the MongoDB database for all the API and mobile app interactions (request and
  response). Along with the above, it shows the operations analytics generated from
  the logs.

  The HTML page of the dashboard has three tabs - Android App Logs, API logs and
  Operation Analytics. Android App Logs and API Logs show the following in a
  tabular form:

  Android App Logs:
        - Sr. No.
        - Mobile Brand
        - Mobile Model
        - Request Artist Name
        - Request Song Name
        - Request Time Stamp
        - Response Artist Name
        - Response Song Name
        - Response Time Stamp
        - Status Code
        - Latency (in milliseconds)
        - Lyrics

 API App Logs:
        - Sr. No.
        - Request Artist Name
        - Request Song Name
        - Request Time Stamp
        - Response Artist Name
        - Response Song Name
        - Response Time Stamp
        - Status Code
        - Latency (in milliseconds)
        - Lyrics

 The following operation analytics have been calculated:
        - Top 10 songs searched in LyricsFinder
        - Top 10 artists searched in LyricsFinder
        - Average Latency - Android App
        - Average Latency - API
        - Top 5 Android phone models using LyricsFinder
--%>

<%@ page import="org.json.JSONObject" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
    <head>
        <meta name="viewport" content="width=device-width, initial-scale=1">
        <style>
            body {
                font-family: Arial;
            }

            /* Source for HTML tabs, related CSS style and related JavaScript: https://www.w3schools.com/howto/howto_js_tabs.asp */
            /* Style the tab */
            .tab {
                overflow: hidden;
                border: 1px solid #ccc;
                background-color: #f1f1f1;
            }

            /* Style the buttons inside the tab */
            .tab button {
                background-color: inherit;
                float: left;
                border: none;
                outline: none;
                cursor: pointer;
                padding: 14px 16px;
                transition: 0.3s;
                font-size: 17px;
            }

            /* Change background color of buttons on hover */
            .tab button:hover {
                background-color: #ddd;
            }

            /* Create an active/current tablink class */
            .tab button.active {
                background-color: #ccc;
            }

            /* Style the tab content */
            .tabcontent {
                display: none;
                padding: 6px 12px;
                border: 1px solid #ccc;
                border-top: none;
            }

            /* Source for formatting table:
               https://stackoverflow.com/questions/19794211/horizontal-scroll-on-overflow-of-table
             */
            table {
                font-family: arial, sans-serif;
                border-collapse: collapse;
                display: block;
                margin: 0 auto;
                overflow-x: auto;
                table-layout: fixed;
            }

            /* Source for fixing width of nth child:
               https://www.geeksforgeeks.org/how-to-set-fixed-width-for-td-in-a-table/
             */
            .mobileLogsTable td:nth-child(1) {
                min-width: 60px;
            }

            .mobileLogsTable td:nth-child(2) {
                   min-width: 120px;
            }

            .mobileLogsTable td:nth-child(3) {
                min-width: 180px;
            }

            .mobileLogsTable td:nth-child(4) {
                min-width: 170px;
            }

            .mobileLogsTable td:nth-child(5) {
                min-width: 170px;
            }

            .mobileLogsTable td:nth-child(6) {
                min-width: 200px;
            }

            .mobileLogsTable td:nth-child(7) {
                min-width: 180px;
            }

            .mobileLogsTable td:nth-child(8) {
                min-width: 170px;
            }

            .mobileLogsTable td:nth-child(9) {
                min-width: 200px;
            }

            .mobileLogsTable td:nth-child(10) {
                min-width: 120px;
            }

            .mobileLogsTable td:nth-child(11) {
                min-width: 120px;
            }

            .mobileLogsTable td:nth-child(12) {
                min-width: 500px;
            }

            .apiLogsTable td:nth-child(1) {
                min-width: 60px;
            }

            .apiLogsTable td:nth-child(2) {
                min-width: 170px;
            }

            .apiLogsTable td:nth-child(3) {
                min-width: 170px;
            }

            .apiLogsTable td:nth-child(4) {
                min-width: 200px;
            }

            .apiLogsTable td:nth-child(5) {
                min-width: 180px;
            }

            .apiLogsTable td:nth-child(6) {
                min-width: 170px;
            }

            .apiLogsTable td:nth-child(7) {
                min-width: 200px;
            }

            .apiLogsTable td:nth-child(8) {
                min-width: 120px;
            }

            .apiLogsTable td:nth-child(9) {
                min-width: 120px;
            }

            .apiLogsTable td:nth-child(10) {
                min-width: 500px;
            }

            /* Style the td and th in table */
            td, th {
                border: 1px solid #dddddd;
                text-align: left;
                padding: 8px;
                word-wrap:break-word;
                vertical-align:top;
            }

            /* Source for coloring table rows with background color:
               https://www.w3schools.com/html/html_table_styling.asp
            */
            tr:nth-child(even) {
                background-color: #FFFFF0;
            }

            tr:nth-child(odd) {
                background-color: #FFF8DC;
            }
        </style>
    </head>

    <body>

        <!-- Define three tabs in the HTML page (for Android Logs, API logs, and Operation Analytics) -->
        <!-- Source for HTML tabs, related CSS style and related JavaScript: https://www.w3schools.com/howto/howto_js_tabs.asp -->
        <div class="tab">
            <button class="tablinks" onclick="openDashboard(event, 'android')" id="defaultOpen">Android App Logs</button>
            <button class="tablinks" onclick="openDashboard(event, 'api')">API Logs</button>
            <button class="tablinks" onclick="openDashboard(event, 'analytics')">Operation Analytics</button>
        </div>

        <!-- Design the layout of the Android App Logs tab -->
        <div id="android" class="tabcontent">

            <!-- Defining header for the tab -->
            <h1 style="color: #B22222">Android App Logs</h1>

            <!-- Design table for the logs from the request-response interactions from the Android App -->
            <!-- Source for HTML tables and related CSS style: https://www.w3schools.com/html/html_tables.asp -->
            <table class="mobileLogsTable">
                <!-- Define the headers for the table -->
                <tr style="background-color: lightgrey">
                    <th>Sr. No.</th>
                    <th>Mobile Brand</th>
                    <th>Mobile Model</th>
                    <th>Request Artist Name</th>
                    <th>Request Song Name</th>
                    <th>Request Timestamp</th>
                    <th>Response Artist Name</th>
                    <th>Response Song Name</th>
                    <th>Response Timestamp</th>
                    <th>Status Code</th>
                    <th>Latency (ms)</th>
                    <th>Lyrics</th>
                </tr>

                <!-- Using JSP, dynamically add the content of the table -->
                <% for (int i = 1; i <= ((JSONObject) request.getAttribute("mobileLogs")).length(); i++) { %>
                <tr>
                    <td><%= i %></td>
                    <td><%= ((JSONObject) (((JSONObject) request.getAttribute("mobileLogs")).get(String.valueOf(i)))).get("mobile_brand")%></td>
                    <td><%= ((JSONObject) (((JSONObject) request.getAttribute("mobileLogs")).get(String.valueOf(i)))).get("mobile_model")%></td>
                    <td><%= ((JSONObject) (((JSONObject) request.getAttribute("mobileLogs")).get(String.valueOf(i)))).get("request_artist_name")%></td>
                    <td><%= ((JSONObject) (((JSONObject) request.getAttribute("mobileLogs")).get(String.valueOf(i)))).get("request_song_name")%></td>
                    <td><%= ((JSONObject) (((JSONObject) request.getAttribute("mobileLogs")).get(String.valueOf(i)))).get("request_timestamp")%></td>
                    <td><%= ((JSONObject) (((JSONObject) request.getAttribute("mobileLogs")).get(String.valueOf(i)))).get("response_artist_name")%></td>
                    <td><%= ((JSONObject) (((JSONObject) request.getAttribute("mobileLogs")).get(String.valueOf(i)))).get("response_song_name")%></td>
                    <td><%= ((JSONObject) (((JSONObject) request.getAttribute("mobileLogs")).get(String.valueOf(i)))).get("response_timestamp")%></td>
                    <td><%= ((JSONObject) (((JSONObject) request.getAttribute("mobileLogs")).get(String.valueOf(i)))).get("status_code")%></td>
                    <td><%= ((JSONObject) (((JSONObject) request.getAttribute("mobileLogs")).get(String.valueOf(i)))).get("latency")%></td>
                    <td><%= ((JSONObject) (((JSONObject) request.getAttribute("mobileLogs")).get(String.valueOf(i)))).get("lyrics").toString().replace("\n", "<br>")%></td>
                </tr>
                <% } %>
            </table>
        </div>

        <!-- Design the layout of the API Logs tab -->
        <div id="api" class="tabcontent">

            <!-- Defining header for the tab -->
            <h1 style="color: #B22222">API Logs</h1>

            <!-- Design table for the logs from the request-response interactions from the API -->
            <!-- Source for HTML tables and related CSS style: https://www.w3schools.com/html/html_tables.asp -->
            <table class="apiLogsTable">
                <!-- Define the headers for the table -->
                <tr style="background-color: lightgrey">
                    <th>Sr. No.</th>
                    <th>Request Artist Name</th>
                    <th>Request Song Name</th>
                    <th>Request Timestamp</th>
                    <th>Response Artist Name</th>
                    <th>Response Song Name</th>
                    <th>Response Timestamp</th>
                    <th>Status Code</th>
                    <th>Latency (ms)</th>
                    <th>Lyrics</th>
                </tr>

                <!-- Using JSP, dynamically add the content of the table -->
                <% for (int i = 1; i <= ((JSONObject) request.getAttribute("apiLogs")).length(); i++) { %>
                <tr>
                    <td><%= i %></td>
                    <td><%= ((JSONObject) (((JSONObject) request.getAttribute("apiLogs")).get(String.valueOf(i)))).get("request_artist_name")%></td>
                    <td><%= ((JSONObject) (((JSONObject) request.getAttribute("apiLogs")).get(String.valueOf(i)))).get("request_song_name")%></td>
                    <td><%= ((JSONObject) (((JSONObject) request.getAttribute("apiLogs")).get(String.valueOf(i)))).get("request_timestamp")%></td>
                    <td><%= ((JSONObject) (((JSONObject) request.getAttribute("apiLogs")).get(String.valueOf(i)))).get("response_artist_name")%></td>
                    <td><%= ((JSONObject) (((JSONObject) request.getAttribute("apiLogs")).get(String.valueOf(i)))).get("response_song_name")%></td>
                    <td><%= ((JSONObject) (((JSONObject) request.getAttribute("apiLogs")).get(String.valueOf(i)))).get("response_timestamp")%></td>
                    <td><%= ((JSONObject) (((JSONObject) request.getAttribute("apiLogs")).get(String.valueOf(i)))).get("status_code")%></td>
                    <td><%= ((JSONObject) (((JSONObject) request.getAttribute("apiLogs")).get(String.valueOf(i)))).get("latency")%></td>
                    <td><%= ((JSONObject) (((JSONObject) request.getAttribute("apiLogs")).get(String.valueOf(i)))).get("lyrics").toString().replace("\n", "<br>")%></td>
                </tr>
                <% } %>
            </table>
        </div>

        <!-- Design the layout of the Operation Analytics tab -->
        <div id="analytics" class="tabcontent">

            <!-- Defining header for the tab -->
            <h1 style="color: #B22222">Operation Analytics</h1>
            <h2 style="color: steelblue"><span style="color: black">Operation Analytics 1:</span> Top 10 songs searched in LyricsFinder</h2>

            <!-- Design table for the top ten songs -->
            <!-- Source for HTML tables and related CSS style: https://www.w3schools.com/html/html_tables.asp -->
            <table class="topTenSongsTable">
                <!-- Define the headers for the table -->
                <tr style="background-color: lightgrey">
                    <th>Rank</th>
                    <th>Song Name</th>
                    <th>Number of Searches</th>
                </tr>

                <!-- Using JSP, dynamically add the content of the table -->
                <% for (int i = 1; i <= ((JSONObject) request.getAttribute("topTenSongs")).length(); i++) { %>
                <tr>
                    <td><%= i %></td>
                    <td><%= ((JSONObject) (((JSONObject) request.getAttribute("topTenSongs")).get(String.valueOf(i)))).get("song_name")%></td>
                    <td><%= ((JSONObject) (((JSONObject) request.getAttribute("topTenSongs")).get(String.valueOf(i)))).get("number_of_searches")%></td>
                </tr>
                <% } %>
            </table>

            <br>
            <h2 style="color: steelblue"><span style="color: black">Operation Analytics 2:</span> Top 10 artists searched in LyricsFinder</h2>

            <!-- Design table for the top ten artists -->
            <table class="topTenArtistsTable">
                <!-- Define the headers for the table -->
                <tr style="background-color: lightgrey">
                    <th>Rank</th>
                    <th>Artist Name</th>
                    <th>Number of Searches</th>
                </tr>

                <!-- Using JSP, dynamically add the content of the table -->
                <% for (int i = 1; i <= ((JSONObject) request.getAttribute("topTenArtists")).length(); i++) { %>
                <tr>
                    <td><%= i %></td>
                    <td><%= ((JSONObject) (((JSONObject) request.getAttribute("topTenArtists")).get(String.valueOf(i)))).get("artist_name")%></td>
                    <td><%= ((JSONObject) (((JSONObject) request.getAttribute("topTenArtists")).get(String.valueOf(i)))).get("number_of_searches")%></td>
                </tr>
                <% } %>
            </table>

            <!-- Using JSP, dynamically add the content of the average latency of Android App interactions -->
            <br>
            <h2 style="color: steelblue"><span style="color: black">Operation Analytics 3:</span> Average Latency - Android App</h2>
            <p style="font-size: 18px">Average search latency for Android App = <b><%= String.format("%.2f", (double) request.getAttribute("avgLatencyAndroidApp")) %> milliseconds</b></p>

            <!-- Using JSP, dynamically add the content of the average latency of API interactions -->
            <br>
            <h2 style="color: steelblue"><span style="color: black">Operation Analytics 4:</span> Average Latency - API</h2>
            <p style="font-size: 18px">Average search latency for Vagalume API (3rd Party API) = <b><%= String.format("%.2f", (double) request.getAttribute("avgLatencyAPI")) %> milliseconds</b></p>

            <br>
            <h2 style="color: steelblue"><span style="color: black">Operation Analytics 5:</span> Top 5 Android phone models using LyricsFinder</h2>

            <!-- Design table for the top five phones-->
            <table class="topFivePhonesTable">
                <!-- Define the headers for the table -->
                <tr style="background-color: lightgrey">
                    <th>Rank</th>
                    <th>Mobile Brand</th>
                    <th>Mobile Model</th>
                    <th>Number of Requests</th>
                </tr>

                <!-- Using JSP, dynamically add the content of the table -->
                <% for (int i = 1; i <= ((JSONObject) request.getAttribute("topFivePhones")).length(); i++) { %>
                <tr>
                    <td><%= i %></td>
                    <td><%= ((JSONObject) (((JSONObject) request.getAttribute("topFivePhones")).get(String.valueOf(i)))).get("mobile_brand")%></td>
                    <td><%= ((JSONObject) (((JSONObject) request.getAttribute("topFivePhones")).get(String.valueOf(i)))).get("mobile_model")%></td>
                    <td><%= ((JSONObject) (((JSONObject) request.getAttribute("topFivePhones")).get(String.valueOf(i)))).get("number_of_requests")%></td>
                </tr>
                <% } %>
            </table>
        </div>

        <!-- Defines Javascript for the HTML page -->
        <!-- Source for HTML tabs, related CSS style and related JavaScript: https://www.w3schools.com/howto/howto_js_tabs.asp -->
        <script>
            // Display different contents when the tab links are pressed
            function openDashboard(evt, tab) {
                var i, tabcontent, tablinks;

                // Get the tab content
                tabcontent = document.getElementsByClassName("tabcontent");
                for (i = 0; i < tabcontent.length; i++) {
                    tabcontent[i].style.display = "none";
                }

                // Get the tab link
                tablinks = document.getElementsByClassName("tablinks");
                for (i = 0; i < tablinks.length; i++) {
                    tablinks[i].className = tablinks[i].className.replace(" active", "");
                }

                // Display the block related to the tab
                document.getElementById(tab).style.display = "block";
                evt.currentTarget.className += " active";
            }

            // Get the element with id="defaultOpen" and click on it
            document.getElementById("defaultOpen").click();

        </script>
    </body>
</html>
