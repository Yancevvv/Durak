package durak;

public class DeckSpy extends Spy {
    private final int SPY_ROUND_INTERVAL = 5; // Каждый 5-й раунд

    public DeckSpy(float efficiency) {
        super(efficiency);
    }

    @Override
    public Card collectInformation(Player player, Deck deck, int currentRound) {
        // Работает каждый 5-й раунд
        if (currentRound % SPY_ROUND_INTERVAL != 0) {
            return null;
        }

        if (random.nextFloat() < efficiency) {
            if (!deck.isEmpty()) {
                return deck.peekTopCard();
            }
        }
        return null;
    }

    public int getSpyRoundInterval() {
        return SPY_ROUND_INTERVAL;
    }
}