package game;
import java.util.*;

public class Game {
    private GroupOfCards deck;
    private GroupOfCards discardPile;
    private List<Player> players;
    private int currentPlayerIndex;
    private boolean reverseDirection;
    private Scanner scanner;

    public Game(List<String> playerNames) {
        deck = new GroupOfCards();
        discardPile = new GroupOfCards();
        players = new ArrayList<>();
        currentPlayerIndex = 0;
        reverseDirection = false;
        scanner = new Scanner(System.in);

        for (String playerName : playerNames) {
            players.add(new Player(playerName));
        }

        initializeDeck();
    }

    // Initializes the deck of cards with all possible combinations of colors and values
    private void initializeDeck() {
        String[] colors = { "Red", "Blue", "Green", "Yellow" };
        String[] values = { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "Skip", "Reverse", "Draw Two" };

        for (String color : colors) {
            for (String value : values) {
                deck.addCard(new Card(color, value));
            }
        }

        deck.shuffle();
    }

    // Starts the game and plays until a player wins
    public void play() {
        System.out.println("UNO Game Started!\n");

        // Initial deal
        dealCards(7);

        // Play until a player wins
        while (true) {
            Player currentPlayer = players.get(currentPlayerIndex);
            System.out.println("It's " + currentPlayer.getName() + "'s turn.");
            System.out.println("Card on top of the discard pile: " + discardPile.getTopCard());
            System.out.println("Your hand: " + currentPlayer.getHand());

            // Check if the current player has won
            if (currentPlayer.getHandSize() == 0) {
                System.out.println("\n" + currentPlayer.getName() + " wins!");
                break;
            }

            if (canPlay(currentPlayer)) {
                System.out.println("Choose a card to play (Enter the card index):");
                int index = chooseCardIndex(currentPlayer);
                Card selectedCard = currentPlayer.getHand().get(index);

                if (isValidPlay(selectedCard)) {
                    currentPlayer.playCard(index);
                    discardPile.addCard(selectedCard);
                    processCard(selectedCard);
                } else {
                    System.out.println("Invalid card selection. Please choose a playable card.\n");
                }
            } else {
                System.out.println(currentPlayer.getName() + ", you cannot play. Drawing a card.\n");
                Card drawnCard = deck.drawCard();

                if (drawnCard != null) {
                    currentPlayer.addCardToHand(drawnCard);
                    System.out.println(currentPlayer.getName() + " drew " + drawnCard + "\n");
                } else {
                    System.out.println("Deck is empty. Shuffling the discard pile to form a new deck.\n");
                    reshuffleDeck();
                }
            }

            nextPlayer();
        }

        System.out.println("Game Over!");
        scanner.close();
    }

    // Deals a specified number of cards to each player from the deck
    private void dealCards(int numCards) {
        for (int i = 0; i < numCards; i++) {
            for (Player player : players) {
                Card card = deck.drawCard();
                player.addCardToHand(card);
            }
        }

        Card topCard = deck.drawCard();
        discardPile.addCard(topCard);
    }

    // Checks if the player can play a card (has a card with matching color or value)
    private boolean canPlay(Player player) {
        Card topCard = discardPile.getTopCard();
        for (Card card : player.getHand()) {
            if (card.getColor().equals(topCard.getColor()) || card.getValue().equals(topCard.getValue())) {
                return true;
            }
        }
        return false;
    }

    // Allows the player to choose a card index from their hand
    private int chooseCardIndex(Player player) {
        while (true) {
            System.out.print("Your choice: ");
            if (scanner.hasNextInt()) {
                int index = scanner.nextInt();
                if (index >= 0 && index < player.getHandSize()) {
                    return index;
                } else {
                    System.out.println("Invalid card index. Please choose a valid card index.");
                }
            } else {
                System.out.println("Invalid input. Please enter a valid card index.");
                scanner.next();
            }
        }
    }

    // Checks if a selected card can be played based on the current top card of the discard pile
    private boolean isValidPlay(Card card) {
        Card topCard = discardPile.getTopCard();
        return card.getColor().equals(topCard.getColor()) || card.getValue().equals(topCard.getValue());
    }

    // Processes the effect of a special card (e.g., Skip, Reverse, Draw Two)
    private void processCard(Card card) {
        switch (card.getValue()) {
            case "Skip":
                nextPlayer();
                break;
            case "Reverse":
                reverseDirection = !reverseDirection;
                break;
            case "Draw Two":
                Player nextPlayer = players.get(getNextPlayerIndex());
                Card drawnCard = deck.drawCard();

                if (drawnCard != null) {
                    nextPlayer.addCardToHand(drawnCard);
                    nextPlayer.addCardToHand(drawnCard);
                    System.out.println(nextPlayer.getName() + " drew two cards.\n");
                } else {
                    System.out.println("Deck is empty. No cards drawn.\n");
                }
                break;
        }
    }

    // Moves to the next player based on the direction of play (clockwise or counterclockwise)
    private void nextPlayer() {
        if (!reverseDirection) {
            currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
        } else {
            currentPlayerIndex = (currentPlayerIndex - 1 + players.size()) % players.size();
        }
    }

    // Returns the index of the next player based on the direction of play
    private int getNextPlayerIndex() {
        if (!reverseDirection) {
            return (currentPlayerIndex + 1) % players.size();
        } else {
            return (currentPlayerIndex - 1 + players.size()) % players.size();
        }
    }

    // Reshuffles the deck by transferring cards from the discard pile and shuffling them
    private void reshuffleDeck() {
        Card topCard = discardPile.drawCard();
        while (discardPile.getSize() > 0) {
            Card card = discardPile.drawCard();
            deck.addCard(card);
        }
        deck.shuffle();
        discardPile.addCard(topCard);
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter the number of players: ");
        int numPlayers = scanner.nextInt();
        scanner.nextLine(); // Consume the newline character

        List<String> playerNames = new ArrayList<>();
        for (int i = 0; i < numPlayers; i++) {
            System.out.print("Enter the name of Player " + (i + 1) + ": ");
            String playerName = scanner.nextLine();
            playerNames.add(playerName);
        }

        Game game = new Game(playerNames);
        game.play();

        scanner.close();
    }
}