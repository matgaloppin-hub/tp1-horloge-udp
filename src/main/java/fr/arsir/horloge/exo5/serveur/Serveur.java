package fr.arsir.horloge.exo5.serveur;

import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;

import fr.arsir.horloge.exo5.jeu.Jeu;

public class Serveur {

    private static final String HOST = "localhost";
    private static final int PORT = 6666;

    public void demarrer() {

        try (ServerSocket serveurSocket = new ServerSocket()) {

            serveurSocket.bind(new InetSocketAddress(HOST, PORT));

            System.out.println(
                    "Serveur tic-tac-toe démarré sur " + HOST + ":" + PORT);

            Jeu jeu = new Jeu();

            System.out.println("En attente du joueur X...");
            Socket socketX = serveurSocket.accept();
            Joueur joueurX = new Joueur(socketX, jeu, 'X');
            System.out.println("Joueur X connecté. En attente du joueur O...");

            Socket socketO = serveurSocket.accept();
            Joueur joueurO = new Joueur(socketO, jeu, 'O');
            System.out.println("Joueur O connecté.");

            joueurX.setAdversaire(joueurO);
            joueurO.setAdversaire(joueurX);

            // tirage au sort du joueur qui commence
            char premier = new Random().nextBoolean() ? 'X' : 'O';
            jeu.setJoueurCourant(premier);
            System.out.println("Le joueur " + premier + " commence.");

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