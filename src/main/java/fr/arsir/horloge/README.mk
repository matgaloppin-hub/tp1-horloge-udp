###exercice 2 : Serveur Horloge UDP
Justification UDP :
UDP est utilisé car il est plus rapide et permet un échange direct de datagrammes, ce qui est adapté à la mesure des temps de transmission entre le client et le serveur.

#Exercice 5 : Q2
Q2 - Protocole (tic-tac-toe, TCP, 1 ligne = 1 message)

Client -> Serveur :
  COUP n     jouer la case n (1 a 9)
  QUITTER    abandonner

Serveur -> Client :
  BIENVENUE X|O    ton symbole
  DEBUT X|O        qui commence (tire au sort)
  GRILLE .........  etat de la grille (. = vide)
  ADVERSAIRE n     l'adversaire a joue la case n
  TON_TOUR         a toi de jouer
  INVALIDE         coup refuse, rejoue
  GAGNE/PERDU/NUL  resultat
  FIN              fin de la connexion

Principe :
  Le serveur gere le tour. Coup interdit (hors tour, case prise, hors 1-9) -> INVALIDE.
  Coup valide -> le serveur envoie GRILLE aux deux, puis ADVERSAIRE n + TON_TOUR a l'autre.
  Fin -> GAGNE / PERDU (ou NUL), puis FIN.