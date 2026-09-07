package fr.arsir.horloge.exo4.serveur;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

import fr.arsir.horloge.exo4.service.Service;

public class ServeurTCP {

    private static final int PORT = 6666;

    private final Service service;

    public ServeurTCP() {
        this.service = new Service();
    }

    public void demarrer() {

        try {
            ServerSocket serveurSocket = new ServerSocket(PORT);

            System.out.println("Serveur TCP démarré sur le port " + PORT);

            Socket socketClient = serveurSocket.accept();

            System.out.println("Client connecté.");

            BufferedReader entree =
                    new BufferedReader(
                            new InputStreamReader(
                                    socketClient.getInputStream()
                            )
                    );

            PrintWriter sortie =
                    new PrintWriter(
                            socketClient.getOutputStream(),
                            true
                    );

            String ligne;

            while ((ligne = entree.readLine()) != null) {

                String resultat = service.capitaliser(ligne);

                sortie.println(resultat);

                System.out.println("Reçu : " + ligne);
                System.out.println("Envoyé : " + resultat);
            }

            socketClient.close();
            serveurSocket.close();

        } catch (Exception e) {
            System.err.println("Erreur serveur : " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        ServeurTCP serveur = new ServeurTCP();
        serveur.demarrer();
    }
}