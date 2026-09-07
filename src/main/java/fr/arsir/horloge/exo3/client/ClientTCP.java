package fr.arsir.horloge.exo3.client;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientTCP {

    private static final String HOST = "localhost";
    private static final int PORT = 6666;

    public void dialoguer() {

        // 1 - Création du canal + demande de connexion
        try (Socket socket = new Socket(HOST, PORT);
             BufferedReader reseauEntree = new BufferedReader(
                     new InputStreamReader(socket.getInputStream()));
             PrintWriter reseauSortie = new PrintWriter(
                     socket.getOutputStream(), true);
             BufferedReader clavier = new BufferedReader(
                     new InputStreamReader(System.in))) {

            System.out.println(
                    "Connecté au serveur " + HOST + ":" + PORT);
            System.out.println(
                    "Commandes : DATE | HOUR | FULL | CLOSE");

            String saisie;

            while (true) {

                System.out.print("> ");

                saisie = clavier.readLine();

                if (saisie == null) {
                    saisie = "CLOSE";
                }

                saisie = saisie.trim();

                if (saisie.isEmpty()) {
                    continue;
                }

                // 2 - Émettre la requête
                reseauSortie.println(saisie);

                // 3 - Recevoir la réponse
                String reponse = reseauEntree.readLine();

                System.out.println("Serveur : " + reponse);

                // 4 - Fin du dialogue
                if (saisie.equalsIgnoreCase("CLOSE") || reponse == null) {
                    break;
                }
            }

        } catch (Exception e) {
            System.err.println("Erreur client : " + e.getMessage());
        }

        // 5 - La socket est fermée automatiquement (try-with-resources)
        System.out.println("Déconnecté.");
    }

    public static void main(String[] args) {
        ClientTCP client = new ClientTCP();
        client.dialoguer();
    }
}
