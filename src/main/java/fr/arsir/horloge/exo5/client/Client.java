package fr.arsir.horloge.exo5.client;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Client {

    private static final String HOST = "localhost";
    private static final int PORT = 6666;

    private char monSymbole = '?';
    private final char[] grille = "........."
            .toCharArray();

    public void jouer() {

        try (Socket socket = new Socket(HOST, PORT);
             BufferedReader serveur = new BufferedReader(
                     new InputStreamReader(socket.getInputStream()));
             PrintWriter versServeur = new PrintWriter(
                     socket.getOutputStream(), true);
             BufferedReader clavier = new BufferedReader(
                     new InputStreamReader(System.in))) {

            String ligne;

            while ((ligne = serveur.readLine()) != null) {

                String[] parts = ligne.split(" ", 2);
                String commande = parts[0];
                String argument = (parts.length > 1) ? parts[1] : "";

                switch (commande) {

                    case "BIENVENUE":
                        monSymbole = argument.charAt(0);
                        System.out.println("Vous jouez les « " + monSymbole + " »");
                        break;

                    case "MESSAGE":
                        System.out.println("[serveur] " + argument);
                        break;

                    case "DEBUT":
                        System.out.println("La partie commence, "
                                + argument + " joue en premier.");
                        break;

                    case "GRILLE":
                        majGrille(argument);
                        afficherGrille();
                        break;

                    case "ADVERSAIRE":
                        System.out.println("L'adversaire a joué la case " + argument + ".");
                        break;

                    case "TON_TOUR":
                        envoyerCoup(clavier, versServeur);
                        break;

                    case "INVALIDE":
                        System.out.println("Coup refusé : " + argument);
                        envoyerCoup(clavier, versServeur);
                        break;

                    case "GAGNE":
                        System.out.println("Vous avez GAGNÉ ! ");
                        break;

                    case "PERDU":
                        System.out.println("Vous avez perdu.");
                        break;

                    case "NUL":
                        System.out.println("Match nul.");
                        break;

                    case "FIN":
                        System.out.println("Fin de la partie. Déconnexion.");
                        return;

                    default:
                        System.out.println("[?] " + ligne);
                }
            }

        } catch (Exception e) {
            System.err.println("Erreur client : " + e.getMessage());
        }
    }

    private void majGrille(String compacte) {
        for (int i = 0; i < 9 && i < compacte.length(); i++) {
            grille[i] = compacte.charAt(i);
        }
    }

    private void afficherGrille() {
        System.out.println();
        for (int ligne = 0; ligne < 3; ligne++) {
            StringBuilder sb = new StringBuilder();
            for (int colonne = 0; colonne < 3; colonne++) {
                int i = ligne * 3 + colonne;
                char c = grille[i];
                sb.append(' ')
                        .append(c == '.' ? (char) ('1' + i) : c)
                        .append(' ');
                if (colonne < 2) {
                    sb.append('|');
                }
            }
            System.out.println(sb);
            if (ligne < 2) {
                System.out.println("---+---+---");
            }
        }
        System.out.println();
    }

    private void envoyerCoup(BufferedReader clavier, PrintWriter versServeur)
            throws Exception {

        while (true) {
            System.out.print("Votre coup (1-9, ou 'q' pour abandonner) : ");
            String saisie = clavier.readLine();

            if (saisie == null || saisie.trim().equalsIgnoreCase("q")) {
                versServeur.println("QUITTER");
                return;
            }

            saisie = saisie.trim();

            try {
                int caseJeu = Integer.parseInt(saisie);
                if (caseJeu >= 1 && caseJeu <= 9) {
                    versServeur.println("COUP " + caseJeu);
                    return;
                }
            } catch (NumberFormatException e) {
                // saisie pas valide, on reboucle
            }
            System.out.println("Entrée invalide, tapez un chiffre entre 1 et 9.");
        }
    }

    public static void main(String[] args) {
        Client client = new Client();
        client.jouer();
    }
}