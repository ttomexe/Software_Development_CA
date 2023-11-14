package Tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

//import javax.smartcardio.CardChannel;

import org.junit.Test;

import Main.Card;
import Main.Player;
import Main.cardDeck;
import Main.cardGame;

public class playerTest {
    
    @Test
    public void getPlayerNumber() {
        Player player = new Player(1);
        assertEquals(1, player.getPlayerNumber());
    }

    @Test
    public void getHand() {
        Player player = new Player(1);
        assertNotNull(player.getHand());
        assertTrue(player.getHand().isEmpty());
    }

    @Test
    public void getHandValues() {
        Player player = new Player(1);
        player.addCard(new Card(2)); 
        player.addCard(new Card(5));
        assertEquals(List.of(2, 5), player.getHandValues());
    }

    @Test
    public void addCard() {
        Player player = new Player(1);
        Card card = new Card(3);
        player.addCard(card);
        assertEquals(List.of(card), player.getHand());
    }

    @Test
    public void findDiscard() {
        Player player = new Player(1);
        player.addCard(new Card(1));
        player.addCard(new Card(2));
        player.addCard(new Card(3));
        assertEquals(1, player.findDiscard());
    }

    @Test
    public void discard() {
        
    }



}
    

