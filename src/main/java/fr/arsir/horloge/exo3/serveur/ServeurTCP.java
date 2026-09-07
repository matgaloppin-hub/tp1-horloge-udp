package fr.arsir.horloge.exo3.serveur;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import fr.arsir.horloge.exo3.service.HorlogeService;

public class ServeurTCP {

    private static final String HOST = "localhost";
    private static final int PORT = 6666;

    private final HorlogeService horlogeService;

    public ServeurTCP() {
        this.horlogeService = new HorlogeService();
    }

    public void demarrer() {

        try {
            // 1 - Création du canal et réservation du port
            ServerSocket socketServeur = new ServerSocket(PORT);

            System.out.println(
                    "Serveur démarré sur " + HOST + ":" + PORT);

            while (true) {

                // 2 - Mise en attente et acceptation de la connexion
                Socket socketClient = socketServeur.accept();

                System.out.println(
                        "Client connecté : " + socketClient.getInetAddress());

                // 3 - Ouverture des flux (entrée puis sortie)
                ObjectInputStream fluxEntree =
                        new ObjectInputStream(socketClient.getInputStream());

                ObjectOutputStream fluxSortie =
                        new ObjectOutputStream(socketClient.getOutputStream());

                // 4 - Émettre et recevoir tant que le client ne demande pas CLOSE
                boolean actif = true;

                while (actif) {

                    // requête envoyée par le client (DATE, HOUR, FULL ou CLOSE)
                    String requete = ((String) fluxEntree.readObject())
                            .trim().toUpperCase();

                    System.out.println("Reçu : " + requete);

                    String reponse;

                    switch (requete) {
                        case "DATE":
                            reponse = horlogeService.getDateCourante();
                            break;
                        case "HOUR":
                            reponse = horlogeService.getHeureCourante();
                            break;
                        case "FULL":
                            reponse = horlogeService.getDateEtHeure();
                            break;
                        case "CLOSE":
                            reponse = "BYE";
                            actif = false;
                            break;
                        default:
                            reponse = "Commande inconnue (DATE, HOUR, FULL, CLOSE)";
                    }

                    fluxSortie.writeObject(reponse);

                    System.out.println("Réponse envoyée : " + reponse);
                }

                // 5 - Libérer le canal
                fluxEntree.close();
                fluxSortie.close();
                socketClient.close();

                System.out.println("Connexion fermée");
            }

        } catch (Exception e) {
            System.err.println("Erreur serveur : " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        ServeurTCP serveur = new ServeurTCP();
        serveur.demarrer();
    }
}
