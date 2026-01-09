package group02;

import java.io.*;
import java.util.Map;
import java.time.LocalTime;
import static java.time.temporal.ChronoUnit.MINUTES;

/**
 * Manages the overall state of the game, including the current pet, player, and game settings.
 */
public class GameState {
    /** The directory where game saves are stored */
    public static final String SAVE_DIRECTORY = "saves/";
    /** The current pet in the game */
    private Pet pet;
    /** The player object */
    private Player player;
    /** The time the session started */
    private LocalTime sessionStartTime= LocalTime.now();

    /**
     * Creates a new game state with default settings if it's the first time playing
     * and loads settings from previous play sessions otherwise.
     */
    public GameState() {
        File savesDir = new File(GameState.SAVE_DIRECTORY + "settings.txt");
        if (savesDir.exists()) {
            this.player = new Player(GameState.SAVE_DIRECTORY + "settings.txt");
        }else {
            this.player = new Player();
        }
        this.player.incrementSessions();
    }

    /**
     * Starts a new game with the given pet name and type.
     *
     * @param petName The name of the pet.
     * @param petType The type of the pet.
     */
    public void startNewGame(String petName, String petType) {
        this.pet = new Pet(petName, petType);
        this.pet.addItem("Kibble", 5);
        this.pet.addItem("Treats", 2);
        this.pet.addItem("Premium Food", 1);
        this.pet.addItem("Vaccine", 1);
    }

    /**
     * Saves the settings to a file called settings.txt
     *
     * @param exit If true, saves the total play time and number of sessions
     */
    public void saveSettings(boolean exit) {
        try (PrintWriter writer = new PrintWriter(new File("saves/settings.txt"))) {
            writer.println("parentalPassword=" + this.player.getParentalPassword());
            writer.println("dailyTimeLimit=" + this.player.getDailyTimeLimit());
            writer.println("allowedStartTime=" + this.player.getAllowedStartTime());
            writer.println("allowedEndTime=" + this.player.getAllowedEndTime());
            if (exit) {
                writer.println("totalPlayTime=" + (this.player.getTotalPlayTime()) + sessionStartTime.until(LocalTime.now(), MINUTES));
            }else {
                writer.println("totalPlayTime=" + this.player.getTotalPlayTime());
            }
            writer.println("numberOfSessions=" + this.player.getNumberOfSessions());
            writer.println("timeRestrictionsEnabled=" + this.player.isTimeRestrictionsEnabled());
            writer.println("fullscreen=" + this.player.isFullScreen());

        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Failed to save settings to " + "settings.txt");
        }
    }

    /**
     * Saves the pet to the given file.
     *
     * @param petSaveFile The file to save the pet to.
     */
    public void savePet(String petSaveFile) {
        try (PrintWriter writer = new PrintWriter(SAVE_DIRECTORY + petSaveFile)) {
            writer.println("name=" + this.pet.getName());
            writer.println("type=" + this.pet.getType());
            writer.println("maxHealth=" + this.pet.getMaxHealth());
            writer.println("health=" + this.pet.getHealth());
            writer.println("happiness=" + this.pet.getHappiness() );
            writer.println("fullness=" + this.pet.getFullness() );
            writer.println("energy=" + this.pet.getEnergy() );
            writer.println("currency=" + this.pet.getCurrency() );
            writer.println("score=" + this.pet.getScore());
            writer.println("creationDate=" + this.pet.getCreationDate().toString() );

            Map<String, Integer> inv = this.pet.getInventory();
            for (Map.Entry<String, Integer> entry : inv.entrySet()) {
                writer.println(entry.getKey() + "=" + entry.getValue());
            }

            System.out.println("Game saved to saves/" + petSaveFile);


        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Failed to save pet to " + petSaveFile);
        }
    }

    /**
     * Saves the settings and pet to the given file.
     *
     * @param petSaveFile The file to save the pet to.
     */
    public void saveAll(String petSaveFile, boolean exit) {
        savePet(petSaveFile);
        saveSettings(exit);
    }

    /**
     * Exits the game and saves the settings and pet for exiting during a game.
     *
     * @param petSaveFile The file to save the pet to.
     */
    public void exitGame(String petSaveFile) {
        saveAll(petSaveFile, true);
    }

    /**
     * Returns the current pet.
     *
     * @return The current pet.
     */
    public Pet getPet() {
        return pet;
    }

    /**
     * Returns the player object.
     *
     * @return The player object.
     */
    public Player getPlayer() {
        return player;
    }

    /**
     * Sets the current pet.
     *
     * @param pet The new pet.
     */
    public void setPet(Pet pet) {
        this.pet = pet;
    }

    public void saveToFile(String fileName) {
        try {
            // Ensure we're using the save directory
            String filePath = SAVE_DIRECTORY + fileName;
            PrintWriter writer = new PrintWriter(new FileWriter(filePath));

            // Player information
            writer.println("playerName=" + "hawk");

            // Pet information - only save if pet is not null
            if (pet != null) {
                writer.println("petName=" + pet.getName());
                writer.println("petType=" + pet.getType());
                writer.println("maxHealth=" + pet.getMaxHealth());
                writer.println("health=" + pet.getHealth());
                writer.println("happiness=" + pet.getHappiness());
                writer.println("fullness=" + pet.getFullness());
                writer.println("energy=" + pet.getEnergy());
            }

            writer.println("score=" + pet.getScore());
            writer.println("currency=" + pet.getCurrency());
            // Creation date
            writer.println("creationDate=" + pet.getCreationDate());



            // Inventory
            for (Map.Entry<String, Integer> entry : pet.getInventory().entrySet()) {
                writer.println("inventory_" + entry.getKey() + "=" + entry.getValue());
            }

            // Settings and stats
            writer.println("totalPlayTime=" + player.getTotalPlayTime());
            writer.println("totalPlaySessions=" + player.getNumberOfSessions());
            writer.println("totalFeedings=" + 333);
            writer.println("totalVetVisits=" + 222);

            writer.close();
            System.out.println("Game saved to " + filePath);
        } catch (IOException e) {
            System.err.println("Error saving game: " + e.getMessage());
        }
    }

    public void endGameSession() {
        if (sessionStartTime.until(LocalTime.now(), MINUTES) == 0) {
            System.out.println("No active session to end");
            return;
        }

        long sessionDuration = sessionStartTime.until(LocalTime.now(), MINUTES);

        if (sessionDuration < 1) {
            System.out.println("Session too short (" + (sessionDuration) + " minute), not counting");
            return;
        }

        saveSettings(true);
    }

    public void startSessionTimeTracking () {
        sessionStartTime = LocalTime.now();
        System.out.println("Session tracking started at: " + sessionStartTime);
    }
}