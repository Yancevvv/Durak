package durak;
import java.util.List;
public class RankCardsSpy extends Spy {
    private final durak.strategies.RankCardsStrategy strategy;

    public RankCardsSpy(float efficiency, Rank rank, Source source) {
        super(efficiency);
        this.strategy = new durak.strategies.RankCardsStrategy(rank, source);
    }

    @Override
    public List<Card> collectInformation(Player player, Deck deck) {
        return strategy.collect(player, deck, efficiency);
    }

    public Rank getRank() {
        return strategy.getRank();
    }

    public Source getSource() {
        return strategy.getSource();
    }
}
