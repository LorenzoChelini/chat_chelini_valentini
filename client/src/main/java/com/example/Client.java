package com.example;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class Client {

    private Socket clientSocket;
    private ClientThread inputThread;

    String serverName = "localhost";
    int serverPort = 6789;
    Socket mySocket;
    
    BufferedReader keyboardInput;
    String receivedFromServer;
    DataOutputStream outToServer;
    
    String message;
    String messageType;
    String recipient;

    public Socket connectToServer(){
        try {
            keyboardInput = new BufferedReader(new InputStreamReader(System.in));
            mySocket = new Socket(serverName, serverPort);

            inputThread = new ClientThread(mySocket, this);

            outToServer = new DataOutputStream(mySocket.getOutputStream());

        } catch (Exception e){
            System.out.println(e.getMessage());
            System.out.println("Errore durante la connessione ");
            System.exit(1);
        }
        return mySocket;
    }

    public void communicate(){

        try {
            // INSERIRE NOME 
            System.out.println(inputThread.receive());

            message = keyboardInput.readLine(); // USERNAME

            outToServer.writeBytes(message + "\n");

            
            System.out.println(inputThread.receive());

            inputThread.start();


            while(true){

                // CHIEDE TIPO DI MESSAGGIO DA INVIARE
                message = keyboardInput.readLine();
                
                outToServer.writeBytes(message + "\n");
            
                if (message.equals("/dsn")) {
                    break;
                }
        
            }

            inputThread.join();
            closeConnection();
            
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.out.println("Errore durante la connessione con il server!");
            System.exit(1);
        }
    }

    public void closeConnection() throws IOException{
        outToServer.close();
        
        clientSocket.close();
    }

    public static void main(String[] args){
        Client client = new Client();
        client.connectToServer();
        client.communicate();
    }
}
