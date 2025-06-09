package durak.gui;
import durak.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import javax.swing.*;

public class PlayerPanel extends JPanel {
    private Consumer<Card> cardClickHandler;
    private Map<Card, CardPanel> cardPanels = new HashMap<>();
    public PlayerPanel(Player player, Consumer<Card> cardClickHandler) {
        this.cardClickHandler = cardClickHandler;
        updateCards(player.getCards(), cardClickHandler);
    }

    public void updateCards(List<Card> cards, Consumer<Card> clickHandler) {
        removeAll();
        cardPanels.clear();

        if (cards == null || cards.isEmpty()) {
            System.out.println("Нет карт для отображения");
            revalidate();
            repaint();
            return;
        }

        for (Card card : cards) {
            CardPanel cardPanel = new CardPanel(card);
            cardPanel.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    clickHandler.accept(card);
                }
            });
            cardPanels.put(card, cardPanel);
            add(cardPanel);
        }

        revalidate();
        repaint();
    }
    public void setCardSelected(Card card, boolean selected) {
        CardPanel panel = cardPanels.get(card);
        if (panel != null) {
            panel.setSelected(selected);
        }
    }
}