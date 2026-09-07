package fr.arsir.horloge.exo5.serveur;

import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;

import fr.arsir.horloge.exo5.jeu.Jeu;

/**
 * Q4 - Serveur du tic-tac-toe.
 *
 * La méthode principale :
 *   1. crée une instance de {@link Jeu} ;
 *   2. accepte la connexion de deux clients ;
 *   3. choisit aléatoirement le joueur qui commence ;
 *   4. lance les deux threads {@link Joueur} et attend la fin de la partie.
 *
 * Test possible avec netcat, dans deux terminaux :
 *   nc localhost 6666
 */
public class Serveur {

    private static final String HOST = "localhost";
    private static final int PORT = 6666;

    public void demarrer() {

        try (ServerSocket serveurSocket = new ServerSocket()) {

            serveurSocket.bind(new InetSocketAddress(HOST, PORT));

            System.out.println(
                    "Serveur tic-tac-toe démarré sur " + HOST + ":" + PORT);

            // 1 - État de la partie
            Jeu jeu = new Jeu();

            // 2 - Connexion des deux joueurs
            System.out.println("En attente du joueur X...");
            Socket socketX = serveurSocket.accept();
            Joueur joueurX = new Joueur(socketX, jeu, 'X');
            System.out.println("Joueur X connecté. En attente du joueur O...");

            Socket socketO = serveurSocket.accept();
            Joueur joueurO = new Joueur(socketO, jeu, 'O');
            System.out.println("Joueur O connecté.");

            joueurX.setAdversaire(joueurO);
            joueurO.setAdversaire(joueurX);

            // 3 - Choix aléatoire du joueur qui commence
            char premier = new Random().nextBoolean() ? 'X' : 'O';
            jeu.setJoueurCourant(premier);
            System.out.println("Le joueur " + premier + " commence.");

            // 4 - Lancement des threads et attente de la fin de partie
            joueurX.start();
            joueurO.start();

            joueurX.join();
            joueurO.join();

            char gagnant = jeu.getGagnant();
            if (gagnant != Jeu.VIDE) {
                System.out.println("Partie terminée : victoire de " + gagnant);
            } else {
                System.out.println("Partie terminée.");
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
