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


    public GalleryPresenter(IGalleryGUI gui) {
        if (gui == null) {
            throw new IllegalArgumentException("Not Null GUI!");
        }
        this.gui = gui;
        this.artistRepo = new ArtistRepo();
        this.artworkRepo = new ArtworkRepo();
        this.artworkImageRepo = new ArtworkImageRepo();
        this.artistsTable = FXCollections.observableArrayList();
        this.artworksTable = FXCollections.observableArrayList();

    }





    public void addArtist(String name, String birthDate, String birthPlace, String nationality, String photo) {
        try {
            validateNotEmpty(name, "Numele artistului");
            Artist artist = new Artist(name, birthDate, birthPlace, nationality, photo);
            artistRepo.addArtist(artist);
            refreshArtists();
            gui.displaySuccess(buildSuccessMessage("Artist", name, "added"));
        } catch (SQLException e) {
            gui.showError("Artist Add Error: " + e.getMessage());
        }
    }

    public void updateArtist(String oldName, String newName, String birthDate, String birthPlace, String nationality, String photo) {
        try {
            validateNotEmpty(newName, "Numele artistului");
            Artist updatedArtist = new Artist(newName, birthDate, birthPlace, nationality, photo);
            artistRepo.updateArtist(oldName, updatedArtist);
            refreshArtists();
            gui.displaySuccess(buildSuccessMessage("Artist", oldName, "updated"));
        } catch (SQLException e) {
            gui.showError("Artist Update Error " + e.getMessage());
        }
    }

    public void deleteArtist(String name) {
        try {
            validateNotEmpty(name, "Numele artistului");
            artistRepo.deleteArtist(name);
            refreshArtists();
            gui.displaySuccess(buildSuccessMessage("Artist", name, "deleted"));
        } catch (SQLException e) {
            gui.showError("Artist Delete Error " + e.getMessage());
        }
    }


    public List<String> getArtworksByArtist(String artistName) throws SQLException {
        return artworkRepo.getArtworksByArtist(artistName);
    }






    public void addArtwork(String title, String artistName, String type, double price, int creationYear) {
        try {
            validateNotEmpty(title, "Titlul operei");
            validateNotEmpty(artistName, "Numele artistului");
            validateNonNegative(price, "Prețul");
            Artist artist = artistRepo.findArtistByName(artistName);
            if (artist == null) {
                gui.showError("Artist " + artistName + " does not exist");
                return;
            }
            Artwork artwork = new Artwork(title, artist, type, price, creationYear);
            artworkRepo.addArtwork(artwork, artistName);
            artist.addArtwork(artwork);
            refreshArtworks();
            refreshArtists();
            gui.confirmSuccess(buildSuccessMessage("Artwork", title, "added"));
        } catch (SQLException e) {
            gui.showError("Artwork Add Error " + e.getMessage());
        }
    }

    public void updateArtwork(String oldTitle, String newTitle, String artistName, String type, double price, int creationYear) {
        try {
            validateNotEmpty(newTitle, "Titlul operei");
            validateNotEmpty(artistName, "Numele artistului");
            validateNonNegative(price, "Prețul");
            Artwork existingArtwork = artworkRepo.findArtworkByTitle(oldTitle);
            if (existingArtwork == null) {
                gui.showError("Artwork " + oldTitle + " does not exist");
                return;
            }
            Artist newArtist = artistRepo.findArtistByName(artistName);
            if (newArtist == null) {
                gui.showError("Artist  " + artistName + " does not exist");
                return;
            }
            Artwork updatedArtwork = new Artwork(newTitle, newArtist, type, price, creationYear);
            artworkRepo.updateArtwork(oldTitle, updatedArtwork, artistName);

            Artist oldArtist = existingArtwork.getArtist();
            if (oldArtist != null && !oldArtist.getName().equals(artistName)) {
                oldArtist.getArtworks().remove(existingArtwork);
                artistRepo.updateArtist(oldArtist.getName(), oldArtist);
            }
            newArtist.addArtwork(updatedArtwork);
            artistRepo.updateArtist(newArtist.getName(), newArtist);
            refreshArtworks();
            refreshArtists();
            gui.confirmSuccess(buildSuccessMessage("Artwork", oldTitle, "updated"));
        } catch (SQLException e) {
            gui.showError("Artwork Update Error " + e.getMessage());
        }
    }

    public void deleteArtwork(String title) {
        try {
            validateNotEmpty(title, "Titlul operei");
            Artwork artwork = artworkRepo.findArtworkByTitle(title);
            if (artwork == null) {
                gui.showError("Artwork " + title + " does not exist");
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
            gui.confirmSuccess(buildSuccessMessage("Artwork", title, "deleted"));
        } catch (SQLException e) {
            gui.showError("Artwork Delete Error " + e.getMessage());
        }
    }






    public void addArtworkImage(String artworkTitle, String imagePath) {
        try {
            validateNotEmpty(artworkTitle, "Titlul operei");
            validateNotEmpty(imagePath, "Calea către imagine");
            ArtworkImage image = new ArtworkImage(imagePath, null);
            artworkImageRepo.addImage(artworkTitle, image);
            gui.confirmSuccess("Image added for artwork " + artworkTitle + "!");
        } catch (SQLException e) {
            gui.showError("Image add error " + e.getMessage());
        }
    }


    public List<ArtworkImage> getImagesForArtwork(String artworkTitle) throws SQLException {
        return artworkImageRepo.getImagesByArtwork(artworkTitle);
    }




    public void exportArtworksToCSV(String filePath) {
        try {
            validateNotEmpty(filePath, "Calea către fișier");
            try (FileWriter writer = new FileWriter(filePath)) {
                writer.write("Title,Artist,Type,Price,Year\n");
                for (Artwork artwork : artworksTable) {
                    writer.write(String.format("%s,%s,%s,%.2f,%d\n",
                            escapeCSV(artwork.getTitle()), escapeCSV(artwork.getArtist().getName()),
                            escapeCSV(artwork.getType()), artwork.getPrice(), artwork.getCreationYear()));
                }
            }
            gui.confirmExportSuccess(filePath);
        } catch (IOException e) {
            gui.showError("CSV export error: " + e.getMessage());
        }
    }



    public void exportArtworksToText(String filePath) {
        try {
            validateNotEmpty(filePath, "Calea către fișier");
            try (FileWriter writer = new FileWriter(filePath)) {
                writer.write("Artwork List\n\n");
                for (Artwork artwork : artworksTable) {
                    writer.write(String.format("Title: %s\nArtist: %s\nType: %s\nPrice: %.2f\nYear: %d\n\n",
                            artwork.getTitle(), artwork.getArtist().getName(),
                            artwork.getType(), artwork.getPrice(), artwork.getCreationYear()));
                }
            }
            gui.confirmExportSuccess(filePath);
        } catch (IOException e) {
            gui.showError("DOC export error: " + e.getMessage());
        }
    }





    public void searchArtistsByName(String name) {
        try {
            List<Artist> filteredArtists = artistRepo.searchByName(name);
            gui.displayArtists(filteredArtists);
        } catch (SQLException e) {
            gui.showError("Artist search error: " + e.getMessage());
        }
    }

    public void searchArtworksByTitle(String title) {
        try {
            List<Artwork> filteredArtworks = artworkRepo.searchByTitle(title);
            gui.displayArtworks(filteredArtworks);
        } catch (SQLException e) {
            gui.showError("Artwork search error: " + e.getMessage());
        }
    }












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

    private String escapeCSV(String value) {
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