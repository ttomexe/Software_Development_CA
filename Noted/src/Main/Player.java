package Main;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

public class Player implements Runnable{
    private List<Card> cardHand = new ArrayList<>(); //A list to store all of the cards the player has
    private int playerNumber;                        //The unique identifier that a player has stored as an int
    private final CyclicBarrier barrier;

    public Player(int playerNumber, CyclicBarrier barrier){        //Creation of the player, only difference between players at creation is the id, rest of the attributes are added via a round robin method...
        this.playerNumber = playerNumber;   //...by iterating through the list of all players
        this.barrier = barrier;
    }

    public int getPlayerNumber(){       //When called returns the players unique playerNumber
        return playerNumber;
    }

    public List<Card> getHand(){   //When called returns the list of cards that represents the cards in the players hand
        return cardHand;
    }

    public List<Integer> getHandValues(){  //Used to return the face values of all the cards in the players hand
        List<Integer> cardValues =  new ArrayList<>(); //Creates a list to be returned
        
        for(Card card : cardHand){      //For each card in the cardHand list
            cardValues.add(card.getValue()); //Uses the getValue method to find the face value and adds it to the previously created list
        }

        return cardValues; //Returns the list
    }

    public void addCard(Card card){ //Takes a card as a parameter and is used to add a card to a players hand
        cardHand.add(card);  //Adds the card to a players hand
    }

    public int findDiscard(){  //Method to find what card should be discarded
        int toDiscard = 0;   //
        int index = 0;       //index refers to the index of a card in a players cardHand, is incremented to check each card in the hand
        while(toDiscard < cardHand.size()){  //Loops whilst the value is less than the size of the cardHand
            if (cardHand.get(index).getValue() == playerNumber && index < cardHand.size()-1){ //If the card value is equal to the playersNumber we want to keep it
                index++;  //Increments the index to check next card            ^ Is this needed?
            }
            else{ //If its not equal to the player number, we dont want it so we return its index value to be used in the discard method
                toDiscard = index;
                return toDiscard;
            }
        }
        toDiscard = index; //As the loop is cardHand.size -1 we know that if the first cards arent needed to be discarded then the last one must be the one needed to be discarded
        return(toDiscard);  //As this method is only called after a winCheck, we know there will always be a card to be discarded when this is called
    }

    public void discard(int i){  //Takes an integer as a parameter, this integer represents the position of the card in the players cardHand
        Card c = cardHand.get(i); //Gets the card at index i and saves it as c
        cardHand.remove(i);      //Removes the card from the cardHand at index i
        if(playerNumber == cardGame.getNumberOfPlayers()){ //Checks where the card is going, if the player is the last player. Eg player 4 out of 4 players, discards the card to the cardDeck of the first player
            System.out.println("help1");
            cardGame.getDecks().get(0).addCard(c);  //Gets the cardDeck of index 0 and uses the addCard method to add card c to the deck
        }
        else{  //If the player isnt the last player eg player 3 of 4 players, Then discards to the deck associated with their number
            System.out.println("help2");
            cardGame.getDecks().get(playerNumber).addCard(c);
        }
    }

    public Card draw(cardDeck d){ //Method for drawing a card, takes a cardDeck class as its parameter
        Card drawnCard = d.drawCard(); //Calls cardDeck's drawCard method which returns a Card class and assigns it as drawnCard
        this.cardHand.add(drawnCard); //Adds this drawnCard to the players card List
        return drawnCard;             //And additionally returns the Card class
    }


