package durak;

import org.junit.Before;
import org.junit.Test;
import java.util.ArrayList;
import java.util.List;
import static org.junit.Assert.*;

public class SpyTest {

    private Spy spy;
    private Player player;
    private Deck deck;

    @Before
    public void setUp() {
        // Создаем шпиона с эффективностью 1.0 (100%)
        spy = new Spy(1.0f);

        // Создаем игрока и добавляем карты
        player = new Player();
        List<Card> cards = new ArrayList<>();
        cards.add(new Card(new Suit("Hearts", false),Rank.SIX));
        cards.add(new Card(new Suit("Diamonds", false),Rank.SEVEN));
        cards.add(new Card(new Suit("Clubs", false), Rank.EIGHT));
        player.setCards(cards); // Предполагается, что у Player есть метод setCards
    }

    @Test
    public void testCollectInformation_Successful() {
        // Шпион должен успешно собрать информацию, так как efficiency = 1.0
        Card result = spy.collectInformation(player, deck, 5);
        assertNotNull("Шпион не смог собрать информацию", result);
        assertTrue("Карта не принадлежит игроку", player.getCards().contains(result));
    }

    @Test
    public void testCollectInformation_Failure() {
        // Создаем шпиона с эффективностью 0.0 (0%)
        Spy ineffectiveSpy = new Spy(0.0f);

        // Шпион не должен собрать информацию
        Card result = ineffectiveSpy.collectInformation(player, deck, 5);
        assertNull("Шпион собрал информацию, хотя не должен был", result);
    }

    @Test
    public void testCollectInformation_NoCards() {
        // Игрок без карт
        Player emptyPlayer = new Player();
        emptyPlayer.setCards(new ArrayList<>());

        // Шпион не должен собрать информацию, если у игрока нет карт
        Card result = spy.collectInformation(emptyPlayer, deck, 5);
        assertNull("Шпион собрал информацию, хотя у игрока нет карт", result);
    }

    @Test
    public void testEfficiencyBoundaryValues() {
        // Проверка граничных значений efficiency
        Spy lowEfficiencySpy = new Spy(0.0f);
        Spy highEfficiencySpy = new Spy(1.0f);

        // При efficiency = 0.0 шпион не должен собирать информацию
        assertNull(lowEfficiencySpy.collectInformation(player, deck, 5));

        // При efficiency = 1.0 шпион всегда должен собирать информацию
        assertNotNull(highEfficiencySpy.collectInformation(player, deck, 5));
    }
}