package fr.arsir.horloge.exo5.serveur;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import fr.arsir.horloge.exo5.jeu.Jeu;


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

            // readLine() reste hors du synchronized, sinon on bloque le jeu
            // pendant qu'on attend le réseau
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

            // on sort soit parce que la partie est finie, soit parce que
            // le client a fermé sa connexion
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

            // ça ferme les deux sockets, ce qui débloque le readLine
            // de l'adversaire côté thread
            envoyer("FIN");
            adversaire.envoyer("FIN");
            adversaire.fermerSocket();
            fermerSocket();
        }
    }

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
            // pas grave si ça échoue, on ferme quand même
        }
    }
}