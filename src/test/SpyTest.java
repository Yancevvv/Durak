package durak;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.Arrays;
import java.util.List;

class SpyTest {
    private Spy spy;
    private Player player;
    private Deck deck;
    private Card heartsAce;
    private Card diamondsKing;
    private Card clubsQueen;
    private Card spadesTen;

    @BeforeEach
    void setUp() {
        spy = new Spy(1.0f); // 100% эффективность для тестов
        player = new Player();
        deck = new Deck();

        // Создаем тестовые карты
        heartsAce = new Card(new Suit("Hearts", false), Rank.ACE);
        diamondsKing = new Card(new Suit("Diamonds", false), Rank.KING);
        clubsQueen = new Card(new Suit("Clubs", false), Rank.QUEEN);
        spadesTen = new Card(new Suit("Spades", false), Rank.TEN);

        // Добавляем карты игроку
        player.addCard(heartsAce);
        player.addCard(diamondsKing);
        player.addCard(clubsQueen);
    }

    @Test
    void testCollectInformation_RandomCardMode() {
        spy.setMode(Spy.SpyMode.RANDOM_CARD);

        List<Card> result = spy.collectInformation(player, deck, 1);

        assertFalse(result.isEmpty(), "Должна быть возвращена хотя бы одна карта");
        assertEquals(1, result.size(), "В режиме RANDOM_CARD должна возвращаться одна карта");
        assertTrue(player.getCards().contains(result.get(0)) ||
                        (deck.peekTopCard() != null && deck.peekTopCard().equals(result.get(0))),
                "Возвращенная карта должна быть у игрока или в колоде");
    }

    @Test
    void testCollectInformation_SuitMode_NotFound() {
        spy.setMode(Spy.SpyMode.SUIT);
        spy.setTargetSuit(new Suit("Spades", false)); // У игрока нет пик

        List<Card> result = spy.collectInformation(player, deck, 1);

        assertTrue(result.isEmpty(), "Не должно быть найдено карт масти Spades");
    }

    @Test
    void testCollectInformation_SuitMode_NullSuit() {
        spy.setMode(Spy.SpyMode.SUIT);
        spy.setTargetSuit(null);

        List<Card> result = spy.collectInformation(player, deck, 1);

        assertTrue(result.isEmpty(), "При null масти должен возвращаться пустой список");
    }

    @Test
    void testCollectInformation_RankMode_NotFound() {
        spy.setMode(Spy.SpyMode.RANK);
        spy.setTargetRank(Rank.JACK); // У игрока нет валетов

        List<Card> result = spy.collectInformation(player, deck, 1);

        assertTrue(result.isEmpty(), "Не должно быть найдено карт достоинства JACK");
    }

    @Test
    void testCollectInformation_RankMode_NullRank() {
        spy.setMode(Spy.SpyMode.RANK);
        spy.setTargetRank(null);

        List<Card> result = spy.collectInformation(player, deck, 1);

        assertTrue(result.isEmpty(), "При null достоинстве должен возвращаться пустой список");
    }

    @Test
    void testCollectInformation_CountMode_WithRank() {
        spy.setMode(Spy.SpyMode.COUNT);
        spy.setTargetRank(Rank.KING);

        List<Card> result = spy.collectInformation(player, deck, 1);

        // В текущей реализации COUNT с достоинством ведет себя как RANK
        assertFalse(result.isEmpty(), "Должны быть найдены карты достоинства KING");
        assertEquals(diamondsKing, result.get(0), "Должна быть найдена карта KING");
    }

    @Test
    void testCollectInformation_CountMode_NoSuitOrRank() {
        spy.setMode(Spy.SpyMode.COUNT);
        // Не устанавливаем ни масть, ни достоинство

        List<Card> result = spy.collectInformation(player, deck, 1);

        assertTrue(result.isEmpty(), "Без масти и достоинства должен возвращаться пустой список");
    }

    @Test
    void testCollectInformation_EfficiencyZero() {
        Spy ineffectiveSpy = new Spy(0.0f); // 0% эффективность
        ineffectiveSpy.setMode(Spy.SpyMode.RANDOM_CARD);

        List<Card> result = ineffectiveSpy.collectInformation(player, deck, 1);

        assertTrue(result.isEmpty(), "При эффективности 0% всегда должен возвращаться пустой список");
    }

    @Test
    void testCollectInformation_EmptyPlayerHand() {
        Player emptyPlayer = new Player(); // Игрок без карт
        spy.setMode(Spy.SpyMode.RANDOM_CARD);

        List<Card> result = spy.collectInformation(emptyPlayer, deck, 1);

        // Может вернуть карту из колоды или пустой список
        assertTrue(result.size() <= 1, "Может вернуть не более одной карты");
    }

    @Test
    void testCollectInformation_PriorityDeckOverPlayer() {
        // Настраиваем колоду с известной картой
        List<Card> testDeckCards = Arrays.asList(spadesTen);
        setDeckCards(deck, testDeckCards);

        spy.setMode(Spy.SpyMode.RANDOM_CARD);

        List<Card> result = spy.collectInformation(player, deck, 1);

        assertFalse(result.isEmpty(), "Должна быть возвращена карта");
        assertEquals(spadesTen, result.get(0), "Должна быть возвращена карта из колоды (приоритет колоды)");
    }

    @Test
    void testDefaultMode() {
        // Проверяем, что по умолчанию установлен режим RANDOM_CARD
        assertEquals(Spy.SpyMode.RANDOM_CARD, getSpyMode(spy),
                "По умолчанию должен быть установлен режим RANDOM_CARD");
    }

    @Test
    void testEfficiencyBounds() {
        // Проверяем ограничение эффективности в диапазоне [0, 1]
        Spy spyNegative = new Spy(-1.0f);
        Spy spyExcessive = new Spy(2.0f);

        assertEquals(0.0f, getSpyEfficiency(spyNegative), 0.001f,
                "Отрицательная эффективность должна быть приведена к 0");
        assertEquals(1.0f, getSpyEfficiency(spyExcessive), 0.001f,
                "Эффективность >1 должна быть приведена к 1");
    }

    // Вспомогательные методы для доступа к private полям через рефлексию
    private Spy.SpyMode getSpyMode(Spy spy) {
        try {
            var field = Spy.class.getDeclaredField("mode");
            field.setAccessible(true);
            return (Spy.SpyMode) field.get(spy);
        } catch (Exception e) {
            fail("Не удалось получить режим шпиона: " + e.getMessage());
            return null;
        }
    }

    private float getSpyEfficiency(Spy spy) {
        try {
            var field = Spy.class.getDeclaredField("efficiency");
            field.setAccessible(true);
            return (float) field.get(spy);
        } catch (Exception e) {
            fail("Не удалось получить эффективность шпиона: " + e.getMessage());
            return 0.0f;
        }
    }

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