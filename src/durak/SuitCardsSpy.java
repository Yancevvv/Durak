package durak;
import java.util.List;
public class SuitCardsSpy extends Spy {
    private final durak.strategies.SuitCardsStrategy strategy;

    public SuitCardsSpy(float efficiency, Suit suit, Source source) {
        super(efficiency);
        this.strategy = new durak.strategies.SuitCardsStrategy(suit, source);
    }

    @Override
    public List<Card> collectInformation(Player player, Deck deck) {
        return strategy.collect(player, deck, efficiency);
    }

    public Suit getSuit() {
        return strategy.getSuit();
    }

    public Source getSource() {
        return strategy.getSource();
    }
}