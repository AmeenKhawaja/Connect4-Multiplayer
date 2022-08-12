package com.company;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * This class sets up the network and allows the user to connect with a port number. It then starts two clients,
 * each being a player for the connect 4 game. When both clients have been accepted, a thread in the ConnectFour class
 * begins.
 *
 */
public class ThreadedServer {

    public static void main(String[] args) {
        int portNumber = 1024;
        try (
                ServerSocket serverSocket = new ServerSocket(portNumber);
        ) {
            while (true) {
                Socket client1=serverSocket.accept();
                Socket client2=serverSocket.accept();
                new ConnectFour.ConnectionThread(client1, client2).start();
            }
        } catch (IOException e) {
            System.out.println("Exception caught when trying to listen on port " + portNumber + " or listening for a connection");
            System.out.println(e.getMessage());
        }
    }
}