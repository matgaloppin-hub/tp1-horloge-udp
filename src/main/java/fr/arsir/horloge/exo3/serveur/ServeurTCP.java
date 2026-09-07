package fr.arsir.horloge.exo3.serveur;

import fr.arsir.horloge.exo3.service.HorlogeService;

import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class ServeurTCP {

    private static final int PORT = 6666;

    private final HorlogeService horlogeService;

    public ServeurTCP() {
        this.horlogeService = new HorlogeService();
    }

    public void demarrer() {

        try {
            ServerSocket serveurSocket = new ServerSocket(PORT);

            System.out.println("Serveur TCP démarré sur le port " + PORT);

            Socket socketClient = serveurSocket.accept();

            System.out.println("Client connecté.");

            PrintWriter sortie =
                    new PrintWriter(socketClient.getOutputStream(), true);

            String heure = horlogeService.getHeureCourante();

            sortie.println(heure);

            System.out.println("Heure envoyée : " + heure);

            socketClient.close();
            serveurSocket.close();

            System.out.println("Connexion fermée.");

        } catch (Exception e) {
            System.err.println("Erreur serveur : " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        ServeurTCP serveur = new ServeurTCP();
        serveur.demarrer();
    }
}