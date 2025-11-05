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
    private JButton selectSpyButton; // Новая кнопка для выбора шпиона

    public GameFrame() {
        game = new Game();
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
        JPanel actionPanel = new JPanel(new GridLayout(0, 2, 10, 5));
        actionPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        endAttackBtn = new JButton("Завершить атаку");
        takeCardsBtn = new JButton("Взять карты");
        spyButton = new JButton("Шпионить");
        selectSpyButton = new JButton("Выбрать шпиона"); // Новая кнопка
        JButton menuBtn = new JButton("Меню");

        endAttackBtn.addActionListener(this::handleEndAttack);
        takeCardsBtn.addActionListener(this::handleTakeCards);
        spyButton.addActionListener(this::handleSpyAction);
        selectSpyButton.addActionListener(this::handleSelectSpy);
        menuBtn.addActionListener(e -> returnToMenu());

        // Добавляем кнопки
        actionPanel.add(new JLabel(""));
        actionPanel.add(endAttackBtn);
        actionPanel.add(new JLabel(""));
        actionPanel.add(takeCardsBtn);
        actionPanel.add(new JLabel(""));
        actionPanel.add(spyButton);
        actionPanel.add(new JLabel(""));
        actionPanel.add(selectSpyButton);
        actionPanel.add(new JLabel(""));
        actionPanel.add(menuBtn);

        add(actionPanel, BorderLayout.EAST);

        // Панель игрока (юг)
        playerPanel = new PlayerPanel(game.getCurrentPlayer(), this::handleCardClick);
        add(playerPanel, BorderLayout.SOUTH);
    }

    private void handleSelectSpy(ActionEvent e) {
        String[] spyTypes = {
                "Случайная карта из руки",
                "Случайная карта из колоды",
                "Карты определенной масти",
                "Карты определенного достоинства"
        };

        String choice = (String) JOptionPane.showInputDialog(
                this,
                "Выберите тип шпиона:",
                "Выбор шпиона",
                JOptionPane.QUESTION_MESSAGE,
                null,
                spyTypes,
                spyTypes[0]
        );

        if (choice == null) return;

        try {
            Spy newSpy = switch (choice) {
                case "Случайная карта из руки" ->
                        new RandomCardSpy(1.0f, Spy.Source.HAND);
                case "Случайная карта из колоды" ->
                        new RandomCardSpy(1.0f, Spy.Source.DECK);
                case "Карты определенной масти" -> {
                    String suitName = JOptionPane.showInputDialog(this, "Введите масть (HEARTS, DIAMONDS и т.д.):");
                    if (suitName == null) yield null;
                    Suit suit = Suit.valueOf(suitName.toUpperCase());

                    // Выбор источника
                    Object[] sources = {"Из руки", "Из колоды"};
                    int sourceChoice = JOptionPane.showOptionDialog(this,
                            "Откуда искать карты?",
                            "Источник карт",
                            JOptionPane.DEFAULT_OPTION,
                            JOptionPane.QUESTION_MESSAGE,
                            null,
                            sources,
                            sources[0]);

                    Spy.Source source = (sourceChoice == 1) ? Spy.Source.DECK : Spy.Source.HAND;
                    yield new SuitCardsSpy(1.0f, suit, source);
                }
                case "Карты определенного достоинства" -> {
                    String rankName = JOptionPane.showInputDialog(this, "Введите достоинство (ACE, KING и т.д.):");
                    if (rankName == null) yield null;
                    Rank rank = Rank.valueOf(rankName.toUpperCase());

                    // Выбор источника
                    Object[] sources = {"Из руки", "Из колоды"};
                    int sourceChoice = JOptionPane.showOptionDialog(this,
                            "Откуда искать карты?",
                            "Источник карт",
                            JOptionPane.DEFAULT_OPTION,
                            JOptionPane.QUESTION_MESSAGE,
                            null,
                            sources,
                            sources[0]);

                    Spy.Source source = (sourceChoice == 1) ? Spy.Source.DECK : Spy.Source.HAND;
                    yield new RankCardsSpy(1.0f, rank, source);
                }
                default -> null;
            };

            if (newSpy != null) {
                game.setSpy(newSpy);
                JOptionPane.showMessageDialog(this,
                        "Шпион успешно выбран!",
                        "Успех",
                        JOptionPane.INFORMATION_MESSAGE);
            }

        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this,
                    "Неверные параметры шпиона: " + ex.getMessage(),
                    "Ошибка",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleSpyAction(ActionEvent e) {
        try {
            if (!game.canUseSpy()) {
                JOptionPane.showMessageDialog(this,
                        "Шпион доступен только каждый " + game.SPY_ACTIVATION_ROUND + "-й раунд",
                        "Недоступно", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Получаем информацию от текущего шпиона
            List<Card> result = game.getSpyInfo();

            // Формируем сообщение в зависимости от типа шпиона
            String message;
            Spy currentSpy = game.getSpy();

            if (currentSpy instanceof RandomCardSpy) {
                if (result.isEmpty()) {
                    message = "Шпион ничего не узнал";
                } else {
                    message = "Случайная карта: " + result.get(0);
                }
            } else if (currentSpy instanceof SuitCardsSpy suitSpy) {
                if (result.isEmpty()) {
                    message = "Карт масти " + suitSpy.getSuit().getName() + " не найдено";
                } else if (result.size() == 1) {
                    message = "Найдена карта масти " + suitSpy.getSuit().getName() + ": " + result.get(0);
                } else {
                    message = "Найдено " + result.size() + " карт масти " + suitSpy.getSuit().getName();
                }
            } else if (currentSpy instanceof RankCardsSpy rankSpy) {
                if (result.isEmpty()) {
                    message = "Карт достоинства " + rankSpy.getRank() + " не найдено";
                } else if (result.size() == 1) {
                    message = "Найдена карта достоинства " + rankSpy.getRank() + ": " + result.get(0);
                } else {
                    message = "Найдено " + result.size() + " карт достоинства " + rankSpy.getRank();
                }
            } else {
                message = "Неизвестный тип шпиона";
            }

            JOptionPane.showMessageDialog(this, message, "Шпион",
                    result.isEmpty() ? JOptionPane.WARNING_MESSAGE : JOptionPane.INFORMATION_MESSAGE);

            game.setSpyUsed();
            updateGameState();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Ошибка шпиона: " + ex.getMessage(),
                    "Ошибка", JOptionPane.ERROR_MESSAGE);
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