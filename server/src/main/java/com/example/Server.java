package com.example;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

    private GestoreClienti myClientsHandler = new GestoreClienti();

    public void startServer(){
        try{
            ServerSocket serverSocket = new ServerSocket(6789);
            System.out.println("-SERVER STARTED-");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client connesso: " + clientSocket);
                GestoreConnessioneClienteThread serverClientThread = new GestoreConnessioneClienteThread(clientSocket, myClientsHandler);

                this.myClientsHandler.aggiungiCliente(serverClientThread);

                serverClientThread.start();
            }
        } catch (Exception e){
            System.out.println(e.getMessage());
            System.out.println("ERRORE durante l'istanza del server");
        }
    }

    public static void main(String[] args){
        Server myServer = new Server();
        myServer.startServer();
    }
}