    public void checkWinner(){
        //Whenck checkWinner is called each card in the players hand is compaerd, if they are all the same then the following occurs
        if (getHandValues().get(0) == getHandValues().get(1) && getHandValues().get(0) == getHandValues().get(2) && getHandValues().get(0) == getHandValues().get(3)){
            cardGame.changeGameOver(); //The method changeGameOver is called in cardGame signalling the end of the game
            try{
                BufferedWriter writer = new BufferedWriter(new FileWriter("src/Main/Player"+String.valueOf(playerNumber)+".txt", true));
                writer.write("player "+String.valueOf(playerNumber)+" wins"); //A writer is started and a formatted winning message is recorded to the players .txt file
                writer.newLine();//new Lines for structure
                writer.write("player "+String.valueOf(playerNumber)+" exits"); // Writes "Player n exits"
                writer.newLine();//Another new line for structure
                writer.write("player "+String.valueOf(playerNumber)+" final hand: "+getHandValues().toString()); //Converts the list of handValues to string form and writes it
                writer.close();//closes the writer
            }catch(IOException e){System.out.println("Player"+String.valueOf(playerNumber)+" output text file not found.");}
            //If an issue occurs with the file I/O then the IOException will inform the user of what has happened.
            System.out.println("player "+String.valueOf(playerNumber)+" wins"); //Writes that the player has won in the terminal

            cardGame.win(this); // and finally calls cardGames win method on the winning player
            
        }
    
    }
    

    public void lose(Player winner){ //When a players is told that its lost it does the following
        try{
            //It writes to the associated players .txt file and appends the following on the end
            BufferedWriter writer = new BufferedWriter(new FileWriter("src/Main/Player"+String.valueOf(playerNumber)+".txt", true));
            //The Player winner is the Player class that has won, it gets its player number and writes the structured message "" player "Winner" has informed player "playerNumber" that player "Winner" has won""
            writer.write("player "+String.valueOf(winner.getPlayerNumber())+" has informed player "+String.valueOf(playerNumber)+" that player"+String.valueOf(winner.getPlayerNumber())+" has won");
            writer.newLine();// Writes on a new Line
            writer.write("player "+String.valueOf(playerNumber)+" exits"); //Writes that the player exits in its own file
            writer.newLine();
            writer.write("player "+String.valueOf(playerNumber)+" hand: "+getHandValues().toString()); //And then displays its final hand  from List>String form
            writer.close();
        }catch(IOException e){System.out.println("Player"+String.valueOf(playerNumber)+" output text file not found.");}
        //If an issue occurs with the file I/O then the IOException will inform the user of what has happened.

    }
    

    public void run() {
    checkWinner(); // Before anything happens, check if the initial hand is a winning hand

    while (!cardGame.getGameOver()) {
        try {
            barrier.await(); // Wait for all players to reach this point
            System.out.println("Barrier 1 passed");
            while (cardGame.cardDecks.get(playerNumber - 1).getHand().size() <= 0) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            Card drawnCard = draw(cardGame.cardDecks.get(playerNumber - 1));
            int toDiscard = findDiscard();
            int discardValue = cardHand.get(toDiscard).getValue();
            discard(toDiscard);

            try {
                BufferedWriter writer = new BufferedWriter(new FileWriter("src/Main/Player" + String.valueOf(playerNumber) + ".txt", true));
                writer.write("player " + String.valueOf(playerNumber) + " draws a " + String.valueOf(drawnCard.getValue()) + " from deck " + String.valueOf(playerNumber));
                writer.newLine();
                if (playerNumber == cardGame.getNumberOfPlayers()) {
                    writer.write("player " + String.valueOf(playerNumber) + " discards a " + String.valueOf(discardValue) + " to deck 1");
                } else {
                    writer.write("player " + String.valueOf(playerNumber) + " discards a " + String.valueOf(discardValue) + " to deck " + String.valueOf(playerNumber + 1));
                }
                writer.newLine();
                writer.write("player " + String.valueOf(playerNumber) + " current hand is " + getHandValues().toString());
                writer.newLine();
                writer.close();
            } catch (IOException e) {
                System.out.println("Player" + String.valueOf(playerNumber) + " output text file not found.");
            }
            
            barrier.await();
            System.out.println("Barrier 2 passed");
            checkWinner(); // After all logic and file writing, checks if the current hand is a winner.

            barrier.await(); // Wait for all players to reach this point before continuing
            System.out.println("Barrier 3 passed");
        } catch (InterruptedException | BrokenBarrierException e) {
            e.printStackTrace();
        }
    }
}
}
