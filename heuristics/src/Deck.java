import java.util.Iterator;


class Deck implements Iterable<Card> {
	private Card[] cards;
	private int size;
	public static int MAX_SIZE = 52;

	public Deck() {
		this.cards = new Card[MAX_SIZE];
		this.size = 0;
	}

	public boolean add(Card card) {
		if (size >= MAX_SIZE) {
			return false;
		}
		cards[size] = card;
		size++;
		return true;
	}

	public int getSize() {
		return size;
	}

	public class DeckIterator implements Iterator<Card> {
		private int currIndex;
		private Deck deck;

		public DeckIterator(Deck deck) {
			this.currIndex = 0;
			this.deck = deck;
		}

		public boolean hasNext() {
			if (currIndex < deck.size) {
				return true;
			}
			return false;
		}

		public Card next() {
			Card currCard = deck.cards[currIndex];
			currIndex++;
			return currCard;
		}

		public void remove() {
			throw new UnsupportedOperationException();
		}
	}

	public Iterator<Card> iterator() {
		return new DeckIterator(this);
	}
}
