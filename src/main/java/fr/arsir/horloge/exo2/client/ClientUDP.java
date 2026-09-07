package fr.arsir.horloge.exo2.client;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class ClientUDP {

        private static final String HOST = "localhost";
        private static final int PORT = 6666;
        private static final int BUFFER_SIZE = 1024;

        public void synchroniser() {

                try {
                        // 1 - Création du canal
                        DatagramSocket socketClient = new DatagramSocket();

                        InetAddress adresseServeur = InetAddress.getByName(HOST);

                        byte[] envoyees;
                        byte[] recues = new byte[BUFFER_SIZE];

                        // T1 : heure du client avant l'envoi
                        long T1 = System.currentTimeMillis();

                        // 2 - Émettre
                        String message = String.valueOf(T1);

                        envoyees = message.getBytes();

                        DatagramPacket messageEnvoye = new DatagramPacket(
                                        envoyees,
                                        envoyees.length,
                                        adresseServeur,
                                        PORT);

                        socketClient.send(messageEnvoye);

                        // 3 - Recevoir
                        DatagramPacket paquetRecu = new DatagramPacket(
                                        recues,
                                        recues.length);

                        socketClient.receive(paquetRecu);

                        // T2 : heure du client après réception
                        long T2 = System.currentTimeMillis();

                        String reponse = new String(
                                        paquetRecu.getData(),
                                        0,
                                        paquetRecu.getLength());

                        System.out.println(
                                        "Réponse du serveur : " + reponse);

                        // Récupération de T1', T2'
                        String[] valeurs = reponse.split(";");

                        long T1Prime = Long.parseLong(valeurs[1]);

                        long T2Prime = Long.parseLong(valeurs[2]);

                        // Calcul du délai réseau aller-retour
                        long delta = (T2 - T1)
                                        - (T2Prime - T1Prime);

                        // Calcul du décalage entre les horloges
                        double theta = ((T1Prime + T2Prime) / 2.0)
                                        - ((T1 + T2) / 2.0);

                        // Affichage des résultats
                        System.out.println("Delta = " + delta + " ms");
                        System.out.println("Theta = " + theta + " ms");

                        // Heure locale avant correction
                        long heureLocale = System.currentTimeMillis();

                        // Correction de l'horloge avec θ
                        double heureCorrigee = heureLocale + theta;

                        System.out.println(
                                        "Heure locale : "
                                                        + heureLocale
                                                        + " ms");

                        System.out.println(
                                        "Heure corrigée : "
                                                        + heureCorrigee
                                                        + " ms");

                        // 4 - Libérer le canal
                        socketClient.close();

                } catch (Exception e) {
                        System.err.println(
                                        "Erreur client : " + e.getMessage());
                }
        }

        public static void main(String[] args) {
                ClientUDP client = new ClientUDP();
                client.synchroniser();
        }
}
