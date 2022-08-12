package com.company;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Arrays;

/*
    Name: Ameen Khawaja
*/

/* =====================================================================================================================
/**
 * SDK used: Amazon corretto version 11.0.15
 *
 * This program runs through a terminal, by executing the command: nc localhost 1024.
 *
 * I've created an unlisted 1-minute YouTube video where I demonstrate the game and connecting to multiple
 * concurrent clients, where a total of 4 players are playing 2 different matches.
 * It can be watched here: https://www.youtube.com/watch?v=KpQ2o_axK4w&ab_channel=AmeenK
 *
 * Additionally, in the ZIP folder, I left sample output pictures showing horizontal wins, diagonal wins,
 * vertical wins, and how the program in general runs.
 */
/* =====================================================================================================================

 */
/*
 * This entire class is responsible for implementing Connect 4 and separating it into a thread to handle
 * input/output over sockets. Instead of a traditional GUI, I implemented the gameplay of Connect 4 to be
 * terminal player vs terminal player. Run the java file and type 'nc localhost 1024' in the terminal to begin
 */

public class ConnectFour {

    public static class ConnectionThread extends Thread {

        Player p = new Player('R', 'Y');
        private final char[][] board = new char[6][7];
        private final char boardPlaceHolder = 'O';
        private boolean isWinnerDecided, playerOneTurn = true, playerTwoTurn = true,
                isPlayerOneWinner, isPlayerTwoWinner;
        private int terminalColInput, terminalInputIndexed;
        private final Socket client1, client2;
        String playerOneReadyStatus, playerTwoReadyStatus;

        public ConnectionThread(Socket c1, Socket c2) {
            client1 = c1;
            client2 = c2;
        }

        /**
         * This method is responsible for printing out an output of the board each time the
         * user inputs a number.
         *
         * @param board          - 2d-array that contains the current state of connect 4 board
         * @param terminalOutput - prints out result to terminal
         */
        public void showBoard(char[][] board, PrintWriter terminalOutput) {
            terminalOutput.println("1  2  3  4  5  6  7 ");
            terminalOutput.println("====================");
            System.out.println("====================");
            for (int row = 0; row < board.length; row++) {
                for (int column = 0; column < board[row].length; column++) {
                    System.out.print(board[row][column] + "  ");
                    terminalOutput.print(board[row][column] + "  ");
                }
                System.out.println();
                terminalOutput.println();
            }
            System.out.println("====================");
            terminalOutput.println("====================");
        }

        /**
         * This method is responsible for showing the blueprint of the board, I used the letter 'O'
         * to act as a placeholder until the user fills it up with either letter R or Y
         *
         * @param board - fills the 2d-array with all 'O' as the placeholder values
         */
        public void fillBoard(char[][] board) {
            for (char[] row : board) {
                Arrays.fill(row, boardPlaceHolder);
            }
        }

        /**
         * This method is responsible for placing player 1 or player 2's input into the board and printing
         * out the result right as they've chosen what column to insert into. I basically brute force each column
         * by checking bottom to top, while it may not be the most efficient, it's the only implementation I could get
         * to work.
         *
         * @param board          - the 2d-array/board we want to place a letter into
         * @param insertInColumn - keeps track of the column number the user wants to insert into
         * @param letter         - stores either player 1 'R' or player 2 'Y' as a char
         * @param terminalOutput - prints out result to terminal
         */
        public void placePiece(char[][] board, int insertInColumn, char letter, PrintWriter terminalOutput) {
            if (board[5][insertInColumn] == boardPlaceHolder) {
                board[5][insertInColumn] = letter;
                showBoard(board, terminalOutput);
            } else if (board[4][insertInColumn] == boardPlaceHolder) {
                board[4][insertInColumn] = letter;
                showBoard(board, terminalOutput);
            } else if (board[3][insertInColumn] == boardPlaceHolder) {
                board[3][insertInColumn] = letter;
                showBoard(board, terminalOutput);
            } else if (board[2][insertInColumn] == boardPlaceHolder) {
                board[2][insertInColumn] = letter;
                showBoard(board, terminalOutput);
            } else if (board[1][insertInColumn] == boardPlaceHolder) {
                board[1][insertInColumn] = letter;
                showBoard(board, terminalOutput);
            } else if (board[0][insertInColumn] == boardPlaceHolder) {
                board[0][insertInColumn] = letter;
                showBoard(board, terminalOutput);
            } else if (board[0][insertInColumn] != boardPlaceHolder) {
                System.out.println("Column full! Try placing in a different column!");
                terminalOutput.println("Column full! Try placing in a different column!");
            }
        }

