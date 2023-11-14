package Tests;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;

import java.util.*;

import org.junit.Test;

import Main.Player;
import Main.cardDeck;
import Main.cardGame;
import junit.*;

public class cardGameTest {
    
    @Test
    public void invalidPlayerNumberNotAllowed(){
        //Test that given an invalid number of players, checkValidPlayerNumber method returns false.
        //Tests the scenarios of 0 and a negative integer as inputs, both of which should return false.
        assertFalse(cardGame.checkValidPlayerNumber(0));
        assertFalse(cardGame.checkValidPlayerNumber(-1));
    }

    @Test
    public void validPlayerNumberAllowed(){
        //Test that given a valid number of players, checkValidPlayerNumber method returns true.
        //Tests the scenario of a postitve integer (valid input)
        assertTrue(cardGame.checkValidPlayerNumber(5));
 
    }

    @Test
    public void invalidCardPackNotAllowed(){
        //Test that given an invalid card pack file (that is, a file not containing exactly 8n rows), checkValidDeck method returns false.
        cardGame.setNumberOfPlayers(10);
        try{
            //Given sample file Numbers.txt where 8n = 80 and file contains 32 rows, method should return false.
            assertFalse(cardGame.checkValidDeck("src/Main/Numbers.txt"));
        }catch(Exception e){}
        
    }

    @Test
    public void validCardPackAllowed(){
        //Test that given a valid card pack file (that is, a file containing exactly 8n rows), checkValidDeck method returns true.
        cardGame.setNumberOfPlayers(4);
        try{
            //Given sample file Numbers.txt where 8n = 32 and file contains 32 rows, method should return true.
            assertTrue(cardGame.checkValidDeck("src/Main/Numbers.txt"));
        }catch(Exception e){}
        
    }

    @Test
    public void distributeCardsCorrectly(){ //Maybe revisit to allow to be used for input values instead of one example
        //Test that for an example valid number of players and valid card pack , the cards are distributed to players and decks as expected.
        cardGame.setNumberOfPlayers(4);
        cardGame.setDeck("src/Main/Numbers.txt");
        cardGame.distributeCards();

        for(int i =1; i<5; i++){
            List<Integer> values = new ArrayList<>();
            values.add(i);
            values.add(i+4);
            values.add(i+8);
            values.add(i+12);
            for(int j =0; j<4;j++){
                assertEquals(cardGame.getPlayers().get(i-1).getHandValues().get(j), values.get(j));
            }
        }

        for(cardDeck deck : cardGame.getDecks()){
            List<Integer> values = new ArrayList<>();
            values.add(16+deck.getcardDeckNumber());
            values.add(16+deck.getcardDeckNumber()+4);
            values.add(16+deck.getcardDeckNumber()+8);
            values.add(16+deck.getcardDeckNumber()+12);
            for(int j =0; j<4;j++){
                assertEquals(deck.getHandValues().get(j), values.get(j));
            }
        }
    }
}
