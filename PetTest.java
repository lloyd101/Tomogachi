package group02;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class PetTest {
    private Pet pet;

    @BeforeEach
    void setUp() {
        pet = new Pet("Buddy", "dog");
    }

    @Test
    void testPetConstructorWithNameAndType() {
        assertEquals("Buddy", pet.getName());
        assertEquals("DOG", pet.getType());
        assertEquals(100, pet.getHealth());
        assertEquals(100, pet.getFullness());
        assertEquals(100, pet.getEnergy());
        assertEquals(100, pet.getHappiness());
    }

    @Test
    void testSetAndGetHealth() {
        pet.setHealth(80);
        assertEquals(80, pet.getHealth());
    }

    @Test
    void testDeadState() {
        pet.setHealth(0);
        pet.update();
        assertEquals(Pet.State.DEAD, pet.getState());
    }

    @Test
    void testInventoryManagement() {
        pet.addItem("Bone", 2);
        assertEquals(2, pet.getItemCount("Bone"));
        assertTrue(pet.removeItem("Bone", 1));
        assertEquals(1, pet.getItemCount("Bone"));
    }

    @Test
    void testGetCreationDate() {
        assertEquals(LocalDate.now(), pet.getCreationDate());
    }
}
