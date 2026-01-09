package group02;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.LocalTime;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;


public class GameStateTest {

    private GameState gameState;
    private static final String SAVE_DIRECTORY = "saves/";

    @BeforeEach
    public void setUp() {
        gameState = new GameState();
        // Ensure the save directory exists
        new File(SAVE_DIRECTORY).mkdirs();
    }

    @Test
    public void testStartNewGame() {
        gameState.startNewGame("Buddy", "Dog");
        Pet pet = gameState.getPet();

        assertNotNull(pet, "Pet should not be null after starting a new game");
        assertEquals("Buddy", pet.getName(), "Pet name should be set correctly");
        assertEquals("DOG", pet.getType(), "Pet type should be set correctly");
        assertEquals(5, pet.getInventory().get("Kibble"), "Pet should have 5 Kibble");
    }

    @Test
    public void testSaveSettings() throws Exception {
        // Simulate a session
        gameState.startSessionTimeTracking();
        Thread.sleep(2000); // Wait for 2 seconds to simulate play time
        gameState.saveSettings(true);

        File settingsFile = new File(SAVE_DIRECTORY + "settings.txt");
        assertTrue(settingsFile.exists(), "Settings file should be created");

        String content = new String(Files.readAllBytes(Paths.get(SAVE_DIRECTORY + "settings.txt")));
        assertTrue(content.contains("numberOfSessions=" + gameState.getPlayer().getNumberOfSessions()), "Number of sessions should be recorded in settings");
    }

    @Test
    public void testEndGameSession() {
        gameState.startSessionTimeTracking();
        gameState.endGameSession();

        File settingsFile = new File(SAVE_DIRECTORY + "settings.txt");
        assertTrue(settingsFile.exists(), "Settings file should be updated after ending session");

        // Verify session duration was recorded
        // For a more detailed test, parse the file content and verify time calculations
    }

    @Test
    public void testExitGame() {
        gameState.startNewGame("Buddy", "Dog");
        gameState.exitGame("pet_save_test.txt");

        File petSaveFile = new File(SAVE_DIRECTORY + "pet_save_test.txt");
        assertTrue(petSaveFile.exists(), "Pet save file should be created when exiting the game");

        // Verify pet details are saved (name, type, etc.)
    }
}