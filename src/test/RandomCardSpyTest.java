package durak;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.List;

class RandomCardSpyTest {
    private RandomCardSpy spy;
    private Player player;
    private Deck deck;
    private Card heartsAce;
    private Card diamondsKing;

    @BeforeEach
    void setUp() {
        player = new Player();
        deck = new Deck();

        heartsAce = new Card(new Suit("Hearts", false), Rank.ACE);
        diamondsKing = new Card(new Suit("Diamonds", false), Rank.KING);

        player.addCard(heartsAce);
        player.addCard(diamondsKing);
    }

    @Test
    void testCollectInformation_FromHand_Success() {
        spy = new RandomCardSpy(1.0f, Spy.Source.HAND);

        List<Card> result = spy.collectInformation(player, deck);

        assertFalse(result.isEmpty(), "Должна быть возвращена карта из руки");
        assertEquals(1, result.size(), "Должна быть возвращена одна карта");
        assertTrue(player.getCards().contains(result.get(0)),
                "Возвращенная карта должна быть из руки игрока");
    }

    @Test
    void testCollectInformation_FromDeck_Success() {
        // Настраиваем колоду с известной картой
        List<Card> testCards = List.of(
                new Card(new Suit("Spades", true), Rank.QUEEN)
        );
        setDeckCards(deck, testCards);

        spy = new RandomCardSpy(1.0f, Spy.Source.DECK);

        List<Card> result = spy.collectInformation(player, deck);

        assertFalse(result.isEmpty(), "Должна быть возвращена карта из колоды");
        assertEquals(testCards.get(0), result.get(0),
                "Должна быть возвращена верхняя карта колоды");
    }

    @Test
    void testCollectInformation_FromHand_EmptyPlayer() {
        Player emptyPlayer = new Player();
        spy = new RandomCardSpy(1.0f, Spy.Source.HAND);

        List<Card> result = spy.collectInformation(emptyPlayer, deck);

        assertTrue(result.isEmpty(), "При пустой руке должен возвращаться пустой список");
    }

    @Test
    void testCollectInformation_FromDeck_EmptyDeck() {
        Deck emptyDeck = new Deck();
        setDeckCards(emptyDeck, List.of()); // Очищаем колоду

        spy = new RandomCardSpy(1.0f, Spy.Source.DECK);

        List<Card> result = spy.collectInformation(player, emptyDeck);

        assertTrue(result.isEmpty(), "При пустой колоде должен возвращаться пустой список");
    }

    @Test
    void testCollectInformation_LowEfficiency() {
        spy = new RandomCardSpy(0.0f, Spy.Source.HAND); // 0% эффективность

        List<Card> result = spy.collectInformation(player, deck);

        assertTrue(result.isEmpty(), "При эффективности 0% всегда должен возвращаться пустой список");
    }

    @Test
    void testGetSource() {
        spy = new RandomCardSpy(1.0f, Spy.Source.DECK);

        assertEquals(Spy.Source.DECK, spy.getSource(),
                "Геттер source должен возвращать правильное значение");
    }

    @Test
    void testGetEfficiency() {
        spy = new RandomCardSpy(0.7f, Spy.Source.HAND);

        assertEquals(0.7f, spy.getEfficiency(), 0.001f,
                "Геттер efficiency должен возвращать правильное значение");
    }

    @Test
    void testSetEfficiency() {
        spy = new RandomCardSpy(0.5f, Spy.Source.HAND);
        spy.setEfficiency(0.8f);

        assertEquals(0.8f, spy.getEfficiency(), 0.001f,
                "Сеттер efficiency должен устанавливать значение");
    }

    // Вспомогательный метод для установки карт в колоду
    private void setDeckCards(Deck deck, List<Card> cards) {
        try {
            var field = Deck.class.getDeclaredField("cards");
            field.setAccessible(true);
            field.set(deck, cards);
        } catch (Exception e) {
            fail("Не удалось установить тестовые карты в колоду: " + e.getMessage());
        }
    }
}