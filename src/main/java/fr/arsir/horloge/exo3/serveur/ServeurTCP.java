package fr.arsir.horloge.exo3.serveur;

import fr.arsir.horloge.exo3.service.HorlogeService;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class ServeurTCP {

    private static final String HOST = "localhost";
    private static final int PORT = 6666;

    private final HorlogeService horlogeService;

    public ServeurTCP() {
        this.horlogeService = new HorlogeService();
    }

    public void demarrer() {

        // 1 - Création du canal d'écoute + réservation du port
        try (ServerSocket serveurSocket = new ServerSocket()) {

            serveurSocket.bind(new InetSocketAddress(HOST, PORT));

            System.out.println(
                    "Serveur TCP démarré sur " + HOST + ":" + PORT);

            // Le serveur reste à l'écoute et sert les clients l'un
            // après l'autre.
            while (true) {

                // 2 - Mise en attente / acceptation d'une connexion
                Socket socketClient = serveurSocket.accept();

                System.out.println(
                        "Client connecté : "
                                + socketClient.getRemoteSocketAddress());

                traiterClient(socketClient);
            }

        } catch (Exception e) {
            System.err.println("Erreur serveur : " + e.getMessage());
        }
    }

    /**
     * Dialogue avec un client : on lit ses requêtes ligne par ligne
     * (DATE, HOUR, FULL) jusqu'à CLOSE ou jusqu'à ce qu'il coupe la
     * connexion.
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

                String reponse;

                switch (requete) {
                    case "DATE":
                        reponse = horlogeService.getDateCourante();
                        break;
                    case "HOUR":
                        reponse = horlogeService.getHeureCourante();
                        break;
                    case "FULL":
                        reponse = horlogeService.getDateHeureCourante();
                        break;
                    default:
                        reponse = "ERREUR : commande inconnue "
                                + "(DATE | HOUR | FULL | CLOSE)";
                }

                // 4 - Émission de la réponse
                sortie.println(reponse);

                System.out.println("Réponse envoyée : " + reponse);
            }

        } catch (Exception e) {
            System.err.println("Erreur client : " + e.getMessage());
        }

        // 5 - La socket est fermée automatiquement (try-with-resources)
        System.out.println("Connexion fermée.\n");
    }

    public static void main(String[] args) {
        ServeurTCP serveur = new ServeurTCP();
        serveur.demarrer();
    }
}
