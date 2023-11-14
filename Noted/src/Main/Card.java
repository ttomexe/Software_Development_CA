package Main;
public class Card {
    
    private int faceValue; //The faceValue of the card

    public Card(int faceValue) { //Creation of the card class, takes an integer as a parameter and saves it as the faceValue of the card
        this.faceValue = faceValue;
    }

    public int getValue() { //When called returns the faceValue of the card
        return faceValue;
    }

}