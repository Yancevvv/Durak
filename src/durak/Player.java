package durak;
import java.util.List;
import java.util.ArrayList;
public class Player {
    private List<Card> cardsOnHand = new ArrayList<>();
    private boolean isAttacker;
    public Player() {
        this.cardsOnHand = new ArrayList<>();
        this.isAttacker = false;
    }
    public void setCards(List<Card> cards) {
        this.cardsOnHand = cards;
    }
    public void addCard(Card card) {
        if (card != null) {
            cardsOnHand.add(card);
        }
    }
    public void takeCards(List<Card> cardsToTake) {
        if (cardsToTake == null || cardsToTake.isEmpty()) {
            return;
        }
        System.out.println("Игрок забирает " + cardsToTake.size() + " карт");
        this.cardsOnHand.addAll(cardsToTake);
    }
    public boolean hasCard(Card card) {
        return cardsOnHand.contains(card);
    }
    public void removeCard(Card card) {
        cardsOnHand.remove(card);
    }
    public Card beatCard(Card cardToBeat) {
        for (Card card : cardsOnHand) {
            if (card.beat(cardToBeat)) {
                cardsOnHand.remove(card);
                return card;
            }
        }
        return null;
    }
    public Card getLowestTrump() {
        Card lowestTrump = null;

        for (Card card : cardsOnHand) {
            // Проверяем, является ли карта козырем
            if (card.getSuit().isTrump()) {
                // Если это первый найденный козырь или карта младше текущего самого младшего
                if (lowestTrump == null ||
                        card.getRank().getValue() < lowestTrump.getRank().getValue()) {
                    lowestTrump = card;
                }
            }
        }

        return lowestTrump;
    }
    public List<Card> getCards() {
        return new ArrayList<>(cardsOnHand);
    }
    public void setAttacker(boolean attacker) {
        isAttacker = attacker;
    }
    public boolean isAttacker() {
        return isAttacker;
    }
    public boolean hasCards() {
        return !cardsOnHand.isEmpty();
    }

    public int cardCount() {
        return cardsOnHand.size();
    }
}
