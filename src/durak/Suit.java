package durak;
import java.util.HashMap;
import java.util.Map;
public class Suit {
    private String name;
    private boolean trump;
    // Статические константы
    public static final Suit HEARTS = new Suit("HEARTS", false);
    public static final Suit DIAMONDS = new Suit("DIAMONDS", false);
    public static final Suit CLUBS = new Suit("CLUBS", false);
    public static final Suit SPADES = new Suit("SPADES", false);
    private static final Map<String, Suit> BY_NAME = new HashMap<>();
    static {
        BY_NAME.put("HEARTS", HEARTS);
        BY_NAME.put("DIAMONDS", DIAMONDS);
        BY_NAME.put("CLUBS", CLUBS);
        BY_NAME.put("SPADES", SPADES);
    }

    public Suit(String name, boolean isTrump) {
        this.name = name;
        this.trump = isTrump;
    }

    public String getName() {
        return name;
    }

    public boolean isTrump() {
        return trump;
    }
    public void setTrump(boolean trump) {
        this.trump = trump;
    }
    // Статический метод для получения масти по имени
    public static Suit valueOf(String name) {
        Suit suit = BY_NAME.get(name.toUpperCase());
        if (suit == null) {
            throw new IllegalArgumentException("Unknown suit: " + name);
        }
        return suit;
    }
    @Override
    public String toString() {
        return name; // + (trump ? " (козырь)" : "")
    }
}
