package durak;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.List;

class SuitCardsSpyTest {
    private SuitCardsSpy spy;
    private Player player;
    private Deck deck;
    private Suit hearts;
    private Suit spades;
    private Card heartsAce;
    private Card heartsKing;
    private Card spadesQueen;

    @BeforeEach
    void setUp() {
        player = new Player();
        deck = new Deck();

        hearts = new Suit("Hearts", false);
        spades = new Suit("Spades", false);

        heartsAce = new Card(hearts, Rank.ACE);
        heartsKing = new Card(hearts, Rank.KING);
        spadesQueen = new Card(spades, Rank.QUEEN);

        player.addCard(heartsAce);
        player.addCard(heartsKing);
        player.addCard(spadesQueen);
    }

    @Test
    void testCollectInformation_FromHand_MultipleCards() {
        spy = new SuitCardsSpy(1.0f, hearts, Spy.Source.HAND);

        List<Card> result = spy.collectInformation(player, deck);

        assertEquals(2, result.size(), "Должны быть найдены 2 карты масти Hearts");
        assertTrue(result.contains(heartsAce), "Должна содержать Hearts ACE");
        assertTrue(result.contains(heartsKing), "Должна содержать Hearts KING");
        assertFalse(result.contains(spadesQueen), "Не должна содержать Spades QUEEN");
    }

    @Test
    void testCollectInformation_FromHand_SingleCard() {
        spy = new SuitCardsSpy(1.0f, spades, Spy.Source.HAND);

        List<Card> result = spy.collectInformation(player, deck);

        assertEquals(1, result.size(), "Должна быть найдена 1 карта масти Spades");
        assertEquals(spadesQueen, result.get(0), "Должна быть найдена Spades QUEEN");
    }

    @Test
    void testCollectInformation_FromHand_NoMatchingCards() {
        Suit clubs = new Suit("Clubs", false);
        spy = new SuitCardsSpy(1.0f, clubs, Spy.Source.HAND);

        List<Card> result = spy.collectInformation(player, deck);

        assertTrue(result.isEmpty(), "При отсутствии карт масти должен возвращаться пустой список");
    }

    @Test
    void testCollectInformation_FromDeck_MatchingCard() {
        // Настраиваем колоду с картой нужной масти
        List<Card> testCards = List.of(
                new Card(hearts, Rank.JACK)
        );
        setDeckCards(deck, testCards);

        spy = new SuitCardsSpy(1.0f, hearts, Spy.Source.DECK);

        List<Card> result = spy.collectInformation(player, deck);

        assertFalse(result.isEmpty(), "Должна быть найдена карта из колоды");
        assertEquals(testCards.get(0), result.get(0),
                "Должна быть найдена Hearts JACK из колоды");
    }

    @Test
    void testCollectInformation_FromDeck_NonMatchingCard() {
        // Настраиваем колоду с картой другой масти
        List<Card> testCards = List.of(
                new Card(spades, Rank.TEN)
        );
        setDeckCards(deck, testCards);

        spy = new SuitCardsSpy(1.0f, hearts, Spy.Source.DECK);

        List<Card> result = spy.collectInformation(player, deck);

        assertTrue(result.isEmpty(),
                "При несовпадении масти должен возвращаться пустой список");
    }

    @Test
    void testCollectInformation_FromDeck_EmptyDeck() {
        Deck emptyDeck = new Deck();
        setDeckCards(emptyDeck, List.of());

        spy = new SuitCardsSpy(1.0f, hearts, Spy.Source.DECK);

        List<Card> result = spy.collectInformation(player, emptyDeck);

        assertTrue(result.isEmpty(), "При пустой колоде должен возвращаться пустой список");
    }

    @Test
    void testCollectInformation_LowEfficiency() {
        spy = new SuitCardsSpy(0.0f, hearts, Spy.Source.HAND);

        List<Card> result = spy.collectInformation(player, deck);

        assertTrue(result.isEmpty(), "При эффективности 0% должен возвращаться пустой список");
    }

    @Test
    void testGetSuit() {
        spy = new SuitCardsSpy(1.0f, hearts, Spy.Source.HAND);

        assertEquals(hearts, spy.getSuit(), "Геттер suit должен возвращать правильное значение");
    }

    @Test
    void testGetSource() {
        spy = new SuitCardsSpy(1.0f, hearts, Spy.Source.DECK);

        assertEquals(Spy.Source.DECK, spy.getSource(),
                "Геттер source должен возвращать правильное значение");
    }

    @Test
    void testNullSuit() {
        // Проверяем, что шпион корректно обрабатывает null (хотя в конструкторе это не должно происходить)
        spy = new SuitCardsSpy(1.0f, hearts, Spy.Source.HAND);

        // Устанавливаем null через рефлексию для тестирования
        try {
            var field = SuitCardsSpy.class.getDeclaredField("strategy");
            field.setAccessible(true);
            var strategy = field.get(spy);

            var strategyField = strategy.getClass().getDeclaredField("suit");
            strategyField.setAccessible(true);
            strategyField.set(strategy, null);

        } catch (Exception e) {
            fail("Не удалось установить null suit: " + e.getMessage());
        }

        List<Card> result = spy.collectInformation(player, deck);

        assertTrue(result.isEmpty(), "При null suit должен возвращаться пустой список");
    }

    // Вспомогательный метод
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