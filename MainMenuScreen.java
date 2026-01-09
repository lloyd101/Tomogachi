package group02;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.geometry.Insets;
import java.time.LocalTime;
import javafx.scene.layout.BorderPane;
import javafx.scene.control.Alert;
import javafx.application.Platform;


/**
 * The main menu screen of the game.
 */
public class MainMenuScreen {
    private final Stage stage;
    private final GameState gameState;
    private Scene scene;

    /**
     * Constructor for MainMenuScreen.
     *
     * @param stage The primary stage for the application.
     * @param gameState The current game state.
     */
    public MainMenuScreen(Stage stage, GameState gameState) {
        this.stage = stage;
        this.gameState = gameState;

        // Ensure game starts in windowed mode
        stage.setFullScreen(false);
        gameState.getPlayer().setFullScreen(false);

        // Add window close handler to update session time when closing the window
        stage.setOnCloseRequest(e -> {
            gameState.saveSettings(true);
        });
    }

    /**
     * Creates a styled button with the specified text.
     *
     * @param text The text to display on the button.
     * @return A styled Button object.
     */
    private Button createStyledButton(String text) {
        Button button = new Button(text);
        button.setStyle("""
            -fx-background-color: #4CAF50;
            -fx-text-fill: white;
            -fx-font-size: 18px;
            -fx-padding: 10px 20px;
            -fx-min-width: 200px;
            -fx-background-radius: 5;
            -fx-cursor: hand;
            """);

        // Add hover effect
        button.setOnMouseEntered(e -> button.setStyle("""
            -fx-background-color: #45a049;
            -fx-text-fill: white;
            -fx-font-size: 18px;
            -fx-padding: 10px 20px;
            -fx-min-width: 200px;
            -fx-background-radius: 5;
            -fx-cursor: hand;
            """));

        button.setOnMouseExited(e -> button.setStyle("""
            -fx-background-color: #4CAF50;
            -fx-text-fill: white;
            -fx-font-size: 18px;
            -fx-padding: 10px 20px;
            -fx-min-width: 200px;
            -fx-background-radius: 5;
            -fx-cursor: hand;
            """));

        return button;
    }

    /**
     * Displays the main menu screen.
     */
    public void show() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #F5F5DC;"); // Beige background

        // Create the main content
        VBox mainContent = new VBox(20);
        mainContent.setAlignment(Pos.CENTER);
        mainContent.setPadding(new Insets(20));

        Text title = new Text("Tamagotchi Game");
        title.setStyle("-fx-font-size: 36px; -fx-font-weight: bold; -fx-fill: #8B4513;"); // Dark brown text

        // Create a container for the buttons
        VBox buttonContainer = new VBox(15);
        buttonContainer.setAlignment(Pos.CENTER);
        buttonContainer.setPadding(new Insets(20));
        buttonContainer.setStyle("-fx-background-color: #FFFFFF; -fx-border-color: #8B4513; -fx-border-width: 2px; -fx-border-radius: 10; -fx-background-radius: 10;");
        buttonContainer.setMaxWidth(400);

        Button newGameButton = createStyledButton("New Game");
        newGameButton.setOnAction(e -> {
            if (checkPlayTimeAllowed()) {
                NewGameScreen newGameScreen = new NewGameScreen(stage, gameState);
                newGameScreen.show();
            }
        });

        Button loadGameButton = createStyledButton("Load Game");
        loadGameButton.setOnAction(e -> {
            if (checkPlayTimeAllowed()) {
                LoadGameScreen loadGameScreen = new LoadGameScreen(stage, gameState);
                loadGameScreen.show();
            }
        });

        Button tutorialButton = createStyledButton("How to Play");
        tutorialButton.setOnAction(e -> {
            TutorialScreen tutorialScreen = new TutorialScreen(stage, gameState);
            tutorialScreen.show();
        });

        Button settingsButton = createStyledButton("Settings");
        settingsButton.setOnAction(e -> {
            SettingsScreen settingsScreen = new SettingsScreen(stage, gameState);
            settingsScreen.show();
        });

        Button parentalControlsButton = createStyledButton("Parental Controls");
        parentalControlsButton.setOnAction(e -> {
            ParentalControlsScreen controlsScreen = new ParentalControlsScreen(stage, gameState);
            controlsScreen.show();
        });

        Button exitButton = createStyledButton("Exit");
        exitButton.setOnAction(e -> {
            System.out.println("Exit button clicked - updating session time");
            gameState.saveSettings(true);
            Platform.exit();
        });
        exitButton.setStyle("""
            -fx-background-color: #F44336;
            -fx-text-fill: white;
            -fx-font-size: 18px;
            -fx-padding: 10px 20px;
            -fx-min-width: 200px;
            -fx-background-radius: 5;
            -fx-cursor: hand;
            """);

        // Add hover effect for exit button
        exitButton.setOnMouseEntered(e -> exitButton.setStyle("""
            -fx-background-color: #D32F2F;
            -fx-text-fill: white;
            -fx-font-size: 18px;
            -fx-padding: 10px 20px;
            -fx-min-width: 200px;
            -fx-background-radius: 5;
            -fx-cursor: hand;
            """));

        exitButton.setOnMouseExited(e -> exitButton.setStyle("""
            -fx-background-color: #F44336;
            -fx-text-fill: white;
            -fx-font-size: 18px;
            -fx-padding: 10px 20px;
            -fx-min-width: 200px;
            -fx-background-radius: 5;
            -fx-cursor: hand;
            """));

        buttonContainer.getChildren().addAll(
                newGameButton,
                loadGameButton,
                tutorialButton,
                settingsButton,
                parentalControlsButton,
                exitButton
        );

        mainContent.getChildren().addAll(title, buttonContainer);

        // Create team information section
        VBox teamInfo = new VBox(5);
        teamInfo.setAlignment(Pos.CENTER);
        teamInfo.setPadding(new Insets(10));
        teamInfo.setStyle("-fx-background-color: rgba(255, 255, 255, 0.8);");

        Text teamNumber = new Text("Team 2");
        teamNumber.setStyle("-fx-font-size: 14px; -fx-fill: #8B4513;");

        Text term = new Text("Winter 2025");
        term.setStyle("-fx-font-size: 14px; -fx-fill: #8B4513;");

        Text course = new Text("CS2212 - Western University");
        course.setStyle("-fx-font-size: 14px; -fx-fill: #8B4513;");

        Text developers = new Text("Developers: Hamza Khamissa, Mohannad Salem, Paul Chukwufumnaya Okogwu, Lloyd Manuel Ngaindjo, Thierry Duc Pierre Marc Huot");
        developers.setStyle("-fx-font-size: 12px; -fx-fill: #8B4513;");
        developers.setWrappingWidth(700);

        teamInfo.getChildren().addAll(teamNumber, term, course, developers);

        // Set up the BorderPane
        root.setCenter(mainContent);
        root.setBottom(teamInfo);

        scene = new Scene(root, 800, 600);
        stage.setScene(scene);
        stage.setTitle("Tamagotchi Game - Main Menu");
    }

    /**
     * Checks if the player is allowed to play at the current time.
     *
     * @return true if allowed, false otherwise.
     */
    private boolean checkPlayTimeAllowed() {
        if (!gameState.getPlayer().isAllowedToPlay(LocalTime.now())) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Play Time Restricted");
            alert.setHeaderText("You are not allowed to play at this time");
            alert.setContentText("The current time is outside your allowed play time. Please try again later.");
            alert.showAndWait();
            return false;
        }
        return true;
    }
} 