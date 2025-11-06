package durak;
import durak.gui.MainMenuFrame;
import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Collections;

public class Game {
    private Deck deck;
    private Player player1;
    private Player player2;
    private Spy spy;
    private int spyRoundCounter = 0;
    public final int SPY_ACTIVATION_ROUND = 3; // Каждый 3-й раунд
    private boolean spyUsedThisRound = false;
    private Table table;
    private Player currentPlayer;
    private Player defendingPlayer;
    private Suit trumpSuit;
    private boolean isAttackingPhase;

    public Game() {
        this.deck = new Deck();
        this.trumpSuit = deck.getTrumpSuit();
        this.player1 = new Player();
        this.player2 = new Player();
        this.table = new Table();
        // Шпион по умолчанию - случайная карта из руки
        this.spy = new RandomCardSpy(1f, Spy.Source.HAND);

        deck.distributingCards(List.of(player1, player2));
        // Первым ходит игрок с младшим козырем
        currentPlayer = findFirstAttacker();
        defendingPlayer = (currentPlayer == player1) ? player2 : player1;
        isAttackingPhase = true;
    }
    // Метод для смены шпиона
    public void setSpy(Spy newSpy) {
        this.spy = newSpy;
    }
    public Player getCurrentPlayer() {
        return currentPlayer;
    }
    public Deck getDeck() {
        return deck;
    }
    public int getDeckSize() {
        return deck.size();
    }
    public Table getTable() {
        return table;
    }
    public Suit getTrumpSuit() {
        return deck.getTrumpSuit();
    }
    public Player getPlayer1() {
        return player1;
    }
    public Player getPlayer2() {
        return player2;
    }
    public Spy getSpy(){return spy;}
    public boolean hasSpy() {
        return spy != null;
    }
    public List<Card> getSpyInfo() {
        if (deck.isEmpty() ||
                spyRoundCounter % SPY_ACTIVATION_ROUND != 0 ||
                spyUsedThisRound) {
            return Collections.emptyList();
        }
        return spy.collectInformation(this); // ← передаём игру
    }
    public boolean canUseSpy() {
        return !deck.isEmpty() &&
                spyRoundCounter % SPY_ACTIVATION_ROUND == 0 && // Каждый 3-й раунд
                !spyUsedThisRound; // Шпион ещё не использован в этом раунде
    }
    public void setSpyUsed() {
        this.spyUsedThisRound = true;
    }
    public boolean isSpyUsedThisRound() {
        return spyUsedThisRound;
    }
    public boolean isAttackingPhase() {
        return isAttackingPhase;
    }
    public Player getDefendingPlayer() {
        return defendingPlayer;
    }
    private int findUnprotectedCardIndex(Card card) {
        List<Card> attackCards = table.getAttackCards();
        for (int i = 0; i < attackCards.size(); i++) {
            if (table.getDefenseCards().get(i) == null && attackCards.get(i).equals(card)) {
                return i;
            }
        }
        return -1;
    }

    private boolean canContinueAttack() {
        Player attacker = currentPlayer; // Атакующий - это текущий игрок
        List<Card> attackCards = table.getAttackCards();

        // Если на столе уже 6 пар карт, больше класть нельзя
        if (attackCards.size() >= 6) {
            return false;
        }

        for (Card card : attacker.getCards()) {
            for (Card tableCard : table.getCards()) {
                if (card.getRank() == tableCard.getRank()) {
                    return true; // Можно подкинуть карту того же достоинства
                }
            }
        }
        return false; // Нет карт для подкидывания
    }

    public void makeMove(Player player, Card card) throws IllegalMoveException {

        if (!player.hasCard(card)) {
            throw new IllegalMoveException("У вас нет такой карты.");
        }

        if (isAttackingPhase) {
            // Фаза атаки
            if (player != currentPlayer) {
                throw new IllegalMoveException("Сейчас не ваш ход.");
            }

            if (!canAddToAttack(card)) {
                throw new IllegalMoveException("Вы не можете добавить эту карту к атаке.");
            }

            table.addAttackCard(card);
            player.removeCard(card);

            // Переключаемся к защите
            switchTurnToDefender();
        } else {
            // Фаза защиты
            if (player != defendingPlayer) {
                throw new IllegalMoveException("Сейчас фаза защиты. Вы не можете атаковать.");
            }

            List<Card> unbeaten = getUnbeatenCards();
            if (unbeaten.isEmpty()) {
                throw new IllegalMoveException("Нет карт для защиты.");
            }

            Card toBeat = unbeaten.get(0); // Первая незащищенная карта
            if (!canBeat(toBeat, card)) {
                throw new IllegalMoveException(card + " не бьёт " + toBeat);
            }

            int index = findUnprotectedCardIndex(toBeat);
            table.defendCard(index, card);
            player.removeCard(card);

            if (getUnbeatenCards().isEmpty()) {
                if (table.getAttackCards().size() < 6 && canContinueAttack()) {
                    // Если атакующий может подкинуть карты и не превышен лимит карт,
                    // переходим к фазе атаки
                    isAttackingPhase = true;
                } else {
                    // Иначе завершаем раунд и отбившийся игрок становится атакующим
                    completeRound();
                }
            }
        }
    }

