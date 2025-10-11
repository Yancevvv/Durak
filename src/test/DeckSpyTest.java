package durak;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;

class DeckSpyTest {
    private DeckSpy spy;
    private Player player;
    private Deck deck;

    @BeforeEach
    void setUp() {
        spy = new DeckSpy(1.0f); // 100% эффективность
        player = new Player();
        deck = new Deck();
    }

    @Test
    void testCollectInformation_SuccessOnFifthRound() {
        // Создаем тестовую колоду с известной верхней картой
        List<Card> testCards = new ArrayList<>();
        Card topCard = new Card(new Suit("Hearts", false), Rank.ACE);
        testCards.add(topCard);
        testCards.add(new Card(new Suit("Diamonds", false), Rank.KING));

        // Используем рефлексию или метод для установки тестовых карт
        setDeckCards(deck, testCards);

        Card result = spy.collectInformation(player, deck, 5);

        assertNotNull(result, "Шпион должен вернуть карту на 5-м раунде");
        assertEquals(topCard, result, "Должна быть возвращена верхняя карта колоды");
    }

    @Test
    void testCollectInformation_FailOnNonFifthRound() {
        setupTestDeck();

        Card result1 = spy.collectInformation(player, deck, 1);
        Card result4 = spy.collectInformation(player, deck, 4);
        Card result6 = spy.collectInformation(player, deck, 6);

        assertNull(result1, "Шпион не должен работать на 1-м раунде");
        assertNull(result4, "Шпион не должен работать на 4-м раунде");
        assertNull(result6, "Шпион не должен работать на 6-м раунде");
    }

    @Test
    void testCollectInformation_EmptyDeck() {
        // Создаем пустую колоду
        Deck emptyDeck = new Deck();
        // Очищаем колоду (в реальном коде нужен метод clear())
        setDeckCards(emptyDeck, new ArrayList<>());

        Card result = spy.collectInformation(player, emptyDeck, 5);

        assertNull(result, "Шпион должен вернуть null при пустой колоде");
    }

    @Test
    void testCollectInformation_WithPartialEfficiency() {
        DeckSpy halfEffectiveSpy = new DeckSpy(0.5f);
        setupTestDeck();

        int successCount = 0;
        int totalAttempts = 1000;

        for (int i = 0; i < totalAttempts; i++) {
            Card result = halfEffectiveSpy.collectInformation(player, deck, 5);
            if (result != null) {
                successCount++;
            }
        }

        // Проверяем, что успешных попыток примерно 50% (±5%)
        double successRate = (double) successCount / totalAttempts;
        assertTrue(successRate > 0.45 && successRate < 0.55,
                "Эффективность 50% должна давать примерно 50% успешных попыток");
    }

    @Test
    void testCollectInformation_AlwaysReturnsTopCard() {
        // Проверяем, что всегда возвращается именно верхняя карта
        List<Card> testCards = Arrays.asList(
                new Card(new Suit("Clubs", false), Rank.TEN),
                new Card(new Suit("Spades", true), Rank.QUEEN),
                new Card(new Suit("Hearts", false), Rank.JACK)
        );
        setDeckCards(deck, testCards);

        Card expectedTopCard = testCards.get(0);

        // Многократно вызываем и проверяем, что всегда возвращается верхняя карта
        for (int i = 0; i < 10; i++) {
            Card result = spy.collectInformation(player, deck, 5);
            assertEquals(expectedTopCard, result,
                    "Шпион должен всегда возвращать верхнюю карту колоды");
        }
    }

    @Test
    void testGetSpyRoundInterval() {
        assertEquals(5, spy.getSpyRoundInterval(),
                "Интервал раундов должен быть 5");
    }

    @Test
    void testEfficiencyBounds() {
        // Проверяем, что эффективность ограничена диапазоном [0, 1]
        DeckSpy spyNegative = new DeckSpy(-1.0f);
        DeckSpy spyExcessive = new DeckSpy(2.0f);

        assertEquals(0.0f, spyNegative.getEfficiency(), 0.001f,
                "Отрицательная эффективность должна быть приведена к 0");
        assertEquals(1.0f, spyExcessive.getEfficiency(), 0.001f,
                "Эффективность >1 должна быть приведена к 1");
    }

    // Вспомогательные методы
    private void setupTestDeck() {
        List<Card> testCards = Arrays.asList(
                new Card(new Suit("Hearts", false), Rank.ACE),
                new Card(new Suit("Diamonds", false), Rank.KING)
        );
        setDeckCards(deck, testCards);
    }

    private void setDeckCards(Deck deck, List<Card> cards) {
        // В реальном проекте нужно добавить метод setCards() в Deck
        // Или использовать рефлексию для тестов
        try {
            var field = Deck.class.getDeclaredField("cards");
            field.setAccessible(true);
            field.set(deck, new ArrayList<>(cards));
        } catch (Exception e) {
            fail("Не удалось установить тестовые карты в колоду: " + e.getMessage());
        }
    }
}