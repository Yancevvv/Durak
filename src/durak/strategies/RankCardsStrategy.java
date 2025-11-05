package durak.strategies;

import durak.*;
import java.util.*;

public class RankCardsStrategy implements SpyStrategy {
    private final Rank rank;
    private final Spy.Source source;

    public RankCardsStrategy(Rank rank, Spy.Source source) {
        this.rank = rank;
        this.source = source;
    }
    public Spy.Source getSource() {
        return source;
    }
    // Геттер для достоинства
    public Rank getRank() {
        return rank;
    }
    @Override
    public List<Card> collect(Player player, Deck deck, float efficiency) {
        if (rank == null || new Random().nextFloat() >= efficiency) {
            return List.of();
        }

        return switch (source) {
            case DECK -> {
                if (!deck.isEmpty() && deck.peekTopCard().getRank().equals(rank)) {
                    yield List.of(deck.peekTopCard());
                }
                yield List.of();
            }
            case HAND -> {
                List<Card> result = new ArrayList<>();
                for (Card c : player.getCards()) {
                    if (c.getRank().equals(rank)) {
                        result.add(c);
                    }
                }
                yield result;
            }
        };
    }
}