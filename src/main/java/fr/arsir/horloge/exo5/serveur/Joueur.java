package fr.arsir.horloge.exo5.serveur;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import fr.arsir.horloge.exo5.jeu.Jeu;

// Q3 - un thread par client (cf. CM). Les deux Joueur partagent la même
// instance de Jeu, qui sert aussi de verrou pour sérialiser les coups.
public class Joueur extends Thread {

    private final Socket socketClient;
    private final Jeu jeu;
    private final char symbole;

    private final BufferedReader fluxEntree;
    private final PrintWriter fluxSortie;

    private Joueur adversaire;

    public Joueur(Socket socketClient, Jeu jeu, char symbole) throws Exception {

        this.socketClient = socketClient;
        this.jeu = jeu;
        this.symbole = symbole;

        // ouverture des flux du canal
        this.fluxEntree = new BufferedReader(
                new InputStreamReader(socketClient.getInputStream()));

        this.fluxSortie = new PrintWriter(
                socketClient.getOutputStream(), true);

        envoyer("BIENVENUE " + symbole);
    }

    public void setAdversaire(Joueur adversaire) {
        this.adversaire = adversaire;
    }

    // envoie une ligne de protocole au client
    public void envoyer(String message) {
        fluxSortie.println(message);
    }

    @Override
    public void run() {

        try {
            // 1 - Début de partie
            envoyer("DEBUT " + jeu.getJoueurCourant());
            envoyer("GRILLE " + jeu.grilleCompacte());

            if (jeu.getJoueurCourant() == symbole) {
                envoyer("TON_TOUR");
            }

            String ligne;

            // 2 - Recevoir les coups tant que la partie n'est pas finie
            //     (lecture hors du bloc synchronisé pour ne pas bloquer le jeu)
            while (!jeu.estTermine()
                    && (ligne = fluxEntree.readLine()) != null) {

                ligne = ligne.trim().toUpperCase();

                if (ligne.equals("QUITTER")) {
                    break;
                }

                if (ligne.startsWith("COUP")) {
                    traiterCoup(lireCase(ligne));
                } else {
                    envoyer("INVALIDE Commande inconnue (COUP <n> | QUITTER)");
                }
            }

            // 3 - Libérer le canal (fin normale ou abandon)
            if (!socketClient.isClosed()) {
                terminer();
            }

        } catch (Exception e) {
            if (!socketClient.isClosed()) {
                System.err.println(
                        "Erreur joueur " + symbole + " : " + e.getMessage());
            }
            fermer();
        }
    }

    // numéro de case d'un message "COUP n" (-1 si absent ou invalide)
    private int lireCase(String ligne) {
        try {
            return Integer.parseInt(ligne.substring(4).trim());
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    // 4 - Traitement d'un coup, sous le verrou du jeu partagé
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

            // partie finie : on ferme les deux canaux, ce qui débloque
            // le readLine() du thread adverse
            envoyer("FIN");
            adversaire.envoyer("FIN");
            adversaire.fermer();
            fermer();
        }
    }

    // prévient l'adversaire puis ferme les deux canaux
    private void terminer() {

        if (adversaire != null) {
            if (!jeu.estTermine()) {
                adversaire.envoyer("MESSAGE L'adversaire a quitté la partie.");
            }
            adversaire.envoyer("FIN");
            adversaire.fermer();
        }
        envoyer("FIN");
        fermer();
    }

    void fermer() {
        try {
            if (!socketClient.isClosed()) {
                socketClient.close();
            }
        } catch (Exception e) {
            // fermeture au mieux
        }
    }
}
