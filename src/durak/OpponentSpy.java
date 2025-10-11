package durak;

import java.util.List;

public class OpponentSpy extends Spy {
    private final int SPY_ROUND_INTERVAL = 3; // Каждый 3-й раунд

    public OpponentSpy(float efficiency) {
        super(efficiency);
    }

    @Override
    public Card collectInformation(Player player, Deck deck, int currentRound) {
        // Работает каждый 3-й раунд
        if (currentRound % SPY_ROUND_INTERVAL != 0) {
            return null;
        }

        if (random.nextFloat() < efficiency) {
            List<Card> cards = player.getCards();
            if (!cards.isEmpty()) {
                return cards.get(random.nextInt(cards.size()));
            }
        }
        return null;
    }

    public int getSpyRoundInterval() {
        return SPY_ROUND_INTERVAL;
    }
}