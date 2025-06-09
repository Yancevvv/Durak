package durak;
import java.util.Random;
import java.util.List;
public class Spy {
    private float efficiency;
    private final Random random = new Random();
    private final int SPY_ROUND_INTERVAL = 5; // Шпион работает каждый 5-й раунд
    public Spy(float efficiency) {
        this.efficiency = Math.min(1.0f, Math.max(0.0f, efficiency)); // Ограничиваем 0-1
    }

    public Card collectInformation(Player player, Deck deck, int currentRound) {
        // Проверяем, что это подходящий раунд для шпиона
        if (currentRound % SPY_ROUND_INTERVAL != 0) {
            return null;
        }

        // С вероятностью efficiency показываем верхнюю карту колоды
        if (random.nextFloat() < efficiency) {
            if (!deck.isEmpty()) {
                // Получаем верхнюю карту (не удаляя ее из колоды)
                return deck.peekTopCard();
            }
        }
        return null;
    }
}