        /**
         * This method checks each column vertically for a match of 4 of the same letters. My logic behind
         * checking for a vertical match is to get the most recent letter placed into the board, then check the other
         * rows in that column by incrementing the row by 1 more each time
         *
         * @param playerOneOrPlayerTwo - checks for 'R' and 'Y'
         */
        public void checkVertical(char playerOneOrPlayerTwo) {
            try {
                for (int row = 0; row < board.length; row++) {
                    for (int column = 0; column < board[row].length; column++) {
                        if (board[row][column] == playerOneOrPlayerTwo
                                && board[row + 1][column] == playerOneOrPlayerTwo
                                && board[row + 2][column] == playerOneOrPlayerTwo
                                && board[row + 3][column] == playerOneOrPlayerTwo) {
                            isWinnerDecided = true;
                            if (playerOneOrPlayerTwo == 'R') {
                                isPlayerOneWinner = true;
                            } else if (playerOneOrPlayerTwo == 'Y') {
                                isPlayerTwoWinner = true;
                            }
                        }
                    }
                }
            } catch (Exception e) {
                // No need to print out the exceptions
            }
        }

        /**
         * This method checks each row horizontally for a match of 4 of the same letters. My logic behind
         * checking for a horizontal match is to get the most recent letter placed into the board, then check the other
         * columns in that row by incrementing the column array index by 1 more each time
         *
         * @param playerOneOrPlayerTwo - checks for 'R' and 'Y'
         */
        public void checkHorizontal(char playerOneOrPlayerTwo) {
            try {
                for (int row = 0; row < board.length; row++) {
                    for (int column = 0; column < board[row].length; column++) {
                        if (board[row][column] == playerOneOrPlayerTwo &&
                                board[row][column + 1] == playerOneOrPlayerTwo &&
                                board[row][column + 2] == playerOneOrPlayerTwo &&
                                board[row][column + 3] == playerOneOrPlayerTwo) {
                            isWinnerDecided = true;
                            if (playerOneOrPlayerTwo == 'R') {
                                isPlayerOneWinner = true;
                            } else if (playerOneOrPlayerTwo == 'Y') {
                                isPlayerTwoWinner = true;
                            }
                        }
                    }
                }
            } catch (Exception e) {
                // no need to print out exception error if any occurs.
            }
        }

        /**
         * This method checks each column and row diagonally  for a match of 4 of the same
         * letters. My logic behind checking for a diagonal match is to get the most recent letter placed into the board,
         * then from that letter check the first letter diagonal to it (incrementing row by 1 and decrease column by 1).
         * Then, I check if there is another diagonal letter by incrementing the row by 2 and decreasing column by 2,
         * and finally I increment the row by 3 and decrement the column by 3 because the fourth diagonal letter
         * should be 3 away from the first one.
         *
         * @param playerOneOrPlayerTwo - checks for 'R' and 'Y' diagonally
         */
        public void checkDiagonal(char playerOneOrPlayerTwo) {
            try {
                for (int row = 0; row < board.length; row++) {
                    for (int column = 0; column < board[row].length; column++) {
                        if (board[row][column] == playerOneOrPlayerTwo
                                && board[row + 1][column - 1] == playerOneOrPlayerTwo
                                && board[row + 2][column - 2] == playerOneOrPlayerTwo
                                && board[row + 3][column - 3] == playerOneOrPlayerTwo
                        ) {
                            System.out.println("diagonal found " + playerOneOrPlayerTwo);
                            isWinnerDecided = true;
                            if (playerOneOrPlayerTwo == 'R') {
                                isPlayerOneWinner = true;
                            } else if (playerOneOrPlayerTwo == 'Y') {
                                isPlayerTwoWinner = true;
                            }
                        }
                        if (board[row][column] == playerOneOrPlayerTwo
                                && board[row + 1][column + 1] == playerOneOrPlayerTwo
                                && board[row + 2][column + 2] == playerOneOrPlayerTwo
                                && board[row + 3][column + 3] == playerOneOrPlayerTwo) {
                            isWinnerDecided = true;
                            if (playerOneOrPlayerTwo == 'R') {
                                isPlayerOneWinner = true;
                            } else if (playerOneOrPlayerTwo == 'Y') {
                                isPlayerTwoWinner = true;
                            }
                        }

                    }
                }
            } catch (Exception e) {
                // don't print out exception if it occurs
            }
        }

