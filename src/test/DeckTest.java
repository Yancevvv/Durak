package durak;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class DeckTest {
    @Test
    void testDeckCreation() {
        Deck deck = new Deck();
        assertEquals(36, deck.size());
    }

    @Test
    void testShuffle() {
        Deck deck1 = new Deck();
        Deck deck2 = new Deck();
        deck2.mix();

        assertNotEquals(deck1.takeCard(), deck2.takeCard());
    }

    @Test
    void testTakeCard() {
        Deck deck = new Deck();
        Card card = deck.takeCard();
        assertNotNull(card);
        assertEquals(35, deck.size());
    }
}