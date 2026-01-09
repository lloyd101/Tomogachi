package group02;

import javafx.application.Application;
import javafx.stage.Stage;
import java.io.*;

/**
 * Main class to launch the Tamagotchi virtual pet application.
 */
public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        // Create the saves directory if it doesn't exist
        File savesDir = new File("saves");
        if (!savesDir.exists()) {
            savesDir.mkdir();
        }

        // Initialize game state
        GameState gameState = new GameState();

        // Set up stage properties
        primaryStage.setTitle("Tamagotchi Game");
        primaryStage.setResizable(true);
        primaryStage.setMinWidth(800);
        primaryStage.setMinHeight(600);

        // Force windowed mode at startup
        primaryStage.setFullScreen(false);
        gameState.getPlayer().setFullScreen(false);

        // Show main menu
        MainMenuScreen mainMenu = new MainMenuScreen(primaryStage, gameState);
        mainMenu.show();

        // Update session time when window is closed
        primaryStage.setOnCloseRequest(event -> {
            System.out.println("Window close requested - updating session time");

            if (gameState.getPet() == null) {
                gameState.saveSettings(true);
            }else {
                gameState.saveAll(gameState.getPet().getType().toUpperCase() + "_save.txt", true);
            }


        });

        primaryStage.show();
    }

    /**
     * Main method to launch the application.
     */
    public static void main(String[] args) {
        launch(args);
    }
} 