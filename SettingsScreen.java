package group02;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Separator;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

/**
 * Settings screen for the Tamagotchi game.
 * Allows users to adjust settings such as display mode.
 */
public class SettingsScreen {
    private final Stage stage;
    private final GameState gameState;
    private Scene scene;
    private RadioButton windowedButton;
    private RadioButton fullscreenButton;

    /**
     * Constructor for SettingsScreen.
     *
     * @param stage The primary stage for the application.
     * @param gameState The current game state.
     */
    public SettingsScreen(Stage stage, GameState gameState) {
        this.stage = stage;
        this.gameState = gameState;
        createScene();
    }

    /**
     * Creates the settings screen scene.
     */
    private void createScene() {
        VBox root = new VBox(20);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(20));
        root.setStyle("-fx-background-color: #F5F5DC;");

        Text title = new Text("Settings");
        title.setStyle("-fx-font-size: 32px; -fx-font-weight: bold; -fx-fill: #8B4513;");

        
        // Display settings
        VBox displayBox = createDisplaySettings();

        // Navigation buttons
        HBox buttonBox = new HBox(20);
        buttonBox.setAlignment(Pos.CENTER);

        Button saveButton = createStyledButton("Save Settings");
        saveButton.setOnAction(e -> {
            saveSettings();
            applySettings();
        });

        Button backButton = createStyledButton("Back to Main Menu");
        backButton.setStyle(backButton.getStyle().replace("#4CAF50", "#8B4513"));
        backButton.setOnAction(e -> {
            MainMenuScreen mainMenu = new MainMenuScreen(stage, gameState);
            mainMenu.show();
        });

        buttonBox.getChildren().addAll(saveButton, backButton);

        root.getChildren().addAll(
            title,
            displayBox,
            buttonBox
        );

        scene = new Scene(root, 800, 600);
        stage.setScene(scene);
    }

    /**
     * Creates the display settings section.
     *
     * @return VBox containing display settings.
     */
    private VBox createDisplaySettings() {
        VBox displayBox = new VBox(15);
        displayBox.setAlignment(Pos.CENTER);
        displayBox.setPadding(new Insets(25));
        displayBox.setMaxWidth(500);
        displayBox.setStyle("-fx-background-color: #FFFFFF; -fx-border-color: #8B4513; -fx-border-width: 2px; -fx-border-radius: 10; -fx-background-radius: 10;");

        Text displayTitle = new Text("Display Settings");
        displayTitle.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        displayTitle.setStyle("-fx-fill: #8B4513;");

        // Display mode selection
        VBox displayMode = new VBox(10);
        displayMode.setAlignment(Pos.CENTER);
        Text displayModeTitle = new Text("Display Mode:");
        displayModeTitle.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        displayModeTitle.setStyle("-fx-fill: #8B4513;");

        ToggleGroup displayGroup = new ToggleGroup();
        windowedButton = new RadioButton("Windowed");
        fullscreenButton = new RadioButton("Fullscreen");
        windowedButton.setToggleGroup(displayGroup);
        fullscreenButton.setToggleGroup(displayGroup);
        windowedButton.setStyle("-fx-font-size: 16px;");
        fullscreenButton.setStyle("-fx-font-size: 16px;");

        // Set initial selection based on current state
        if (stage.isFullScreen()) {
            fullscreenButton.setSelected(true);
        } else {
            windowedButton.setSelected(true);
        }

        // Add listeners to handle display mode changes
        windowedButton.setOnAction(e -> {
            stage.setFullScreen(false);
            gameState.getPlayer().setFullScreen(false);
        });

        fullscreenButton.setOnAction(e -> {
            stage.setFullScreen(true);
            gameState.getPlayer().setFullScreen(true);
        });

        displayMode.getChildren().addAll(displayModeTitle, windowedButton, fullscreenButton);
        displayBox.getChildren().addAll(displayTitle, new Separator(), displayMode);

        return displayBox;
    }

    /**
     * Creates the volume settings section.
     */
    private void saveSettings() { gameState.saveSettings(false); }

    /**
     * Applies the settings to the game.
     */
    private void applySettings() {
        // Apply fullscreen setting immediately
        stage.setFullScreen(gameState.getPlayer().isFullScreen());
    }

    /**
     * Creates a styled button with hover effects.
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
        
        // Add hover effect
        button.setOnMouseEntered(e -> button.setStyle("""
            -fx-background-color: #45a049;
            -fx-text-fill: white;
            -fx-font-size: 16px;
            -fx-padding: 10px 20px;
            -fx-background-radius: 5;
            -fx-cursor: hand;
            -fx-min-width: 150px;
            """));
        
        button.setOnMouseExited(e -> button.setStyle("""
            -fx-background-color: #4CAF50;
            -fx-text-fill: white;
            -fx-font-size: 16px;
            -fx-padding: 10px 20px;
            -fx-background-radius: 5;
            -fx-cursor: hand;
            -fx-min-width: 150px;
            """));
        
        return button;
    }

    /**
     * Displays the settings screen.
     */
    public void show() {
        stage.setScene(scene);
        stage.setTitle("Settings");
    }
} 