package durak;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.List;

class RankCardsSpyTest {
    private RankCardsSpy spy;
    private Player player;
    private Deck deck;
    private Card aceHearts;
    private Card aceDiamonds;
    private Card kingSpades;

    @BeforeEach
    void setUp() {
        player = new Player();
        deck = new Deck();

        aceHearts = new Card(new Suit("Hearts", false), Rank.ACE);
        aceDiamonds = new Card(new Suit("Diamonds", false), Rank.ACE);
        kingSpades = new Card(new Suit("Spades", false), Rank.KING);

        player.addCard(aceHearts);
        player.addCard(aceDiamonds);
        player.addCard(kingSpades);
    }

    @Test
    void testCollectInformation_FromHand_MultipleCards() {
        spy = new RankCardsSpy(1.0f, Rank.ACE, Spy.Source.HAND);

        List<Card> result = spy.collectInformation(player, deck);

        assertEquals(2, result.size(), "Должны быть найдены 2 карты достоинства ACE");
        assertTrue(result.contains(aceHearts), "Должна содержать ACE Hearts");
        assertTrue(result.contains(aceDiamonds), "Должна содержать ACE Diamonds");
        assertFalse(result.contains(kingSpades), "Не должна содержать KING Spades");
    }

    @Test
    void testCollectInformation_FromHand_SingleCard() {
        spy = new RankCardsSpy(1.0f, Rank.KING, Spy.Source.HAND);

        List<Card> result = spy.collectInformation(player, deck);

        assertEquals(1, result.size(), "Должна быть найдена 1 карта достоинства KING");
        assertEquals(kingSpades, result.get(0), "Должна быть найдена KING Spades");
    }

    @Test
    void testCollectInformation_FromHand_NoMatchingCards() {
        spy = new RankCardsSpy(1.0f, Rank.QUEEN, Spy.Source.HAND);

        List<Card> result = spy.collectInformation(player, deck);

        assertTrue(result.isEmpty(),
                "При отсутствии карт достоинства должен возвращаться пустой список");
    }

    @Test
    void testCollectInformation_FromDeck_MatchingCard() {
        // Настраиваем колоду с картой нужного достоинства
        List<Card> testCards = List.of(
                new Card(new Suit("Clubs", false), Rank.ACE)
        );
        setDeckCards(deck, testCards);

        spy = new RankCardsSpy(1.0f, Rank.ACE, Spy.Source.DECK);

        List<Card> result = spy.collectInformation(player, deck);

        assertFalse(result.isEmpty(), "Должна быть найдена карта из колоды");
        assertEquals(testCards.get(0), result.get(0),
                "Должна быть найдена ACE Clubs из колоды");
    }

    @Test
    void testCollectInformation_FromDeck_NonMatchingCard() {
        // Настраиваем колоду с картой другого достоинства
        List<Card> testCards = List.of(
                new Card(new Suit("Hearts", false), Rank.JACK)
        );
        setDeckCards(deck, testCards);

        spy = new RankCardsSpy(1.0f, Rank.ACE, Spy.Source.DECK);

        List<Card> result = spy.collectInformation(player, deck);

        assertTrue(result.isEmpty(),
                "При несовпадении достоинства должен возвращаться пустой список");
    }

    @Test
    void testCollectInformation_FromDeck_EmptyDeck() {
        Deck emptyDeck = new Deck();
        setDeckCards(emptyDeck, List.of());

        spy = new RankCardsSpy(1.0f, Rank.ACE, Spy.Source.DECK);

        List<Card> result = spy.collectInformation(player, emptyDeck);

        assertTrue(result.isEmpty(), "При пустой колоде должен возвращаться пустой список");
    }

    @Test
    void testCollectInformation_LowEfficiency() {
        spy = new RankCardsSpy(0.0f, Rank.ACE, Spy.Source.HAND);

        List<Card> result = spy.collectInformation(player, deck);

        assertTrue(result.isEmpty(), "При эффективности 0% должен возвращаться пустой список");
    }

    @Test
    void testGetRank() {
        spy = new RankCardsSpy(1.0f, Rank.KING, Spy.Source.HAND);

        assertEquals(Rank.KING, spy.getRank(),
                "Геттер rank должен возвращать правильное значение");
    }

    @Test
    void testGetSource() {
        spy = new RankCardsSpy(1.0f, Rank.ACE, Spy.Source.DECK);

        assertEquals(Spy.Source.DECK, spy.getSource(),
                "Геттер source должен возвращать правильное значение");
    }

    @Test
    void testNullRank() {
        // Проверяем обработку null rank
        spy = new RankCardsSpy(1.0f, Rank.ACE, Spy.Source.HAND);

        // Устанавливаем null через рефлексию
        try {
            var field = RankCardsSpy.class.getDeclaredField("strategy");
            field.setAccessible(true);
            var strategy = field.get(spy);

            var strategyField = strategy.getClass().getDeclaredField("rank");
            strategyField.setAccessible(true);
            strategyField.set(strategy, null);

        } catch (Exception e) {
            fail("Не удалось установить null rank: " + e.getMessage());
        }

        List<Card> result = spy.collectInformation(player, deck);

        assertTrue(result.isEmpty(), "При null rank должен возвращаться пустой список");
    }

    @Test
    void testAllRanks() {
        // Тестируем для всех возможных достоинств
        for (Rank rank : Rank.values()) {
            spy = new RankCardsSpy(1.0f, rank, Spy.Source.HAND);
            List<Card> result = spy.collectInformation(player, deck);

            // Проверяем, что все найденные карты имеют правильное достоинство
            for (Card card : result) {
                assertEquals(rank, card.getRank(),
                        "Все найденные карты должны иметь указанное достоинство");
            }
        }
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