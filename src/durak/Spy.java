// durak/Spy.java
package durak;

import java.util.List;

public abstract class Spy {
    protected float efficiency;
    public enum Source { DECK, HAND }

    public Spy(float efficiency) {
        this.efficiency = Math.min(1.0f, Math.max(0.0f, efficiency));
    }

    // Метод вызывается из Game — шпион сам получит нужные данные
    public abstract List<Card> collectInformation(Game game);
}