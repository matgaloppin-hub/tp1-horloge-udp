package fr.arsir.horloge.exo3.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class HorlogeService {

    private static final DateTimeFormatter FORMAT_DATE =
            DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private static final DateTimeFormatter FORMAT_HEURE =
            DateTimeFormatter.ofPattern("HH:mm:ss");

    public String getDateCourante() {
        return LocalDate.now().format(FORMAT_DATE);
    }

    public String getHeureCourante() {
        return LocalTime.now().format(FORMAT_HEURE);
    }

    public String getDateHeureCourante() {
        LocalDateTime maintenant = LocalDateTime.now();

        return maintenant.format(FORMAT_DATE)
                + " "
                + maintenant.format(FORMAT_HEURE);
    }
}
