package durak;

import java.util.*;

public class SuitCardsSpy extends Spy {
    private final Suit suit;
    private final Source source;

    public SuitCardsSpy(float efficiency, Suit suit, Source source) {
        super(efficiency);
        this.suit = suit;
        this.source = source;
    }

    @Override
    public List<Card> collectInformation(Game game) {
        if (suit == null || new Random().nextFloat() >= efficiency) {
            return List.of();
        }

        Player opponent = (game.getCurrentPlayer() == game.getPlayer1())
                ? game.getPlayer2() : game.getPlayer1();

        return switch (source) {
            case DECK -> {
                Deck deck = game.getDeck();
                if (!deck.isEmpty() && deck.peekTopCard().getSuit().equals(suit)) {
                    yield List.of(deck.peekTopCard());
                }
                yield List.of();
            }
            case HAND -> {
                List<Card> result = new ArrayList<>();
                for (Card c : opponent.getCards()) {
                    if (c.getSuit().equals(suit)) {
                        result.add(c);
                    }
                }
                yield result;
            }
        };
    }

    public Suit getSuit() { return suit; }
    public Source getSource() { return source; }
}