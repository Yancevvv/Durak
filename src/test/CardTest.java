package durak;
import org.junit.jupiter.api.Test;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

public class CardTest {
    @Test
    void testCardBeat() {
        Suit hearts = new Suit("Hearts", false);
        Suit diamonds = new Suit("Diamonds", true); // козырь

        Card sixHearts = new Card(hearts, Rank.SIX);
        Card sevenHearts = new Card(hearts, Rank.SEVEN);
        Card sixDiamonds = new Card(diamonds, Rank.SIX);

        assertTrue(sevenHearts.beat(sixHearts));
        assertFalse(sixHearts.beat(sevenHearts));
        assertTrue(sixDiamonds.beat(sixHearts)); // козырь бьет
        assertFalse(sixHearts.beat(sixDiamonds));
    }
}
