package Main;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.io.*;
import java.nio.*;
import java.nio.file.Files;
import java.nio.file.Path;
public class cardGame {

    private static List<Integer> cardPack = new ArrayList<>();
    static List<String> packLines = new ArrayList<>();
    static List<Player> players = new ArrayList<>();
    static List<cardDeck> cardDecks = new ArrayList<>();
    static List<Thread> activePlayers = new ArrayList<>();
    private static boolean validPack = false;
    private static boolean validNumberOfPlayers = false;
    private static int numberOfPlayers;
    private static boolean gameOver;

    public static List<Player> getPlayers(){
        return players;
    }
    public static List<cardDeck> getDecks(){
        return cardDecks;
    }
    public static int getNumberOfPlayers(){
        return numberOfPlayers;
    }
    public static boolean getGameOver(){
        return gameOver;
    }
    public static void changeGameOver(){
        gameOver = true;
    }

    //////Methods to manually set variables or run specific blocks of code purely for testing purposes//////
    public static void setNumberOfPlayers(int n){
        numberOfPlayers = n;
    }
    public static void setDeck(String packPath){
        try{
            Path filePath = Path.of(packPath);            
            packLines = Files.readAllLines(filePath);
        }catch(Exception e){};
        
        for (String line : packLines){
            //Converts the string list to an integer list for ease of use later
            int number = Integer.parseInt(line);
            cardPack.add(number);
        }
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////



    //NOTE: Changed main functionality of checking valid player number and card deck to reside in their own functions to allow for unit testing.
    public static boolean checkValidPlayerNumber(int numberOfPlayers){

        if (numberOfPlayers <= 0) { //Check input number of players is valid
            System.out.println("Invalid amount of players"); //Inform user if not
            return false;
            } else{return true;} //Allow program to continue if the input is valid
    }

    public static boolean checkValidDeck(String packPath) throws IOException{
        // takes input from user and converts each line into an item on a list
        Path filePath = Path.of(packPath);            //NOTE: to work for Numbers.txt write src/Main/Numbers.txt, dont need to force "src" as part of path in parameter
        packLines = Files.readAllLines(filePath);

        for (String line : packLines){
            //Converts the string list to an integer list for ease of use later
            int number = Integer.parseInt(line);
            cardPack.add(number);
        }
        System.out.println(cardPack.size());
        if(cardPack.size() == (8*numberOfPlayers)){
            return true;
        } else{
            System.out.println("Invalid card pack. A valid card pack must have 8n rows.");
            return false;
        }
    }

    public static void distributeCards(){
        //Distribute all the cards from the pack to each player in round-robin fashion, then similarly distribute the remaining cards to the decks.

        for (int i = 0; i < numberOfPlayers; i++) {
            Player newPlayer = new Player(i+1);
            players.add(newPlayer);
            cardDeck newCardDeck = new cardDeck(i+1);
            cardDecks.add(newCardDeck);
        }
       
        for (int i = 0; i < numberOfPlayers*4;) {
            for(Player player : players){
                Card card = new Card(cardPack.get(i));
                player.addCard(card);
                i++;
            } 
        }

        for(int i = numberOfPlayers*4; i < cardPack.size();){
            for(cardDeck cardDeck : cardDecks){
                Card card = new Card(cardPack.get(i));
                cardDeck.addCard(card);
                i++;
            }
        }


    }

    public static void writeDecks(){
        for(cardDeck d : cardDecks){
            try{
                BufferedWriter writer = new BufferedWriter(new FileWriter("src/Main/Deck"+String.valueOf(d.getcardDeckNumber())+".txt"));
                writer.write("deck"+d.getcardDeckNumber()+" contents: "+d.getHandValues().toString());
                writer.close();
            }catch(IOException e){System.out.println("Deck"+d.getcardDeckNumber()+" output file not found.");}
            
        }
    }

    public static void startGame(){
        // Here we start up  the player threads, to be worked on

        for(Player player : players){
            try{
                BufferedWriter writer = new BufferedWriter(new FileWriter("src/Main/Player"+String.valueOf(player.getPlayerNumber())+".txt"));
                writer.write("player "+String.valueOf(player.getPlayerNumber())+" initial hand: "+player.getHandValues().toString());
                writer.newLine();
                writer.close();
            }catch(IOException e){System.out.println("Player"+String.valueOf(player.getPlayerNumber())+" output text file not found.");}
            Thread t = new Thread(player);
            activePlayers.add(t);
            t.start();
            
        }
    }


    public static void win(Player winner){
        writeDecks();
        for (Player p : players){
            if(p != winner){
                p.lose(winner);
            }
        }
    }

    public static void main(String[] args) throws InterruptedException{
        Scanner myScanner = new Scanner(System.in); //Scanner to read user inputs for number of players and card pack path.
        //loop requesting input for number of players, repeating until a valid input is provided.
        while(validNumberOfPlayers == false){
            System.out.println("Enter number of players: ");
            numberOfPlayers = myScanner.nextInt(); //Read next input integer
            myScanner.nextLine(); //Consume blank space     Revisit the reasoning Later
            validNumberOfPlayers = checkValidPlayerNumber(numberOfPlayers);
        }
        
        while(validPack == false){
            System.out.println("Enter path of card pack: ");
            String packPath = myScanner.next(); //Read next input string
            
            try {
                validPack = checkValidDeck(packPath);
            } catch (IOException e) {
                
                //Returns an error if the file is read incorrectly, used for debugging
                System.err.println("An error occurred while reading the file: " + e.getMessage());
                e.printStackTrace();
            }
        }

        myScanner.close(); //Close scanner as no more user inputs are required now.
        distributeCards();
        for(Player p : players){
            System.out.println(p.getHandValues());
        }
        startGame();
        

    }
}