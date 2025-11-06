package durak;

import java.util.*;

public class RankCardsSpy extends Spy {
    private final Rank rank;
    private final Source source;

    public RankCardsSpy(float efficiency, Rank rank, Source source) {
        super(efficiency);
        this.rank = rank;
        this.source = source;
    }

    @Override
    public List<Card> collectInformation(Game game) {
        if (rank == null || new Random().nextFloat() >= efficiency) {
            return List.of();
        }

        Player opponent = (game.getCurrentPlayer() == game.getPlayer1())
                ? game.getPlayer2() : game.getPlayer1();

        return switch (source) {
            case DECK -> {
                Deck deck = game.getDeck();
                if (!deck.isEmpty() && deck.peekTopCard().getRank().equals(rank)) {
                    yield List.of(deck.peekTopCard());
                }
                yield List.of();
            }
            case HAND -> {
                List<Card> result = new ArrayList<>();
                for (Card c : opponent.getCards()) {
                    if (c.getRank().equals(rank)) {
                        result.add(c);
                    }
                }
                yield result;
            }
        };
    }

    public Rank getRank() { return rank; }
    public Source getSource() { return source; }
}