package durak;
import java.util.*;

public class Spy {
    private float efficiency; // Эффективность шпиона (0.0 - 1.0)
    private final Random random = new Random();
    private SpyMode mode; // Режим работы шпиона
    private Suit targetSuit; // Целевая масть (если выбран режим масти)
    private Rank targetRank; // Целевое достоинство (если выбран режим достоинства)
    public enum SpyMode {
        RANDOM_CARD, // Случайная карта
        SUIT,        // Карта определённой масти
        RANK,        // Карта определённого достоинства
        COUNT        // Определённое количество карт
    }
    public Spy(float efficiency) {
        this.efficiency = Math.min(1.0f, Math.max(0.0f, efficiency)); // Ограничиваем эффективность в диапазоне [0, 1]
        this.mode = SpyMode.RANDOM_CARD; // По умолчанию случайная карта
    }
    public void setMode(SpyMode mode) {
        this.mode = mode;
    }

    public void setTargetSuit(Suit suit) {
        this.targetSuit = suit;
    }

    public void setTargetRank(Rank rank) {
        this.targetRank = rank;
    }
    public List<Card> collectInformation(Player player, Deck deck, int count) {
        if (random.nextFloat() >= efficiency) {
            return Collections.emptyList();
        }

        switch (mode) {
            case RANDOM_CARD:
                Card card = getRandomCard(player, deck);
                return card != null ? Arrays.asList(card) : Collections.emptyList();

            case SUIT:
                return getCardsBySuit(player, deck, targetSuit);
            case RANK:
                return getCardsByRank(player, deck, targetRank);
            case COUNT:
                return getCardsByCount(player, deck, count);
            default:
                return Collections.emptyList();
        }
    }git 
    private Card getRandomCard(Player player, Deck deck) {
        // Приоритет: карта из колоды
        if (!deck.isEmpty()) {
            return deck.peekTopCard();
        }

        // Или карта из руки игрока
        List<Card> cards = player.getCards();
        return cards.isEmpty() ? null : cards.get(random.nextInt(cards.size()));
    }
    private List<Card> getCardsBySuit(Player player, Deck deck, Suit suit) {
        List<Card> result = new ArrayList<>();

        if (suit == null) return result;

        // Проверяем колоду
        if (!deck.isEmpty()) {
            Card topCard = deck.peekTopCard();
            if (topCard.getSuit().equals(suit)) {
                result.add(topCard);
            }
        }

        // Проверяем руку игрока
        for (Card card : player.getCards()) {
            if (card.getSuit().equals(suit)) {
                result.add(card);
            }
        }

        return result;
    }
    private List<Card> getCardsByRank(Player player, Deck deck, Rank rank) {
        List<Card> result = new ArrayList<>();

        if (rank == null) return result;

        // Проверяем колоду
        if (!deck.isEmpty()) {
            Card topCard = deck.peekTopCard();
            if (topCard.getRank().equals(rank)) {
                result.add(topCard);
            }
        }

        // Проверяем руку игрока
        for (Card card : player.getCards()) {
            if (card.getRank().equals(rank)) {
                result.add(card);
            }
        }

        return result;
    }
    private List<Card> getCardsByCount(Player player, Deck deck, int count) {
        if (targetSuit != null) {
            return getCardsBySuit(player, deck, targetSuit);
        } else if (targetRank != null) {
            return getCardsByRank(player, deck, targetRank);
        } else {
            return Collections.emptyList();
        }
    }
}