package fr.arsir.horloge.exo1.serveur;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;

import fr.arsir.horloge.exo1.service.HorlogeService;

public class ServeurUDP {

    private static final String HOST = "localhost";
    private static final int PORT = 6666;
    private static final int BUFFER_SIZE = 1024;

    private final HorlogeService horlogeService;

    public ServeurUDP() {
        this.horlogeService = new HorlogeService();
    }

    public void demarrer() {
        try {
            // 1 - Création du canal
            DatagramSocket socketServeur = new DatagramSocket(null);

            // 2 - Réservation du port
            InetSocketAddress adresse =
                    new InetSocketAddress(HOST, PORT);

            socketServeur.bind(adresse);

            System.out.println(
                    "Serveur démarré sur "
                            + HOST
                            + ":"
                            + PORT
            );

            byte[] recues = new byte[BUFFER_SIZE];
            byte[] envoyees;

            while (true) {

                // 3 - Recevoir
                DatagramPacket paquetRecu =
                        new DatagramPacket(recues, recues.length);

                socketServeur.receive(paquetRecu);

                String message = new String(
                        paquetRecu.getData(),
                        0,
                        paquetRecu.getLength()
                );

                System.out.println("Reçu : " + message);

                // Récupération de l'adresse du client
                InetAddress adrClient =
                        paquetRecu.getAddress();

                int prtClient =
                        paquetRecu.getPort();

                // 4 - Émettre
                String reponse =
                        horlogeService.getHeureCourante();

                envoyees = reponse.getBytes();

                DatagramPacket paquetEnvoye =
                        new DatagramPacket(
                                envoyees,
                                envoyees.length,
                                adrClient,
                                prtClient
                        );

                socketServeur.send(paquetEnvoye);

                System.out.println(
                        "Réponse envoyée : " + reponse
                );
            }

        } catch (Exception e) {
            System.err.println(
                    "Erreur serveur : " + e.getMessage()
            );
        }
    }

    public static void main(String[] args) {
        ServeurUDP serveur = new ServeurUDP();
        serveur.demarrer();
    }
}