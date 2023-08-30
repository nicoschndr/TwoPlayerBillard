package pool.poolModel;

import java.io.Serializable;

/**
 * This class represents a player in the billard game. A player represents a human playing billard his name, team, wins
 * and if he is currently playing.
 */
public class Player implements Serializable {
    private int id, wins;
    private String name, team;
    private boolean currentlyPlaying;

    public Player(int id, String name, String team, int wins, boolean currentlyPlaying) {
        this.id = id;
        this.name = name;
        this.team = team;
        this.wins = wins;
        this.currentlyPlaying = currentlyPlaying;
    }

    /**
     * This method returns the type of balls the player needs to pot.
     *
     * @return the type of balls the player needs to pot as a String
     */
    public String getTeam() {
        return this.team;
    }

    /**
     * This method returns a boolean variable which says if a player is currently playing or not.
     *
     * @return a boolean variable that says if a player is currently playing or not
     */
    public boolean getCurrentlyPlaying() {
        return this.currentlyPlaying;
    }

    /**
     * This method sets the boolean that defines if a player is currently playing to a given value. (true or false)
     *
     * @param currentlyPlaying boolean that indicates if a player is currently playing or not
     */
    public void setCurrentlyPlaying(boolean currentlyPlaying) {
        this.currentlyPlaying = currentlyPlaying;
    }

    /**
     * This method returns the id of a player.
     *
     * @return the id of a player as integer
     */
    public int getPlayerId() {
        return this.id;
    }

    /**
     * This method returns the name of a player.
     *
     * @return the name of a player as a String
     */
    public String getPlayerName() {
        return this.name;
    }

    /**
     * this method returns the wins of a player.
     *
     * @return the wins of a player as integer
     */
    public int getPlayerWins() {
        return this.wins;
    }

    /**
     * This method increments the number of wins of a player.
     */
    public void setWins() {
        this.wins++;
    }

    /**
     * This method sets the team a player is playing in (solids or stripes).
     *
     * @param team the team a player is playing in (solids or stripes) as a String
     */
    public void setTeam(String team) {
        this.team = team;
    }
}