    private boolean canAddToAttack(Card card) {
        List<Card> attackCards = table.getAttackCards();

        // Если стол пустой, можно положить любую карту
        if (attackCards.isEmpty()) {
            return true;
        }

        // Если на столе уже 6 пар карт, больше класть нельзя
        if (attackCards.size() >= 6) {
            return false;
        }

        for (Card tableCard : table.getCards()) {
            if (tableCard.getRank() == card.getRank()) {
                return true;
            }
        }

        return false; // Карта не соответствует условиям
    }

    public void takeCards() {
        if (isAttackingPhase) {
            throw new IllegalMoveException("Вы не можете взять карты в фазе атаки.");
        }

        defendingPlayer.takeCards(table.getCards());
        table.clear();

        // После взятия карт ход переходит к атакующему
        endRound();
    }

    public boolean canBeat(Card attackingCard, Card defendingCard) {
        // Карта бьет, если:
        // 1. Масть совпадает и достоинство выше
        // 2. Или это козырь, а атакующая карта - нет
        if (defendingCard.getSuit().equals(attackingCard.getSuit())) {
            return defendingCard.getRank().getValue() > attackingCard.getRank().getValue();
        }
        return defendingCard.getSuit().isTrump() && !attackingCard.getSuit().isTrump();
    }

    public List<Card> getUnbeatenCards() {
        List<Card> unbeaten = new ArrayList<>();
        List<Card> attackCards = table.getAttackCards();
        List<Card> defenseCards = table.getDefenseCards();

        for (int i = 0; i < attackCards.size(); i++) {
            if (i >= defenseCards.size() || defenseCards.get(i) == null) {
                unbeaten.add(attackCards.get(i));
            }
        }
        return unbeaten;
    }

    private void switchTurns() {
        Player temp = currentPlayer;
        currentPlayer = defendingPlayer;
        defendingPlayer = temp;
    }

    private void switchTurnToDefender() {
        isAttackingPhase = false;
    }

    private void completeRound() {
        spyRoundCounter++;
        spyUsedThisRound = false;

        table.clear();

        // Защитник становится атакующим
        Player temp = currentPlayer;
        currentPlayer = defendingPlayer;
        defendingPlayer = temp;

        deck.distributingCards(List.of(currentPlayer, defendingPlayer));

        isAttackingPhase = true;
    }

    private void endRound() {
        spyRoundCounter++;
        spyUsedThisRound = false;

        table.clear();

        deck.distributingCards(List.of(currentPlayer, defendingPlayer));

        isAttackingPhase = true;
    }

    public static class IllegalMoveException extends RuntimeException {
        public IllegalMoveException(String message) {
            super(message);
        }
    }

    private Player findFirstAttacker() {
        Card lowestTrump1 = player1.getLowestTrump();
        Card lowestTrump2 = player2.getLowestTrump();

        if (lowestTrump1 == null && lowestTrump2 == null) {
            return player1; // если козырей нет, ходит первый игрок
        }
        if (lowestTrump1 == null) return player2;
        if (lowestTrump2 == null) return player1;

        return (lowestTrump1.getRank().getValue() < lowestTrump2.getRank().getValue()) ? player1 : player2;
    }

    public void completeAttack() {

        table.clear();

        deck.distributingCards(List.of(currentPlayer, defendingPlayer));

        isAttackingPhase = true;
    }

    public boolean isGameOver() {
        return (player1.getCards().isEmpty() || player2.getCards().isEmpty()) && deck.isEmpty();
    }

    public static void main(String[] args) {
        // Запуск в Event Dispatch Thread (EDT) для Swing
        SwingUtilities.invokeLater(() -> {
            MainMenuFrame mainMenu = new MainMenuFrame();
            mainMenu.setVisible(true);
        });
    }
}