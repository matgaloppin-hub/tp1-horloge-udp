package fr.arsir.horloge.exo3.serveur;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

import fr.arsir.horloge.exo3.service.HorlogeService;

/**
 * Serveur de l'horloge parlante en TCP (exercice 3).
 *
 * Q1 : dès qu'une connexion est acceptée, le serveur envoie l'heure
 *      courante puis ferme la socket.
 * Q2 : le client peut envoyer DATE, HOUR ou FULL autant de fois qu'il
 *      le souhaite ; la connexion ne se ferme que sur réception de CLOSE.
 *
 * Ce fichier implémente la version Q2 (qui englobe Q1).
 */
public class ServeurTCP {

    private static final String HOST = "localhost";
    private static final int PORT = 6666;

    private final HorlogeService horlogeService;

    public ServeurTCP() {
        this.horlogeService = new HorlogeService();
    }

    public void demarrer() {

        // 1 - Création du canal d'écoute + réservation du port
        try (ServerSocket socketEcoute = new ServerSocket()) {

            socketEcoute.bind(new InetSocketAddress(HOST, PORT));

            System.out.println(
                    "Serveur TCP démarré sur " + HOST + ":" + PORT
            );

            // Le serveur tourne indéfiniment et sert les clients
            // les uns après les autres (mono-thread).
            while (true) {

                // 2 - Acceptation d'une connexion (bloquant)
                //     accept() renvoie une NOUVELLE socket dédiée
                //     au dialogue avec ce client.
                Socket socketClient = socketEcoute.accept();

                System.out.println(
                        "Client connecté : "
                                + socketClient.getRemoteSocketAddress()
                );

                traiterClient(socketClient);
            }

        } catch (Exception e) {
            System.err.println("Erreur serveur : " + e.getMessage());
        }
    }

    /**
     * Dialogue complet avec un client :
     * on lit ses requêtes ligne par ligne jusqu'à CLOSE
     * (ou jusqu'à ce qu'il coupe la connexion).
     */
    private void traiterClient(Socket socketClient) {

        try (Socket socket = socketClient;
             BufferedReader entree = new BufferedReader(
                     new InputStreamReader(socket.getInputStream()));
             PrintWriter sortie = new PrintWriter(
                     socket.getOutputStream(), true)) {

            String requete;

            // readLine() renvoie null quand le client ferme la connexion
            while ((requete = entree.readLine()) != null) {

                requete = requete.trim().toUpperCase();
                System.out.println("Reçu : " + requete);

                // 3 - Traitement de la requête
                if (requete.equals("CLOSE")) {
                    sortie.println("BYE");
                    System.out.println("Fermeture demandée par le client");
                    break;
                }

                String reponse = switch (requete) {
                    case "DATE" -> horlogeService.getDate();
                    case "HOUR" -> horlogeService.getHeure();
                    case "FULL" -> horlogeService.getDateHeure();
                    default     -> "ERREUR : commande inconnue "
                            + "(DATE | HOUR | FULL | CLOSE)";
                };

                // 4 - Émission de la réponse
                sortie.println(reponse);
                System.out.println("Réponse envoyée : " + reponse);
            }

        } catch (Exception e) {
            System.err.println("Erreur client : " + e.getMessage());
        }
        // 5 - La socket est fermée automatiquement (try-with-resources)
        System.out.println("Connexion terminée\n");
    }

    public static void main(String[] args) {
        new ServeurTCP().demarrer();
    }
}
