/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gr.forth.ics.isl.xsearch;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 *
 * @author Pavlos Fafalios (fafalios@ics.forth.gr, fafalios@csd.uoc.gr)
 */
public class TCPServer extends Thread {
// the socket used by the server

    private ServerSocket serverSocket;
    private int port;
    // server constructor

    public TCPServer(int port) {
        this.port = port;
    }

    @Override
    public void run() {
        /* create socket server and wait for connection requests */
        try {
            serverSocket = new ServerSocket(port);
            System.out.println("Server waiting for client on port " + serverSocket.getLocalPort());

            while (true) {
                Socket socket = serverSocket.accept();  // accept connection
                System.out.println("New client asked for a connection");
                TCPThread t = new TCPThread(socket);    // make a thread of it
                System.out.println("Starting a thread for a new Client");
                t.start();
            }
        } catch (IOException e) {
            System.out.println("Exception on new ServerSocket: " + e);
        }
    }
}
