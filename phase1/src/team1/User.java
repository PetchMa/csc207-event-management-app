package team1;

import java.util.ArrayList;
import java.util.List;

public class User {
    ///
    /**
     * The users of the system
     */
    // === Instance Variables ===
    private String username;
    private String password;
    private String userEmail;
    private boolean loggedIn;
    // Events the user has created. I'm pretty sure it's fine to be private
    private List<String> userEvents;
    // List of events that the user will attend. The event can be their own or another user's and must be published.
    private List<String> attendEvents;
    // needs to be public so that it can be used in the constructor and the UserManager class can access it.
    public enum UserType {
        R, A, T
    }
    private UserType userType;
    // === Representation Invariants ===
    // username.length() > 0
    // password.length() > 0
    // === Methods ===
    public User(String username, String password, String userEmail, UserType type) {
        this.userType = type;
        this.username = username;
        this.password = password;
        this.userEmail = userEmail;
        this.userEvents = new ArrayList<>();
        this.attendEvents = new ArrayList<>();
        this.loggedIn = false;
    }
    // === Getters ===

    /**
     * Get the user's username
     * @return String user's username
     */
    public String getUsername() {
        return this.username;
    }
    /**
     * Get the user's password
     * @return String user's password
     */
    public String getPassword() {
        return this.password;
    }
    /**
     * Get the user's email
     * @return String user's email
     */
    public String getUserEmail() {
        return this.userEmail;
    }
    /**
     * Get the user's logged in status
     * @return boolean The user's logged in status
     */
    public boolean isLoggedIn() {
        return this.loggedIn;
    }
    /**
     * Get the user's created events
     * @return List<Event> List of created events
     */
    public List<String> getUserEvents() {
        return this.userEvents;
    }
    /**
     * Get the user's attended events
     * @return List<Event> List of attended events
     */
    public List<String> getAttendEvents() {
        return this.userEvents;
    }

    /**
     * Get the string representation of a user
     * @return String The string representation of a user.
     */
    @Override
    public String toString() {
        return "User{" +
                "username='" + this.username + '\'' +
                ", userEmail='" + this.userEmail + '\'' +
                ", userEvents=" + this.userEvents +
                '}';
        // Test
    }

    // === Setters ===

    /**
     * Set a new username for the user
     * @param username The new username
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Set a new password for the user
     * @param password the new password
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Set a new email for the user
     * @param userEmail the new email
     */
    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    /**
     * Set a new logged in status for the user
     * @param loggedIn The new logged in status
     */
    public void setLoggedIn(boolean loggedIn) {
        this.loggedIn = loggedIn;
    }

    /**
     * Set a new list of created events
     * @param userEvents The new list of created events
     */
    public void setUserEvents(List<String> userEvents) {
        this.userEvents = userEvents;
    }

    /**
     * Set a new list of attended events
     * @param attendEvents The new list of attended events
     */
    public void setAttendEvents(List<String> attendEvents) {
        this.attendEvents = attendEvents;
    }

}
