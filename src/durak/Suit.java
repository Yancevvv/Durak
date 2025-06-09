package durak;
public class Suit {
    private String name;
    private boolean trump;

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

    @Override
    public String toString() {
        return name; // + (trump ? " (козырь)" : "")
    }
}
