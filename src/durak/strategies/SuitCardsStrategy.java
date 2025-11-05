package durak.strategies;

import durak.*;
import java.util.*;

public class SuitCardsStrategy implements SpyStrategy {
    private final Suit suit;
    private final Spy.Source source;

    public SuitCardsStrategy(Suit suit, Spy.Source source) {
        this.suit = suit;
        this.source = source;
    }
    public Spy.Source getSource() {
        return source;
    }
    // Геттер для масти
    public Suit getSuit() {
        return suit;
    }
    @Override
    public List<Card> collect(Player player, Deck deck, float efficiency) {
        if (suit == null || new Random().nextFloat() >= efficiency) {
            return List.of();
        }

        return switch (source) {
            case DECK -> {
                if (!deck.isEmpty() && deck.peekTopCard().getSuit().equals(suit)) {
                    yield List.of(deck.peekTopCard());
                }
                yield List.of();
            }
            case HAND -> {
                List<Card> result = new ArrayList<>();
                for (Card c : player.getCards()) {
                    if (c.getSuit().equals(suit)) {
                        result.add(c);
                    }
                }
                yield result;
            }
        };
    }
}