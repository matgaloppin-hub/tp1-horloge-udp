package fr.arsir.horloge.exo2.serveur;

import fr.arsir.horloge.exo2.service.HorlogeService;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;

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
            DatagramSocket socketServeur =
                    new DatagramSocket(null);

            // 2 - Réservation du port
            InetSocketAddress adresse =
                    new InetSocketAddress(HOST, PORT);

            socketServeur.bind(adresse);

            System.out.println(
                    "Serveur démarré sur "
                            + HOST + ":" + PORT
            );

            byte[] recues = new byte[BUFFER_SIZE];

            while (true) {

                // 3 - Recevoir
                DatagramPacket paquetRecu =
                        new DatagramPacket(
                                recues,
                                recues.length
                        );

                socketServeur.receive(paquetRecu);

                // T1' : heure de réception du paquet
                long T1Prime =
                        horlogeService.getHeureCourante();

                String message =
                        new String(
                                paquetRecu.getData(),
                                0,
                                paquetRecu.getLength()
                        );

                // T1 a été envoyé par le client
                long T1 = Long.parseLong(message);

                System.out.println("T1 reçu : " + T1);
                System.out.println("T1' : " + T1Prime);

                // T2' : heure juste avant l'envoi de la réponse
                long T2Prime =
                        horlogeService.getHeureCourante();

                // 4 - Émettre
                String reponse =
                        T1 + ";" + T1Prime + ";" + T2Prime;

                byte[] envoyees =
                        reponse.getBytes();

                InetAddress adrClient =
                        paquetRecu.getAddress();

                int prtClient =
                        paquetRecu.getPort();

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
