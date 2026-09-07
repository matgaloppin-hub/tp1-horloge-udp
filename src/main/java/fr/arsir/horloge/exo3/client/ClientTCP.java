package fr.arsir.horloge.exo3.client;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;

public class ClientTCP {

    private static final String HOST = "localhost";
    private static final int PORT = 6666;

    public void demanderHeure() {

        try {
            Socket socket = new Socket(HOST, PORT);

            System.out.println("Connecté au serveur.");

            BufferedReader entree =
                    new BufferedReader(
                            new InputStreamReader(
                                    socket.getInputStream()
                            )
                    );

            String heure = entree.readLine();

            System.out.println("Heure reçue du serveur : " + heure);

            socket.close();

        } catch (Exception e) {
            System.err.println("Erreur client : " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        ClientTCP client = new ClientTCP();
        client.demanderHeure();
    }
}