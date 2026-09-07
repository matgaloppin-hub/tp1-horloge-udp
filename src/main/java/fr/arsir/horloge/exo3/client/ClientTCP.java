package fr.arsir.horloge.exo3.client;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

public class ClientTCP {

    private static final String HOST = "localhost";
    private static final int PORT = 6666;

    public void dialoguer() {

        try {
            // 1 - Création du canal
            Socket socket = new Socket();

            InetSocketAddress adresseServeur =
                    new InetSocketAddress(HOST, PORT);

            // 2 - Connexion avec un délai d'attente de 5 secondes
            socket.connect(adresseServeur, 5000);

            ObjectOutputStream fluxSortie =
                    new ObjectOutputStream(socket.getOutputStream());

            ObjectInputStream fluxEntree =
                    new ObjectInputStream(socket.getInputStream());

            BufferedReader clavier =
                    new BufferedReader(new InputStreamReader(System.in));

            System.out.println("Connecté au serveur " + HOST + ":" + PORT);
            System.out.println("Commandes : DATE, HOUR, FULL, CLOSE");

            String requete;

            // 3 - Émettre et recevoir tant que l'utilisateur saisit une commande
            while ((requete = clavier.readLine()) != null) {

                fluxSortie.writeObject(requete);

                String reponse = (String) fluxEntree.readObject();

                System.out.println("Serveur : " + reponse);

                // fermeture demandée : on arrête le dialogue
                if (requete.trim().equalsIgnoreCase("CLOSE")) {
                    break;
                }
            }

            // 4 - Libérer le canal
            fluxEntree.close();
            fluxSortie.close();
            socket.close();

        } catch (Exception e) {
            System.err.println("Erreur client : " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        ClientTCP client = new ClientTCP();
        client.dialoguer();
    }
}
