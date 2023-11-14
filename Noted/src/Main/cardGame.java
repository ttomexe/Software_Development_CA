package Main;
import java.util.*;
import java.util.concurrent.CyclicBarrier;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path; //Imports used
public class cardGame {

    private static List<Integer> cardPack = new ArrayList<>(); //List for containing values on cards found via the .txt file
    static List<String> packLines = new ArrayList<>();         //List for containg each line in the .txt file to be converted into integer form
    static List<Player> players = new ArrayList<>();           //List that contains all of the player classes created 
    static List<cardDeck> cardDecks = new ArrayList<>();       //List that contains all of the cardDeck classes created
    static List<Thread> activePlayers = new ArrayList<>();     //List that contains the threads of each activePlayer
    private static boolean validPack = false;                  //Boolean used for a loop for checking if a pack is valid for the game
    private static boolean validNumberOfPlayers = false;       //Boolean ussed for a loop checking amount of players is valid
    private static int numberOfPlayers;                        //Integer containg the amount of players in a game
    private static boolean gameOver;                           //Boolean indicating the current status of the game
    private final static CyclicBarrier barrier = new CyclicBarrier(8); // Initialize the barrier
    

    // ... other game-related code ...

    public static List<Player> getPlayers(){ //Returns all of the players
        return players;
    }
    public static List<cardDeck> getDecks(){//Returns all of the Decks
        return cardDecks;
    }
    public static int getNumberOfPlayers(){//Returns the number of players in the game
        return numberOfPlayers;
    }
    public static boolean getGameOver(){ //Returns the state of the game
        return gameOver;
    }
    public static void changeGameOver(){ //Changes the state of the gameOver to true to signal the end of the game
        gameOver = true;
    }

    // private static void initializeBarrier(int numberOfPlayers) {
    //     // Initialize the barrier with the specified number of parties
    //     barrier = new CyclicBarrier(numberOfPlayers);
    // }

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
        cardPack.clear(); //Clears the previous cardPack if a failed attempt was made. Starts from fresh
        Path filePath = Path.of(packPath);            //NOTE: to work for Numbers.txt write src/Main/Numbers.txt, dont need to force "src" as part of path in parameter
        packLines = Files.readAllLines(filePath);

