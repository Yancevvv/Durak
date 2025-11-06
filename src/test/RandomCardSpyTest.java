package durak;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;
import java.util.List;

class RandomCardSpyTest {
    private Game game;
    private Player player1;
    private Player player2;
    private Deck deck;

    @BeforeEach
    void setUp() {
        game = new Game();
        player1 = game.getPlayer1();
        player2 = game.getPlayer2();
        deck = game.getDeck();
    }

    @Test
    void testCollectInformationFromDeckWithFullEfficiency() {
        RandomCardSpy spy = new RandomCardSpy(1.0f, Spy.Source.DECK);
        game.setSpy(spy);

        // Убедимся, что колода не пуста
        assertFalse(deck.isEmpty());

        Card topCard = deck.peekTopCard();
        List<Card> result = spy.collectInformation(game);

        assertEquals(1, result.size());
        assertEquals(topCard, result.get(0));
    }

    @Test
    void testCollectInformationFromDeckWithZeroEfficiency() {
        RandomCardSpy spy = new RandomCardSpy(0.0f, Spy.Source.DECK);
        game.setSpy(spy);

        List<Card> result = spy.collectInformation(game);

        assertTrue(result.isEmpty());
    }

    @Test
    void testCollectInformationFromHandWithFullEfficiency() {
        RandomCardSpy spy = new RandomCardSpy(1.0f, Spy.Source.HAND);
        game.setSpy(spy);

        Player currentPlayer = game.getCurrentPlayer();
        Player opponent = (currentPlayer == player1) ? player2 : player1;

        // Убедимся, что у противника есть карты
        assertFalse(opponent.getCards().isEmpty());

        List<Card> result = spy.collectInformation(game);

        // Должны получить одну случайную карту из руки противника
        assertEquals(1, result.size());
        Card spyCard = result.get(0);
        assertTrue(opponent.getCards().contains(spyCard));
    }
    @Test
    void testGetSource() {
        RandomCardSpy spyDeck = new RandomCardSpy(0.5f, Spy.Source.DECK);
        RandomCardSpy spyHand = new RandomCardSpy(0.7f, Spy.Source.HAND);

        assertEquals(Spy.Source.DECK, spyDeck.getSource());
        assertEquals(Spy.Source.HAND, spyHand.getSource());
    }

    @Test
    void testEfficiencyBoundaries() {
        // Проверяем, что эффективность ограничена [0,1]
        RandomCardSpy spyLow = new RandomCardSpy(-0.5f, Spy.Source.DECK);
        RandomCardSpy spyHigh = new RandomCardSpy(1.5f, Spy.Source.DECK);

        // Должны быть корректированы до границ
        assertTrue(spyLow.efficiency >= 0.0f && spyLow.efficiency <= 1.0f);
        assertTrue(spyHigh.efficiency >= 0.0f && spyHigh.efficiency <= 1.0f);
    }
}