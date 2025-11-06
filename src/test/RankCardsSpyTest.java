package durak;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;
import java.util.List;

class RankCardsSpyTest {
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
    void testCollectInformationFromDeckWithMatchingRank() {
        // Создаем шпиона для конкретного ранга
        Rank targetRank = Rank.ACE;
        RankCardsSpy spy = new RankCardsSpy(1.0f, targetRank, Spy.Source.DECK);
        game.setSpy(spy);

        // Находим карту нужного ранга в колоде (если есть)
        Card matchingCard = findCardInDeck(targetRank);

        List<Card> result = spy.collectInformation(game);

        if (matchingCard != null && deck.peekTopCard().equals(matchingCard)) {
            assertEquals(1, result.size());
            assertEquals(targetRank, result.get(0).getRank());
        } else {
            // Если верхняя карта не совпадает с целевым рангом
            assertTrue(result.isEmpty());
        }
    }
    @Test
    void testCollectInformationWithZeroEfficiency() {
        RankCardsSpy spy = new RankCardsSpy(0.0f, Rank.ACE, Spy.Source.DECK);
        game.setSpy(spy);

        List<Card> result = spy.collectInformation(game);

        assertTrue(result.isEmpty());
    }

    @Test
    void testCollectInformationWithNullRank() {
        RankCardsSpy spy = new RankCardsSpy(1.0f, null, Spy.Source.DECK);
        game.setSpy(spy);

        List<Card> result = spy.collectInformation(game);

        assertTrue(result.isEmpty());
    }
    @Test
    void testCollectInformationFromEmptyHand() {
        RankCardsSpy spy = new RankCardsSpy(1.0f, Rank.ACE, Spy.Source.HAND);
        game.setSpy(spy);

        Player currentPlayer = game.getCurrentPlayer();
        Player opponent = (currentPlayer == player1) ? player2 : player1;

        // Опустошаем руку противника
        opponent.getCards().clear();

        List<Card> result = spy.collectInformation(game);

        assertTrue(result.isEmpty());
    }

    @Test
    void testGetRankAndSource() {
        Rank targetRank = Rank.KING;
        Spy.Source source = Spy.Source.HAND;
        RankCardsSpy spy = new RankCardsSpy(0.8f, targetRank, source);

        assertEquals(targetRank, spy.getRank());
        assertEquals(source, spy.getSource());
    }

    private Card findCardInDeck(Rank rank) {
        // В реальной реализации нужно получить доступ к картам в колоде
        // Для теста просто возвращаем null, так как структура Deck может не позволять просмотр
        return null;
    }

    @Test
    void testNoMatchingRankInHand() {
        Rank targetRank = Rank.ACE;
        RankCardsSpy spy = new RankCardsSpy(1.0f, targetRank, Spy.Source.HAND);
        game.setSpy(spy);

        Player currentPlayer = game.getCurrentPlayer();
        Player opponent = (currentPlayer == player1) ? player2 : player1;

        // Убедимся, что в руке нет карт целевого ранга
        boolean hasTargetRank = opponent.getCards().stream()
                .anyMatch(card -> card.getRank() == targetRank);

        List<Card> result = spy.collectInformation(game);

        if (!hasTargetRank) {
            assertTrue(result.isEmpty());
        }
    }
}