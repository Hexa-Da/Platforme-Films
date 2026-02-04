// imports minimum : JUnit seulement
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import edu.polytech.springexample.model.FictionalCharacter;

class FictionalCharacterTest {

    @Test
    void testFictionalCharacterFields() {
        FictionalCharacter character =
            new FictionalCharacter(1L, "Harry", "Potter", "Harry Potter");

        assertEquals(1L, character.getId());
        assertEquals("Harry", character.getFirstname());
        assertEquals("Potter", character.getLastname());
        assertEquals("Harry Potter", character.getUniverse());
    }
}