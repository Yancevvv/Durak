package durak;
import java.util.List;
public class RandomCardSpy extends Spy {
    private final durak.strategies.RandomCardStrategy strategy;

    public RandomCardSpy(float efficiency, Source source) {
        super(efficiency);
        this.strategy = new durak.strategies.RandomCardStrategy(source);
    }

    @Override
    public List<Card> collectInformation(Player player, Deck deck) {
        return strategy.collect(player, deck, efficiency);
    }

    public Source getSource() {
        return strategy.getSource();
    }
}