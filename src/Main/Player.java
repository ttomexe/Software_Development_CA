package Main;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class Player implements Runnable{
    private List<Card> cardHand = new ArrayList<>();
    private int playerNumber;

    public Player(int playerNumber){
        this.playerNumber = playerNumber;
    }

    public int getPlayerNumber(){
        return playerNumber;
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

    public void addCard(Card card){
        cardHand.add(card);
    }

    public int findDiscard(){
        int toDiscard = 0;
        int index = 0;
        while(toDiscard < cardHand.size()){
            if (cardHand.get(index).getValue() == playerNumber && index < cardHand.size()-1){
                index++;
            }
            else{
                toDiscard = index;
                return toDiscard;
            }
        }
        toDiscard = index;
        return(toDiscard);
    }

    public void discard(int i){
        Card c = cardHand.get(i);
        cardHand.remove(i);
        if(playerNumber == cardGame.getNumberOfPlayers()){
            cardGame.getDecks().get(0).addCard(c);
        }
        else{
            cardGame.getDecks().get(playerNumber).addCard(c);
        }
    }

    public Card draw(cardDeck d){
        Card drawnCard = d.drawCard();
        this.cardHand.add(drawnCard);
        return drawnCard;
    }


    public void checkWinner(){

        if (getHandValues().get(0) == getHandValues().get(1) && getHandValues().get(0) == getHandValues().get(2) && getHandValues().get(0) == getHandValues().get(3)){
            cardGame.changeGameOver();
            try{
                BufferedWriter writer = new BufferedWriter(new FileWriter("src/Main/Player"+String.valueOf(playerNumber)+".txt", true));
                writer.write("player "+String.valueOf(playerNumber)+" wins");
                writer.newLine();
                writer.write("player "+String.valueOf(playerNumber)+" exits");
                writer.newLine();
                writer.write("player "+String.valueOf(playerNumber)+" final hand: "+getHandValues().toString());
                writer.close();
            }catch(IOException e){System.out.println("Player"+String.valueOf(playerNumber)+" output text file not found.");}
            System.out.println("player "+String.valueOf(playerNumber)+" wins");

            cardGame.win(this);
            
        }
    
        //interrupt other threads and exit
    }
    

    public void lose(Player winner){
        try{
            BufferedWriter writer = new BufferedWriter(new FileWriter("src/Main/Player"+String.valueOf(playerNumber)+".txt", true));
            writer.write("player "+String.valueOf(winner.getPlayerNumber())+" has informed player "+String.valueOf(playerNumber)+" that player"+String.valueOf(winner.getPlayerNumber())+" has won");
            writer.newLine();
            writer.write("player "+String.valueOf(playerNumber)+" exits");
            writer.newLine();
            writer.write("player "+String.valueOf(playerNumber)+" hand: "+getHandValues().toString());
            writer.close();
        }catch(IOException e){System.out.println("Player"+String.valueOf(playerNumber)+" output text file not found.");}

    }
    

    public void run(){
        //Check if has winning hand, discard and pick up, then wait thread, and notify next player
        boolean initialHand = true;
        while(!cardGame.getGameOver()){
            while(initialHand){
                System.out.println("initialHand check");
                checkWinner();
                initialHand = false;
            }
            while(cardGame.cardDecks.get(playerNumber-1).getHand().size() <= 0){
                try{
                    Thread.sleep(1000);
                }catch(InterruptedException e){}
            }

            Card drawnCard = draw(cardGame.cardDecks.get(playerNumber-1));
            int toDiscard = findDiscard();
            int discardValue = cardHand.get(toDiscard).getValue();
            discard(toDiscard);

            try{
                BufferedWriter writer = new BufferedWriter(new FileWriter("src/Main/Player"+String.valueOf(playerNumber)+".txt", true));
                writer.write("player "+String.valueOf(playerNumber)+" draws a "+String.valueOf(drawnCard.getValue())+" from deck "+String.valueOf(playerNumber));
                writer.newLine();
                if(playerNumber == cardGame.getNumberOfPlayers()){
                    writer.write("player "+String.valueOf(playerNumber)+" discards a "+String.valueOf(discardValue)+" to deck 1");
                }else{writer.write("player "+String.valueOf(playerNumber)+" discards a "+String.valueOf(discardValue)+" to deck "+String.valueOf(playerNumber+1));}
                writer.newLine();
                writer.write("player "+String.valueOf(playerNumber)+" current hand is "+getHandValues().toString());
                writer.newLine();
                writer.close();
            }catch(IOException e){System.out.println("Player"+String.valueOf(playerNumber)+" output text file not found.");}

            checkWinner();
        }
        
    
    }
}
