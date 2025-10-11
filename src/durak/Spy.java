package durak;

import java.util.Random;

public abstract class Spy {
    protected float efficiency;
    protected final Random random = new Random();

    public Spy(float efficiency) {
        this.efficiency = Math.min(1.0f, Math.max(0.0f, efficiency));
    }

    public abstract Card collectInformation(Player player, Deck deck, int currentRound);

    public float getEfficiency() {
        return efficiency;
    }

    public void setEfficiency(float efficiency) {
        this.efficiency = efficiency;
    }
}