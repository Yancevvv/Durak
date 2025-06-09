package durak;
import java.util.List;
import java.util.ArrayList;
import java.util.Random;

public class Deck {
    private List<Card> cards;
    private Suit trumpSuit;
    private Random random = new Random();
    public Deck() {
        this.cards = new ArrayList<>();
        buildDeck();
        mix();
        identifyTrumpSuit();
    }

    private void buildDeck() {
        Suit[] suits = {
                new Suit("Hearts", false),
                new Suit("Diamonds", false),
                new Suit("Clubs", false),
                new Suit("Spades", false)
        };

        for (Suit suit : suits) {
            for (Rank rank : Rank.values()) {
                cards.add(new Card(suit, rank));
            }
        }
    }
    public List<Card> getCards() {
        return new ArrayList<>(cards); // Возвращаем копию для безопасности
    }
    public void mix() {
        for (int i = 0; i < cards.size(); i++) {
            int j = random.nextInt(cards.size());
            Card temp = cards.get(i);
            cards.set(i, cards.get(j));
            cards.set(j, temp);
        }
    }

    public Card takeCard() {
        if (cards.isEmpty()) {
            return null;
        }
        return cards.remove(0);
    }
    public void identifyTrumpSuit() {
        if (!cards.isEmpty()) {
            // Берем последнюю карту в колоде как козырь
            Card trumpCard = cards.get(cards.size() - 1);
            this.trumpSuit = trumpCard.getSuit();
            this.trumpSuit.setTrump(true); // Помечаем масть как козырную

            // Для отладки:
            System.out.println("Определен козырь: " + trumpSuit);
        }
    }
    public void distributingCards(List<Player> players) {
        for (Player player : players) {
            while (player.getCards().size() < 6 && !cards.isEmpty()) {
                player.addCard(takeCard());
            }
        }
    }
    public void addCard(Card card) {
        if (card != null) {
            cards.add(card);
        }
    }

    public boolean isEmpty() {
        return cards.isEmpty();
    }
    public Suit getTrumpSuit() {
        return trumpSuit;
    }

    public int size() {
        return cards.size();
    }
}