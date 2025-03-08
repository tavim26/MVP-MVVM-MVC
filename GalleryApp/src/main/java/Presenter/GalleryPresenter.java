package Presenter;

import Model.Artist;
import Model.Artwork;
import Model.ArtworkImage;
import Repository.ArtistRepo;
import Repository.ArtworkRepo;
import Repository.ArtworkImageRepo;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class GalleryPresenter {
    private final ArtistRepo artistRepo;
    private final ArtworkRepo artworkRepo;
    private final ArtworkImageRepo artworkImageRepo;
    private final IGalleryGUI gui;
    private final ObservableList<Artist> artistsTable;
    private final ObservableList<Artwork> artworksTable;

    // Constructor
    public GalleryPresenter(IGalleryGUI gui) {
        if (gui == null) {
            throw new IllegalArgumentException("Interfața GUI nu poate fi null!");
        }
        this.gui = gui;
        this.artistRepo = new ArtistRepo();
        this.artworkRepo = new ArtworkRepo();
        this.artworkImageRepo = new ArtworkImageRepo();
        this.artistsTable = FXCollections.observableArrayList();
        this.artworksTable = FXCollections.observableArrayList();

    }

    // --- Metode pentru artiști ---

    public void addArtist(String name, String birthDate, String birthPlace, String nationality, String photo) {
        try {
            validateNotEmpty(name, "Numele artistului");
            Artist artist = new Artist(name, birthDate, birthPlace, nationality, photo);
            artistRepo.addArtist(artist);
            refreshArtists();
            gui.displaySuccess(buildSuccessMessage("Artistul", name, "adăugat"));
        } catch (SQLException e) {
            gui.showError("Eroare la adăugarea artistului: " + e.getMessage());
        }
    }

    public void updateArtist(String oldName, String newName, String birthDate, String birthPlace, String nationality, String photo) {
        try {
            validateNotEmpty(newName, "Numele artistului");
            Artist updatedArtist = new Artist(newName, birthDate, birthPlace, nationality, photo);
            artistRepo.updateArtist(oldName, updatedArtist);
            refreshArtists();
            gui.displaySuccess(buildSuccessMessage("Artistul", oldName, "actualizat"));
        } catch (SQLException e) {
            gui.showError("Eroare la actualizarea artistului: " + e.getMessage());
        }
    }

    public void deleteArtist(String name) {
        try {
            validateNotEmpty(name, "Numele artistului");
            artistRepo.deleteArtist(name);
            refreshArtists();
            gui.displaySuccess(buildSuccessMessage("Artistul", name, "șters"));
        } catch (SQLException e) {
            gui.showError("Eroare la ștergerea artistului: " + e.getMessage());
        }
    }

    public void searchArtist(String name) {
        try {
            validateNotEmpty(name, "Numele pentru căutare");
            Artist artist = artistRepo.findArtistByName(name);
            if (artist != null) {
                gui.displayArtistSearchResult(artist);
            } else {
                gui.showError("Artistul cu numele " + name + " nu a fost găsit.");
            }
        } catch (SQLException e) {
            gui.showError("Eroare la căutarea artistului: " + e.getMessage());
        }
    }

    public List<String> getArtworksByArtist(String artistName) throws SQLException {
        return artworkRepo.getArtworksByArtist(artistName);
    }

    // --- Metode pentru opere de artă ---

    public void addArtwork(String title, String artistName, String type, double price, int creationYear) {
        try {
            validateNotEmpty(title, "Titlul operei");
            validateNotEmpty(artistName, "Numele artistului");
            validateNonNegative(price, "Prețul");
            Artist artist = artistRepo.findArtistByName(artistName);
            if (artist == null) {
                gui.showError("Artistul " + artistName + " nu există!");
                return;
            }
            Artwork artwork = new Artwork(title, artist, type, price, creationYear);
            artworkRepo.addArtwork(artwork, artistName);
            artist.addArtwork(artwork); // Folosim metoda din Model
            refreshArtworks();
            refreshArtists();
            gui.confirmSuccess(buildSuccessMessage("Opera", title, "adăugată"));
        } catch (SQLException e) {
            gui.showError("Eroare la adăugarea operei: " + e.getMessage());
        }
    }

    public void updateArtwork(String oldTitle, String newTitle, String artistName, String type, double price, int creationYear) {
        try {
            validateNotEmpty(newTitle, "Titlul operei");
            validateNotEmpty(artistName, "Numele artistului");
            validateNonNegative(price, "Prețul");
            Artwork existingArtwork = artworkRepo.findArtworkByTitle(oldTitle);
            if (existingArtwork == null) {
                gui.showError("Opera " + oldTitle + " nu există!");
                return;
            }
            Artist newArtist = artistRepo.findArtistByName(artistName);
            if (newArtist == null) {
                gui.showError("Artistul " + artistName + " nu există!");
                return;
            }
            Artwork updatedArtwork = new Artwork(newTitle, newArtist, type, price, creationYear);
            artworkRepo.updateArtwork(oldTitle, updatedArtwork, artistName);
            // Gestionăm relația bidirecțională prin Model
            Artist oldArtist = existingArtwork.getArtist();
            if (oldArtist != null && !oldArtist.getName().equals(artistName)) {
                oldArtist.getArtworks().remove(existingArtwork);
                artistRepo.updateArtist(oldArtist.getName(), oldArtist);
            }
            newArtist.addArtwork(updatedArtwork);
            artistRepo.updateArtist(newArtist.getName(), newArtist);
            refreshArtworks();
            refreshArtists();
            gui.confirmSuccess(buildSuccessMessage("Opera", oldTitle, "actualizată"));
        } catch (SQLException e) {
            gui.showError("Eroare la actualizarea operei: " + e.getMessage());
        }
    }

    public void deleteArtwork(String title) {
        try {
            validateNotEmpty(title, "Titlul operei");
            Artwork artwork = artworkRepo.findArtworkByTitle(title);
            if (artwork == null) {
                gui.showError("Opera " + title + " nu există!");
                return;
            }
            Artist artist = artwork.getArtist();
            if (artist != null) {
                artist.getArtworks().remove(artwork);
                artistRepo.updateArtist(artist.getName(), artist);
            }
            artworkRepo.deleteArtwork(title);
            refreshArtworks();
            refreshArtists();
            gui.confirmSuccess(buildSuccessMessage("Opera", title, "ștearsă"));
        } catch (SQLException e) {
            gui.showError("Eroare la ștergerea operei: " + e.getMessage());
        }
    }

    public void searchArtwork(String title) {
        try {
            validateNotEmpty(title, "Titlul pentru căutare");
            Artwork artwork = artworkRepo.findArtworkByTitle(title);
            if (artwork != null) {
                gui.displayArtworkSearchResult(artwork);
            } else {
                gui.showError("Opera cu titlul " + title + " nu a fost găsită.");
            }
        } catch (SQLException e) {
            gui.showError("Eroare la căutarea operei: " + e.getMessage());
        }
    }

    public void filterByArtistName(String artistName) {
        try {
            validateNotEmpty(artistName, "Numele artistului");
            artworksTable.setAll(artworkRepo.filterByArtistName(artistName));
            gui.displayFilteredArtworks(artworksTable);
        } catch (SQLException e) {
            gui.showError("Eroare la filtrarea operelor după artist: " + e.getMessage());
        }
    }

    public void filterByType(String type) {
        try {
            validateNotEmpty(type, "Tipul operei");
            artworksTable.setAll(artworkRepo.filterByType(type));
            gui.displayFilteredArtworks(artworksTable);
        } catch (SQLException e) {
            gui.showError("Eroare la filtrarea operelor după tip: " + e.getMessage());
        }
    }

    public void filterByMaxPrice(double maxPrice) {
        try {
            validateNonNegative(maxPrice, "Prețul maxim");
            artworksTable.setAll(artworkRepo.filterByMaxPrice(maxPrice));
            gui.displayFilteredArtworks(artworksTable);
        } catch (SQLException e) {
            gui.showError("Eroare la filtrarea operelor după preț: " + e.getMessage());
        }
    }

    public void addArtworkImage(String artworkTitle, String imagePath) {
        try {
            validateNotEmpty(artworkTitle, "Titlul operei");
            validateNotEmpty(imagePath, "Calea către imagine");
            ArtworkImage image = new ArtworkImage(imagePath, null);
            artworkImageRepo.addImage(artworkTitle, image);
            gui.confirmSuccess("Imaginea a fost adăugată pentru opera " + artworkTitle + "!");
        } catch (SQLException e) {
            gui.showError("Eroare la adăugarea imaginii: " + e.getMessage());
        }
    }

    // În GalleryPresenter.java
    public List<ArtworkImage> getImagesForArtwork(String artworkTitle) throws SQLException {
        return artworkImageRepo.getImagesByArtwork(artworkTitle);
    }

    public void exportArtworksToCSV(String filePath) {
        try {
            validateNotEmpty(filePath, "Calea către fișier");
            try (FileWriter writer = new FileWriter(filePath)) {
                writer.write("Titlu,Artist,Tip,Pret,An\n");
                for (Artwork artwork : artworksTable) {
                    writer.write(String.format("%s,%s,%s,%.2f,%d\n",
                            escapeCSV(artwork.getTitle()), escapeCSV(artwork.getArtist().getName()),
                            escapeCSV(artwork.getType()), artwork.getPrice(), artwork.getCreationYear()));
                }
            }
            gui.confirmExportSuccess(filePath);
        } catch (IOException e) {
            gui.showError("Eroare la exportul în CSV: " + e.getMessage());
        }
    }

    public void exportArtworksToText(String filePath) { // Redenumit pentru claritate
        try {
            validateNotEmpty(filePath, "Calea către fișier");
            try (FileWriter writer = new FileWriter(filePath)) {
                writer.write("Lista operelor de artă\n\n");
                for (Artwork artwork : artworksTable) {
                    writer.write(String.format("Titlu: %s\nArtist: %s\nTip: %s\nPreț: %.2f\nAn: %d\n\n",
                            artwork.getTitle(), artwork.getArtist().getName(),
                            artwork.getType(), artwork.getPrice(), artwork.getCreationYear()));
                }
            }
            gui.confirmExportSuccess(filePath);
        } catch (IOException e) {
            gui.showError("Eroare la exportul în text: " + e.getMessage());
        }
    }



    public void searchArtistsByName(String name) {
        try {
            List<Artist> filteredArtists = artistRepo.searchByName(name);
            gui.displayArtists(filteredArtists);
        } catch (SQLException e) {
            gui.showError("Eroare la căutarea artiștilor: " + e.getMessage());
        }
    }

    public void searchArtworksByTitle(String title) {
        try {
            List<Artwork> filteredArtworks = artworkRepo.searchByTitle(title);
            gui.displayArtworks(filteredArtworks);
        } catch (SQLException e) {
            gui.showError("Eroare la căutarea operelor: " + e.getMessage());
        }
    }

    // --- Metode ajutătoare ---

    public void refreshArtists() {
        try {
            artistsTable.setAll(artistRepo.getAllArtists());
            gui.displayArtists(artistsTable);
        } catch (SQLException e) {
            gui.showError("Eroare la actualizarea listei artiștilor: " + e.getMessage());
        }
    }

    public void refreshArtworks() {
        try {
            artworksTable.setAll(artworkRepo.getAllArtworks());
            gui.displayArtworks(artworksTable);
        } catch (SQLException e) {
            gui.showError("Eroare la actualizarea listei operelor: " + e.getMessage());
        }
    }

    private void validateNotEmpty(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            gui.showError(fieldName + " este obligatoriu!");
            throw new IllegalArgumentException(fieldName + " nu poate fi null sau gol!");
        }
    }

    private void validateNonNegative(double value, String fieldName) {
        if (value < 0) {
            gui.showError(fieldName + " nu poate fi negativ!");
            throw new IllegalArgumentException(fieldName + " trebuie să fie >= 0!");
        }
    }

    private String buildSuccessMessage(String entity, String name, String action) {
        return String.format("%s %s a fost %s cu succes!", entity, name, action);
    }

    private String escapeCSV(String value) { // Pentru a preveni probleme cu virgulele în CSV
        if (value.contains(",")) {
            return "\"" + value + "\"";
        }
        return value;
    }

    public ObservableList<Artist> getArtistsTable() {
        return artistsTable;
    }

    public ObservableList<Artwork> getArtworksTable() {
        return artworksTable;
    }
}