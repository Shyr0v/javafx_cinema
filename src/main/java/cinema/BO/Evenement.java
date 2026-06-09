package cinema.BO;

import cinema.BO.Cinema;

import java.time.LocalDate;

public class Evenement {
    private int id;
    private String nom;
    private LocalDate date;
    private int nbrPlace;
    private boolean gratuit;
    private Cinema cinema; // lien vers le cinéma

    public Evenement() {}

    public Evenement(String nom, LocalDate date, int nbrPlace, boolean gratuit, Cinema cinema) {
        this.nom = nom;
        this.date = date;
        this.nbrPlace = nbrPlace;
        this.gratuit = gratuit;
        this.cinema = cinema;
    }

    // Getters / Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public int getNbrPlace() { return nbrPlace; }
    public void setNbrPlace(int nbrPlace) { this.nbrPlace = nbrPlace; }

    public boolean isGratuit() { return gratuit; }
    public void setGratuit(boolean gratuit) { this.gratuit = gratuit; }

    public Cinema getCinema() { return cinema; }
    public void setCinema(Cinema cinema) { this.cinema = cinema; }
}