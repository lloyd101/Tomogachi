package group02;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.geometry.Insets;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalTime;
import java.util.Optional;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Screen;

/**
 * ParentalSettingsScreen class handles the parental settings functionality.
 * It allows parents to set time restrictions and view game statistics.
 */
public class ParentalSettingsScreen {
    private final Stage stage;
    private final GameState gameState;
    private Scene scene;
    private CheckBox enableTimeRestrictions;
    private ComboBox<String> startTime;
    private ComboBox<String> endTime;

    /**
     * Constructor for ParentalSettingsScreen.
     *
     * @param stage The primary stage for the application.
     * @param gameState The current game state.
     */
    public ParentalSettingsScreen(Stage stage, GameState gameState) {
        this.stage = stage;
        this.gameState = gameState;
        createScene();
    }

    /**
     * Creates the scene for the parental settings screen.
     */
    private void createScene() {
        // Create a ScrollPane as the root container
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: #F5F5DC; -fx-background-color: #F5F5DC;");

        VBox root = new VBox(30);
        root.setAlignment(Pos.TOP_CENTER);
        root.setPadding(new Insets(40));
        root.setStyle("-fx-background-color: #F5F5DC;"); // Beige background

        Text title = new Text("Parental Controls");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 36));
        title.setStyle("-fx-fill: #8B4513;"); // Dark brown text

        // Time settings section (combines daily limit and allowed times)
        VBox timeSettingsBox = createTimeSection();

        // Game statistics section
        VBox statsSection = createStatsSection();

        // Navigation buttons
        HBox buttonBox = new HBox(20);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setPadding(new Insets(20, 0, 40, 0));

        Button saveButton = createStyledButton("Save Settings");
        saveButton.setOnAction(e -> {
            saveSettings();
            showSuccessDialog();
        });

        Button revivePetButton = createStyledButton("Revive Pet");
        revivePetButton.setOnAction(e -> {
            showRevivePetDialog();
        });

        Button resetStatsButton = createStyledButton("Reset Stats");
        resetStatsButton.setOnAction(e -> {
            resetAllStats();
        });

        Button backButton = createStyledButton("Back to Main Menu");
        backButton.setStyle(backButton.getStyle().replace("#4CAF50", "#8B4513")); // Brown for back button
        backButton.setOnAction(e -> {
            // Save settings before going back
            saveSettings();
            MainMenuScreen mainMenu = new MainMenuScreen(stage, gameState);
            mainMenu.show();
        });

        buttonBox.getChildren().addAll(saveButton, revivePetButton, resetStatsButton, backButton);

        // Add all sections to root
        root.getChildren().addAll(
                title,
                timeSettingsBox,
                statsSection,
                buttonBox
        );

        // Set the VBox as the content of ScrollPane
        scrollPane.setContent(root);

        // Create the scene with the ScrollPane
        scene = new Scene(scrollPane, 800, Math.min(700, Screen.getPrimary().getVisualBounds().getHeight() * 0.9));
    }

    /**
     * Creates the time settings section.
     *
     * @return VBox containing the time settings UI elements.
     */
    private VBox createTimeSection() {
        VBox timeBox = new VBox(20);
        timeBox.setAlignment(Pos.CENTER);
        timeBox.setPadding(new Insets(25));
        timeBox.setMaxWidth(500);
        timeBox.setStyle("-fx-background-color: #FFFFFF; -fx-border-color: #8B4513; -fx-border-width: 2px; -fx-border-radius: 10; -fx-background-radius: 10;");

        Text timeTitle = new Text("Time Restrictions");
        timeTitle.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        timeTitle.setStyle("-fx-fill: #8B4513;");

        // Add enable/disable time restrictions checkbox
        enableTimeRestrictions = new CheckBox("Enable Time Restrictions");
        enableTimeRestrictions.setSelected(gameState.getPlayer().isTimeRestrictionsEnabled());
        enableTimeRestrictions.setFont(Font.font("Arial", FontWeight.NORMAL, 16));

        // Allowed play times
        Text playTimesLabel = new Text("Allowed Play Times:");
        playTimesLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 16));

        HBox timePickerBox = new HBox(20);
        timePickerBox.setAlignment(Pos.CENTER);
        timePickerBox.setPadding(new Insets(10, 0, 0, 0));

        startTime = new ComboBox<>();
        endTime = new ComboBox<>();
        startTime.setPrefWidth(100);
        endTime.setPrefWidth(100);

        // Populate time options (24-hour format)
        for (int hour = 0; hour < 24; hour++) {
            String time = String.format("%02d:00", hour);
            startTime.getItems().add(time);
            endTime.getItems().add(time);
        }

        startTime.setValue(formatTime(gameState.getPlayer().getAllowedStartTime()));
        endTime.setValue(formatTime(gameState.getPlayer().getAllowedEndTime()));

        VBox startBox = new VBox(5);
        startBox.setAlignment(Pos.CENTER);
        Text fromText = new Text("From:");
        fromText.setFont(Font.font("Arial", FontWeight.NORMAL, 14));
        startBox.getChildren().addAll(fromText, startTime);

        VBox endBox = new VBox(5);
        endBox.setAlignment(Pos.CENTER);
        Text toText = new Text("To:");
        toText.setFont(Font.font("Arial", FontWeight.NORMAL, 14));
        endBox.getChildren().addAll(toText, endTime);

        timePickerBox.getChildren().addAll(startBox, endBox);

        timeBox.getChildren().addAll(
                timeTitle,
                new Separator(),
                enableTimeRestrictions,
                playTimesLabel,
                timePickerBox
        );

        return timeBox;
    }

    /**
     * Creates the game statistics section.
     *
     * @return VBox containing the game statistics UI elements.
     */
    private VBox createStatsSection() {
        VBox statsBox = new VBox(15);
        statsBox.setAlignment(Pos.CENTER);
        statsBox.setPadding(new Insets(25));
        statsBox.setMaxWidth(500);
        statsBox.setStyle("-fx-background-color: #FFFFFF; -fx-border-color: #8B4513; -fx-border-width: 2px; -fx-border-radius: 10; -fx-background-radius: 10;");

        Text statsTitle = new Text("Game Statistics");
        statsTitle.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        statsTitle.setStyle("-fx-fill: #8B4513;");

        GridPane statsGrid = new GridPane();
        statsGrid.setHgap(20);
        statsGrid.setVgap(10);
        statsGrid.setAlignment(Pos.CENTER);

        // Calculate time played
        long totalMinutesPlayed = gameState.getPlayer().getTotalPlayTime() / (1000 * 60);
        String timePlayedStr = String.format("%d hours, %d minutes",
                totalMinutesPlayed / 60, totalMinutesPlayed % 60);

        addStatRow(statsGrid, 0, "Total Time Played:", timePlayedStr);

        // Calculate average session time more accurately
        long avgSessionMinutes = 0;
        if (gameState.getPlayer().getNumberOfSessions() > 0) {
            avgSessionMinutes = totalMinutesPlayed / gameState.getPlayer().getNumberOfSessions();
        }

        String averageTimeStr = String.format("%d minutes", avgSessionMinutes);
        addStatRow(statsGrid, 1, "Average Session Time:", averageTimeStr);

        // Add total sessions
        addStatRow(statsGrid, 2, "Total Sessions:", String.valueOf(gameState.getPlayer().getNumberOfSessions()));

        statsBox.getChildren().addAll(statsTitle, new Separator(), statsGrid);

        return statsBox;
    }

    /**
     * Adds a row to the statistics grid.
     *
     * @param grid The GridPane to add the row to.
     * @param row The row index.
     * @param label The label for the statistic.
     * @param value The value of the statistic.
     */
    private void addStatRow(GridPane grid, int row, String label, String value) {
        Text labelText = new Text(label);
        Text valueText = new Text(value);
        labelText.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        valueText.setFont(Font.font("Arial", FontWeight.NORMAL, 14));
        grid.add(labelText, 0, row);
        grid.add(valueText, 1, row);
    }

    /**
     * Creates a styled button.
     *
     * @param text The text to display on the button.
     * @return A Button with the specified text and style.
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
        return button;
    }

    /**
     * Shows an alert dialog with the specified title and message.
     *
     * @param title The title of the alert.
     * @param message The message to display in the alert.
     */
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Shows a success dialog after saving settings.
     */
    private void showSuccessDialog() { showAlert("Success", "Settings have been saved successfully!"); }

    /**
     * Formats a LocalTime object to a string in HH:00 format.
     *
     * @param time The LocalTime object to format.
     * @return A string representing the formatted time.
     */
    private String formatTime(LocalTime time) { return String.format("%02d:00", time.getHour()); }

    /**
     * Parses a string in HH:00 format to a LocalTime object.
     *
     * @param timeStr The string to parse.
     * @return A LocalTime object representing the parsed time.
     */
    private LocalTime parseTime(String timeStr) {
        String[] parts = timeStr.split(":");
        return LocalTime.of(Integer.parseInt(parts[0]), 0);
    }

    /**
     * Saves the parental settings.
     */
    private void saveSettings() {
        // Save the time restriction settings
        gameState.getPlayer().setTimeRestrictionsEnabled(enableTimeRestrictions.isSelected());
        gameState.getPlayer().setAllowedStartTime(parseTime(startTime.getValue()));
        gameState.getPlayer().setAllowedEndTime(parseTime(endTime.getValue()));

        // Save settings to a special parental controls file, not the main game file
        gameState.saveSettings(false);
    }

    /**
     * Resets all statistics to zero.
     */
    private void resetAllStats() {
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Reset Statistics");
        confirmAlert.setHeaderText("Reset All Statistics?");
        confirmAlert.setContentText("This will reset all time tracking statistics to zero. This action cannot be undone.");

        Optional<ButtonType> result = confirmAlert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            gameState.getPlayer().resetStats();
            showAlert("Statistics Reset", "All play time statistics have been reset to zero.");
        }
    }

    /**
     * Shows a dialog to revive a pet.
     */
    private void showRevivePetDialog() {
        // Create dialog
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Revive Pet");
        dialog.setHeaderText("Select a save slot to revive pet");

        // Set the button types
        ButtonType dogSaveType = new ButtonType("Dog Save", ButtonBar.ButtonData.OK_DONE);
        ButtonType catSaveType = new ButtonType("Cat Save", ButtonBar.ButtonData.OK_DONE);
        ButtonType bunnySaveType = new ButtonType("Bunny Save", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButtonType = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(dogSaveType, catSaveType, bunnySaveType, cancelButtonType);

        // Create content
        VBox content = new VBox(10);
        content.setPadding(new Insets(20));

        Text infoText = new Text("Reviving will set the pet's health, happiness, energy, and fullness to maximum values.");
        infoText.setWrappingWidth(400);

        content.getChildren().add(infoText);
        dialog.getDialogPane().setContent(content);

        // Convert the result
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == dogSaveType) {
                return "dog";
            } else if (dialogButton == catSaveType) {
                return "cat";
            } else if (dialogButton == bunnySaveType) {
                return "bunny";
            }
            return null;
        });

        // Show the dialog and process the result
        Optional<String> result = dialog.showAndWait();
        result.ifPresent(this::revivePet);
    }

    /**
     * Revives the pet by reading from the save file and updating stats.
     *
     * @param petType The type of pet to revive (dog, cat, or bunny).
     */
    private void revivePet(String petType) {
        String saveFile = GameState.SAVE_DIRECTORY + petType.toLowerCase() + "_save.txt";
        File file = new File(saveFile);

        if (!file.exists()) {
            showAlert("Error", "No save file found for " + petType);
            return;
        }

        try {
            // Read the save file
            java.util.Map<String, String> saveData = new java.util.HashMap<>();
            java.util.Scanner scanner = new java.util.Scanner(file);
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] parts = line.split("=");
                if (parts.length == 2) {
                    saveData.put(parts[0], parts[1]);
                }
            }
            scanner.close();

            // Update pet stats to maximum values
            saveData.put("health", "100");
            saveData.put("happiness", "100");
            saveData.put("fullness", "100");
            saveData.put("energy", "100");

            // Write back to file
            PrintWriter writer = new PrintWriter(file);
            for (java.util.Map.Entry<String, String> entry : saveData.entrySet()) {
                writer.println(entry.getKey() + "=" + entry.getValue());
            }
            writer.close();

            showAlert("Success", petType + " has been revived with maximum stats!");

        } catch (IOException e) {
            showAlert("Error", "Failed to revive pet: " + e.getMessage());
        }
    }

    /**
     * Shows the parental settings screen.
     */
    public void show() {
        stage.setScene(scene);
        stage.setTitle("Parental Controls");
        
        // Set minimum dimensions for the window
        stage.setMinWidth(600);
        stage.setMinHeight(400);
    }
} 