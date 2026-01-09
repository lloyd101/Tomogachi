package group02;
import group02.*;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.geometry.Insets;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javafx.scene.image.ImageView;
import java.util.Map;

/**
 * LoadGameScreen class handles the loading of saved games.
 */
public class LoadGameScreen {
    private final Stage stage;
    private final GameState gameState;
    private final List<Pet> saveSlots = new ArrayList<>();

    /**
     * Constructor for LoadGameScreen.
     *
     * @param stage The primary stage for the application.
     * @param gameState The current game state.
     */
    public LoadGameScreen(Stage stage, GameState gameState) {
        this.stage = stage;
        this.gameState = gameState;
        loadSaveSlots();
    }

    /**
     * Loads the save slots with existing pet data.
     */
    private void loadSaveSlots() {
        // Look for save files in the saves directory
        File savesDir = new File("saves");
        if (!savesDir.exists()) {
            savesDir.mkdir();
        }

        // Check for existing save files - one for each animal type
        saveSlots.clear();
        
        // Look for dog save
        File dogSave = new File("saves/dog_save.txt");
        if (dogSave.exists()) {
            saveSlots.add(new Pet("dog_save.txt"));
        } else {
            saveSlots.add(new Pet("Empty Slot", "DOG"));
        }
        
        // Look for cat save
        File catSave = new File("saves/cat_save.txt");
        if (catSave.exists()) {
            saveSlots.add(new Pet("cat_save.txt"));
        } else {
            saveSlots.add(new Pet("Empty Slot", "CAT"));
        }
        
        // Look for bunny save
        File bunnySave = new File("saves/bunny_save.txt");
        if (bunnySave.exists()) {
            saveSlots.add(new Pet("bunny_save.txt"));
        } else {
            saveSlots.add(new Pet("Empty Slot", "BUNNY"));
        }
    }

    /**
     * Creates a save slot box for a pet.
     *
     * @param pet The pet to display in the save slot.
     * @return A VBox containing the pet's information and load button.
     */
    private VBox createSaveSlotBox(Pet pet) {
        VBox box = new VBox(15);
        box.setAlignment(Pos.CENTER);
        box.setPadding(new Insets(15));
        box.setMinWidth(200);
        box.setMinHeight(300);
        box.setStyle("-fx-background-color: #FFFFFF; -fx-border-color: #8B4513; -fx-border-width: 2px; -fx-border-radius: 10; -fx-background-radius: 10;");

        // Animal type label
        Text animalType = new Text(pet.getType());
        animalType.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-fill: #8B4513;");

        // Create pet sprite image
        ImageView portrait = null;
        String prefix = pet.getType();
        String spritePath = "images/" + prefix + "_NORMAL.png";
        
        try {
            File imageFile = new File(spritePath);
            System.out.println("Load screen - attempting to load sprite: " + spritePath);
            System.out.println("Animal type: " + pet.getType());
            System.out.println("File exists: " + imageFile.exists());
            System.out.println("File absolute path: " + imageFile.getAbsolutePath());
            String imageURI = imageFile.toURI().toString();
            System.out.println("Image URI: " + imageURI);
            portrait = new ImageView(new javafx.scene.image.Image(imageURI));
            System.out.println("Sprite loaded successfully");
        } catch (Exception e) {
            System.err.println("Error loading sprite in load screen: " + spritePath);
            System.err.println("Error details: " + e.getMessage());
            e.printStackTrace();
            // Create a colored rectangle as fallback
            Rectangle fallback = new Rectangle(120, 120);
            fallback.setFill(Color.GRAY);
            portrait = new ImageView();
        }

        if (portrait != null) {
            portrait.setFitWidth(120);
            portrait.setFitHeight(120);
            portrait.setPreserveRatio(true);
        }

        // Pet name
        Text nameText = new Text(pet.getName());
        nameText.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        // Stats
        VBox statsBox = new VBox(5);
        statsBox.setAlignment(Pos.CENTER);
        
        Text healthText = new Text("Health: " + pet.getHealth() + "/" + pet.getMaxHealth());
        Text energyText = new Text("Energy: " + pet.getEnergy() + "%");
        Text fullnessText = new Text("Fullness: " + pet.getFullness() + "%");
        Text happinessText = new Text("Happiness: " + pet.getHappiness() + "%");
        Text currencyText = new Text("Dabloons: " + pet.getCurrency());
        Text scoreText = new Text("Score: " + pet.getScore());

        // Style all stat texts
        healthText.setStyle("-fx-font-size: 14px;");
        energyText.setStyle("-fx-font-size: 14px;");
        fullnessText.setStyle("-fx-font-size: 14px;");
        happinessText.setStyle("-fx-font-size: 14px;");
        currencyText.setStyle("-fx-font-size: 14px;");
        scoreText.setStyle("-fx-font-size: 14px;");

        statsBox.getChildren().addAll(healthText, energyText, fullnessText, happinessText, currencyText, scoreText);

        // Load button
        Button loadButton = createStyledButton("Load Game");
        loadButton.setOnAction(e -> loadGame(pet));

        box.getChildren().addAll(animalType, portrait, nameText, statsBox, loadButton);
        return box;
    }

