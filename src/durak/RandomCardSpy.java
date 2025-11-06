package durak;

import java.util.*;
import java.util.Random;

public class RandomCardSpy extends Spy {
    private final Source source;
    private final Random random = new Random();

    public RandomCardSpy(float efficiency, Source source) {
        super(efficiency);
        this.source = source;
    }

    @Override
    public List<Card> collectInformation(Game game) {
        if (random.nextFloat() >= efficiency) {
            return List.of();
        }

        Player opponent = (game.getCurrentPlayer() == game.getPlayer1())
                ? game.getPlayer2() : game.getPlayer1();

        Card card = switch (source) {
            case DECK -> game.getDeck().isEmpty() ? null : game.getDeck().peekTopCard();
            case HAND -> {
                List<Card> cards = opponent.getCards();
                yield cards.isEmpty() ? null : cards.get(random.nextInt(cards.size()));
            }
        };

        return card != null ? List.of(card) : List.of();
    }

    public Source getSource() { return source; }
}