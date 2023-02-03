/**
 * Authors: Shivam Patel and Gautam Naik
 * Andrew IDs: shpatel, gnaik
 * Emails: shpatel@cmu.edu, gnaik@andrew.cmu.edu
 * Last Modified: November 21, 2022
 * File: LyricsFinderAndroidModel.java
 * Part Of: Project4WebService
 * <p>
 * This Java file acts as the Model for the web server.
 * verifySongName() - To verify the song name entered by the user of the Android application
 * verifyArtistName() - To verify the artist name entered by the user of the Android application
 * <p>
 * Link to 3rd Party API Documentation: https://api.vagalume.com.br/docs/letras/
 * Link to dashboard: https://still-reaches-60603.herokuapp.com/
 */

// Defines the package for the Java file
package ds.project4.webservice;

public class LyricsFinderAndroidModel {
    /**
     * Function to verify the song name entered by the user of the Android application
     * @param name Song name to be verified
     * @return Boolean value stating if the song name verification was successful or not
     */
    public boolean verifySongName(String name) {

        // Loop over every character in the name
        for (int i = 0; i < name.length(); i++) {

            // If the character is not a letter, digit, ' ', '!', '&', '(', ')', single quote, double quotes,
            // it is an invalid character for song name

            if (!Character.isLetterOrDigit(name.charAt(i))
                    && name.charAt(i) != ' '
                    && name.charAt(i) != '!'
                    && name.charAt(i) != '&'
                    && name.charAt(i) != '('
                    && name.charAt(i) != ')'
                    && name.charAt(i) != '\''
                    && name.charAt(i) != '\"') {

                // Song name is invalid
                return false;
            }
        }

        // Song name is valid
        return true;
    }

    /**
     * Function to verify the artist name entered by the user of the Android application
     * @param name Artist name to be verified
     * @return Boolean value stating if the artist name verification was successful or not
     */
    public boolean verifyArtistName(String name) {

        // Loop over every character in the name
        for (int i = 0; i < name.length(); i++) {

            // If the character is not a letter, digit, space or a single apostrophe,
            // it is an invalid character for artist name

            if (!Character.isLetterOrDigit(name.charAt(i))
                    && name.charAt(i) != ' '
                    && name.charAt(i) != '\'') {

                // Artist name is invalid
                return false;
            }
        }
        // Artist name is valid
        return true;
    }
}
