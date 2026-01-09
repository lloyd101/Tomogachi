package group02;

import javafx.animation.AnimationTimer;
import javafx.animation.ScaleTransition;
import javafx.animation.Timeline;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.io.File;
import javafx.scene.image.ImageView;
import javafx.util.Duration;

/**
 * The GameScreen class handles the main gameplay screen where the player interacts with their pet.
 */
public class GameScreen {
    private Stage stage;
    private GameState gameState;
    private Pet pet;
    private Scene scene;
    private Stage dialog;
    
    // UI elements
    private Text petNameText;
    private Text healthText;
    private Text happinessText;
    private Text fullnessText;
    private Text energyText;
    private ProgressBar healthBar;
    private ProgressBar happinessBar;
    private ProgressBar fullnessBar;
    private ProgressBar energyBar;
    private ImageView petSprite;
    private Text scoreText;
    private Text currencyText;
    
    // Game loop for periodic updates
    private AnimationTimer gameLoop;
    private long lastUpdate = 0;
    
    private VBox root;
    private VBox centerBox;
    private VBox inventoryBox;
    
    private boolean healthWarningShown = false;
    private boolean energyWarningShown = false;
    private boolean fullnessWarningShown = false;
    private boolean happinessWarningShown = false;

    /**
     * Constructor for the GameScreen class.
     *
     * @param stage The primary stage for the application.
     * @param gameState The current game state.
     * @param pet The pet object representing the player's pet.
     */
    public GameScreen(Stage stage, GameState gameState, Pet pet) {
        this.stage = stage;
        this.gameState = gameState;
        this.pet = pet;
        createScene();
    }
    
    private void createScene() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #F5F5DC;");

        // Create the top bar with pet name and stats
        HBox topBar = createTopBar();
        root.setTop(topBar);
        
        // Create center area with pet sprite and stats
        HBox centerArea = new HBox(0);
        centerArea.setAlignment(Pos.CENTER);
        
        // Create inventory display (left side)
        VBox inventoryBox = createInventoryDisplay();
        inventoryBox.setPrefWidth(250);
        inventoryBox.setMaxWidth(250);
        
        // Create pet sprite area (center)
        VBox petArea = new VBox(10);
        petArea.setAlignment(Pos.CENTER);
        petArea.setPadding(new Insets(20));
        petArea.setStyle("-fx-background-color: #FAFAD2;");
        HBox.setHgrow(petArea, Priority.ALWAYS);
        
        // Create pet sprite
        String prefix = pet.getType().toUpperCase();
        String spritePath = "images/" + prefix + "_NORMAL.png";
        try {
            File imageFile = new File(spritePath);
            System.out.println("Initial sprite loading: " + spritePath);
            System.out.println("Pet type: " + pet.getType());
            System.out.println("File exists: " + imageFile.exists());
            System.out.println("File absolute path: " + imageFile.getAbsolutePath());
            String imageURI = imageFile.toURI().toString();
            System.out.println("Image URI: " + imageURI);
            javafx.scene.image.Image image = new javafx.scene.image.Image(imageURI);
            petSprite = new ImageView(image);
            petSprite.setFitWidth(200);
            petSprite.setFitHeight(200);
            petSprite.setPreserveRatio(true);
            petSprite.setSmooth(true);
            
            // Add idle animation
            Timeline idleAnimation = new Timeline(
                new KeyFrame(Duration.ZERO, 
                    new KeyValue(petSprite.translateYProperty(), 0)
                ),
                new KeyFrame(Duration.seconds(1), 
                    new KeyValue(petSprite.translateYProperty(), -10)
                ),
                new KeyFrame(Duration.seconds(2), 
                    new KeyValue(petSprite.translateYProperty(), 0)
                )
            );
            idleAnimation.setCycleCount(Timeline.INDEFINITE);
            idleAnimation.play();
            
            petArea.getChildren().add(petSprite);
            System.out.println("Initial sprite loaded successfully");
        } catch (Exception e) {
            System.err.println("Error loading initial sprite: " + spritePath);
            System.err.println("Error details: " + e.getMessage());
            e.printStackTrace();
            // Create a colored rectangle as fallback
            Rectangle fallback = new Rectangle(200, 200);
            fallback.setFill(getPetColor());
            petArea.getChildren().add(fallback);
        }
        
        // Create stats display (right side)
        VBox statsBox = createStatsDisplay();
        statsBox.setPrefWidth(250);
        statsBox.setMaxWidth(250);
        
        centerArea.getChildren().addAll(inventoryBox, petArea, statsBox);
        root.setCenter(centerArea);

        // Create bottom bar with buttons
        HBox bottomBar = createControls();
        root.setBottom(bottomBar);

        scene = new Scene(root, 800, 600);
        
