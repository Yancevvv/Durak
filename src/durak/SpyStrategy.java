package durak;

import java.util.List;

public interface SpyStrategy {
    List<Card> collect(Player player, Deck deck, float efficiency);
}