package com.example;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class GestoreConnessioneClienteThread extends Thread {
    
    private Socket socket;
    private GestoreClienti gestoreClienti;
    private DataOutputStream outVersoCliente;
    private BufferedReader inDalCliente;

    private String nomeCliente;

    public GestoreConnessioneClienteThread(Socket socket, GestoreClienti gestoreClienti) throws IOException {
        this.socket = socket;
        this.gestoreClienti = gestoreClienti;
        this.outVersoCliente = new DataOutputStream(socket.getOutputStream());
        this.inDalCliente = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.nomeCliente = "";
    }

    public String getNomeCliente() {
        return nomeCliente;
    }

    @Override
    public void run() {
        // in attesa che il client mandi qualcosa
        try {

            effettuaLogin();
            inviaListaClientiConnessi();
            boolean connesso = true;
            while (connesso) {
                // scambiare messaggi
                String msg = this.inDalCliente.readLine();
                System.out.println("messaggio:" + msg);

                // devo capire che tipo di messaggio è
                String[] splitted = msg.split(" ");

                boolean result;

                switch (splitted[0]) {   
                    // se è un messaggio da inviare a tutti
                    case "/tutti":
                        msg = "[" + this.nomeCliente + "/TUTTI]" + msg.substring(6);
                        result = this.gestoreClienti.inviaMessaggioATutti(this.nomeCliente, msg);
                        if (!result) erroreNessunoConnesso();
                        else confermaMessaggio();      
                        break;

                    // se è un messaggio per chiudere la connessione 
                    case "/chiudi": 
                        connesso = false;
                        break;

                    // se è un messaggio da inviare a uno solo
                    default: 
                        String destinatario = splitted[0].substring(1);
                        msg = "[" + this.nomeCliente + "]" + msg.substring(msg.indexOf(" ") + 1);
                        result = this.gestoreClienti.inviaMessaggioAUno(this.nomeCliente, destinatario, msg);
                        if (!result) erroreDestinatarioNonTrovato();
                        else confermaMessaggio();        
                        break;
                }

            }

            disconnetti();
            this.gestoreClienti.esci(this.nomeCliente);
            spegni();

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    // Invia messaggio al cliente
    public void inviaMessaggio(String msg) throws IOException {
        this.outVersoCliente.writeBytes(msg + "\n");
    }

    // Chiusura connessione
    public void spegni() throws IOException {
        inDalCliente.close();
        outVersoCliente.close();
    }

    // Lista di tutti i clienti connessi in quel momento
    public void inviaListaClientiConnessi() throws IOException {
        String msg = "[SERVER] Lista clienti connessi:\n";
        inviaMessaggio(msg + this.gestoreClienti.elencoConnessi());
    }

    // Invia messaggio a tutti in caso di disconnessione di un cliente
    public void disconnetti() throws IOException {
        inviaMessaggio("ARRIVEDERCI!");
        this.gestoreClienti.inviaMessaggioATutti(this.nomeCliente, "[SERVER] " + this.nomeCliente + " si è disconnesso\n");
    }

    // Messaggio conferma
    public void confermaMessaggio() throws IOException {
        inviaMessaggio("[SERVER] Invio completato\n");
    }

    // Errore nome già presente
    public void erroreNome() throws IOException {
        inviaMessaggio("[SERVER] ERRORE: Nome già in uso. Riprova:");
    }

    // Tutte le azioni svolte nel login
    public void effettuaLogin() throws IOException {
        // chiedere di inviare un nome
        inviaMessaggio("[SERVER] USERNAME: ");

        // Controlla se il nome è già presente
        String nome;
        boolean trovato;

        do {
            nome = this.inDalCliente.readLine();
            trovato = this.gestoreClienti.controllaNome(nome); 
            if (trovato) {
                erroreNome();
            }
        } while (trovato);

        // Associa il nome a Cliente
        this.nomeCliente = nome;

        System.out.println(this.nomeCliente);
        inviaMessaggio("[SERVER] Login confermato\n");

        // Manda un messaggio a tutti di avvenuta connessione
        this.gestoreClienti.inviaMessaggioATutti(this.nomeCliente, "[SERVER] " + this.nomeCliente + " si è connesso\n");

        istruzioni();
    }

    // Istruzioni per la comunicazione
    public void istruzioni() throws IOException {
        inviaMessaggio("\n-ISTRUZIONI PER LA COMUNICAZIONE-");
        inviaMessaggio("/destinatario --> Messaggio singolo");
        inviaMessaggio("/tutti --> Messaggio a tutti");
        inviaMessaggio("/chiudi --> Chiusura connessione\n");

    }

    // errore in caso di mancanza del destinatario del messaggio
    public void erroreDestinatarioNonTrovato() throws IOException {
        inviaMessaggio("[SERVER] ERRORE: Destinatario non trovato!\n");
    }

    // errore in caso ci sia solo il cliente mittente connesso
    public void erroreNessunoConnesso() throws IOException {
        inviaMessaggio("[SERVER] ERRORE: Nessun altro è connesso!\n");
    }
}