    /**
     * Loads the game with the selected pet.
     *
     * @param pet The pet to load.
     */
    private void loadGame(Pet pet) {
        if (gameState.getPet() == null || gameState.getPet().getName().contains("Empty Slot")) {
            return;
        }


        gameState.setPet(pet);

        GameScreen gameScreen = new GameScreen(stage, gameState, pet);
        gameScreen.show();
    }

    /**
     * Creates a styled button with hover effect.
     *
     * @param text The text to display on the button.
     * @return The created Button.
     */
    private Button createStyledButton(String text) {
        Button button = new Button(text);
        button.setStyle("""
            -fx-background-color: #4CAF50;
            -fx-text-fill: white;
            -fx-font-size: 14px;
            -fx-padding: 10px 20px;
            -fx-background-radius: 5;
            -fx-cursor: hand;
            -fx-min-width: 150px;
            """);
        
        // Add hover effect
        button.setOnMouseEntered(e -> button.setStyle("""
            -fx-background-color: #45a049;
            -fx-text-fill: white;
            -fx-font-size: 14px;
            -fx-padding: 10px 20px;
            -fx-background-radius: 5;
            -fx-cursor: hand;
            -fx-min-width: 150px;
            """));
        
        button.setOnMouseExited(e -> button.setStyle("""
            -fx-background-color: #4CAF50;
            -fx-text-fill: white;
            -fx-font-size: 14px;
            -fx-padding: 10px 20px;
            -fx-background-radius: 5;
            -fx-cursor: hand;
            -fx-min-width: 150px;
            """));
        
        return button;
    }

    /**
     * Displays the load game screen.
     */
    public void show() {
        VBox root = new VBox(20);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(20));
        root.setStyle("-fx-background-color: #F5F5DC;"); // Beige background

        Text title = new Text("Load Game");
        title.setStyle("-fx-font-size: 36px; -fx-font-weight: bold; -fx-fill: #8B4513;"); // Dark brown text

        // Create a container for the save slots
        VBox saveContainer = new VBox(20);
        saveContainer.setAlignment(Pos.CENTER);
        saveContainer.setPadding(new Insets(25));
        saveContainer.setStyle("-fx-background-color: #FFFFFF; -fx-border-color: #8B4513; -fx-border-width: 2px; -fx-border-radius: 10; -fx-background-radius: 10;");
        saveContainer.setMaxWidth(700);

        // Create grid layout for save slots
        GridPane saveGrid = new GridPane();
        saveGrid.setHgap(20);
        saveGrid.setVgap(20);
        saveGrid.setAlignment(Pos.CENTER);

        for (int i = 0; i < saveSlots.size(); i++) {
            Pet slot = saveSlots.get(i);
            VBox slotBox = createSaveSlotBox(slot);
            saveGrid.add(slotBox, i, 0);
        }

        saveContainer.getChildren().add(saveGrid);

        Button backButton = createStyledButton("Back to Main Menu");
        backButton.setStyle(backButton.getStyle().replace("#4CAF50", "#8B4513")); // Brown for back button
        backButton.setOnAction(e -> {
            MainMenuScreen mainMenu = new MainMenuScreen(stage, gameState);
            mainMenu.show();
        });

        root.getChildren().addAll(title, saveContainer, backButton);

        Scene scene = new Scene(root, 800, 600);
        stage.setScene(scene);
        stage.setTitle("Tamagotchi Game - Load Game");
    }

    private record SaveSlot(String animalType, String petType, PetStats stats) {
        public String name() {
            return stats != null ? stats.name() : "Empty Slot";
        }

        public int health() {
            return stats != null ? stats.health() : 0;
        }

        public int maxHealth() {
            return stats != null ? stats.maxHealth() : 100;
        }

        public int energy() {
            return stats != null ? stats.energy() : 0;
        }

        public int fullness() {
            return stats != null ? stats.fullness() : 0;
        }

        public int happiness() {
            return stats != null ? stats.happiness() : 0;
        }

        public int currency() {
            return stats != null ? stats.currency() : 0;
        }

        public int score() {
            return stats != null ? stats.score() : 0;
        }
    }

    private record PetStats(String name, int health, int maxHealth, int energy, int fullness, int happiness, int currency, int score,
                            Map<String, Integer> inventory) {}
} 