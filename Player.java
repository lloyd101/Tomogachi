package group02;

import java.io.*;
import java.time.LocalTime;
import static java.time.temporal.ChronoUnit.MINUTES;


/**
 * Represents the player in the Tamagotchi game.
 * Stores player information like name and score.
 */
public class Player {
    /** The player's total play time */
    private int totalPlayTime;
    /** The number of sessions played */
    private int numberOfSessions;
    /** The parental password */
    private String parentalPassword;
    /** The daily time limit in minutes */
    private int dailyTimeLimit;
    /** The allowed start time */
    private LocalTime allowedStartTime;
    /** The allowed end time */
    private LocalTime allowedEndTime;

    private boolean timeRestrictionsEnabled = false;

    private boolean fullScreen;

    /** The start time of the current session */
    private long sessionStartTime;


    /**
     * Player constructor. Constructs a new Player object.
     */
    public Player() {
        this.totalPlayTime = 0;
        this.numberOfSessions = 0;
        this.allowedStartTime = LocalTime.of(8, 0);
        this.allowedEndTime = LocalTime.of(20, 0);
        this.parentalPassword = null;
        this.fullScreen = false;
    }

    /**
     * Player constructor. Constructs a new Player object.
     *
     * @param settingsFile The file to read the player settings from.
     */
    public Player(String settingsFile){
        try (BufferedReader reader = new BufferedReader(new FileReader(settingsFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("=");

                switch (parts[0]) {
                    case "parentalPassword" -> this.parentalPassword = parts[1];
                    case "dailyTimeLimit" -> this.dailyTimeLimit = Integer.parseInt(parts[1]);
                    case "allowedStartTime" -> this.allowedStartTime = java.time.LocalTime.parse(parts[1]);
                    case "allowedEndTime" -> this.allowedEndTime = java.time.LocalTime.parse(parts[1]);
                    case "totalPlayTime" -> this.totalPlayTime = Integer.parseInt(parts[1]);
                    case "numberOfSessions" -> this.numberOfSessions = Integer.parseInt(parts[1]);
                    case "timeRestrictionsEnabled" -> this.timeRestrictionsEnabled = Boolean.parseBoolean(parts[1]);
                    case "fullScreen" -> this.fullScreen = Boolean.parseBoolean(parts[1]);
                    }
                }
            if (parentalPassword.equals("null")) {
                parentalPassword = null;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /** Increment the number of sessions played. */
    public void incrementSessions() { this.numberOfSessions++; }

    /**
     * Get the player's total play time.
     *
     * @return The player's total play time.
     */
    public int getTotalPlayTime() { return totalPlayTime; }

    /**
     * Set the player's total play time.
     *
     * @param totalPlayTime The player's total play time.
     */
    public void setTotalPlayTime(int totalPlayTime) { this.totalPlayTime = totalPlayTime; }

    /**
     * Get the number of sessions played.
     *
     * @return The number of sessions played.
     */
    public int getNumberOfSessions() { return numberOfSessions; }

    /**
     * Get the parental password.
     *
     * @return The parental password.
     */
    public String getParentalPassword(){ return parentalPassword; }

    /**
     * Set the parental password.
     *
     * @param parentalPassword The parental password.
     */
    public void setParentalPassword(String parentalPassword) { this.parentalPassword = parentalPassword; }

    /**
     * Get the daily time limit.
     *
     * @return The daily time limit.
     */
    public int getDailyTimeLimit(){ return dailyTimeLimit; }

    /**
     * Set the daily time limit.
     *
     * @param dailyTimeLimit The daily time limit.
     */
    public void setDailyTimeLimit(int dailyTimeLimit) { this.dailyTimeLimit = dailyTimeLimit; }

    /**
     * Get the allowed start time.
     *
     * @return The allowed start time.
     */
    public LocalTime getAllowedStartTime(){ return allowedStartTime; }

    /**
     * Set the allowed start time.
     *
     * @param allowedStartTime The allowed start time.
     */
    public void setAllowedStartTime(LocalTime allowedStartTime) { this.allowedStartTime = allowedStartTime; }

    /**
     * Get the allowed end time.
     *
     * @return The allowed end time.
     */
    public LocalTime getAllowedEndTime(){ return allowedEndTime; }

    /**
     * Set the allowed end time.
     *
     * @param allowedEndTime The allowed end time.
     */
    public void setAllowedEndTime(LocalTime allowedEndTime) { this.allowedEndTime = allowedEndTime; }

    /**
     * Checks if the player is allowed to play based on the current time and session start time.
     *
     * @param sessionStartTime The time the current session started.
     * @return True if the player is allowed to play, false otherwise.
     */
    public boolean isAllowedToPlay(LocalTime sessionStartTime){
        boolean withinDailyLimit = getDailyTimeLimit() - sessionStartTime.until(LocalTime.now(), MINUTES) >= 0;
        boolean withinAllowedTime = (LocalTime.now().isAfter(allowedStartTime) && LocalTime.now().isBefore(allowedEndTime));
        System.out.println(withinDailyLimit + " " + withinAllowedTime);
        System.out.println(sessionStartTime.until(LocalTime.now(), MINUTES));

        return withinAllowedTime && withinDailyLimit;
    }

    /**
     * Checks if the parental password is set.
     *
     * @return True if the parental password is set, false otherwise.
     */
    public boolean hasParentalPassword() {
        return parentalPassword != null && !parentalPassword.isEmpty();
    }

    /**
     * Checks if the given password matches the parental password.
     *
     * @param password The password to check.
     * @return True if the password is correct, false otherwise.
     */
    public boolean isPasswordCorrect(String password){
        return parentalPassword.equals(password);
    }

    /**
     * Checks if time restrictions are enabled.
     *
     * @return True if time restrictions are enabled, false otherwise.
     */
    public boolean isTimeRestrictionsEnabled() { return timeRestrictionsEnabled; }

    /**
     * Resets the player's statistics.
     */
    public void resetStats() {
        totalPlayTime = 0;
        numberOfSessions = 0;
    }

    /**
     * Sets the time restrictions enabled status.
     *
     * @param enabled True to enable time restrictions, false to disable.
     */
    public void setTimeRestrictionsEnabled(boolean enabled) { this.timeRestrictionsEnabled = enabled; }

    public boolean isFullScreen() { return fullScreen; }

    public void setFullScreen(boolean fullScreen) { this.fullScreen = fullScreen; }
}