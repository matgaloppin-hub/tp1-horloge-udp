package fr.arsir.horloge.exo4.client;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientTCP {

    private static final String HOST = "localhost";
    private static final int PORT = 6666;

    public void demarrer() {

        try {
            Socket socket = new Socket(HOST, PORT);

            System.out.println("Connecté au serveur.");
            System.out.println("Entrez du texte :");

            BufferedReader clavier =
                    new BufferedReader(
                            new InputStreamReader(System.in)
                    );

            BufferedReader entree =
                    new BufferedReader(
                            new InputStreamReader(
                                    socket.getInputStream()
                            )
                    );

            PrintWriter sortie =
                    new PrintWriter(
                            socket.getOutputStream(),
                            true
                    );

            String ligne;

            while ((ligne = clavier.readLine()) != null) {

                sortie.println(ligne);

                String reponse = entree.readLine();

                System.out.println("Serveur : " + reponse);
            }

            socket.close();

        } catch (Exception e) {
            System.err.println("Erreur client : " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        ClientTCP client = new ClientTCP();
        client.demarrer();
    }
}