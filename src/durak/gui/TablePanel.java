package durak.gui;

import durak.Card;
import durak.Table;
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class TablePanel extends JPanel {
    private Card highlightedCard;

    public TablePanel() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(new Color(0, 100, 0));
        setPreferredSize(new Dimension(800, 250));
    }

    public void updateTable(Table table) {
        // Получаем атакующие и защитные карты
        List<Card> attackCards = table.getAttackCards();
        List<Card> defenseCards = table.getDefenseCards();

        // Очищаем предыдущее состояние
        removeAll();

        // Создаём панели для атакующих и защитных карт
        JPanel attackPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, -20, 0));
        attackPanel.setOpaque(false);

        JPanel defensePanel = new JPanel(new FlowLayout(FlowLayout.CENTER, -20, 0));
        defensePanel.setOpaque(false);

        // Рисуем карты атаки и защиты
        for (int i = 0; i < attackCards.size(); i++) {
            // Карта атаки
            CardPanel attackCardPanel = new CardPanel(attackCards.get(i));
            attackPanel.add(attackCardPanel);

            // Карта защиты (если есть)
            if (i < defenseCards.size() && defenseCards.get(i) != null) {
                CardPanel defenseCardPanel = new CardPanel(defenseCards.get(i));
                defenseCardPanel.setBorder(BorderFactory.createLineBorder(Color.BLUE, 1));
                defensePanel.add(defenseCardPanel);
            } else {
                // Пустое место для карты защиты
                JPanel emptyPanel = new JPanel();
                emptyPanel.setPreferredSize(new Dimension(80, 120));
                emptyPanel.setOpaque(false);
                defensePanel.add(emptyPanel);
            }
        }

        // Добавляем панели на стол
        add(attackPanel);
        add(defensePanel);

        // Обновляем компонент
        revalidate();
        repaint();
    }

    public void updateTable(List<Card> cards, Card highlighted) {

        this.highlightedCard = highlighted;
        removeAll();

        // Разделяем карты на атакующие и защитные
        List<Card> attackCards = new ArrayList<>();
        List<Card> defenseCards = new ArrayList<>();

        for (int i = 0; i < cards.size(); i++) {
            if (i % 2 == 0) {
                attackCards.add(cards.get(i));
            } else {
                defenseCards.add(cards.get(i));
            }
        }

        // Создаём панели для атакующих и защитных карт
        JPanel attackPanel = createCardPanel(attackCards, true);
        JPanel defensePanel = createCardPanel(defenseCards, false);

        // Настраиваем основную панель
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        add(attackPanel);
        add(defensePanel);

        // Обновляем компонент
        revalidate();
        repaint();
    }

    private JPanel createCardPanel(List<Card> cards, boolean isAttack) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, -20, 0));
        panel.setOpaque(false);

        for (Card card : cards) {
            CardPanel cardPanel = new CardPanel(card);
            if (card.equals(highlightedCard)) {
                cardPanel.setHighlighted(true);
            }
            if (!isAttack) { // Для защитных карт
                cardPanel.setBorder(BorderFactory.createLineBorder(Color.BLUE, 1));
            }
            panel.add(cardPanel);
        }

        return panel;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D)g;

        // Фон стола с текстурой
        g2d.setColor(new Color(0, 80, 0));
        g2d.fillRect(0, 0, getWidth(), getHeight());

        // Разделительная линия
        g2d.setColor(new Color(255, 255, 255, 100));
        g2d.setStroke(new BasicStroke(2));
        g2d.drawLine(50, getHeight()/2, getWidth()-50, getHeight()/2);
    }
}