        // Setup keyboard shortcuts
        setupKeyboardShortcuts();
        
        // Start the game loop
        setupGameLoop();
    }
    
    private HBox createTopBar() {
        HBox topBar = new HBox(20);
        topBar.setPadding(new Insets(15));
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.setStyle("-fx-background-color: #D2B48C;");

        // Pet name display
        petNameText = new Text(pet.getName());
        petNameText.setFont(Font.font("Arial", FontWeight.BOLD, 24));

        // Score display
        scoreText = new Text("Score: " + pet.getScore());
        scoreText.setFont(Font.font("Arial", 18));

        // Currency display
        currencyText = new Text("Dabloons: " + pet.getCurrency());
        currencyText.setFont(Font.font("Arial", 18));
        
        // Add a save button
        Button saveButton = createStyledButton("Save Game");
        saveButton.setOnAction(e -> saveGame());
        
        // Add a back button
        Button backButton = createStyledButton("Main Menu");
        backButton.setOnAction(e -> {
            saveGame(); // Save before returning to menu
            MainMenuScreen mainMenuScreen = new MainMenuScreen(stage, gameState);
            mainMenuScreen.show();
        });

        // Adding spacer to push save button to the right
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        topBar.getChildren().addAll(petNameText, scoreText, currencyText, spacer, saveButton, backButton);
        return topBar;
    }

    private VBox createInventoryDisplay() {
        inventoryBox = new VBox(15);
        inventoryBox.setPadding(new Insets(15));
        inventoryBox.setStyle("-fx-background-color: #E6E6FA; -fx-border-color: #8B4513; -fx-border-width: 2px;");
        inventoryBox.setPrefWidth(200);
        
        Text inventoryTitle = new Text("Inventory");
        inventoryTitle.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        
        // Display food items
        VBox foodItems = new VBox(5);
        Text foodTitle = new Text("Food:");
        foodTitle.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        
        Text kibbleCount = new Text("Kibble: " + pet.getItemCount("Kibble"));
        Text treatsCount = new Text("Treats: " + pet.getItemCount("Treats"));
        Text premiumFoodCount = new Text("Premium Food: " + pet.getItemCount("Premium Food"));
        
        foodItems.getChildren().addAll(foodTitle, kibbleCount, treatsCount, premiumFoodCount);
        
        // Display gifts
        VBox giftItems = new VBox(5);
        Text giftTitle = new Text("Gifts:");
        giftTitle.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        
        Text toyBallCount = new Text("Toy Ball: " + pet.getItemCount("Toy Ball"));
        Text comfortBlanketCount = new Text("Comfort Blanket: " + pet.getItemCount("Comfort Blanket"));
        Text healthTreatCount = new Text("Health Treat: " + pet.getItemCount("Health Treat"));
        
        giftItems.getChildren().addAll(giftTitle, toyBallCount, comfortBlanketCount, healthTreatCount);
        
        inventoryBox.getChildren().addAll(inventoryTitle, foodItems, giftItems);
        
        // Add tooltips section at the bottom
        VBox tooltipsBox = new VBox(5);
        tooltipsBox.setStyle("-fx-background-color: #F5F5DC; -fx-border-color: #8B4513; -fx-border-width: 1px; -fx-border-radius: 5px;");
        tooltipsBox.setPadding(new Insets(10));
        
        Text tooltipsTitle = new Text("Hot Keys");
        tooltipsTitle.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        
        Text feedTip = new Text("F - Feed Pet");
        Text playTip = new Text("P - Play");
        Text sleepTip = new Text("S - Sleep");
        Text giftTip = new Text("G - Give Gift");
        Text shopTip = new Text("B - Shop");
        Text vetTip = new Text("V - Visit Vet");
        Text menuTip = new Text("ESC - Main Menu");
        
        // Style the tooltips
        feedTip.setStyle("-fx-font-size: 12px;");
        playTip.setStyle("-fx-font-size: 12px;");
        sleepTip.setStyle("-fx-font-size: 12px;");
        giftTip.setStyle("-fx-font-size: 12px;");
        shopTip.setStyle("-fx-font-size: 12px;");
        vetTip.setStyle("-fx-font-size: 12px;");
        menuTip.setStyle("-fx-font-size: 12px;");
        
        tooltipsBox.getChildren().addAll(tooltipsTitle, feedTip, playTip, sleepTip, giftTip, shopTip, vetTip, menuTip);
        
        // Add spacer to push tooltips to bottom
        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);
        
        inventoryBox.getChildren().addAll(spacer, tooltipsBox);
        return inventoryBox;
    }

    private VBox createStatsDisplay() {
        VBox statsBox = new VBox(15);
        statsBox.setPadding(new Insets(15));
        statsBox.setStyle("-fx-background-color: #E6E6FA; -fx-border-color: #8B4513; -fx-border-width: 2px;");
        statsBox.setPrefWidth(200);
        
        Text statsTitle = new Text("Pet Stats");
        statsTitle.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        
        // Create the status bars
        healthBar = createStatusBar(pet.getHealth(), Color.RED);
        healthText = new Text("Health: " + pet.getHealth() + "/" + pet.getMaxHealth());
        
        happinessBar = createStatusBar(pet.getHappiness(), Color.PINK);
        happinessText = new Text("Happiness: " + pet.getHappiness());
        
        fullnessBar = createStatusBar(pet.getFullness(), Color.GREEN);
        fullnessText = new Text("Fullness: " + pet.getFullness());
        
        energyBar = createStatusBar(pet.getEnergy(), Color.BLUE);
        energyText = new Text("Energy: " + pet.getEnergy());
        
        statsBox.getChildren().addAll(
            statsTitle,
            healthText, healthBar,
            happinessText, happinessBar,
            fullnessText, fullnessBar,
            energyText, energyBar
        );
        
        return statsBox;
    }

    private HBox createControls() {
        HBox controls = new HBox(15);
        controls.setPadding(new Insets(15));
        controls.setAlignment(Pos.CENTER);
        controls.setStyle("-fx-background-color: #D2B48C;");
        
        Button feedButton = createStyledButton("Feed");
        feedButton.setOnAction(e -> showFeedOptions());
        
        Button playButton = createStyledButton("Play");
        playButton.setOnAction(e -> play());
        
        Button sleepButton = createStyledButton("Sleep");
        sleepButton.setOnAction(e -> sleep());
        
        Button exerciseButton = createStyledButton("Exercise");
        exerciseButton.setOnAction(e -> exercise());
        
        Button giftButton = createStyledButton("Gift");
        giftButton.setOnAction(e -> showUseItemOptions());
        
        Button shopButton = createStyledButton("Shop");
        shopButton.setOnAction(e -> {
            stopGameLoop(); // Stop game loop before opening shop
            ShopScreen shopScreen = new ShopScreen(stage, gameState);
            shopScreen.show();
        });
        
        Button vetButton = createStyledButton("Vet");
        vetButton.setOnAction(e -> visitVet());
        
        controls.getChildren().addAll(feedButton, playButton, sleepButton, exerciseButton, giftButton, shopButton, vetButton);
        return controls;
    }

    /**
     * Start the game loop for periodic updates. we will use the term "tick" to refer to the periodic updates.
     */
    private void setupGameLoop() {
        if (gameLoop != null) {
            gameLoop.stop();
        }
        
        gameLoop = new AnimationTimer() {
            @Override
            public void handle(long now) {
                // Update every 3 seconds (3,000,000,000 nanoseconds)
                if (now - lastUpdate >= 3_000_000_000L) {
                    lastUpdate = now;
                    updatePetStats();
                    updatePetSprite();
                }
            }
        };
        gameLoop.start();
    }

    /**
     * Stop the game loop.
     */
    private void stopGameLoop() {
        if (gameLoop != null) {
            gameLoop.stop();
            gameLoop = null;
        }
    }

    /**
     * Update the pet by one tick.
     */
    private void updatePetStats() {
        // Update the pet's state (this will handle stat decreases)
        pet.update();
        
        // Update the UI
        updateStats();
        
        // Check for stat warnings
        checkStatWarnings();
        
        // Show game over if health reaches 0
        if (pet.getHealth() == 0) {
            showGameOver();
        }
    }

    /**
     * Setup keyboard shortcuts for various actions.
     */
    private void setupKeyboardShortcuts() {
        scene.setOnKeyPressed(e -> {
            switch (e.getCode()) {
                case F -> showFeedOptions();
                case P -> play();
                case S -> sleep();
                case G -> showUseItemOptions();
                case B -> {
                    stopGameLoop(); // Stop game loop before opening shop
                    ShopScreen shopScreen = new ShopScreen(stage, gameState);
                    shopScreen.show();
                }
                case V -> visitVet();
                case ESCAPE -> {
                    stopGameLoop(); // Stop game loop before returning to menu
                    saveGame(); // Save before returning to menu
                    MainMenuScreen mainMenu = new MainMenuScreen(stage, gameState);
                    mainMenu.show();
                }
            }
        });
    }

    /**
     * Show the food options dialog.
     */
    private void showFeedOptions() {
        Stage feedStage = new Stage();
        feedStage.initModality(Modality.APPLICATION_MODAL);
        feedStage.initOwner(stage);
        
        VBox content = new VBox(20);
        content.setPadding(new Insets(20));
        content.setAlignment(Pos.CENTER);
        content.setStyle("-fx-background-color: #F5F5DC;");
        
        Text title = new Text("Choose Food");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        title.setFill(Color.FORESTGREEN);
        
        GridPane foodGrid = new GridPane();
        foodGrid.setHgap(15);
        foodGrid.setVgap(15);
        foodGrid.setAlignment(Pos.CENTER);
        
        int row = 0;
        int col = 0;
        
        // Basic food items
        if (pet.getItemCount("Kibble") > 0) {
            VBox kibbleBox = createFoodButton("Kibble", "Basic food (+10 Fullness)", feedStage);
            foodGrid.add(kibbleBox, col++, row);
            if (col > 1) { col = 0; row++; }
        }
        
        if (pet.getItemCount("Treats") > 0) {
            VBox treatsBox = createFoodButton("Treats", "Special treats (+15 Fullness, +5 Happiness)", feedStage);
            foodGrid.add(treatsBox, col++, row);
            if (col > 1) { col = 0; row++; }
        }
        
        if (pet.getItemCount("Premium Food") > 0) {
            VBox premiumBox = createFoodButton("Premium Food", "High quality food (+25 Fullness, +10 Happiness)", feedStage);
            foodGrid.add(premiumBox, col++, row);
            if (col > 1) { col = 0; row++; }
        }
        
        // Pet-specific food items
        String petType = pet.getType().toLowerCase();
        String petSpecificItem = "";
        String petSpecificDesc = "";
        
        switch (petType) {
            case "cat":
                petSpecificItem = "Tuna";
                petSpecificDesc = "Cat's favorite food (+20 Fullness, +20 Happiness)";
                break;
            case "bunny":
                petSpecificItem = "Carrots";
                petSpecificDesc = "Bunny's favorite food (+20 Fullness, +15 Energy)";
                break;
            case "dog":
                petSpecificItem = "Dog Treats";
                petSpecificDesc = "Dog's favorite food (+20 Fullness, +15 Happiness)";
                break;
        }
        
        if (!petSpecificItem.isEmpty() && pet.getItemCount(petSpecificItem) > 0) {
            VBox petSpecificBox = createFoodButton(petSpecificItem, petSpecificDesc, feedStage);
            foodGrid.add(petSpecificBox, col++, row);
            if (col > 1) { col = 0; row++; }
        }
        
        // If no food in inventory
        if (row == 0 && col == 0) {
            Text noFood = new Text("No food in inventory!\nVisit the shop to buy some.");
            noFood.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);
            foodGrid.add(noFood, 0, 0);
        }
        
        Button closeButton = createStyledButton("Cancel");
        closeButton.setOnAction(e -> feedStage.close());
        
        content.getChildren().addAll(title, foodGrid, closeButton);
        
        Scene feedScene = new Scene(content, 500, 400);
        feedStage.setScene(feedScene);
        feedStage.setTitle("Feed " + pet.getName());
        feedStage.showAndWait();
    }

    /**
     * Create a button for food items.
     *
     * @param name        The name of the food item.
     * @param description The description of the food item.
     * @param dialog      The dialog to close after feeding.
     * @return A VBox containing the food button and its details.
     */
    private VBox createFoodButton(String name, String description, Stage dialog) {
        VBox box = new VBox(10);
        box.setAlignment(Pos.CENTER);
        box.setPadding(new Insets(10));
        box.setMinWidth(150);
        box.setMinHeight(150);
        box.setMaxWidth(150);
        box.setStyle("-fx-border-color: #E0E0E0; -fx-border-radius: 5; -fx-background-color: #F8F8F8;");
        
        Text nameText = new Text(name);
        nameText.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        
        Text descText = new Text(description);
        descText.setWrappingWidth(130);
        descText.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);
        
        Text countText = new Text("Available: " + pet.getItemCount(name));
        
        box.getChildren().addAll(nameText, descText, countText);
        
        // Hover effect
        box.setOnMouseEntered(e -> box.setStyle("-fx-border-color: #4CAF50; -fx-border-radius: 5; -fx-background-color: #F8F8F8;"));
        box.setOnMouseExited(e -> box.setStyle("-fx-border-color: #E0E0E0; -fx-border-radius: 5; -fx-background-color: #F8F8F8;"));
        
        // Click effect
        box.setOnMouseClicked(e -> {
            if (pet.getItemCount(name) > 0) {
                switch (name) {
                    case "Kibble":
                        pet.setFullness(Math.min(100, pet.getFullness() + 10));
                        break;
                    case "Treats":
                        pet.setFullness(Math.min(100, pet.getFullness() + 15));
                        pet.setHappiness(Math.min(100, pet.getHappiness() + 5));
                        break;
                    case "Premium Food":
                        pet.setFullness(Math.min(100, pet.getFullness() + 25));
                        pet.setHappiness(Math.min(100, pet.getHappiness() + 10));
                        break;
                    case "Tuna":
                        if (pet.getType().equalsIgnoreCase("cat")) {
                            pet.setFullness(Math.min(100, pet.getFullness() + 20));
                            pet.setHappiness(Math.min(100, pet.getHappiness() + 20));
                        }
                        break;
                    case "Carrots":
                        if (pet.getType().equalsIgnoreCase("bunny")) {
                            pet.setFullness(Math.min(100, pet.getFullness() + 20));
                            pet.setEnergy(Math.min(100, pet.getEnergy() + 15));
                        }
                        break;
                    case "Dog Treats":
                        if (pet.getType().equalsIgnoreCase("dog")) {
                            pet.setFullness(Math.min(100, pet.getFullness() + 20));
                            pet.setHappiness(Math.min(100, pet.getHappiness() + 15));
                        }
                        break;
                }
                pet.removeItem(name, 1);
                dialog.close();
                updateInventoryDisplay();
                updateStats();
            }
        });
        
        return box;
    }

    /**
     * Play with the pet.
     * This increases happiness and decreases energy and also adds score and earns dabloons.
     */
    private void play() {
        // Increase happiness, decrease energy
        pet.setHappiness(pet.getHappiness() + 15);
        pet.setEnergy(pet.getEnergy() - 10);
        
        // Add score and earn dabloons
        pet.setScore(pet.getScore() + 15);
        int earnedDabloons = 5 + (int)(Math.random() * 5); // 5-9 dabloons
        pet.addCurrency(earnedDabloons);
        
        showAlert("Playing!", "You played with " + pet.getName() + ".\nHappiness +15, Energy -10\nEarned " + earnedDabloons + " dabloons!\nScore +15");
        
        // Update the UI to reflect changes
        updateStats();
        updateTopBar();
        
        // Return to normal sprite after delay
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(2), e -> updatePetSprite()));
        timeline.play();
    }

    /**
     * Puts the pet to sleep
     * This increases energy and decreases fullness and also adds score and earns dabloons.
     */
    private void sleep() {
        // Update sprite to sleep animation
        pet.sleep();

        // Add score and earn dabloons
        pet.setScore(pet.getScore() + 10);
        int earnedDabloons = 3 + (int)(Math.random() * 3); // 3-5 dabloons
        pet.addCurrency(earnedDabloons);
        
        showAlert("Sleeping!", pet.getName() + " is sleeping.\nEnergy +100, Fullness -20\nEarned " + earnedDabloons + " dabloons!\nScore +10");
        
        // Update the UI to reflect changes
        updateStats();
        updateTopBar();
        
        // Return to normal sprite after delay
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(3), e -> updatePetSprite()));
        timeline.play();
    }

    private void showUseItemOptions() {
        // Create a modal dialog
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.initOwner(stage);
        dialog.setTitle("Use Gift");
        
        VBox content = new VBox(15);
        content.setPadding(new Insets(20));
        content.setAlignment(Pos.CENTER);
        content.setStyle("-fx-background-color: #F5F5DC;");
        
        Text title = new Text("Select Gift");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        
        GridPane itemGrid = new GridPane();
        itemGrid.setHgap(15);
        itemGrid.setVgap(15);
        itemGrid.setAlignment(Pos.CENTER);
        
        int row = 0;
        int col = 0;
        
        // Check if gifts are available
        if (pet.getItemCount("Toy Ball") > 0) {
            VBox toyBallBox = createItemButton("Toy Ball", "A fun toy (+15 Happiness)", dialog);
            itemGrid.add(toyBallBox, col++, row);
            if (col > 1) { col = 0; row++; }
        }
        
        if (pet.getItemCount("Comfort Blanket") > 0) {
            VBox blanketBox = createItemButton("Comfort Blanket", "A cozy blanket (+20 Happiness, +5 Health)", dialog);
            itemGrid.add(blanketBox, col++, row);
            if (col > 1) { col = 0; row++; }
        }
        
        if (pet.getItemCount("Health Treat") > 0) {
            VBox healthTreatBox = createItemButton("Health Treat", "A special treat (+10 Health, +15 Happiness)", dialog);
            itemGrid.add(healthTreatBox, col++, row);
            if (col > 1) { col = 0; row++; }
        }
        
        // If no gifts in inventory
        if (row == 0 && col == 0) {
            Text noItems = new Text("No gifts in inventory!\nVisit the shop to buy some.");
            noItems.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);
            itemGrid.add(noItems, 0, 0);
        }
        
        Button closeButton = createStyledButton("Cancel");
        closeButton.setOnAction(e -> dialog.close());
        
        content.getChildren().addAll(title, itemGrid, closeButton);
        
        Scene dialogScene = new Scene(content, 400, 350);
        dialog.setScene(dialogScene);
        dialog.showAndWait();
    }
    
    private VBox createItemButton(String name, String description, Stage dialog) {
        VBox box = new VBox(10);
        box.setAlignment(Pos.CENTER);
        box.setPadding(new Insets(10));
        box.setMinWidth(150);
        box.setMinHeight(150);
        box.setMaxWidth(150);
        box.setStyle("-fx-border-color: #E0E0E0; -fx-border-radius: 5; -fx-background-color: #F8F8F8;");
        
        Text nameText = new Text(name);
        nameText.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        
        Text descText = new Text(description);
        descText.setWrappingWidth(130);
        descText.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);
        
        Text countText = new Text("Available: " + pet.getItemCount(name));
        
        box.getChildren().addAll(nameText, descText, countText);
        
        // Hover effect
        box.setOnMouseEntered(e -> box.setStyle("-fx-border-color: #4CAF50; -fx-border-radius: 5; -fx-background-color: #F8F8F8;"));
        box.setOnMouseExited(e -> box.setStyle("-fx-border-color: #E0E0E0; -fx-border-radius: 5; -fx-background-color: #F8F8F8;"));
        
        // Click effect
        box.setOnMouseClicked(e -> {
            useItem(name);
            dialog.close();
        });
        
        return box;
    }

    /**
     * Use an item from the inventory.
     *
     * @param itemName
     */
    private void useItem(String itemName) {
        if (pet == null) return;

        switch (itemName) {
            case "Toy Ball":
                pet.setHappiness(pet.getHappiness() + 15);
                break;
            case "Comfort Blanket":
                pet.setHappiness(pet.getHappiness() + 20);
                pet.setMaxHealth(pet.getMaxHealth() + 5);
                break;
            case "Health Treat":
                pet.setMaxHealth(pet.getMaxHealth() + 10);
                pet.setHappiness(pet.getHappiness() + 15);
                break;
            default:
                showAlert("Unknown Item", "This item doesn't seem to do anything!");
                return;
        }

        // Remove the item from inventory
        pet.removeItem(itemName, 1);
        
        // Show success message
        showAlert("Gift Used", "You gave " + itemName + " to " + pet.getName() + "!");
        
        // Update the UI
        updateStats();
    }

    /**
     * Visit the vet to heal the pet. Visiting the vet costs 50 dabloons.
     */
    private void visitVet() {
        // Check if the player has enough currency
        if (pet.getCurrency() >= 50) {
            // Deduct the cost
            pet.spendCurrency(50);
            
            // Heal the pet to full health
            pet.setHealth(pet.getMaxHealth());
            
            // Add score
            pet.setScore(pet.getScore() + 20);
            
            // Update the display
            updateStats();
            updateTopBar();
            
            showAlert("Vet Visit", "Your pet has been healed to full health!\nScore +20");
        } else {
            showAlert("Not Enough Dabloons", "You need 50 dabloons to visit the vet.");
        }
    }

    /**
     * Saves the current pet being played
     */
    private void saveGame() {
        stopGameLoop(); // Stop the game loop before saving
        
        String fileName = pet.getType().toLowerCase() + "_save.txt";
        try {
            // Save pet information
            gameState.savePet(fileName);
            
            System.out.println("Game saved to " + fileName);
            
            // Show confirmation
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Game Saved");
            alert.setHeaderText("Game Saved Successfully");
            alert.setContentText("Your game has been saved to: " + fileName);
            alert.showAndWait();
            
        } catch (Exception e) {
            System.err.println("Error saving game: " + e.getMessage());
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Save Error");
            alert.setHeaderText("Could Not Save Game");
            alert.setContentText("An error occurred while saving the game: " + e.getMessage());
            alert.showAndWait();
        }
    }

    /**
     * Update the stats display.
     */
    private void updateStats() {
        // Update text displays
        healthText.setText("Health: " + pet.getHealth() + "/" + pet.getMaxHealth());
        happinessText.setText("Happiness: " + pet.getHappiness());
        fullnessText.setText("Fullness: " + pet.getFullness());
        energyText.setText("Energy: " + pet.getEnergy());
        
        // Update progress bars
        healthBar.setProgress(pet.getHealth() / 100.0);
        happinessBar.setProgress(pet.getHappiness() / 100.0);
        fullnessBar.setProgress(pet.getFullness() / 100.0);
        energyBar.setProgress(pet.getEnergy() / 100.0);
    }
    
    private ProgressBar createStatusBar(int currentValue, Color color) {
        ProgressBar bar = new ProgressBar();
        bar.setProgress(currentValue / 100.0);
        bar.setStyle("-fx-accent: " + toRGBCode(color) + ";");
        return bar;
    }
    
    private String toRGBCode(Color color) {
        return String.format("#%02X%02X%02X",
                (int) (color.getRed() * 255),
                (int) (color.getGreen() * 255),
                (int) (color.getBlue() * 255));
    }
    
    private Color getPetColor() {
        switch (pet.getType().toLowerCase()) {
            case "dog":
                return Color.BROWN;
            case "cat":
                return Color.ORANGE;
            case "bunny":
                return Color.LIGHTGRAY;
            default:
                return Color.BLUE;
        }
    }
    
    private void scaleTransition(ImageView node, double scale, double duration) {
        ScaleTransition st = new ScaleTransition(javafx.util.Duration.seconds(duration), node);
        st.setFromX(1.0);
        st.setFromY(1.0);
        st.setToX(scale);
        st.setToY(scale);
        st.setCycleCount(2);
        st.setAutoReverse(true);
        st.play();
    }
    
    private Button createStyledButton(String text) {
        Button button = new Button(text);
        button.setStyle(
            "-fx-background-color: #4CAF50; " +
            "-fx-text-fill: white; " +
            "-fx-font-size: 14px; " +
            "-fx-padding: 8px 16px; " +
            "-fx-border-radius: 5px; " +
            "-fx-background-radius: 5px;"
        );
        
        // Add hover effect
        button.setOnMouseEntered(e -> button.setStyle(
            "-fx-background-color: #45a049; " +
            "-fx-text-fill: white; " +
            "-fx-font-size: 14px; " +
            "-fx-padding: 8px 16px; " +
            "-fx-border-radius: 5px; " +
            "-fx-background-radius: 5px;"
        ));
        
        button.setOnMouseExited(e -> button.setStyle(
            "-fx-background-color: #4CAF50; " +
            "-fx-text-fill: white; " +
            "-fx-font-size: 14px; " +
            "-fx-padding: 8px 16px; " +
            "-fx-border-radius: 5px; " +
            "-fx-background-radius: 5px;"
        ));
        
        return button;
    }
    
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void show() {
        stage.setTitle("Virtual Pet Game - " + pet.getName());
        stage.setScene(scene);
        stage.show();
    }
    
    private void updateTopBar() {
        scoreText.setText("Score: " + pet.getScore());
        currencyText.setText("Dabloons: " + pet.getCurrency());
    }

    /**
     * Update the pet sprite based on its current state.
     */
    private void updatePetSprite() {
        String prefix = pet.getType().toUpperCase();
        String spritePath;
        
        if (pet.getState() == Pet.State.HUNGRY) {
            spritePath = "images/" + prefix + "_HUNGRY.png";
        } else if (pet.getState() == Pet.State.SLEEP) {
            spritePath = "images/" + prefix + "_SLEEP.png";
        } else if (pet.getState() == Pet.State.ANGRY) {
            spritePath = "images/" + prefix + "_ANGRY.png";
        } else {
            spritePath = "images/" + prefix + "_NORMAL.png";
        }
        
        try {
            File imageFile = new File(spritePath);
            System.out.println("Attempting to load sprite: " + spritePath);
            System.out.println("Pet type: " + pet.getType());
            System.out.println("File exists: " + imageFile.exists());
            System.out.println("File absolute path: " + imageFile.getAbsolutePath());
            String imageURI = imageFile.toURI().toString();
            System.out.println("Image URI: " + imageURI);
            javafx.scene.image.Image image = new javafx.scene.image.Image(imageURI);
            petSprite.setImage(image);
            System.out.println("Sprite updated successfully");
        } catch (Exception e) {
            System.err.println("Error loading sprite: " + spritePath);
            System.err.println("Error details: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Show the game over screen.
     */
    private void showGameOver() {
        // Create a modal dialog
        Stage gameOverDialog = new Stage();
        gameOverDialog.initModality(Modality.APPLICATION_MODAL);
        gameOverDialog.initOwner(stage);
        gameOverDialog.setTitle("Game Over");
        
        VBox content = new VBox(20);
        content.setPadding(new Insets(30));
        content.setAlignment(Pos.CENTER);
        content.setStyle("-fx-background-color: #F5F5DC;");
        
        // Title
        Text title = new Text("Game Over");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 32));
        
        // Score display
        Text scoreText = new Text("Total Score: " + pet.getScore());
        scoreText.setFont(Font.font("Arial", 24));
        
        // Buttons
        Button reviveButton = createStyledButton("Revive Pet");
        Button mainMenuButton = createStyledButton("Main Menu");
        
        reviveButton.setOnAction(e -> {
            pet.setHealth(pet.getMaxHealth());
            pet.setFullness(100);
            pet.setEnergy(100);
            pet.setHappiness(100);
            updateStats();
            updateTopBar();
            gameOverDialog.close();
        });
        
        mainMenuButton.setOnAction(e -> {
            saveGame(); // Auto-save before returning to menu
            MainMenuScreen mainMenuScreen = new MainMenuScreen(stage, gameState);
            mainMenuScreen.show();
        });
        
        content.getChildren().addAll(title, scoreText, reviveButton, mainMenuButton);
        
        Scene dialogScene = new Scene(content, 400, 300);
        gameOverDialog.setScene(dialogScene);
        
        // Use Platform.runLater to show the dialog safely
        Platform.runLater(() -> {
            gameOverDialog.show();
            // Stop the game loop when showing game over
            if (gameLoop != null) {
                gameLoop.stop();
            }
        });
    }

    /**
     * Exercise the pet.
     * This increases health but decreases energy and fullness.
     */
    private void exercise() {
        if (pet.getEnergy() < 15) {
            showAlert("Too Tired", "Your pet needs more energy to exercise!");
            return;
        }
        if (pet.getFullness() < 10) {
            showAlert("Too Tired", "Your pet needs more energy to exercise!");
            return;
        }
        
        // Increase health but decrease energy significantly
        pet.setHealth(pet.getHealth() + 15);
        pet.setEnergy(pet.getEnergy() - 15);
        pet.setFullness(pet.getFullness() - 10);
        
        // Add score and earn dabloons
        pet.setScore(pet.getScore() + 20);
        int earnedDabloons = 5 + (int)(Math.random() * 5); // 5-9 dabloons
        pet.addCurrency(earnedDabloons);
        
        showAlert("Exercise Complete!", "Your pet has exercised!\n" +
                                                     "Health +15, Energy -30\n" +
                                                     "Earned " + earnedDabloons + " dabloons!\n" +
                                                     "Score +20");
        
        // Update the UI
        updateStats();
        updateTopBar();
    }

    /**
     * Check for stat warnings and show alerts if necessary.
     */
    private void checkStatWarnings() {
        // Check health warning
        if (pet.getHealth() < 25 && !healthWarningShown) {
            showWarning("Low Health Warning", "Your pet's health is dangerously low!");
            healthWarningShown = true;
        } else if (pet.getHealth() >= 25) {
            healthWarningShown = false;
        }

        // Check energy warning
        if (pet.getEnergy() < 25 && !energyWarningShown) {
            showWarning("Low Energy Warning", "Your pet is very tired!");
            energyWarningShown = true;
        } else if (pet.getEnergy() >= 25) {
            energyWarningShown = false;
        }

        // Check fullness warning
        if (pet.getFullness() < 25 && !fullnessWarningShown) {
            showWarning("Low Fullness Warning", "Your pet is very hungry!");
            fullnessWarningShown = true;
        } else if (pet.getFullness() >= 25) {
            fullnessWarningShown = false;
        }

        // Check happiness warning
        if (pet.getHappiness() < 25 && !happinessWarningShown) {
            showWarning("Low Happiness Warning", "Your pet is very unhappy!");
            happinessWarningShown = true;
        } else if (pet.getHappiness() >= 25) {
            happinessWarningShown = false;
        }
    }

    /**
     * Show a warning alert.
     *
     * @param title   The title of the alert.
     * @param message The message to display.
     */
    private void showWarning(String title, String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        });
    }

    /**
     * Update the inventory display based on the pet's current items.
     */
    private void updateInventoryDisplay() {
        // Update food items count
        VBox foodItems = (VBox) inventoryBox.getChildren().get(1);
        Text kibbleCount = (Text) foodItems.getChildren().get(1);
        Text treatsCount = (Text) foodItems.getChildren().get(2);
        Text premiumFoodCount = (Text) foodItems.getChildren().get(3);
        
        kibbleCount.setText("Kibble: " + pet.getItemCount("Kibble"));
        treatsCount.setText("Treats: " + pet.getItemCount("Treats"));
        premiumFoodCount.setText("Premium Food: " + pet.getItemCount("Premium Food"));
        
        // Update special items count
        VBox specialItems = (VBox) inventoryBox.getChildren().get(2);
        Text toyBallCount = new Text("Toy Ball: " + pet.getItemCount("Toy Ball"));
        Text comfortBlanketCount = new Text("Comfort Blanket: " + pet.getItemCount("Comfort Blanket"));
        Text healthTreatCount = new Text("Health Treat: " + pet.getItemCount("Health Treat"));
        
        specialItems.getChildren().clear();
        specialItems.getChildren().addAll(
            new Text("Gifts:"),
            toyBallCount,
            comfortBlanketCount,
            healthTreatCount
        );
    }
} 