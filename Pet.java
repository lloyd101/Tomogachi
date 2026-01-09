package group02;

import java.io.*;
import java.util.Map;
import java.util.HashMap;
import javafx.scene.image.Image;
import java.time.LocalDate;

/**
 * Pet object containing information about the pet and functions relating to the
 * pet's appearance and changing the pet's stats.
 */
public class Pet {
    /** The pet's name */
    private String name;
    /** The pet's type (e.g., DOG, BUNNY, CAT) */
    private String type;
    /** The pet's health */
    private int health;
    /** The pet's maximum health */
    private int maxHealth;
    /** The pet's happiness */
    private int happiness;
    /** The pet's fullness */
    private int fullness;
    /** The pet's energy */
    private int energy;
    /** The pet's currency */
    private int currency;
    /** The pet's score */
    private int score;
    /** The pet's creation date */
    private LocalDate creationDate;
    /** The player's inventory of items */
    private Map<String, Integer> inventory = new HashMap<>();
    /** The pet's current state (e.g., NORMAL, ANGRY, HUNGRY, SLEEP, DEAD) */
    private State state;
    /** The pet's current sprite image */
    private Image currentSprite;
    /** Map of sprite images for each pet type */
    private static final Map<String, Map<String, Image>> sprites = new HashMap<>();

    /**
     * Pet constructor. Constructs a new Pet object with the given name and type.
     *
     * @param name The name of the pet.
     * @param type The type of the pet (e.g., DOG, BUNNY, CAT).
     */
    public Pet(String name, String type) {
        this.name = name;
        this.type = type.toUpperCase();
        this.maxHealth = 100;
        this.health = this.maxHealth;
        this.happiness = 100;
        this.fullness = 100;
        this.energy = 100;
        this.currency = 100;
        this.score = 0;
        this.state = State.NORMAL;
        this.creationDate = LocalDate.now();
        updateSprite();
    }

