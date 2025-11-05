package durak;

import java.util.List;

public abstract class Spy {
    protected float efficiency;
    protected SpyStrategy strategy;
    public enum SpyMode { RANDOM_CARD, SUIT, RANK, COUNT }
    public enum Source { DECK, HAND }
    public Spy(float efficiency) {
        this.efficiency = Math.min(1.0f, Math.max(0.0f, efficiency));
    }

    public void setStrategy(SpyStrategy strategy) {
        this.strategy = strategy;
    }
    public void setEfficiency(float efficiency){this.efficiency = efficiency;}
    public float getEfficiency(){return this.efficiency;}
    public List<Card> collectInformation(Player player, Deck deck) {
        if (strategy == null) {
            return List.of();
        }
        return strategy.collect(player, deck, efficiency);
    }
}