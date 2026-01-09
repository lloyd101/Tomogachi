package group02;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

class PlayerTest {
    private Player player;

    @BeforeEach
    void setUp() {
        player = new Player();
    }

    @Test
    void testDefaultConstructor() {
        assertEquals(0, player.getTotalPlayTime());
        assertFalse(player.isFullScreen());
        assertNull(player.getParentalPassword());
    }

    @Test
    void testSetAndGetParentalPassword() {
        player.setParentalPassword("secret");
        assertTrue(player.isPasswordCorrect("secret"));
        assertFalse(player.isPasswordCorrect("wrong"));
    }

    @Test
    void testIncrementSessions() {
        player.incrementSessions();
        assertEquals(1, player.getNumberOfSessions());
    }

    @Test
    void testSetAndGetDailyTimeLimit() {
        player.setDailyTimeLimit(120);
        assertEquals(120, player.getDailyTimeLimit());
    }
}
