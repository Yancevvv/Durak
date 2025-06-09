package durak;
public class Card {
    private Suit suit;
    private Rank rank;

    public Card(Suit suit, Rank rank) {
        this.suit = suit;
        this.rank = rank;
    }

    public boolean beat(Card other) {
        // Текущая карта - козырь, а другая - нет
        if (this.suit.isTrump() && !other.suit.isTrump()) {
            return true;
        }
        // Другая карта - козырь, а текущая - нет
        if (!this.suit.isTrump() && other.suit.isTrump()) {
            return false;
        }
        // Обе карты одной масти или обе козыри
        if (this.suit == other.suit) {
            return this.rank.getValue() > other.rank.getValue();
        }
        // Разные масти и не козыри
        return false;
    }
    public Suit getSuit() {
        return suit;
    }

    public Rank getRank() {
        return rank;
    }

    @Override
    public String toString() {
        return rank + " of " + suit;
    }
}
