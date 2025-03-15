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
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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

    public void initializeComboBoxItems() {
        gui.setTypeComboBoxItems(List.of("Painting", "Sculpture", "Photography"));
        gui.setFilterByTypeBoxItems(List.of("All", "Painting", "Sculpture", "Photography"));
    }

    // CRUD Artists
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

            // Actualizăm numele artistului în toate operele asociate
            List<Artwork> artworks = artworkRepo.getAllArtworks();
            for (Artwork artwork : artworks) {
                if (artwork.getArtist().getName().equals(oldName)) {
                    artwork.setArtist(updatedArtist);
                    artworkRepo.updateArtwork(artwork.getTitle(), artwork, newName);
                }
            }

            refreshArtists();
            refreshArtworks();
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

    // CRUD Artworks
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
            Artist oldArtist = artistRepo.findArtistByName(existingArtwork.getArtist().getName());
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
            Artist artist = artistRepo.findArtistByName(artwork.getArtist().getName());
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

    // Images
    public void addArtworkImage(String artworkTitle, String imagePath) {
        try {
            validateNotEmpty(artworkTitle, "Titlul operei");
            validateNotEmpty(imagePath, "Calea către imagine");
            ArtworkImage image = new ArtworkImage(imagePath, null);
            artworkImageRepo.addImage(artworkTitle, image);
            displayArtworkImages(artworkTitle);
            gui.confirmSuccess("Image added for artwork " + artworkTitle + "!");
        } catch (SQLException e) {
            gui.showError("Image add error " + e.getMessage());
        }
    }

    public void displayArtworkImages(String artworkTitle) throws SQLException {
        List<ArtworkImage> images = artworkImageRepo.getImagesByArtwork(artworkTitle);
        List<String> imagePaths = images.stream().map(ArtworkImage::getImagePath).collect(Collectors.toList());
        gui.displayArtworkImages(imagePaths);
    }

    // Export
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

    // Search
    public void searchArtistsByName(String name) {
        try {
            if (name == null || name.isEmpty()) {
                refreshArtists();
            } else {
                List<Artist> filteredArtists = artistRepo.searchByName(name);
                gui.displayArtists(filteredArtists);
            }
        } catch (SQLException e) {
            gui.showError("Artist search error: " + e.getMessage());
        }
    }

    public void searchArtworksByTitle(String title) {
        try {
            if (title == null || title.isEmpty()) {
                refreshArtworks();
            } else {
                List<Artwork> filteredArtworks = artworkRepo.searchByTitle(title);
                gui.displayArtworks(filteredArtworks);
            }
        } catch (SQLException e) {
            gui.showError("Artwork search error: " + e.getMessage());
        }
    }

    // Filters
    public void applyFilters(String artistName, String type, String priceText) {
        List<Artwork> filteredArtworks = new ArrayList<>(artworksTable);
        if (artistName != null && !artistName.equals("All")) {
            filteredArtworks = filteredArtworks.stream()
                    .filter(artwork -> artwork.getArtist().getName().equals(artistName))
                    .collect(Collectors.toList());
        }
        if (type != null && !type.equals("All")) {
            filteredArtworks = filteredArtworks.stream()
                    .filter(artwork -> artwork.getType().equals(type))
                    .collect(Collectors.toList());
        }
        if (priceText != null && !priceText.trim().isEmpty()) {
            double maxPrice = parseDouble(priceText, "Preț maxim");
            if (maxPrice >= 0) {
                filteredArtworks = filteredArtworks.stream()
                        .filter(artwork -> artwork.getPrice() <= maxPrice)
                        .collect(Collectors.toList());
            }
        }
        gui.displayFilteredArtworks(filteredArtworks);
    }

    // Refresh
    public void refreshArtists() throws SQLException {
        artistsTable.setAll(artistRepo.getAllArtists());
        gui.displayArtists(artistsTable);
        List<String> artistNames = artistsTable.stream().map(Artist::getName).collect(Collectors.toList());
        artistNames.add(0, "All");
        gui.setArtistComboBoxItems(artistNames);
        gui.setFilterByArtistBoxItems(artistNames);
    }

    public void refreshArtworks() throws SQLException {
        artworksTable.setAll(artworkRepo.getAllArtworks());
        gui.displayArtworks(artworksTable);
    }

    // Helper methods
    public List<String> getArtworksByArtist(String artistName) throws SQLException {
        return artworkRepo.getArtworksByArtist(artistName);
    }

    public void populateArtistFields(Artist artist) {
        gui.populateArtistFields(artist);
    }

    public void clearArtistFields() {
        gui.clearArtistFields();
    }

    public void populateArtworkFields(Artwork artwork) {
        gui.populateArtworkFields(artwork);
    }

    public void clearArtworkFields() {
        gui.clearArtworkFields();
    }

    public boolean confirmAction(String message) {
        return gui.confirmAction(message);
    }

    private double parseDouble(String value, String fieldName) {
        try {
            double result = Double.parseDouble(value.trim());
            if (result < 0) {
                gui.showError(fieldName + " cannot be negative!");
                return -1;
            }
            return result;
        } catch (NumberFormatException e) {
            gui.showError("Introduce a valid " + fieldName.toLowerCase());
            return -1;
        }
    }

    private int parseInt(String value, String fieldName) {
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException e) {
            gui.showError("Introduce a valid " + fieldName.toLowerCase());
            return -1;
        }
    }

    private void validateNotEmpty(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            gui.showError(fieldName + " is mandatory!");
            throw new IllegalArgumentException(fieldName + " cannot be null or empty!");
        }
    }

    private void validateNonNegative(double value, String fieldName) {
        if (value < 0) {
            gui.showError(fieldName + " cannot be negative!");
            throw new IllegalArgumentException(fieldName + " must be non-negative!");
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




    public void toggleArtistCheckboxes(boolean isAddChecked) {
        if (isAddChecked) {
            gui.clearArtistFields();
            gui.setArtistFieldsEditable(true);
        } else {
            gui.setArtistFieldsEditable(true);
        }
    }

    public void toggleArtworkCheckboxes(boolean isAddChecked) {
        if (isAddChecked) {
            gui.clearArtworkFields();
            gui.setArtworkFieldsEditable(true);
        } else {
            gui.setArtworkFieldsEditable(true);
        }
    }

    public void disableArtistFields() {
        gui.setArtistFieldsEditable(false);
    }

    public void disableArtworkFields() {
        gui.setArtworkFieldsEditable(false);
    }


}