    /**
     * Pet constructor. Constructs a new Pet object with the given pet file.
     *
     * @param petFile The file to read the pet information from.
     */
    public Pet(String petFile){
        try (BufferedReader reader = new BufferedReader(new FileReader("saves\\" + petFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("=");

                switch (parts[0]) {
                    case "name" -> this.name = parts[1];
                    case "type" -> this.type = parts[1];
                    case "maxHealth" -> this.maxHealth = Integer.parseInt(parts[1]);
                    case "health" -> this.health = Integer.parseInt(parts[1]);
                    case "happiness" -> this.happiness = Integer.parseInt(parts[1]);
                    case "fullness" -> this.fullness = Integer.parseInt(parts[1]);
                    case "energy" -> this.energy = Integer.parseInt(parts[1]);
                    case "currency" -> this.currency = Integer.parseInt(parts[1]);
                    case "score" -> this.score = Integer.parseInt(parts[1]);
                    case "creationDate" -> this.creationDate = LocalDate.parse(parts[1]);
                    default -> this.inventory.put(parts[0], Integer.parseInt(parts[1]));
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * Enum representing different states of the pet.
     */
    public enum State {
        NORMAL, ANGRY, HUNGRY, SLEEP, DEAD;

        public static State fromInt(int value) {
            return switch (value) {
                case 0 -> NORMAL;
                case 1 -> ANGRY;
                case 2 -> HUNGRY;
                case 3 -> SLEEP;
                case 4 -> DEAD;
                default -> throw new IllegalStateException("Unexpected value: " + value);
            };
        }
    }

    /**
     * Static block to load all sprites for each pet type.
     */
    static {
        // Load all sprites for each pet type
        loadSpritesForType("DOG");
        loadSpritesForType("BUNNY");
        loadSpritesForType("CAT");
    }

    /**
     * Updates the pet's attributes over time to simulate needs.
     */
    public void update() {
        // sleep state
        if (this.state == State.SLEEP){
            setEnergy(Math.min(getEnergy() + 10, 100));
            if (getEnergy() >= 100) {
                this.state = State.NORMAL;
            }
        } else {
            setEnergy(Math.max(getEnergy() - 1, 0));
        }

        // hungry state
        if (this.state == State.HUNGRY) {
            setHealth(Math.max(getHealth() - 1, 0));
            setHappiness(Math.max(getHappiness() - 3, 0));
            if (this.fullness > 0) {
                this.state = State.NORMAL;
            }
        }else {
            setHappiness(Math.max(getHappiness() - 1, 0));
            setFullness(Math.max(getFullness() - 1, 0));
        }

        // angry state
        if (this.state == State.ANGRY) {
            if (this.happiness >= 50) {
                this.state = State.NORMAL;
            }
        }




        if (this.health <= 0) { // dead state
            this.state = State.DEAD;
        }
        else if (this.energy <= 0) { // sleep state
            setHealth(Math.max(getHealth() - 10, 0));
            sleep();
        }
        else if (this.state == State.SLEEP) {
            System.out.println("PET IS ASLEEP");
        }
        else if (this.fullness <= 0) { // hungry state
            this.state = State.HUNGRY;
        }
        if (this.happiness <= 0) { // angry state
            this.state = State.ANGRY;
        }




        // Update sprite after all changes
        updateSprite();
    }

    /**
     * Updates the sprite based on the pet's current state and attributes.
     */
    public void updateSprite() {
        Map<String, Image> typeSprites = sprites.get(type);
        if (typeSprites == null) return;

        // Choose the appropriate sprite based on pet's state and stats
        if (!isAlive()) {
            this.currentSprite = typeSprites.get("DEAD");
        } else if (state == State.SLEEP) {
            this.currentSprite = typeSprites.get("SLEEP");
        } else if (fullness == 0) {
            this.currentSprite = typeSprites.get("HUNGRY");
        } else if (happiness == 0) {
            this.currentSprite = typeSprites.get("ANGRY");
        } else {
            this.currentSprite = typeSprites.get("NORMAL");
        }
    }

    /**
     * Loads sprite images for a specific pet type and stores them in the sprite map.
     * Each pet type has different state-based sprites such as NORMAL, ANGRY, SLEEP, HUNGRY, and DEAD.
     *
     * @param petType The type of pet for which sprites should be loaded (e.g., "DOG", "BUNNY", "CAT").
     */
    private static void loadSpritesForType(String petType) {
        Map<String, Image> stateSprites = new HashMap<>();
        String[] states = {"NORMAL", "ANGRY", "SLEEP", "HUNGRY", "DEAD"};

        for (String state : states) {
            try {
                String path = "images/" + petType + "_" + state + ".png";
                Image sprite = new Image(new File(path).toURI().toString());
                stateSprites.put(state, sprite);
            } catch (Exception e) {
                System.err.println("Failed to load sprite: " + petType + "_" + state);
                e.printStackTrace();
            }
        }

        sprites.put(petType, stateSprites);
    }

    /**
     * Put the pet to sleep to restore energy, decreases fullness.
     */
    public void sleep() {
        this.health = Math.max(this.health - 10, 0);
        this.state = State.SLEEP;
    }

    /**
     * Get the pet's currency.
     *
     * @return The pet's currency.
     */
    public int getCurrency(){ return this.currency; }

    /**
     * Add currency to the pet's total.
     *
     * @param amount The amount of currency to add.
     */
    public void addCurrency(int amount){ this.currency += amount; }

    /**
     * Spend currency if player has enough.
     *
     * @param amount The amount of currency to spend.
     * @return true if player has enough currency to spend, false otherwise.
     */
    public boolean spendCurrency(int amount) {
        if (this.currency >= amount) {
            this.currency -= amount;
            return true;
        }
        return false;
    }

    /**
     * Check if the pet is alive.
     *
     * @return true if the pet is alive, false otherwise.
     */
    public boolean isAlive() { return health > 0; }

    /**
     * Get the pet's name.
     *
     * @return The pet's name.
     */
    public String getName() { return this.name; }

    /**
     * Set the pet's name.
     *
     * @param name The new name for the pet.
     */
    public void setName(String name) { this.name = name; }

    /**
     * Get the pet's type.
     *
     * @return The pet's type.
     */
    public String getType() { return this.type; }

    /**
     * Set the pet's type.
     *
     * @param type The new type for the pet.
     */
    public void setType(String type) { this.type = type; }

    /**
     * Get the pet's health.
     *
     * @return The pet's health.
     */
    public int getHealth() { return this.health; }

    /**
     * Set the pet's health.
     *
     * @param health The new health value.
     */
    public void setHealth(int health) {
        this.health = health;
        updateSprite();
    }

    /**
     * Get the pet's maximum health.
     *
     * @return The pet's maximum health.
     */
    public int getMaxHealth() { return this.maxHealth; }

    /**
     * Set the pet's maximum health.
     *
     * @param maxHealth The new maximum health value.
     */
    public void setMaxHealth(int maxHealth) { this.maxHealth = maxHealth; }

    /**
     * Get the pet's state.
     *
     * @return The pet's state.
     */
    public State getState() { return this.state; }

    /**
     * Set the pet's state.
     *
     * @param state The new state for the pet.
     */
    public void setState(State state) {
        this.state = state;
        updateSprite();
    }

    /**
     * Get the pet's fullness.
     *
     * @return The pet's fullness.
     */
    public int getFullness() { return this.fullness; }

    /**
     * Set the pet's fullness.
     *
     * @param fullness The new fullness value.
     */
    public void setFullness(int fullness) {
        this.fullness = Math.max(0, Math.min(fullness, 100));
        updateSprite();
    }

    /**
     * Get the pet's energy.
     *
     * @return The pet's energy.
     */
    public int getEnergy() { return this.energy; }

    /**
     * Set the pet's energy.
     *
     * @param energy The new energy value.
     */
    public void setEnergy(int energy) {
        this.energy = Math.max(0, Math.min(energy, 100));
        updateSprite();
    }

    /**
     * Get the pet's happiness.
     *
     * @return The pet's happiness.
     */
    public int getHappiness() { return this.happiness; }

    /**
     * Set the pet's happiness.
     *
     * @param happiness The new happiness value.
     */
    public void setHappiness(int happiness) {
        this.happiness = Math.max(0, Math.min(happiness, 100));
        updateSprite();
    }

    /**
     * Get the pet's score.
     *
     * @return The pet's score.
     */
    public int getScore() { return this.score; }

    /**
     * Set the pet's score.
     *
     * @param score The new score value.
     */
    public void setScore(int score) { this.score = score; }

    /**
     * Get the pet's inventory count for a specific item.
     *
     * @param itemName The name of the item to check.
     * @return The count of the item in the pet's inventory.
     */
    public int getItemCount(String itemName) { return inventory.getOrDefault(itemName, 0); }

    /**
     * Add an item to the pet's inventory.
     *
     * @param itemName The name of the item to add.
     * @param amount The amount of the item to add.
     */
    public void addItem(String itemName, int amount) { inventory.put(itemName, getItemCount(itemName) + amount); }

    /**
     * Remove an item from the pet's inventory.
     *
     * @param itemName The name of the item to remove.
     * @param amount The amount of the item to remove.
     */
    public boolean removeItem(String itemName, int amount) {
        int currentAmount = getItemCount(itemName);
        if (currentAmount >= amount) {
            inventory.put(itemName, currentAmount - amount);
            return true;
        }
        return false;
    }

    /**
     * Get the pet's creation date.
     *
     * @return The pet's creation date.
     */
    public LocalDate getCreationDate() { return creationDate; }

    /**
     * Set the pet's creation date.
     *
     * @param creationDate The new creation date.
     */
    public void setCreationDate(LocalDate creationDate) { this.creationDate = creationDate; }

    /**
     * Get the pet's inventory.
     *
     * @return The pet's inventory.
     */
    public Map<String, Integer> getInventory(){ return this.inventory; }
}