        /**
         * This method checks for a negative diagonal, meaning it checks from top left to bottom right
         *
         * @param playerOneOrPlayerTwo - checks for 'R' and 'Y' diagonally
         */
        private void checkNegativeDiagonal(char playerOneOrPlayerTwo) {
            try {
                // only loop row and column until row and column equals 3 because there is only 3 possibilities of a
                // negative slope/diagonal forming in a 6 by 7 grid.
                for (int row = 0; row <= 3; row++) {
                    for (int column = 0; column <= 3; column++) {
                        if (board[row][column] == playerOneOrPlayerTwo
                                && board[row + 1][column + 1] == playerOneOrPlayerTwo
                                && board[row + 2][column + 2] == playerOneOrPlayerTwo
                                && board[row + 3][column + 3] == playerOneOrPlayerTwo) {
                            isWinnerDecided = true;
                            if (playerOneOrPlayerTwo == 'R') {
                                isPlayerOneWinner = true;
                            } else if (playerOneOrPlayerTwo == 'Y') {
                                isPlayerTwoWinner = true;
                            }
                        }
                    }
                }
            } catch (Exception e) {
            }
        }

        /**
         * This method checks if no winner occurs by checking the top row (row 1) if all the placeholder 'O's are gone
         * and if no winner has been decided. If all the placeholder 'O' and a winner hasn't been decided, that means
         * the board is full and neither player 1 or player 2 have won
         *
         * @param terminalOutputOne - prints output to player 1 terminal
         * @param terminalOutputTwo - prints output to player 2 terminal
         */
        public void checkIfNoWinner(PrintWriter terminalOutputOne, PrintWriter terminalOutputTwo) {

            if (board[0][0] != boardPlaceHolder && board[0][1] != boardPlaceHolder
                    && board[0][2] != boardPlaceHolder && board[0][3] != boardPlaceHolder &&
                    board[0][4] != boardPlaceHolder && board[0][5] != boardPlaceHolder &&
                    board[0][6] != boardPlaceHolder && !isWinnerDecided) {
                terminalOutputOne.println("NO WINNER! please type 'nc localhost 1024' to play again!");
                terminalOutputTwo.println("NO WINNER! please type 'nc localhost 1024' to play again!");
//                System.exit(1);
            }
        }

        /**
         * This method checks to see if a winner has been decided, if it has then it will print instructions to terminal
         * to let player one and two know who won respectively. It will then mention how to play again, and shortly
         * after disconnect from the terminal, though I keep the Java program continuously running so more players could
         * play/join if they wish so.
         *
         * @param terminalOutputOne - prints output to player 1 terminal about game status
         * @param terminalOutputTwo - prints output to player 2 terminal about game status
         */
        public void mentionWinner(PrintWriter terminalOutputOne, PrintWriter terminalOutputTwo) {
            if (isPlayerOneWinner) {
                terminalOutputOne.println("You won! ");
                terminalOutputTwo.println("The opponent has beaten you!");
                terminalOutputOne.println("To play again, please type 'nc localhost 1024'!");
                terminalOutputTwo.println("To play again, please type 'nc localhost 1024'!");
                //System.exit(1);
            } else if (isPlayerTwoWinner) {
                terminalOutputTwo.println("You won! ");
                terminalOutputOne.println("The opponent has beaten you!");
                terminalOutputOne.println("To play again, please type 'nc localhost 1024'!");
                terminalOutputTwo.println("To play again, please type 'nc localhost 1024'!");
                //System.exit(1);
            }
        }

