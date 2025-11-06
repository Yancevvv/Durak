package durak;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;
import java.util.List;

class SuitCardsSpyTest {
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
    void testCollectInformationFromDeckWithMatchingSuit() {
        Suit targetSuit = Suit.HEARTS;
        SuitCardsSpy spy = new SuitCardsSpy(1.0f, targetSuit, Spy.Source.DECK);
        game.setSpy(spy);

        Card topCard = deck.peekTopCard();
        List<Card> result = spy.collectInformation(game);

        if (topCard.getSuit().equals(targetSuit)) {
            assertEquals(1, result.size());
            assertEquals(targetSuit, result.get(0).getSuit());
        } else {
            assertTrue(result.isEmpty());
        }
    }
    @Test
    void testCollectInformationWithTrumpSuit() {
        Suit trumpSuit = game.getTrumpSuit();
        SuitCardsSpy spy = new SuitCardsSpy(1.0f, trumpSuit, Spy.Source.DECK);
        game.setSpy(spy);

        Card topCard = deck.peekTopCard();
        List<Card> result = spy.collectInformation(game);

        if (topCard.getSuit().equals(trumpSuit)) {
            assertEquals(1, result.size());
            assertEquals(trumpSuit, result.get(0).getSuit());
            assertTrue(result.get(0).getSuit().isTrump());
        } else {
            assertTrue(result.isEmpty());
        }
    }

    @Test
    void testCollectInformationWithZeroEfficiency() {
        SuitCardsSpy spy = new SuitCardsSpy(0.0f, Suit.CLUBS, Spy.Source.DECK);
        game.setSpy(spy);

        List<Card> result = spy.collectInformation(game);

        assertTrue(result.isEmpty());
    }

    @Test
    void testCollectInformationWithNullSuit() {
        SuitCardsSpy spy = new SuitCardsSpy(1.0f, null, Spy.Source.DECK);
        game.setSpy(spy);

        List<Card> result = spy.collectInformation(game);

        assertTrue(result.isEmpty());
    }
    @Test
    void testCollectInformationFromEmptyHand() {
        SuitCardsSpy spy = new SuitCardsSpy(1.0f, Suit.CLUBS, Spy.Source.HAND);
        game.setSpy(spy);

        Player currentPlayer = game.getCurrentPlayer();
        Player opponent = (currentPlayer == player1) ? player2 : player1;

        // Опустошаем руку противника
        opponent.getCards().clear();

        List<Card> result = spy.collectInformation(game);

        assertTrue(result.isEmpty());
    }

    @Test
    void testGetSuitAndSource() {
        Suit targetSuit = Suit.SPADES;
        Spy.Source source = Spy.Source.HAND;
        SuitCardsSpy spy = new SuitCardsSpy(0.6f, targetSuit, source);

        assertEquals(targetSuit, spy.getSuit());
        assertEquals(source, spy.getSource());
    }

    @Test
    void testNoMatchingSuitInHand() {
        // Выбираем масть, которой вероятно нет в руке противника
        Suit targetSuit = Suit.CLUBS;
        SuitCardsSpy spy = new SuitCardsSpy(1.0f, targetSuit, Spy.Source.HAND);
        game.setSpy(spy);

        Player currentPlayer = game.getCurrentPlayer();
        Player opponent = (currentPlayer == player1) ? player2 : player1;

        // Убедимся, что в руке нет карт целевой масти
        boolean hasTargetSuit = opponent.getCards().stream()
                .anyMatch(card -> card.getSuit() == targetSuit);

        List<Card> result = spy.collectInformation(game);

        if (!hasTargetSuit) {
            assertTrue(result.isEmpty());
        }
    }

    @Test
    void testEfficiencyBoundaries() {
        // Проверяем, что эффективность ограничена [0,1]
        SuitCardsSpy spyLow = new SuitCardsSpy(-0.5f, Suit.HEARTS, Spy.Source.DECK);
        SuitCardsSpy spyHigh = new SuitCardsSpy(1.5f, Suit.HEARTS, Spy.Source.DECK);

        assertTrue(spyLow.efficiency >= 0.0f && spyLow.efficiency <= 1.0f);
        assertTrue(spyHigh.efficiency >= 0.0f && spyHigh.efficiency <= 1.0f);
    }
}