package group02;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.geometry.Insets;
import java.io.File;
import javafx.scene.image.ImageView;
import javafx.scene.text.TextAlignment;

/**
 * Screen for creating a new game, allowing the player to select and name their pet.
 */
public class NewGameScreen {
    private final Stage stage;
    private final GameState gameState;
    private Scene scene;
    private ToggleButton dogButton;
    private ToggleButton catButton;
    private ToggleButton bunnyButton;
    private TextField nameField;
    private Button startButton;
    private String selectedPetType = null;

    /**
     * Constructor for NewGameScreen.
     *
     * @param stage The primary stage for the application.
     * @param gameState The current game state.
     */
    public NewGameScreen(Stage stage, GameState gameState) {
        this.stage = stage;
        this.gameState = gameState;
        createScene();
    }

    /**
     * Creates the scene for the new game screen.
     */
    private void createScene() {
        VBox root = new VBox(20);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(20));
        root.setStyle("-fx-background-color: #F5F5DC;");

        // Title
        Text title = new Text("New Game");
        title.setStyle("-fx-font-size: 36px; -fx-font-weight: bold;");

        // Pet selection
        VBox selectionBox = new VBox(15);
        selectionBox.setAlignment(Pos.CENTER);

        Text subtitle = new Text("Choose Your Pet");
        subtitle.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        // Pet selection buttons in a horizontal layout
        HBox petSelection = new HBox(30);
        petSelection.setAlignment(Pos.CENTER);

        // Create toggle group for pet selection
        ToggleGroup petGroup = new ToggleGroup();

        // Create pet buttons
        dogButton = createPetToggleButton("Dog", "Brown", petGroup);
        catButton = createPetToggleButton("Cat", "Gray", petGroup);
        bunnyButton = createPetToggleButton("Bunny", "LightGray", petGroup);

        petSelection.getChildren().addAll(dogButton, catButton, bunnyButton);

        // Pet name input
        HBox nameBox = new HBox(10);
        nameBox.setAlignment(Pos.CENTER);
        
        Label nameLabel = new Label("Pet Name:");
        nameLabel.setStyle("-fx-font-size: 16px;");
        
        nameField = new TextField();
        nameField.setPrefWidth(200);
        nameField.setPromptText("Enter a name for your pet");

        nameBox.getChildren().addAll(nameLabel, nameField);

        // Start button
        HBox buttonBox = new HBox(20);
        buttonBox.setAlignment(Pos.CENTER);

        startButton = createStyledButton("Start Game");
        startButton.setDisable(true);
        startButton.setOnAction(e -> startGame());

        // Back button
        Button backButton = createStyledButton("Back to Main Menu");
        backButton.setStyle(backButton.getStyle().replace("#4CAF50", "#9E9E9E"));
        backButton.setOnAction(e -> {
            MainMenuScreen mainMenu = new MainMenuScreen(stage, gameState);
            mainMenu.show();
        });

        buttonBox.getChildren().addAll(startButton, backButton);

