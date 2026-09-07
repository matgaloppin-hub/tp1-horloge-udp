package fr.arsir.horloge.exo1.service;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class HorlogeService {

    private static final DateTimeFormatter FORMAT_HEURE =
            DateTimeFormatter.ofPattern("HH:mm:ss");

    public String getHeureCourante() {
        return LocalTime.now().format(FORMAT_HEURE);
    }
}