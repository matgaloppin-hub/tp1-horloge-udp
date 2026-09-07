package fr.arsir.horloge.exo5.jeu;

import java.util.Arrays;

/**
 * Q1 - État d'une partie de tic-tac-toe.
 *
 * La classe ne connaît rien au réseau : elle mémorise la grille, sait
 * quel joueur ('X' ou 'O') doit jouer, teste la validité d'un coup et
 * détecte la fin de partie.
 *
 * Les cases sont numérotées de 1 à 9 :
 *
 *      1 | 2 | 3
 *     ---+---+---
 *      4 | 5 | 6
 *     ---+---+---
 *      7 | 8 | 9
 *
 * Toutes les méthodes sont {@code synchronized} : la grille est partagée
 * par les deux threads {@code Joueur} du serveur.
 */
public class Jeu {

    public static final char VIDE = '.';

    /** Les 8 alignements gagnants (indices 0..8). */
    private static final int[][] ALIGNEMENTS = {
            {0, 1, 2}, {3, 4, 5}, {6, 7, 8}, // lignes
            {0, 3, 6}, {1, 4, 7}, {2, 5, 8}, // colonnes
            {0, 4, 8}, {2, 4, 6}             // diagonales
    };

    private final char[] grille = new char[9];

    private char joueurCourant;

    public Jeu() {
        Arrays.fill(grille, VIDE);
        joueurCourant = 'X';
    }

    public synchronized char getJoueurCourant() {
        return joueurCourant;
    }

    public synchronized void setJoueurCourant(char symbole) {
        joueurCourant = symbole;
    }

    /**
     * Un coup est valide si la partie n'est pas finie, si c'est bien au
     * tour de {@code symbole}, si la case est dans [1, 9] et si elle est
     * libre.
     */
    public synchronized boolean coupValide(int caseJeu, char symbole) {
        if (estTermine()) {
            return false;
        }
        if (symbole != joueurCourant) {
            return false;
        }
        if (caseJeu < 1 || caseJeu > 9) {
            return false;
        }
        return grille[caseJeu - 1] == VIDE;
    }

    /**
     * Joue le coup (supposé valide) et passe la main à l'autre joueur.
     */
    public synchronized void jouer(int caseJeu, char symbole) {
        grille[caseJeu - 1] = symbole;
        joueurCourant = (symbole == 'X') ? 'O' : 'X';
    }

    public synchronized boolean aGagne(char symbole) {
        for (int[] a : ALIGNEMENTS) {
            if (grille[a[0]] == symbole
                    && grille[a[1]] == symbole
                    && grille[a[2]] == symbole) {
                return true;
            }
        }
        return false;
    }

    public synchronized boolean grillePleine() {
        for (char c : grille) {
            if (c == VIDE) {
                return false;
            }
        }
        return true;
    }

    public synchronized boolean estTermine() {
        return aGagne('X') || aGagne('O') || grillePleine();
    }

    /** Retourne 'X', 'O', ou {@link #VIDE} s'il n'y a pas (encore) de gagnant. */
    public synchronized char getGagnant() {
        if (aGagne('X')) {
            return 'X';
        }
        if (aGagne('O')) {
            return 'O';
        }
        return VIDE;
    }

    /** État de la grille sur une seule ligne, ex : "X.O..X..O". */
    public synchronized String grilleCompacte() {
        return new String(grille);
    }

    /** Représentation lisible de la grille (pour la console / nc). */
    public synchronized String grilleAffichee() {
        StringBuilder sb = new StringBuilder();

        for (int ligne = 0; ligne < 3; ligne++) {
            for (int colonne = 0; colonne < 3; colonne++) {
                int i = ligne * 3 + colonne;
                char affichage = (grille[i] == VIDE)
                        ? (char) ('1' + i)
                        : grille[i];
                sb.append(' ').append(affichage).append(' ');
                if (colonne < 2) {
                    sb.append('|');
                }
            }
            sb.append('\n');
            if (ligne < 2) {
                sb.append("---+---+---\n");
            }
        }
        return sb.toString();
    }
}
