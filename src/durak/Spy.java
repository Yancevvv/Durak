package durak;
import java.util.Random;
import java.util.List;
public class Spy {
    private float efficiency;
    private final Random random = new Random();
    public Spy(float efficiency) {
        this.efficiency = Math.min(1.0f, Math.max(0.0f, efficiency)); // Ограничиваем 0-1
    }

    public Card collectInformation(Player player) {
        if (random.nextFloat() < efficiency) {
            List<Card> cards = player.getCards();
            if (!cards.isEmpty()) {
                return cards.get(random.nextInt(cards.size()));
            }
        }
        return null;
    }
}
