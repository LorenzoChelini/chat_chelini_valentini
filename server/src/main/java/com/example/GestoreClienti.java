package com.example;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Classe che si preocccupa di smistare i messaggi ai client
 */
public class GestoreClienti {


    public ArrayList<GestoreConnessioneClienteThread> tuttiClientConnessi = new ArrayList<>();

    // AGGIUNGE UN CLIENT ALLA LISTA
    public void aggiungiCliente(GestoreConnessioneClienteThread thread) {
        this.tuttiClientConnessi.add(thread);
    }

    // INVIA MESSAGGIO A TUTTI 
    public boolean inviaMessaggioATutti(String daNomeCliente, String messaggio) {

        if (tuttiClientConnessi.size() == 1) return false;

        for (GestoreConnessioneClienteThread thread : tuttiClientConnessi) {
            if (!thread.getNomeCliente().equals(daNomeCliente))
                try {
                    thread.inviaMessaggio(messaggio + "\n");
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }

        return true;
    }

    // INVIA MESSAGGIO A UNA SOLA PERSONA TRAMITE CONTROLLO NOME
    public boolean inviaMessaggioAUno(String daNomeCliente, String aNomeCliente, String messaggio) {
        boolean trovato = false;
        for (GestoreConnessioneClienteThread thread : tuttiClientConnessi) {
            if (thread.getNomeCliente().equals(aNomeCliente))
                try {
                    trovato = true;
                    thread.inviaMessaggio(messaggio + "\n");
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
        return trovato;
    }

    // CHIUSURA CLIENT, CICLO PER TROVARE IL THREAD CORRISPONDENTE, CHIUDE
    public void esci(String nomeCliente) {
        for (GestoreConnessioneClienteThread thread : tuttiClientConnessi) {
            if (thread.getNomeCliente().equals(nomeCliente)) {
                tuttiClientConnessi.remove(thread);
                break;
            }
        }
    }

    public String elencoConnessi() {
        StringBuilder lista = new StringBuilder();
        int i = 0;
        for (GestoreConnessioneClienteThread thread : tuttiClientConnessi) {
            lista.append("- ").append(thread.getNomeCliente()).append(" -");
            i++;
            if (i == 3) {
                lista.append("\n");
                i = 0;
            }
        }
        lista.append("\n");
        return lista.toString();
    }

    public boolean controllaNome(String nome) {

        System.out.println("entrato con nome: " + nome);
        // Controllo se nome già presente
        for (GestoreConnessioneClienteThread thread : tuttiClientConnessi) {
            if (thread.getNomeCliente().equals(nome)) return true;
        }

        System.out.println("finito ciclo");
        return false;
    }
}
