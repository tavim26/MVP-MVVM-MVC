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

public class GalleryPresenter {
    private final ArtistRepo artistRepo;
    private final ArtworkRepo artworkRepo;
    private final ArtworkImageRepo artworkImageRepo;
    private final IGalleryGUI gui;
    private final ObservableList<Artist> artistsTable;
    private final ObservableList<Artwork> artworksTable;

    // Constructor
    public GalleryPresenter(IGalleryGUI gui) {
        this.gui = gui;
        this.artistRepo = new ArtistRepo();
        this.artworkRepo = new ArtworkRepo();
        this.artworkImageRepo = new ArtworkImageRepo();
        this.artistsTable = FXCollections.observableArrayList();
        this.artworksTable = FXCollections.observableArrayList();
        if (gui != null) {
            refreshArtists();
            refreshArtworks();


        }
    }

    // --- Metode pentru artiști ---

    public void addArtist(String name, String birthDate, String birthPlace, String nationality, String photo) {
        try {
            if (name == null || name.trim().isEmpty()) {
                gui.showError("Numele artistului este obligatoriu!");
                return;
            }
            Artist artist = new Artist(name, birthDate, birthPlace, nationality, photo);
            artistRepo.addArtist(artist);
            refreshArtists();
            gui.displaySuccess("Artistul " + name + " a fost adăugat cu succes!");
        } catch (SQLException e) {
            gui.showError("Eroare la adăugarea artistului: " + e.getMessage());
        }
    }

    public void updateArtist(String oldName, String newName, String birthDate, String birthPlace, String nationality, String photo) {
        try {
            if (newName == null || newName.trim().isEmpty()) {
                gui.showError("Numele artistului este obligatoriu!");
                return;
            }
            Artist updatedArtist = new Artist(newName, birthDate, birthPlace, nationality, photo);
            artistRepo.updateArtist(oldName, updatedArtist);
            refreshArtists();
            gui.displaySuccess("Artistul " + oldName + " a fost actualizat cu succes!");
        } catch (SQLException e) {
            gui.showError("Eroare la actualizarea artistului: " + e.getMessage());
        }
    }

    public void deleteArtist(String name) {
        try {
            if (name == null || name.trim().isEmpty()) {
                gui.showError("Numele artistului este obligatoriu pentru ștergere!");
                return;
            }
            artistRepo.deleteArtist(name);
            refreshArtists();
            gui.displaySuccess("Artistul " + name + " a fost șters cu succes!");
        } catch (SQLException e) {
            gui.showError("Eroare la ștergerea artistului: " + e.getMessage());
        }
    }

    public void getAllArtists() {
        try {
            artistsTable.setAll(artistRepo.getAllArtists());
            gui.displayArtists(artistsTable);
        } catch (SQLException e) {
            gui.showError("Eroare la obținerea listei artiștilor: " + e.getMessage());
        }
    }

