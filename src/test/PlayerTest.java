package durak;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import java.util.List;

class PlayerTest {
    private Player player;
    private Card heartsAce;
    private Card diamondsKing;
    private Card clubsQueenTrump;
    private Card spadesJack;

    @BeforeEach
    void setUp() {
        player = new Player();
        Suit trumpSuit = new Suit("Clubs", true);

        heartsAce = new Card(new Suit("Hearts", false), Rank.ACE);
        diamondsKing = new Card(new Suit("Diamonds", false), Rank.KING);
        clubsQueenTrump = new Card(trumpSuit, Rank.QUEEN);
        spadesJack = new Card(new Suit("Spades", false), Rank.JACK);
    }

    @Test
    @DisplayName("Добавление карты в руку")
    void addCard_ShouldAddCardToHand() {
        player.addCard(heartsAce);
        assertEquals(1, player.cardCount());
        assertTrue(player.hasCard(heartsAce));
    }

    @Test
    @DisplayName("Добавление null карты не должно изменять руку")
    void addNullCard_ShouldNotChangeHand() {
        player.addCard(null);
        assertEquals(0, player.cardCount());
    }

    @Test
    @DisplayName("Взятие карт из стола")
    void takeCards_ShouldAddAllCardsToHand() {
        List<Card> cardsToTake = List.of(heartsAce, diamondsKing);
        player.takeCards(cardsToTake);

        assertEquals(2, player.cardCount());
        assertTrue(player.hasCard(heartsAce));
        assertTrue(player.hasCard(diamondsKing));
    }

    @Test
    @DisplayName("Попытка взять пустой список карт")
    void takeEmptyCards_ShouldNotChangeHand() {
        player.takeCards(List.of());
        assertEquals(0, player.cardCount());
    }

    @Test
    @DisplayName("Удаление карты из руки")
    void removeCard_ShouldRemoveFromHand() {
        player.addCard(heartsAce);
        player.removeCard(heartsAce);

        assertEquals(0, player.cardCount());
        assertFalse(player.hasCard(heartsAce));
    }

    @Test
    @DisplayName("Попытка удалить отсутствующую карту")
    void removeNonExistentCard_ShouldDoNothing() {
        player.addCard(diamondsKing);
        player.removeCard(heartsAce);

        assertEquals(1, player.cardCount());
    }

    @Test
    @DisplayName("Отбитие карты - успешный случай")
    void beatCard_ShouldReturnBeatingCard() {
        player.addCard(clubsQueenTrump);
        player.addCard(spadesJack);

        Card beatenCard = player.beatCard(diamondsKing);
        assertNotNull(beatenCard);
        assertTrue(beatenCard.beat(diamondsKing));
        assertEquals(1, player.cardCount());
    }

    @Test
    @DisplayName("Отбитие карты - невозможный случай")
    void beatCard_ShouldReturnNullWhenCannotBeat() {
        player.addCard(spadesJack);
        assertNull(player.beatCard(clubsQueenTrump));
    }

    @Test
    @DisplayName("Поиск младшего козыря")
    void getLowestTrump_ShouldReturnCorrectCard() {
        Card clubsSevenTrump = new Card(new Suit("Clubs", true), Rank.SEVEN);
        player.addCard(clubsQueenTrump);
        player.addCard(clubsSevenTrump);

        Card lowestTrump = player.getLowestTrump();
        assertEquals(Rank.SEVEN, lowestTrump.getRank());
    }

    @Test
    @DisplayName("Поиск козыря при отсутствии козырей")
    void getLowestTrump_ShouldReturnNullWhenNoTrumps() {
        player.addCard(heartsAce);
        player.addCard(spadesJack);

        assertNull(player.getLowestTrump());
    }

    @Test
    @DisplayName("Проверка статуса атакующего")
    void attackerStatus_ShouldChangeCorrectly() {
        assertFalse(player.isAttacker());

        player.setAttacker(true);
        assertTrue(player.isAttacker());

        player.setAttacker(false);
        assertFalse(player.isAttacker());
    }

    @Test
    @DisplayName("Проверка наличия карт")
    void hasCards_ShouldReturnCorrectStatus() {
        assertFalse(player.hasCards());

        player.addCard(heartsAce);
        assertTrue(player.hasCards());

        player.removeCard(heartsAce);
        assertFalse(player.hasCards());
    }

    @Test
    @DisplayName("Получение копии карт в руке")
    void getCards_ShouldReturnCopy() {
        player.addCard(heartsAce);
        List<Card> cards = player.getCards();

        assertEquals(1, cards.size());
        cards.clear(); // Не должно влиять на оригинал

        assertEquals(1, player.cardCount());
    }
}