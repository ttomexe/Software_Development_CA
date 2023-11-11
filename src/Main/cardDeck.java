package Main;
import java.util.*;
import java.util.concurrent.locks.ReentrantLock;
public class cardDeck {
    
    private List<Card> cardHand = new ArrayList<>();
    private int cardDeckNumber;
    private cardDeck deck;


    public cardDeck(int cardDeckNumber){
        this.cardDeckNumber = cardDeckNumber;
    }

    public int getcardDeckNumber(){
        return cardDeckNumber;
    }

    public cardDeck getDeck(){
        return deck;
    }

    public List<Card> getHand(){
        
        return cardHand;
    }

    public List<Integer> getHandValues(){
        List<Integer> cardValues =  new ArrayList<>();
        
        for(Card card : cardHand){
            cardValues.add(card.getValue());
        }

        return cardValues;
    }

    public synchronized void addCard(Card card){
        cardHand.add(card);
    }

    public synchronized Card drawCard(){
        Card drawnCard = cardHand.get(0);
        cardHand.remove(0);
        return(drawnCard);
    }

}