    public void searchArtist(String name) {
        try {
            if (name == null || name.trim().isEmpty()) {
                gui.showError("Introduceți un nume pentru căutare!");
                return;
            }
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

    public void associateArtwork(String artistName, String artworkTitle, String type, double price, int creationYear) {
        try {
            if (artistName == null || artistName.trim().isEmpty()) {
                gui.showError("Numele artistului este obligatoriu pentru asociere!");
                return;
            }
            if (artworkTitle == null || artworkTitle.trim().isEmpty()) {
                gui.showError("Titlul operei este obligatoriu!");
                return;
            }
            Artist artist = artistRepo.findArtistByName(artistName);
            if (artist == null) {
                gui.showError("Artistul " + artistName + " nu există!");
                return;
            }
            Artwork artwork = new Artwork(artworkTitle, artist, type, price, creationYear);
            artworkRepo.addArtwork(artwork, artistName);
            refreshArtists();
            gui.displaySuccess("Opera " + artworkTitle + " a fost asociată cu artistul " + artistName + "!");
        } catch (SQLException e) {
            gui.showError("Eroare la asocierea operei: " + e.getMessage());
        }
    }

    // --- Metode pentru opere de artă ---

    public void addArtwork(String title, String artistName, String type, double price, int creationYear) {
        try {
            if (title == null || title.trim().isEmpty()) {
                gui.showError("Titlul operei este obligatoriu!");
                return;
            }
            if (artistName == null || artistName.trim().isEmpty()) {
                gui.showError("Numele artistului este obligatoriu!");
                return;
            }
            if (price < 0) {
                gui.showError("Prețul nu poate fi negativ!");
                return;
            }
            // Găsește artistul
            Artist artist = artistRepo.findArtistByName(artistName);
            if (artist == null) {
                gui.showError("Artistul " + artistName + " nu există!");
                return;
            }
            Artwork artwork = new Artwork(title, artist, type, price, creationYear);
            artworkRepo.addArtwork(artwork, artistName); // Adaugă opera în baza de date

            // Nu mai actualizăm lista de opere manual, ci reîncărcăm artistul
            refreshArtworks();
            refreshArtists(); // Reîncarcă toți artiștii pentru a reflecta schimbarea
            gui.confirmSuccess("Opera " + title + " a fost adăugată cu succes!");
        } catch (SQLException e) {
            gui.showError("Eroare la adăugarea operei: " + e.getMessage());
        }
    }

    public void updateArtwork(String oldTitle, String newTitle, String artistName, String type, double price, int creationYear) {
        try {
            if (newTitle == null || newTitle.trim().isEmpty()) {
                gui.showError("Titlul operei este obligatoriu!");
                return;
            }
            if (artistName == null || artistName.trim().isEmpty()) {
                gui.showError("Numele artistului este obligatoriu!");
                return;
            }
            if (price < 0) {
                gui.showError("Prețul nu poate fi negativ!");
                return;
            }
            // Găsește opera existentă
            Artwork oldArtwork = artworkRepo.findArtworkByTitle(oldTitle);
            if (oldArtwork == null) {
                gui.showError("Opera " + oldTitle + " nu există!");
                return;
            }
            // Găsește artistul nou
            Artist newArtist = artistRepo.findArtistByName(artistName);
            if (newArtist == null) {
                gui.showError("Artistul " + artistName + " nu există!");
                return;
            }
            // Verifică dacă artistul s-a schimbat
            Artist oldArtist = oldArtwork.getArtist();
            if (oldArtist != null && !oldArtist.getName().equals(artistName)) {
                // Scoate opera din lista vechiului artist
                oldArtist.getArtworks().removeIf(art -> art.getTitle().equals(oldTitle));
                artistRepo.updateArtist(oldArtist.getName(), oldArtist);
                // Adaugă opera în lista noului artist
                newArtist.getArtworks().add(oldArtwork);
                artistRepo.updateArtist(newArtist.getName(), newArtist);
            } else if (oldArtist == null) {
                // Dacă opera nu avea artist, doar o adăugăm la noul artist
                newArtist.getArtworks().add(oldArtwork);
                artistRepo.updateArtist(newArtist.getName(), newArtist);
            }
            // Actualizează opera
            Artwork updatedArtwork = new Artwork(newTitle, newArtist, type, price, creationYear);
            artworkRepo.updateArtwork(oldTitle, updatedArtwork, artistName);
            refreshArtworks();
            refreshArtists();
            gui.confirmSuccess("Opera " + oldTitle + " a fost actualizată cu succes!");
        } catch (SQLException e) {
            gui.showError("Eroare la actualizarea operei: " + e.getMessage());
        }
    }

    public void deleteArtwork(String title) {
        try {
            if (title == null || title.trim().isEmpty()) {
                gui.showError("Titlul operei este obligatoriu pentru ștergere!");
                return;
            }
            // Găsește opera
            Artwork artwork = artworkRepo.findArtworkByTitle(title);
            if (artwork == null) {
                gui.showError("Opera " + title + " nu există!");
                return;
            }
            // Scoate opera din lista artistului
            Artist artist = artwork.getArtist();
            if (artist != null) {
                artist.getArtworks().removeIf(art -> art.getTitle().equals(title));
                artistRepo.updateArtist(artist.getName(), artist);
            }
            artworkRepo.deleteArtwork(title);
            refreshArtworks();
            refreshArtists();
            gui.confirmSuccess("Opera " + title + " a fost ștearsă cu succes!");
        } catch (SQLException e) {
            gui.showError("Eroare la ștergerea operei: " + e.getMessage());
        }
    }

    public void getAllArtworks() {
        try {
            artworksTable.setAll(artworkRepo.getAllArtworks());
            gui.displayArtworks(artworksTable);
        } catch (SQLException e) {
            gui.showError("Eroare la obținerea listei operelor: " + e.getMessage());
        }
    }

    public void searchArtwork(String title) {
        try {
            if (title == null || title.trim().isEmpty()) {
                gui.showError("Introduceți un titlu pentru căutare!");
                return;
            }
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

    public void filterArtworks(String artistName, Double minPrice, String type) {
        try {
            if (minPrice != null && minPrice < 0) {
                gui.showError("Prețul minim nu poate fi negativ!");
                return;
            }
            ObservableList<Artwork> filteredArtworks = FXCollections.observableArrayList(
                    artworkRepo.filterArtworks(artistName, minPrice, type));
            gui.displayFilteredArtworks(filteredArtworks);
        } catch (SQLException e) {
            gui.showError("Eroare la filtrarea operelor: " + e.getMessage());
        }
    }

    public void addArtworkImage(String artworkTitle, String imagePath) {
        try {
            if (artworkTitle == null || artworkTitle.trim().isEmpty()) {
                gui.showError("Titlul operei este obligatoriu!");
                return;
            }
            if (imagePath == null || imagePath.trim().isEmpty()) {
                gui.showError("Calea către imagine este obligatorie!");
                return;
            }
            ArtworkImage image = new ArtworkImage(imagePath, null);
            artworkImageRepo.addImage(artworkTitle, image);
            gui.confirmSuccess("Imaginea a fost adăugată pentru opera " + artworkTitle + "!");
        } catch (SQLException e) {
            gui.showError("Eroare la adăugarea imaginii: " + e.getMessage());
        }
    }

    public void exportArtworksToCSV(String filePath) {
        try {
            if (filePath == null || filePath.trim().isEmpty()) {
                gui.showError("Calea către fișier este obligatorie!");
                return;
            }
            try (FileWriter writer = new FileWriter(filePath)) {
                writer.write("Title,Artist,Type,Price,Creation Year\n");
                for (Artwork artwork : artworksTable) {
                    writer.write(String.format("%s,%s,%s,%.2f,%d\n",
                            artwork.getTitle(), artwork.getArtist().getName(),
                            artwork.getType(), artwork.getPrice(), artwork.getCreationYear()));
                }
            }
            gui.confirmExportSuccess(filePath);
        } catch (IOException e) {
            gui.showError("Eroare la exportul în CSV: " + e.getMessage());
        }
    }

    public void exportArtworksToDoc(String filePath) {
        try {
            if (filePath == null || filePath.trim().isEmpty()) {
                gui.showError("Calea către fișier este obligatorie!");
                return;
            }
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
            gui.showError("Eroare la exportul în DOC: " + e.getMessage());
        }
    }

    // --- Metode ajutătoare ---

    private void refreshArtists() {
        getAllArtists();
    }

    private void refreshArtworks() {
        getAllArtworks();
    }

    public ObservableList<Artist> getArtistsTable() {
        return artistsTable;
    }

    public ObservableList<Artwork> getArtworksTable() {
        return artworksTable;
    }
}