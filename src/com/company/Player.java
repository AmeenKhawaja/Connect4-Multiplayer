package com.company;

/**
 * Player class which stores information about player 1 and player 2
 */
public class Player {

    private final char playerOne;
    private final char playerTwo;

    public Player(char playerOne, char playerTwo) {
        this.playerOne = playerOne;
        this.playerTwo = playerTwo;
    }

    public char getPlayerOne() {
        return playerOne;
    }

    public char getPlayerTwo() {
        return playerTwo;
    }

}
