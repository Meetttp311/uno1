package game;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GroupOfCards {
    private List<Card> cards;

    public GroupOfCards() {
        cards = new ArrayList<>();
    }

    public void addCard(Card card) {
        cards.add(card);
    }

    public void addCards(List<Card> newCards) {
        cards.addAll(newCards);
    }

    public int getSize() {
        return cards.size();
    }

    public Card drawCard() {
        if (cards.isEmpty()) {
            return null;
        }
        return cards.remove(cards.size() - 1);
    }

    public Card getTopCard() {
        if (cards.isEmpty()) {
            return null;
        }
        return cards.get(cards.size() - 1);
    }

    public void shuffle() {
        Collections.shuffle(cards);
    }
}