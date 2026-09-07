package fr.arsir.horloge.exo5.serveur;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import fr.arsir.horloge.exo5.jeu.Jeu;

/**
 * Q3 - Un thread {@code Joueur} gère, côté serveur, la communication avec
 * un client. Les deux instances (X et O) partagent la même instance de
 * {@link Jeu}, qui sert aussi de verrou pour sérialiser les coups.
 *
 * Protocole (voir aussi le compte rendu, Q2) :
 *
 *   serveur -> client :
 *     BIENVENUE X|O        symbole attribué
 *     MESSAGE <texte>      information à afficher
 *     DEBUT X|O            la partie commence, ce symbole joue en premier
 *     GRILLE <9 car.>      état complet ('.' = case vide)
 *     ADVERSAIRE <1..9>    l'adversaire a joué cette case
 *     TON_TOUR             à toi de jouer, envoie COUP <n>
 *     INVALIDE <raison>    coup refusé, rejoue
 *     GAGNE | PERDU | NUL  fin de partie
 *     FIN                  le serveur ferme la connexion
 *
 *   client -> serveur :
 *     COUP <1..9>          jouer cette case
 *     QUITTER              abandonner
 */
public class Joueur extends Thread {

    private final Socket socket;
    private final Jeu jeu;
    private final char symbole;

    private final BufferedReader entree;
    private final PrintWriter sortie;

    private Joueur adversaire;

    public Joueur(Socket socket, Jeu jeu, char symbole) throws Exception {
        this.socket = socket;
        this.jeu = jeu;
        this.symbole = symbole;

        this.entree = new BufferedReader(
                new InputStreamReader(socket.getInputStream()));
        this.sortie = new PrintWriter(socket.getOutputStream(), true);

        envoyer("BIENVENUE " + symbole);
        envoyer("MESSAGE En attente d'un second joueur...");
    }

    public void setAdversaire(Joueur adversaire) {
        this.adversaire = adversaire;
    }

    /** Envoie une ligne de protocole au client de ce joueur. */
    public void envoyer(String message) {
        sortie.println(message);
    }

    @Override
    public void run() {

        try {
            envoyer("MESSAGE Les deux joueurs sont connectés.");
            envoyer("DEBUT " + jeu.getJoueurCourant());
            envoyer("GRILLE " + jeu.grilleCompacte());

            if (jeu.getJoueurCourant() == symbole) {
                envoyer("TON_TOUR");
            } else {
                envoyer("MESSAGE Au tour de l'adversaire.");
            }

            String ligne;

            // La lecture réseau se fait HORS du bloc synchronisé pour ne
            // pas garder le verrou du jeu pendant l'attente.
            while (!jeu.estTermine() && (ligne = entree.readLine()) != null) {

                ligne = ligne.trim();

                if (ligne.equalsIgnoreCase("QUITTER")) {
                    abandon();
                    return;
                }

                if (ligne.toUpperCase().startsWith("COUP")) {
                    traiterCoup(lireCase(ligne));
                } else {
                    envoyer("INVALIDE Commande inconnue (COUP <n> | QUITTER)");
                }
            }

            // Sortie de boucle : soit la partie est finie, soit le client
            // a coupé la connexion.
            if (!jeu.estTermine()) {
                abandon();
            } else {
                fermerSocket();
            }

        } catch (Exception e) {
            if (!socket.isClosed()) {
                System.err.println("Joueur " + symbole + " : " + e.getMessage());
            }
            fermerSocket();
        }
    }

    /** Extrait le numéro de case d'un message "COUP n" (-1 si invalide). */
    private int lireCase(String ligne) {
        try {
            return Integer.parseInt(ligne.substring(4).trim());
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private void traiterCoup(int caseJeu) {

        synchronized (jeu) {

            if (!jeu.coupValide(caseJeu, symbole)) {
                envoyer("INVALIDE Coup refusé "
                        + "(pas votre tour, case occupée ou hors limites)");
                return;
            }

            jeu.jouer(caseJeu, symbole);

            System.out.println("Joueur " + symbole + " joue en " + caseJeu);
            System.out.print(jeu.grilleAffichee());

            String grille = "GRILLE " + jeu.grilleCompacte();
            envoyer(grille);
            adversaire.envoyer(grille);
            adversaire.envoyer("ADVERSAIRE " + caseJeu);

            if (jeu.aGagne(symbole)) {
                envoyer("GAGNE");
                adversaire.envoyer("PERDU");
            } else if (jeu.grillePleine()) {
                envoyer("NUL");
                adversaire.envoyer("NUL");
            } else {
                adversaire.envoyer("TON_TOUR");
                return;
            }

            // Partie terminée : on ferme les deux connexions, ce qui
            // débloque le thread de l'adversaire (readLine -> null).
            envoyer("FIN");
            adversaire.envoyer("FIN");
            adversaire.fermerSocket();
            fermerSocket();
        }
    }

    /** Ce joueur quitte : on prévient l'adversaire et on ferme tout. */
    private void abandon() {
        if (adversaire != null) {
            adversaire.envoyer("MESSAGE L'adversaire a quitté la partie.");
            adversaire.envoyer("FIN");
            adversaire.fermerSocket();
        }
        envoyer("FIN");
        fermerSocket();
    }

    void fermerSocket() {
        try {
            if (!socket.isClosed()) {
                socket.close();
            }
        } catch (Exception e) {
            // fermeture au mieux
        }
    }
}
