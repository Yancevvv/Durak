package durak;
import java.util.List;
import java.util.ArrayList;
public class Table {
    private final List<Card> attackCards = new ArrayList<>();
    private final List<Card> defenseCards = new ArrayList<>();
    private final int maxCards = 6;

    public Table() {
        // Пустой конструктор
    }

    public void addAttackCard(Card card) {
        attackCards.add(card);
        defenseCards.add(null); // Инициализируем защиту как null
    }

    public void addCard(Card card) {
        // Для совместимости со старым кодом
        addAttackCard(card);
    }

    public boolean defendCard(int index, Card card) {
        if (index < 0 || index >= attackCards.size()) {
            return false; // Индекс вне диапазона
        }
        if (defenseCards.get(index) != null) {
            return false; // Карта уже отбита
        }
        defenseCards.set(index, card);
        return true;
    }

    public boolean allCardsDefended() {
        for (Card card : defenseCards) {
            if (card == null) {
                return false; // Есть незащищённые карты
            }
        }
        return true;
    }

    // Геттер для карт атаки
    public List<Card> getAttackCards() {
        return new ArrayList<>(attackCards); // Возвращаем копию списка
    }

    // Геттер для карт защиты
    public List<Card> getDefenseCards() {
        return new ArrayList<>(defenseCards); // Возвращаем копию списка
    }

    public boolean containsRank(Rank rank) {
        for (Card card : getCards()) {
            if (card.getRank() == rank) {
                return true;
            }
        }
        return false;
    }

    public Card getLastCard() {
        List<Card> allCards = getCards();
        return allCards.isEmpty() ? null : allCards.get(allCards.size() - 1);
    }

    public int size() {
        return getCards().size(); // Возвращаем размер списка карт
    }

    public void clear() {
        attackCards.clear();
        defenseCards.clear();
    }

    public boolean isFull() {
        return attackCards.size() >= maxCards;
    }

    public boolean isEmpty(){
        return attackCards.isEmpty();
    }

    public List<Card> getCards() {
        List<Card> allCards = new ArrayList<>();
        for (int i = 0; i < attackCards.size(); i++) {
            allCards.add(attackCards.get(i));
            if (i < defenseCards.size() && defenseCards.get(i) != null) {
                allCards.add(defenseCards.get(i));
            }
        }
        return allCards;
    }
}