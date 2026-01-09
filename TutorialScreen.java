package group02;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

/**
 * The tutorial screen of the game, providing instructions on how to play.
 */
public class TutorialScreen {
    private final Stage stage;
    private final GameState gameState;
    private Scene scene;

    /**
     * Constructor for the TutorialScreen.
     *
     * @param stage The primary stage for the application.
     * @param gameState The current game state.
     */
    public TutorialScreen(Stage stage, GameState gameState) {
        this.stage = stage;
        this.gameState = gameState;
        createScene();
    }

    /**
     * Creates the scene for the tutorial screen.
     */
    private void createScene() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #F5F5DC;");

        // Create title
        Text title = new Text("How to Play");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 32));
        title.setStyle("-fx-fill: #8B4513;");
        
        VBox titleBox = new VBox(10);
        titleBox.setAlignment(Pos.CENTER);
        titleBox.setPadding(new Insets(20));
        titleBox.getChildren().add(title);
        root.setTop(titleBox);

        // Create scrollable content
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: #F5F5DC;");
        
        VBox content = new VBox(20);
        content.setPadding(new Insets(20));
        content.setAlignment(Pos.TOP_CENTER);
        
        // Add sections
        createContent(content);
        
        scrollPane.setContent(content);
        root.setCenter(scrollPane);

        // Create bottom bar with back button
        VBox bottomBar = new VBox(10);
        bottomBar.setAlignment(Pos.CENTER);
        bottomBar.setPadding(new Insets(20));
        bottomBar.setStyle("-fx-background-color: #D2B48C;");
        
        Button backButton = createStyledButton("Back to Menu");
        backButton.setOnAction(e -> {
            MainMenuScreen mainMenu = new MainMenuScreen(stage, gameState);
            mainMenu.show();
        });
        
        bottomBar.getChildren().add(backButton);
        root.setBottom(bottomBar);

        scene = new Scene(root, 800, 600);
        stage.setScene(scene);
    }

    /**
     * Creates the content for the tutorial screen.
     *
     * @param content The VBox to add the content to.
     */
    private void createContent(VBox content) {
        // Getting Started section
        VBox gettingStarted = new VBox(10);
        gettingStarted.setStyle("-fx-background-color: #F5F5DC; -fx-border-color: #8B4513; -fx-border-width: 1px; -fx-border-radius: 5px;");
        gettingStarted.setPadding(new Insets(15));
        
        Text gettingStartedTitle = new Text("Getting Started");
        gettingStartedTitle.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        
        Text gettingStartedText = new Text(
            "Welcome to Virtual Pet! Here's how to get started:\n\n" +
            "1. Choose your pet type:\n" +
            "   • Dogs get hungry faster but maintain happiness well\n" +
            "   • Cats need more attention to stay happy but conserve energy well\n" +
            "   • Bunnies get tired quickly but are easy to keep fed\n\n" +
            "2. Take care of your pet:\n" +
            "   • Feed them regularly to maintain fullness\n" +
            "   • Play with them to keep them happy\n" +
            "   • Let them sleep to restore energy\n" +
            "   • Visit the vet (50 dabloons) to heal them\n\n" +
            "3. Earn dabloons by:\n" +
            "   • Playing with your pet\n" +
            "   • Letting them sleep\n" +
            "   • Exercising with them\n\n" +
            "4. Keep your pet healthy:\n" +
            "   • Watch their stats carefully\n" +
            "   • Use gifts to increase their max health\n" +
            "   • Visit the vet when needed\n\n" +
            "Remember: Each pet type has unique traits and favorite foods that provide extra benefits!"
        );
        gettingStartedText.setWrappingWidth(400);
        gettingStartedText.setLineSpacing(5);
        
        gettingStarted.getChildren().addAll(gettingStartedTitle, gettingStartedText);

        // Pet Stats section
        content.getChildren().add(createSection("Pet Stats",
            "Your pet has four main stats to manage:\n\n" +
            "• Health: Your pet's overall well-being\n" +
            "• Energy: How active your pet can be\n" +
            "• Fullness: How hungry your pet is\n" +
            "• Happiness: Your pet's emotional state\n\n" +
            "Warning: Stats below 25% will trigger alerts!"
        ));

        // Pet-Specific Food section
        content.getChildren().add(createSection("Pet-Specific Food",
            "Each pet type has favorite foods that provide extra benefits:\n\n" +
            "• Dogs: Premium Food (+25 Fullness, +15 Happiness)\n" +
            "• Cats: Tuna (+20 Fullness, +20 Happiness)\n" +
            "• Bunnies: Lettuce (+15 Fullness, +25 Energy)\n\n" +
            "Regular food items still work for all pets, but favorites give better results!"
        ));

        // Earning Points & Dabloons section
        content.getChildren().add(createSection("Earning Points & Dabloons",
            "You can earn points and dabloons by:\n\n" +
            "• Playing with your pet (+15 points, 5-9 dabloons)\n" +
            "• Letting your pet sleep (+10 points, 3-5 dabloons)\n" +
            "• Feeding your pet (+10 points)\n" +
            "• Using special items (+20 points)\n" +
            "• Visiting the vet (+20 points)"
        ));

        // Keeping Your Pet Alive section
        content.getChildren().add(createSection("Keeping Your Pet Alive",
            "To keep your pet healthy and happy:\n\n" +
            "• Feed your pet regularly\n" +
            "• Let your pet sleep when tired\n" +
            "• Play with your pet to maintain happiness\n" +
            "• Use special items to boost stats\n" +
            "• Visit the vet when health is low\n\n" +
            "Remember: Each pet type has different needs!"
        ));

        // Hot Keys section
        content.getChildren().add(createSection("Hot Keys",
            "Use these keyboard shortcuts for quick actions:\n\n" +
            "• F - Feed Pet\n" +
            "• P - Play\n" +
            "• S - Sleep\n" +
            "• I - Use Item\n" +
            "• B - Shop\n" +
            "• V - Visit Vet\n" +
            "• ESC - Main Menu"
        ));

        // Shop Items section
        content.getChildren().add(createSection("Shop Items",
            "The shop offers various items:\n\n" +
            "• Regular Food: Kibble, Treats, Premium Food\n" +
            "• Pet-Specific Food: Tuna (Cat), Lettuce (Bunny)\n" +
            "• Special Items: Toys, Gifts, Health Boosters\n\n" +
            "Special items can be used to increase happiness and health!"
        ));

        // Exercise section
        content.getChildren().add(createSection("Exercise",
            "Exercise is important for your pet's health:\n\n" +
            "• Increases health significantly\n" +
            "• Greatly reduces energy\n" +
            "• Available in the bottom bar\n" +
            "• Best used when energy is high\n\n" +
            "Warning: Don't over-exercise your pet!"
        ));

        // Saving Your Game section
        content.getChildren().add(createSection("Saving Your Game",
            "Your game is automatically saved when:\n\n" +
            "• You return to the main menu\n" +
            "• You close the game\n" +
            "• You start a new game\n\n" +
            "You can also manually save using the save button."
        ));
    }

    /**
     * Creates a styled section with a title and content.
     *
     * @param title The title of the section.
     * @param content The content of the section.
     * @return A VBox containing the styled section.
     */
    private VBox createSection(String title, String content) {
        VBox section = new VBox(10);
        section.setStyle("-fx-background-color: #FFFFFF; -fx-border-color: #8B4513; -fx-border-width: 2px; -fx-border-radius: 10; -fx-background-radius: 10;");
        section.setPadding(new Insets(15));
        
        Text titleText = new Text(title);
        titleText.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        titleText.setStyle("-fx-fill: #8B4513;");
        
        Text contentText = new Text(content);
        contentText.setFont(Font.font("Arial", 14));
        contentText.setWrappingWidth(700);
        
        section.getChildren().addAll(titleText, contentText);
        return section;
    }

    /**
     * Creates a styled button with hover effects.
     *
     * @param text The text to display on the button.
     * @return A styled Button.
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
     * Displays the tutorial screen.
     */
    public void show() {
        stage.setTitle("How to Play");
        stage.show();
    }
} 