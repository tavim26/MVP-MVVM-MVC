package Model;

import java.util.ArrayList;
import java.util.List;

public class Artist {

    private String name;
    private String birthDate;
    private String birthPlace;
    private String nationality;

    private List<Artwork> artworks;


    public Artist(String name, String birthDate, String birthPlace, String nationality) {
        this.name = name;
        this.birthDate = birthDate;
        this.birthPlace = birthPlace;
        this.nationality = nationality;
        this.artworks = new ArrayList<>();
    }

    // Getteri și Setteri
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getBirthDate() { return birthDate; }
    public void setBirthDate(String birthDate) { this.birthDate = birthDate; }

    public String getBirthPlace() { return birthPlace; }
    public void setBirthPlace(String birthPlace) { this.birthPlace = birthPlace; }

    public String getNationality() { return nationality; }
    public void setNationality(String nationality) { this.nationality = nationality; }

    public List<Artwork> getArtworks() { return artworks; }
    public void setArtworks(List<Artwork> artworks) { this.artworks = artworks; }
}
