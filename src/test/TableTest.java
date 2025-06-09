package durak;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.List;

public class TableTest {

    private Table table;
    private Card card1;
    private Card card2;
    private Card card3;

    @BeforeEach
    public void setUp() {
        table = new Table();
        card1 = new Card(new Suit("Clubs", false), Rank.SEVEN);
        card2 = new Card(new Suit("Hearts", false), Rank.SEVEN);
        card3 = new Card(new Suit("Diamonds", false), Rank.EIGHT);
    }

    @Test
    public void testAddCard() {
        table.addCard(card1);
        assertEquals(1, table.size());
        assertTrue(table.getCards().contains(card1));
    }

    @Test
    public void testContainsRank() {
        table.addCard(card1); // SEVEN of CLUBS
        table.addCard(card2); // SEVEN of HEARTS
        assertTrue(table.containsRank(Rank.SEVEN));
        assertFalse(table.containsRank(Rank.EIGHT));
    }

    @Test
    public void testGetLastCard() {
        assertNull(table.getLastCard()); // Пустой стол
        table.addCard(card1);
        assertEquals(card1, table.getLastCard());
        table.addCard(card2);
        assertEquals(card2, table.getLastCard());
    }

    @Test
    public void testSize() {
        assertEquals(0, table.size()); // Пустой стол
        table.addCard(card1);
        assertEquals(1, table.size());
        table.addCard(card2);
        assertEquals(2, table.size());
    }

    @Test
    public void testClear() {
        table.addCard(card1);
        table.addCard(card2);
        table.clear();
        assertEquals(0, table.size());
        assertTrue(table.isEmpty());
    }

    @Test
    public void testIsFull() {
        for (int i = 0; i < 5; i++) {
            table.addCard(card1);
        }
        assertFalse(table.isFull()); // После добавления 5 карт
        table.addCard(card2);
        assertTrue(table.isFull()); // После добавления 6-й карты
    }

    @Test
    public void testIsEmpty() {
        assertTrue(table.isEmpty()); // Пустой стол
        table.addCard(card1);
        assertFalse(table.isEmpty());
        table.clear();
        assertTrue(table.isEmpty());
    }

    @Test
    public void testGetCards() {
        table.addCard(card1);
        table.addCard(card2);
        List<Card> cards = table.getCards();
        assertEquals(2, cards.size());
        assertTrue(cards.contains(card1));
        assertTrue(cards.contains(card2));
        // Убедимся, что возвращается копия списка
        cards.add(card3);
        assertEquals(2, table.getCards().size());
    }
}