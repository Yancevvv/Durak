package durak.strategies;

import durak.*;
import java.util.*;

public class RandomCardStrategy implements SpyStrategy {
    private final Spy.Source source;
    private final Random random = new Random();

    public RandomCardStrategy(Spy.Source source) {
        this.source = source;
    }
    public Spy.Source getSource() {
        return source;
    }
    @Override
    public List<Card> collect(Player player, Deck deck, float efficiency) {
        if (random.nextFloat() >= efficiency) return List.of();

        Card card = switch (source) {
            case DECK -> deck.isEmpty() ? null : deck.peekTopCard();
            case HAND -> {
                List<Card> cards = player.getCards();
                yield cards.isEmpty() ? null : cards.get(random.nextInt(cards.size()));
            }
        };

        return card != null ? List.of(card) : List.of();
    }
}