        /**
         * Once player 1 and player 2 have successfully connected, the game starts.
         */
        @Override
        public void run() {

            try (
                    // this logic of getting player one / player two input was taken from Sakai, under the networking
                    // section posted by the Professor.
                    PrintWriter playerOneOutput = new PrintWriter(client1.getOutputStream(), true);
                    PrintWriter playerTwoOutput = new PrintWriter(client2.getOutputStream(), true);
                    BufferedReader playerOneInput = new BufferedReader(new InputStreamReader(client1.getInputStream()));
                    BufferedReader playerTwoInput = new BufferedReader(new InputStreamReader(client2.getInputStream()));
            ) {
                playerOneOutput.println("\n[Player 1] Welcome to connect4");
                playerTwoOutput.println("\n[Player 2] Welcome to connect4");

                playerOneOutput.println("Type R when you're ready to play, game will start when both players are ready\n");
                playerTwoOutput.println("Type R when you're ready to play, game will start when both players are ready\n");

                // First player to join the network gets first move.
                try {
                    // read if the player is typing 'R' for ready
                    playerOneReadyStatus = playerOneInput.readLine();
                    playerTwoReadyStatus = playerTwoInput.readLine();

                    // keep checking if they type 'r' or 'R' or else keep prompting them to type it.
                    do {
                        // if they type 'R' or 'r', it should be fine either way as it ignores the capitalization
                        if (!playerOneReadyStatus.equalsIgnoreCase("R")) {
                            playerOneOutput.println("Type R when you're ready to play, " +
                                    "game will start when both players are ready");
                            playerOneReadyStatus = playerOneInput.readLine();
                        }
                        if (!playerTwoReadyStatus.equalsIgnoreCase("R")) {
                            playerTwoOutput.println("Type R when you're ready to play, " +
                                    "game will start when both players are ready");
                            playerTwoReadyStatus = playerTwoInput.readLine();
                        }
                    } while (!playerOneReadyStatus.equalsIgnoreCase("R")
                            || !playerTwoReadyStatus.equalsIgnoreCase("R"));
                } catch (Exception e) {
                }

                fillBoard(board); // fill board with placeholder 'O'
                showBoard(board, playerOneOutput); // display placeholder board to player 1
                showBoard(board, playerTwoOutput); // display placeholder board to player 2


                // I keep track of which player gets to go by two boolean variables, one for each player.
                // once player one goes, it is the next players turn by alternating boolean values until a winner is
                // decided
                while (!isWinnerDecided) {
                    if (playerOneTurn) {
                        try {
                            playerOneOutput.println("[R] Your turn!");
                            playerTwoOutput.println("Currently waiting for Player 1 to make a move..");
                            // take user input and parse entire String to only return integer value
                            terminalColInput = Integer.parseInt(playerOneInput.readLine());
                            // subtract the user input by 1 because of first element of array is index 0, so input 1 = 0
                            terminalInputIndexed = terminalColInput - 1;
                            placePiece(board, terminalInputIndexed, p.getPlayerOne(), playerOneOutput);
                            showBoard(board, playerTwoOutput);
                            playerOneOutput.println("Opponents turn..\n");
                            playerOneTurn = false;
                            playerTwoTurn = true;
                        } catch (Exception e) {
                            System.out.println("Enter a number between 1 to 7!");
                            playerOneOutput.println("Enter a number between 1 to 7!");
                        }
                    } else if (playerTwoTurn) {
                        try {
                            playerTwoOutput.println("[Y] Your turn! ");
                            terminalColInput = Integer.parseInt(playerTwoInput.readLine());
                            // subtract the user input by 1 because of first element of array is index 0, so input 1 = 0
                            terminalInputIndexed = terminalColInput - 1;
                            placePiece(board, terminalInputIndexed, p.getPlayerTwo(), playerTwoOutput);
                            showBoard(board, playerOneOutput);
                            playerTwoOutput.println("Opponents turn..\n");
                            playerOneTurn = true;
                            playerTwoTurn = false;
                        } catch (Exception e) {
                            System.out.println("Enter a number between 1 to 7!");
                            playerTwoOutput.println("Enter a number between 1 to 7!");
                        }
                    }

                    // each iteration check if a vertical, horizontal, diagonal pattern has been found for both player 1
                    // and player 2.

                    // check for player one
                    checkNegativeDiagonal(p.getPlayerOne());
                    checkVertical(p.getPlayerOne());
                    checkHorizontal(p.getPlayerOne());
                    checkDiagonal(p.getPlayerOne());

                    // check for player two
                    checkNegativeDiagonal(p.getPlayerTwo());
                    checkVertical(p.getPlayerTwo());
                    checkHorizontal(p.getPlayerTwo());
                    checkDiagonal(p.getPlayerTwo());

                    // check if no winner exists and all the slots in the board are occupied
                    checkIfNoWinner(playerOneOutput, playerTwoOutput);
                }
                // if a winner is found, it will print out the result in both player 1 and 2's terminal
                mentionWinner(playerOneOutput, playerTwoOutput);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}