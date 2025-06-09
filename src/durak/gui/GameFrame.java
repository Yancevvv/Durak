package durak.gui;

import durak.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;

public class GameFrame extends JFrame {
    private final Game game;
    private PlayerPanel playerPanel;
    private TablePanel tablePanel;
    private JLabel statusLabel;
    private JLabel trumpLabel;
    private Card selectedCard = null;

    // Кнопки действий
    private JButton endAttackBtn;
    private JButton takeCardsBtn;
    private JButton spyButton;
    public GameFrame() {
        game = new Game(); // Инициализация игровой логики
        setupUI();
        updateGameState();
    }

    private void setupUI() {
        setTitle("Дурак - Игра");
        setSize(1000, 700);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        // Панель информации (верх)
        JPanel infoPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        trumpLabel = new JLabel();
        statusLabel = new JLabel();
        infoPanel.add(trumpLabel);
        infoPanel.add(statusLabel);
        add(infoPanel, BorderLayout.NORTH);

        // Панель стола (центр)
        tablePanel = new TablePanel();
        add(tablePanel, BorderLayout.CENTER);

        // Панель действий (восток)
        JPanel actionPanel = new JPanel(new GridLayout(3, 1, 10, 10));
        endAttackBtn = new JButton("Завершить атаку");
        takeCardsBtn = new JButton("Взять карты");
        JButton menuBtn = new JButton("Меню");

        endAttackBtn.addActionListener(this::handleEndAttack);
        takeCardsBtn.addActionListener(this::handleTakeCards);
        menuBtn.addActionListener(e -> returnToMenu());

        actionPanel.add(endAttackBtn);
        actionPanel.add(takeCardsBtn);
        actionPanel.add(menuBtn);
        add(actionPanel, BorderLayout.EAST);

        // Панель игрока (юг)
        playerPanel = new PlayerPanel(game.getCurrentPlayer(), this::handleCardClick);
        add(playerPanel, BorderLayout.SOUTH);

        // Добавляем кнопку шпиона в панель действий
        spyButton = new JButton("Шпионить");
        spyButton.addActionListener(this::handleSpyAction);
        actionPanel.add(spyButton);
    }
    private void handleSpyAction(ActionEvent e) {
        try {
            if (!game.canUseSpy()) {
                JOptionPane.showMessageDialog(this,
                        "Шпион недоступен. Можно использовать каждый " +
                                game.SPY_ACTIVATION_ROUND + "-й раунд, пока есть карты в колоде.",
                        "Шпион недоступен",
                        JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            Player opponent = (game.getCurrentPlayer() == game.getPlayer1())
                    ? game.getPlayer2() : game.getPlayer1();
            Card spyInfo = game.getSpyInfo(opponent);

            if (spyInfo != null) {
                JOptionPane.showMessageDialog(this,
                        "Шпион сообщает: у противника есть " + spyInfo,
                        "Информация шпиона",
                        JOptionPane.INFORMATION_MESSAGE);
                game.setSpyUsed();
            } else {
                JOptionPane.showMessageDialog(this,
                        "Шпиону не удалось получить информацию",
                        "Информация шпиона",
                        JOptionPane.WARNING_MESSAGE);
            }
            updateGameState();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Ошибка: " + ex.getMessage(),
                    "Ошибка",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
    private void resetSelection() {
        if (selectedCard != null) {
            playerPanel.setCardSelected(selectedCard, false);
            selectedCard = null;
        }
    }

    private void handleCardClick(Card card) {
        try {
            // Убедимся, что это ход текущего игрока
            Player currentPlayer = game.getCurrentPlayer();
            Player defendingPlayer = game.getDefendingPlayer();

            if (game.isAttackingPhase() && !currentPlayer.hasCard(card)) {
                throw new Game.IllegalMoveException("Сейчас не ваш ход.");
            }

            if (!game.isAttackingPhase() && !defendingPlayer.hasCard(card)) {
                throw new Game.IllegalMoveException("Сейчас не ваш ход.");
            }

            // Определяем, кто делает ход
            Player player = game.isAttackingPhase() ? currentPlayer : defendingPlayer;

            // Делаем ход
            game.makeMove(player, card);
            updateGameState();

        } catch (Game.IllegalMoveException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(),
                    "Ошибка хода", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleEndAttack(ActionEvent e) {
        try {
            game.completeAttack();
            updateGameState();
            if (game.isGameOver()) {
                showGameResult();
            }
        } catch (IllegalStateException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleTakeCards(ActionEvent e) {
        try {
            game.takeCards();
            updateGameState();
            if (game.isGameOver()) {
                showGameResult();
            }
        } catch (Game.IllegalMoveException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateGameState() {
        // Проверяем table на null
        if (game == null || game.getTable() == null) {
            System.err.println("Game or table is null!");
            return;
        }

        // Получаем все карты на столе
        List<Card> tableCards = game.getTable().getCards();
        System.out.println("Карт на столе: " + tableCards.size()); // Отладочный вывод

        // Получаем незащищенные карты для подсветки
        List<Card> unbeaten = game.getUnbeatenCards();
        Card toHighlight = unbeaten.isEmpty() ? null : unbeaten.get(0);

        // Обновляем стол
        tablePanel.updateTable(game.getTable());

        // Определяем, кто сейчас ходит
        Player activePlayer = game.isAttackingPhase() ? game.getCurrentPlayer() : game.getDefendingPlayer();

        // Обновляем карты игрока
        playerPanel.updateCards(activePlayer.getCards(), this::handleCardClick);

        // Обновляем кнопки
        boolean isAttacking = game.isAttackingPhase();
        endAttackBtn.setEnabled(isAttacking && game.getTable().getAttackCards().size() > 0);
        takeCardsBtn.setEnabled(!isAttacking && unbeaten.size() > 0);

        // Обновляем доступность кнопки шпиона
        boolean canSpy = game.canUseSpy(); // Используем метод canUseSpy из класса Game
        spyButton.setEnabled(canSpy);

        // Обновляем метки
        trumpLabel.setText("Козырь: " + game.getTrumpSuit());

        String playerName = (activePlayer == game.getPlayer1()) ? "Игрок 1" : "Игрок 2";
        String phase = game.isAttackingPhase() ? "Атака" : "Защита";

        statusLabel.setText("Ход: " + playerName + " (" + phase + ")");
    }

    private void showGameResult() {
        String winner = game.getPlayer1().getCards().isEmpty() ? "Игрок 1" : "Игрок 2";
        JOptionPane.showMessageDialog(this, "Победил " + winner + "!", "Игра окончена", JOptionPane.INFORMATION_MESSAGE);
        returnToMenu();
    }

    private void returnToMenu() {
        this.dispose();
        new MainMenuFrame().setVisible(true);
    }
}