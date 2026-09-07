package fr.arsir.horloge.exo1.client;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class ClientUDP {

    private static final String HOST = "localhost";
    private static final int PORT = 6666;
    private static final int BUFFER_SIZE = 1024;

    public void demanderHeure() {

        try {
            // 1 - Création du canal
            DatagramSocket socketClient =
                    new DatagramSocket();

            InetAddress adresseServeur =
                    InetAddress.getByName(HOST);

            byte[] envoyees;
            byte[] recues = new byte[BUFFER_SIZE];

            // 2 - Émettre
            String message = "HEURE";

            envoyees = message.getBytes();

            DatagramPacket messageEnvoye =
                    new DatagramPacket(
                            envoyees,
                            envoyees.length,
                            adresseServeur,
                            PORT
                    );

            socketClient.send(messageEnvoye);

            // 3 - Recevoir
            DatagramPacket paquetRecu =
                    new DatagramPacket(
                            recues,
                            recues.length
                    );

            socketClient.receive(paquetRecu);

            String reponse =
                    new String(
                            paquetRecu.getData(),
                            0,
                            paquetRecu.getLength()
                    );

            System.out.println(
                    "Heure du serveur : " + reponse
            );

            // 4 - Libérer le canal
            socketClient.close();

        } catch (Exception e) {
            System.err.println(
                    "Erreur client : " + e.getMessage()
            );
        }
    }

    public static void main(String[] args) {
        ClientUDP client = new ClientUDP();
        client.demanderHeure();
    }
}