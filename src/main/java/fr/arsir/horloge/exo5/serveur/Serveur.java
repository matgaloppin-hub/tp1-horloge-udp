package fr.arsir.horloge.exo5.serveur;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;

import fr.arsir.horloge.exo5.jeu.Jeu;

public class Serveur {

    private static final String HOST = "localhost";
    private static final int PORT = 6666;

    public void demarrer() {

        try {
            // 1 - Création du canal et réservation du port
            ServerSocket socketServeur = new ServerSocket(PORT);

            System.out.println(
                    "Serveur tic-tac-toe démarré sur " + HOST + ":" + PORT);

            // 2 - État de la partie
            Jeu jeu = new Jeu();

            // 3 - Connexion des deux joueurs
            System.out.println("En attente du joueur X...");
            Socket socketX = socketServeur.accept();
            Joueur joueurX = new Joueur(socketX, jeu, 'X');

            System.out.println("En attente du joueur O...");
            Socket socketO = socketServeur.accept();
            Joueur joueurO = new Joueur(socketO, jeu, 'O');

            joueurX.setAdversaire(joueurO);
            joueurO.setAdversaire(joueurX);

            // 4 - Tirage au sort du joueur qui commence
            char premier = new Random().nextBoolean() ? 'X' : 'O';
            jeu.setJoueurCourant(premier);
            System.out.println("Le joueur " + premier + " commence");

            // 5 - Lancement des deux threads et attente de la fin de partie
            joueurX.start();
            joueurO.start();
            joueurX.join();
            joueurO.join();

            // 6 - Libérer le canal
            socketServeur.close();

            char gagnant = jeu.getGagnant();
            if (gagnant != Jeu.VIDE) {
                System.out.println("Partie terminée : victoire de " + gagnant);
            } else {
                System.out.println("Partie terminée");
            }

        } catch (Exception e) {
            System.err.println("Erreur serveur : " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        Serveur serveur = new Serveur();
        serveur.demarrer();
    }
}
