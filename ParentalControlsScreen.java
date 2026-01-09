package group02;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.geometry.Insets;

/**
 * ParentalControlsScreen class handles the parental controls functionality.
 * It allows parents to set a password and access parental settings.
 */
public class ParentalControlsScreen {
    private final Stage stage;
    private final GameState gameState;
    private Scene scene;
    private boolean isPasswordSet;

    /**
     * Constructor for ParentalControlsScreen.
     *
     * @param stage The primary stage for the application.
     * @param gameState The current game state.
     */
    public ParentalControlsScreen(Stage stage, GameState gameState) {
        this.stage = stage;
        this.gameState = gameState;
        this.isPasswordSet = gameState.getPlayer().hasParentalPassword();
        createScene();
    }

    /**
     * Creates the scene for the parental controls screen.
     */
    private void createScene() {
        VBox root = new VBox(20);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(20));
        root.setStyle("-fx-background-color: #F5F5DC;");

        if (!isPasswordSet) {
            createPasswordSetupScene(root);
        } else {
            createLoginScene(root);
        }

        scene = new Scene(root, 800, 600);
    }

    /**
     * Creates the password setup scene for parental controls.
     *
     * @param root The root layout for the scene.
     */
    private void createPasswordSetupScene(VBox root) {
        Text title = new Text("Set Parental Controls Password");
        title.setStyle("-fx-font-size: 32px; -fx-font-weight: bold;");

        // Create a container for the password section
        VBox passwordContainer = new VBox(15);
        passwordContainer.setAlignment(Pos.CENTER);
        passwordContainer.setPadding(new Insets(20));
        passwordContainer.setStyle("-fx-background-color: #FFFFFF; -fx-border-color: #8B4513; -fx-border-width: 2px; -fx-border-radius: 10; -fx-background-radius: 10;");
        passwordContainer.setMaxWidth(400);

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Enter password");
        passwordField.setMaxWidth(300);
        passwordField.setStyle("-fx-font-size: 16px; -fx-padding: 10px;");

        PasswordField confirmPasswordField = new PasswordField();
        confirmPasswordField.setPromptText("Confirm password");
        confirmPasswordField.setMaxWidth(300);
        confirmPasswordField.setStyle("-fx-font-size: 16px; -fx-padding: 10px;");

        Label errorLabel = new Label("");
        errorLabel.setStyle("-fx-text-fill: #f44336; -fx-font-size: 14px;");

        Button setPasswordButton = createStyledButton("Set Password");
        setPasswordButton.setOnAction(e -> {
            String password = passwordField.getText();
            String confirmPassword = confirmPasswordField.getText();

            if (password.isEmpty() || confirmPassword.isEmpty()) {
                errorLabel.setText("Please fill in both fields");
            } else if (!password.equals(confirmPassword)) {
                errorLabel.setText("Passwords do not match");
            } else if (password.length() < 6) {
                errorLabel.setText("Password must be at least 6 characters");
            } else {
                gameState.getPlayer().setParentalPassword(password);
                ParentalSettingsScreen settingsScreen = new ParentalSettingsScreen(stage, gameState);
                settingsScreen.show();
            }
        });

        // Add enter key handler
        confirmPasswordField.setOnAction(e -> setPasswordButton.fire());

        passwordContainer.getChildren().addAll(passwordField, confirmPasswordField, errorLabel, setPasswordButton);

        Button backButton = createStyledButton("Back to Main Menu");
        backButton.setStyle(backButton.getStyle().replace("#4CAF50", "#9E9E9E"));
        backButton.setOnAction(e -> {
            MainMenuScreen mainMenu = new MainMenuScreen(stage, gameState);
            mainMenu.show();
        });

        root.getChildren().addAll(
            title,
            passwordContainer,
            backButton
        );
    }

    /**
     * Creates the login scene for parental controls.
     *
     * @param root The root layout for the scene.
     */
    private void createLoginScene(VBox root) {
        Text title = new Text("Parental Controls Login");
        title.setStyle("-fx-font-size: 32px; -fx-font-weight: bold;");

        // Create a container for the password section
        VBox passwordContainer = new VBox(15);
        passwordContainer.setAlignment(Pos.CENTER);
        passwordContainer.setPadding(new Insets(20));
        passwordContainer.setStyle("-fx-background-color: #FFFFFF; -fx-border-color: #8B4513; -fx-border-width: 2px; -fx-border-radius: 10; -fx-background-radius: 10;");
        passwordContainer.setMaxWidth(400);

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Enter password");
        passwordField.setMaxWidth(300);
        passwordField.setStyle("-fx-font-size: 16px; -fx-padding: 10px;");

        Label errorLabel = new Label("");
        errorLabel.setStyle("-fx-text-fill: #f44336; -fx-font-size: 14px;");

        Button loginButton = createStyledButton("Login");
        loginButton.setOnAction(e -> {
            String password = passwordField.getText();
            if (gameState.getPlayer().getParentalPassword().equals(password)) {
                ParentalSettingsScreen settingsScreen = new ParentalSettingsScreen(stage, gameState);
                settingsScreen.show();
            } else {
                errorLabel.setText("Incorrect password");
            }
        });

        // Add enter key handler
        passwordField.setOnAction(e -> loginButton.fire());

        passwordContainer.getChildren().addAll(passwordField, errorLabel, loginButton);

        Button backButton = createStyledButton("Back to Main Menu");
        backButton.setStyle(backButton.getStyle().replace("#4CAF50", "#9E9E9E"));
        backButton.setOnAction(e -> {
            MainMenuScreen mainMenu = new MainMenuScreen(stage, gameState);
            mainMenu.show();
        });

        root.getChildren().addAll(
            title,
            passwordContainer,
            backButton
        );
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
            -fx-font-size: 16px;
            -fx-padding: 10px 20px;
            -fx-background-radius: 5;
            -fx-cursor: hand;
            -fx-min-width: 200px;
            """);
        return button;
    }

    /**
     * Shows the parental controls screen.
     */
    public void show() {
        stage.setScene(scene);
        stage.setTitle("Parental Controls");
    }
} 