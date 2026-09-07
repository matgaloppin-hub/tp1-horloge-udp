package fr.arsir.horloge.exo3.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * Service métier de l'horloge parlante (exercice 3).
 *
 * Il ne connaît rien au réseau : il se contente de fournir,
 * sous forme de texte, la date, l'heure ou les deux.
 */
public class HorlogeService {

    private static final DateTimeFormatter FORMAT_DATE =
            DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private static final DateTimeFormatter FORMAT_HEURE =
            DateTimeFormatter.ofPattern("HH:mm:ss");

    /** Date du jour, ex : 07/09/2026 */
    public String getDate() {
        return LocalDate.now().format(FORMAT_DATE);
    }

    /** Heure courante, ex : 14:53:07 */
    public String getHeure() {
        return LocalTime.now().format(FORMAT_HEURE);
    }

    /** Date + heure, ex : 07/09/2026 14:53:07 */
    public String getDateHeure() {
        LocalDateTime maintenant = LocalDateTime.now();
        return maintenant.format(FORMAT_DATE)
                + " "
                + maintenant.format(FORMAT_HEURE);
    }
}
