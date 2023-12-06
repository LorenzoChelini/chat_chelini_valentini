package com.example;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class ClientThread extends Thread {

    private Socket clientSocket;
    private Client clientOutput;
    private BufferedReader socketReader;

    public ClientThread(Socket socket, Client client) throws IOException {
        this.clientSocket = socket;
        this.clientOutput = client;
        socketReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }

    public void run() {
        try {
            while (true) {
                String receivedMessage = receive();
                System.out.println(receivedMessage);
                if (receivedMessage.equals("BYE!")) break;
            }

            socketReader.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public String receive() throws IOException {
        String message = socketReader.readLine();
        return message;
    }
}