        // Add listeners for input validation
        petGroup.selectedToggleProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                selectedPetType = ((ToggleButton) newVal).getText();
                validateInputs();
            } else {
                selectedPetType = null;
                validateInputs();
            }
        });

        nameField.textProperty().addListener((obs, oldVal, newVal) -> validateInputs());

        selectionBox.getChildren().addAll(subtitle, petSelection, nameBox);
        root.getChildren().addAll(title, selectionBox, buttonBox);

        scene = new Scene(root, 800, 600);
    }

    /**
     * Creates a toggle button for selecting a pet type.
     *
     * @param petType The type of pet (Dog, Cat, Bunny).
     * @param color   The color of the pet.
     * @param group   The ToggleGroup to which this button belongs.
     * @return The created ToggleButton.
     */
    private ToggleButton createPetToggleButton(String petType, String color, ToggleGroup group) {
        VBox buttonContent = new VBox(10);
        buttonContent.setAlignment(Pos.CENTER);
        
        // Create ImageView for the pet sprite
        String spritePath = "images/" + (petType.toUpperCase()) + "_NORMAL.png";
        ImageView petImage = new ImageView(new File(spritePath).toURI().toString());
        petImage.setFitWidth(100);
        petImage.setFitHeight(100);
        petImage.setPreserveRatio(true);
        petImage.setSmooth(true);

        // Pet type label
        Label petLabel = new Label(petType);
        petLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        
        // Add description
        Text description = new Text();
        description.setWrappingWidth(130);
        description.setTextAlignment(TextAlignment.CENTER);
        description.setStyle("-fx-font-size: 12px;");
        
        switch (petType) {
            case "Dog":
                description.setText("Dogs get hungry faster but maintain happiness well. Perfect for active players!");
                break;
            case "Cat":
                description.setText("Cats need more attention to stay happy but conserve energy well. Great for attentive owners!");
                break;
            case "Bunny":
                description.setText("Bunnies get tired quickly but are easy to keep fed. Ideal for patient caretakers!");
                break;
            default:
                description.setText("A wonderful companion!");
        }
        
        buttonContent.getChildren().addAll(petImage, petLabel, description);
        
        // Create toggle button
        ToggleButton button = new ToggleButton();
        button.setGraphic(buttonContent);
        button.setText(petType); // Store pet type in the button text
        button.setContentDisplay(ContentDisplay.TOP);
        button.setToggleGroup(group);
        button.setPrefSize(150, 220); // Increased height to accommodate description
        button.setStyle("""
            -fx-background-color: white;
            -fx-border-color: #E0E0E0;
            -fx-border-radius: 5;
            -fx-background-radius: 5;
            -fx-cursor: hand;
            """);

        // Add hover effect
        button.setOnMouseEntered(e -> 
            button.setStyle(button.getStyle() + "-fx-border-color: #4CAF50; -fx-border-width: 2;"));
        button.setOnMouseExited(e -> 
            button.setStyle(button.getStyle().replace("-fx-border-color: #4CAF50; -fx-border-width: 2;", 
                                                    "-fx-border-color: #E0E0E0;")));

        // Add selected style
        button.selectedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) {
                button.setStyle(button.getStyle() + "-fx-border-color: #4CAF50; -fx-border-width: 3;");
            } else {
                button.setStyle(button.getStyle().replace("-fx-border-color: #4CAF50; -fx-border-width: 3;", 
                                                        "-fx-border-color: #E0E0E0;"));
            }
        });
        
        return button;
    }

    /**
     * Validates the inputs and enables/disables the start button accordingly.
     */
    private void validateInputs() {
        boolean isValid = selectedPetType != null && !nameField.getText().trim().isEmpty();
        startButton.setDisable(!isValid);
    }

    /**
     * Starts the game with the selected pet type and name.
     */
    private void startGame() {
        String petName = nameField.getText().trim();
        
        if (selectedPetType == null || petName.isEmpty()) {
            showError("Please select a pet type and enter a name.");
            return;
        }

        // Create the pet based on selected type
        Pet pet = new Pet(petName, selectedPetType);
        
        // Start the game in GameState with the player's name (using pet name for now)
        gameState.startNewGame(petName, selectedPetType);
        
        // Save the game to a file
        gameState.savePet(pet.getType().toLowerCase() + "_save.txt");
        
        // Show the game screen
        GameScreen gameScreen = new GameScreen(stage, gameState, pet);
        gameScreen.show();
    }

    /**
     * Displays an error message in an alert dialog.
     *
     * @param message The error message to display.
     */
    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Creates a styled button with the given text.
     *
     * @param text The text to display on the button.
     * @return The styled button.
     */
    private Button createStyledButton(String text) {
        Button button = new Button(text);
        button.setStyle("""
            -fx-background-color: #4CAF50;
            -fx-text-fill: white;
            -fx-font-size: 16px;
            -fx-padding: 10px 20px;
            -fx-background-radius: 5;
            -fx-cursor: hand;
            -fx-min-width: 150px;
            """);
        return button;
    }

    /**
     * Displays the new game screen.
     */
    public void show() {
        stage.setScene(scene);
    }
} 