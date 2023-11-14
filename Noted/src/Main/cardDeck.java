package Main;
import java.util.*;
public class cardDeck {
    
    private List<Card> cardHand = new ArrayList<>();  //List to store the cards in the cardDeck
    private int cardDeckNumber;                       //Unique identifier for the cardDeck
    private cardDeck deck;                            //


    public cardDeck(int cardDeckNumber){          //Creation of the deck, only difference between decks at creation is the id, rest of the attributes are added via a round robin method...
        this.cardDeckNumber = cardDeckNumber;     //...by iterating through the list of all cardDecks
    }

    public int getcardDeckNumber(){     //When called returns the cardDeck's cardDeckNumber 
        return cardDeckNumber;
    }

    public cardDeck getDeck(){        //When called, returns the cardDeck itself
        return deck;
    }

    public List<Card> getHand(){      //When called, returns the cards held in the cardDeck in list form    
        return cardHand;
    }

    public List<Integer> getHandValues(){   //When called returns the face values of the cards in a list in integer form
        List<Integer> cardValues =  new ArrayList<>(); //creates an integer list to store face Values
        
        for(Card card : cardHand){   //For each card in the cardHand it...
            cardValues.add(card.getValue()); //...Uses the card classes getValue method to return the face value of the card and adds it to the list
        }

        return cardValues;  //It then returns the list of face values
    }

    public synchronized void addCard(Card card){  //adds a card to the cardDeck, takes a card as a paremeter
        cardHand.add(card);  //adds the inputted card to the cardHand list
    }

    public synchronized Card drawCard(){  //Method used to draw a card from the card hand
        Card drawnCard = cardHand.get(0); //gets the first card from the cardDecks hand (list of cards) and saves it as drawnCard
        cardHand.remove(0);  //Removes the drawnCard from the cardHand
        return(drawnCard);  //And then returns the drawnCard 
    }

}