        for (String line : packLines){
            int number = Integer.parseInt(line); //Converts the string list to an integer list for ease of use later
            cardPack.add(number);                //Then adds the integer to the list cardPack
        }
        System.out.println(cardPack.size()); //Prints the how many card values are in the list, used for testing.
        if(cardPack.size() == (8*numberOfPlayers)){
            return true; //If the card pack entered is of a valid size, 8n where n is the number of players. Then returns true
        } else{
            System.out.println("Invalid card pack. A valid card pack must have 8n rows."); //Otherwise informs the user its not valid and returns false.
            return false;
        }
    }

    public static void distributeCards(){
        //Distribute all the cards from the pack to each player in round-robin fashion, then similarly distribute the remaining cards to the decks.

        for (int i = 0; i < numberOfPlayers; i++) {   //Loops for the amount of players 
            Player newPlayer = new Player(i+1, barrier);       //Loop starts at 0, adds 1 to i to make the playerNumber start from 1 and thus more logical for use later
            players.add(newPlayer);                   //After making the new player class adds it to the list of players for ease of access later
            cardDeck newCardDeck = new cardDeck(i+1); //Everytime a player is created, creates a cardDeck class with matching identifier essentially pairing them together
            cardDecks.add(newCardDeck);               //Also adds the newly created cardDeck class to a list for ease of access later
        }
       
        for (int i = 0; i < numberOfPlayers*4;) {      //As each player needs 4 cards in their starting hand, 4n cards are created where n is the number of players
            for(Player player : players){              //For each player, they are given their cards 1 at a time to fulfill the round robin criteria from the top of the pack
                Card card = new Card(cardPack.get(i)); //From our converted .txt file we create a card for the value at the i index, which is starting at 0 (the top of the pack)
                player.addCard(card);                  // then give the card to the player
                i++;                                   // We increment the i value within this for loop to ensure that the first n cards are hande out sequentially
            } 
        }

        for(int i = numberOfPlayers*4; i < cardPack.size();){  //Similarly to above, we do the same with the remaining cards in the cardPack, however we start with i as 4n where n is the amount of players
            for(cardDeck cardDeck : cardDecks){               //For each Player made a cardDeck had been made, we hand out the rest of the cards in the same round robin fashion
                Card card = new Card(cardPack.get(i));        // The remaining cards from the .txt are distributed to 1 cardDeck at a time
                cardDeck.addCard(card);                       // The newly created card is added to the cardDeck                                   
                i++;                                          // and the value of I is incremented
            }
        }


    }

    public static void writeDecks(){ //After a winner has been found writes the value of the cards in each deck at the end point of the game
        for(cardDeck d : cardDecks){ //For each deck in the cardDecks list
            try{
                //Creates a .txt file in the form "Deck" + cardDeckNumber
                BufferedWriter writer = new BufferedWriter(new FileWriter("src/Main/Deck"+String.valueOf(d.getcardDeckNumber())+".txt"));
                //Writes the decks number and the value for each card in its hand using .getHandValues()
                writer.write("deck"+d.getcardDeckNumber()+" contents: "+d.getHandValues().toString());
                writer.close();  //Closes the writer
            }catch(IOException e){System.out.println("Deck"+d.getcardDeckNumber()+" output file not found.");}
            //If an issue occurs with the file I/O then the IOException will inform the user of what has happened.
            //Repeats for each card Deck
        }
    }

    public static void startGame(){
        // Here we start up the player threads when the startGame is called
        for(Player player : players){ 
            try{
                //For each player created in the player list, we create a .txt file named "player" followed by its playerNumber
                BufferedWriter writer = new BufferedWriter(new FileWriter("src/Main/Player"+String.valueOf(player.getPlayerNumber())+".txt"));
                //We then write in this new .txt the values of the cards in the players hand using getHandValues(). A list is returned which is converted into a string via .toString()
                writer.write("player "+String.valueOf(player.getPlayerNumber())+" initial hand: "+player.getHandValues().toString());
                writer.newLine();     //After the starting information is recorderd, we end the line, start the text on the next line for writing later
                writer.close();       // And then close the writer
            }catch(IOException e){System.out.println("Player"+String.valueOf(player.getPlayerNumber())+" output text file not found.");}
            //If an issue occurs with the file I/O then the IOException will inform the user of what has happened.

            Thread t = new Thread(player); //After the Starting player files have been created each player is assigned a thread to run on
            activePlayers.add(t);         //They are added to the active players list
            t.start();                    // And then all started at the same time via the list
        }
    }


    public static void win(Player winner){ //When a plater has won they call the win method
        writeDecks(); //This then makes each deck output its contents into a text file
        for (Player p : players){ //And then for every player in the players list
            if(p != winner){     //Excluding the player that called win
                p.lose(winner);  //Maked them call the lose method with the Winner Player class as the parameter.
            }
        }
    }

    public static void main(String[] args) throws InterruptedException{
        Scanner myScanner = new Scanner(System.in); //Scanner to read user inputs for number of players and card pack path.
        //loop requesting input for number of players, repeating until a valid input is provided.
        while(validNumberOfPlayers == false){
            System.out.println("Enter number of players: "); //Prompts the user to input
            numberOfPlayers = myScanner.nextInt(); //Read next input integer
            validNumberOfPlayers = checkValidPlayerNumber(numberOfPlayers); //Checks the input to see if its valid, if it is returns True and breaks loop
        }
        
        while(validPack == false){ //Similar to above, loops requesting pack input untill valid input
            System.out.println("Enter path of card pack: ");
            String packPath = myScanner.next(); //Read next input string
            
            try {
                validPack = checkValidDeck(packPath); //uses checkValidDeck to see if its a valid path, if valid returns true and breaks loop, otherwise stays false
            } catch (IOException e) {
                
                //Returns an error if the file is read incorrectly, used for debugging
                System.err.println("An error occurred while reading the file: " + e.getMessage());
                e.printStackTrace();
            }
        }

        myScanner.close(); //Close scanner as no more user inputs are required now.
        distributeCards(); //Distributes the cards to the appropriated players and decks
        
        //Shows the initial hands of all of the players, was used for testing

        // for(Player p : players){
        //     System.out.println(p.getHandValues());
        // }

        startGame(); //Initializes the game
    }
}