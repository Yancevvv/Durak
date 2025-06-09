package durak.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class MainMenuFrame extends JFrame {
    public MainMenuFrame() {
        setTitle("Дурак - Главное меню");
        setSize(800, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Центрируем окно

        // Основная панель
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(0, 100, 0));

        // Заголовок
        JLabel titleLabel = new JLabel("ДУРАК", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 72));
        titleLabel.setForeground(Color.WHITE);

        // Панель с кнопками
        JPanel buttonPanel = new JPanel(new GridLayout(3, 1, 10, 20));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 100, 50, 100));
        buttonPanel.setOpaque(false);

        // Кнопки
        JButton newGameButton = createMenuButton("Новая игра");
        JButton rulesButton = createMenuButton("Правила");
        JButton exitButton = createMenuButton("Выход");

        // Обработчики событий
        newGameButton.addActionListener(this::startNewGame);
        exitButton.addActionListener(e -> System.exit(0));

        buttonPanel.add(newGameButton);
        buttonPanel.add(rulesButton);
        buttonPanel.add(exitButton);

        mainPanel.add(titleLabel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private JButton createMenuButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.PLAIN, 24));
        button.setBackground(new Color(220, 220, 220));
        button.setFocusPainted(false);
        return button;
    }

    private void startNewGame(ActionEvent e) {
        // Закрываем меню
        this.dispose();

        // Создаем и показываем игровое окно
        SwingUtilities.invokeLater(() -> {
            GameFrame gameFrame = new GameFrame();
            gameFrame.setVisible(true);
        });
    }
}