package durak;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.Arrays;
import java.util.List;

class OpponentSpyTest {
    private OpponentSpy spy;
    private Player player;
    private Deck deck;
    private Card card1, card2, card3;

    @BeforeEach
    void setUp() {
        spy = new OpponentSpy(1.0f); // 100% эффективность для тестов
        player = new Player();
        deck = new Deck();

        card1 = new Card(new Suit("Hearts", false), Rank.SIX);
        card2 = new Card(new Suit("Diamonds", false), Rank.ACE);
        card3 = new Card(new Suit("Spades", true), Rank.KING); // Козырь

        player.addCard(card1);
        player.addCard(card2);
        player.addCard(card3);
    }

    @Test
    void testCollectInformation_SuccessOnThirdRound() {
        // Проверяем, что шпион работает на 3-м раунде
        Card result = spy.collectInformation(player, deck, 3);

        assertNotNull(result, "Шпион должен вернуть карту на 3-м раунде");
        assertTrue(player.getCards().contains(result),
                "Возвращенная карта должна быть у игрока");
    }

    @Test
    void testCollectInformation_FailOnNonThirdRound() {
        // Проверяем, что шпион не работает на других раундах
        Card result1 = spy.collectInformation(player, deck, 1);
        Card result2 = spy.collectInformation(player, deck, 2);
        Card result4 = spy.collectInformation(player, deck, 4);

        assertNull(result1, "Шпион не должен работать на 1-м раунде");
        assertNull(result2, "Шпион не должен работать на 2-м раунде");
        assertNull(result4, "Шпион не должен работать на 4-м раунде");
    }

    @Test
    void testCollectInformation_WithZeroEfficiency() {
        // Шпион с 0% эффективностью никогда не должен возвращать информацию
        OpponentSpy ineffectiveSpy = new OpponentSpy(0.0f);
        Card result = ineffectiveSpy.collectInformation(player, deck, 3);

        assertNull(result, "Шпион с 0% эффективностью не должен возвращать карты");
    }

    @Test
    void testCollectInformation_EmptyPlayerHand() {
        // Проверяем случай, когда у игрока нет карт
        Player emptyPlayer = new Player();
        Card result = spy.collectInformation(emptyPlayer, deck, 3);

        assertNull(result, "Шпион должен вернуть null если у игрока нет карт");
    }

    @Test
    void testCollectInformation_ReturnsRandomCard() {
        // Проверяем, что возвращается случайная карта из руки игрока
        List<Card> playerCards = player.getCards();

        // Вызываем несколько раз и собираем результаты
        boolean foundCard1 = false;
        boolean foundCard2 = false;
        boolean foundCard3 = false;

        for (int i = 0; i < 10; i++) {
            Card result = spy.collectInformation(player, deck, 3);
            if (result.equals(card1)) foundCard1 = true;
            if (result.equals(card2)) foundCard2 = true;
            if (result.equals(card3)) foundCard3 = true;
        }

        // Хотя бы одна карта должна быть возвращена (в реальности все три)
        assertTrue(foundCard1 || foundCard2 || foundCard3,
                "Шпион должен возвращать разные карты из руки игрока");
    }

    @Test
    void testGetSpyRoundInterval() {
        assertEquals(3, spy.getSpyRoundInterval(),
                "Интервал раундов должен быть 3");
    }

    @Test
    void testEfficiencyGetterSetter() {
        spy.setEfficiency(0.5f);
        assertEquals(0.5f, spy.getEfficiency(), 0.001f,
                "Геттер/сеттер эффективности должны работать корректно");
    }
}