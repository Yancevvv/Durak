package durak.gui;

import durak.Card;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.net.URL;

public class CardPanel extends JPanel {
    private static final String IMAGE_PATH_FORMAT = "/resources/images/%s_of_%s.png"; // Путь относительно classpath
    private static final ImageIcon BACK_IMAGE = loadBackImage();
    private final Card card; // Храним карту, связанную с этой панелью
    private boolean selected = false;
    private boolean highlighted;
    private final ImageIcon image; // Поле для хранения изображения карты
    public CardPanel(Card card) {
        this.card = card;
        this.image = loadCardImage(card); // Загружаем изображение карты
        setPreferredSize(new Dimension(80, 120)); // Устанавливаем размер панели
        setOpaque(false);
        setBorder(BorderFactory.createEmptyBorder());
        addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                setSelected(!selected);
            }
        });
    }
    public void setHighlighted(boolean highlighted) {
        this.highlighted = highlighted;
        repaint();
    }
    public void setSelected(boolean selected) {
        this.selected = selected;
        repaint();
    }
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Рисуем фон карты
        if (image != null) {
            g.drawImage(image.getImage(), 0, 0, getWidth(), getHeight(), this);
        } else {
            g.setColor(Color.LIGHT_GRAY);
            g.fillRect(0, 0, getWidth(), getHeight());
        }

        // Подсветка выбранной карты
        if (selected) {
            g.setColor(new Color(255, 255, 0, 150)); // Полупрозрачный жёлтый цвет
            g.fillRect(0, 0, getWidth(), getHeight());
        }
    }

    private ImageIcon loadCardImage(Card card) {
        String path = String.format(IMAGE_PATH_FORMAT,
                card.getRank().toString(),
                card.getSuit().toString());

        URL resource = getClass().getResource(path); // Загружаем ресурс из classpath
        if (resource == null) {
            System.err.println("Image not found: " + path);
            return createDefaultCardImage(card);
        }
        return new ImageIcon(resource);
    }

    private static ImageIcon loadBackImage() {
        URL backResource = CardPanel.class.getResource("/images/card_back.png");
        return backResource != null ?
                new ImageIcon(backResource) :
                createDefaultBackImage();
    }

    private static ImageIcon createDefaultCardImage(Card card) {
        BufferedImage image = new BufferedImage(80, 120, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = image.createGraphics();

        // Рисуем простую карту
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, 80, 120);
        g.setColor(Color.BLACK);
        g.drawString(card.toString(), 10, 20);

        g.dispose();
        return new ImageIcon(image);
    }

    private static ImageIcon createDefaultBackImage() {
        BufferedImage image = new BufferedImage(80, 120, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = image.createGraphics();

        // Рисуем рубашку карты
        g.setColor(Color.BLUE);
        g.fillRect(0, 0, 80, 120);
        g.setColor(Color.WHITE);
        g.drawString("Card Back", 10, 20);

        g.dispose();
        return new ImageIcon(image);
    }
}