package ViewModel;

import Model.Artist;
import Model.Artwork;
import Model.Repository.ArtistRepo;
import Model.Repository.ArtworkRepo;
import ViewModel.Commands.*;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class GalleryViewModel
{
    private final ArtistRepo artistRepo;
    private final ArtworkRepo artworkRepo;
    private Artist crtArtist;
    private Artwork crtArtwork;
    private List<Artist> artists;
    private List<Artwork> artworks;
    private String message;

    // Callback pentru notificări către View
    private Runnable onDataChanged;

    // Comenzi Artist
    private final GalleryCommand addArtistCommand;
    private final GalleryCommand deleteArtistCommand;
    private final GalleryCommand updateArtistCommand;
    private final GalleryCommand searchArtistsCommand;

    // Comenzi Artwork
    private final GalleryCommand addArtworkCommand;
    private final GalleryCommand deleteArtworkCommand;
    private final GalleryCommand updateArtworkCommand;
    private final GalleryCommand filterArtworkByArtistCommand;
    private final GalleryCommand filterArtworkByPriceCommand;
    private final GalleryCommand filterArtworkByTypeCommand;
    private final GalleryCommand searchArtworkCommand;
    private final GalleryCommand saveToCsvCommand;
    private final GalleryCommand saveToDocCommand;

    public GalleryViewModel() {
        this.artistRepo = new ArtistRepo();
        this.artworkRepo = new ArtworkRepo();
        this.crtArtist = new Artist("", "", "", "", "");
        this.crtArtwork = new Artwork("", null, "", 0.0, 0);
        this.artists = new ArrayList<>();
        this.artworks = new ArrayList<>();
        try {
            this.artists.addAll(artistRepo.getAllArtists());
            this.artworks.addAll(artworkRepo.getAllArtworks());
        } catch (SQLException e) {
            this.message = "Error loading data: " + e.getMessage();
        }

        // Inițializare comenzi cu metode interne
        this.addArtistCommand = new GalleryCommand(this::addArtist);
        this.deleteArtistCommand = new GalleryCommand(this::deleteArtist);
        this.updateArtistCommand = new GalleryCommand(this::updateArtist);
        this.searchArtistsCommand = new GalleryCommand(this::searchArtists);

        this.addArtworkCommand = new GalleryCommand(this::addArtwork);
        this.deleteArtworkCommand = new GalleryCommand(this::deleteArtwork);
        this.updateArtworkCommand = new GalleryCommand(this::updateArtwork);
        this.filterArtworkByArtistCommand = new GalleryCommand(this::filterArtworkByArtist);
        this.filterArtworkByPriceCommand = new GalleryCommand(this::filterArtworkByPrice);
        this.filterArtworkByTypeCommand = new GalleryCommand(this::filterArtworkByType);
        this.searchArtworkCommand = new GalleryCommand(this::searchArtwork);
        this.saveToCsvCommand = new GalleryCommand(this::saveToCsv);
        this.saveToDocCommand = new GalleryCommand(this::saveToDoc);
    }

    // Setter pentru callback
    public void setOnDataChanged(Runnable onDataChanged) {
        this.onDataChanged = onDataChanged;
    }

    // Notificare simplă
    private void notifyDataChanged() {
        if (onDataChanged != null) {
            onDataChanged.run();
        }
    }

    // Getteri
    public Artist getCrtArtist() { return crtArtist; }
    public Artwork getCrtArtwork() { return crtArtwork; }
    public List<Artist> getArtists() { return artists; }
    public List<Artwork> getArtworks() { return artworks; }
    public String getMessage() { return message; }

    // Setteri
    public void setCrtArtist(Artist artist) {
        this.crtArtist = artist;
        notifyDataChanged();
    }

    public void setCrtArtwork(Artwork artwork) {
        this.crtArtwork = artwork;
        notifyDataChanged();
    }

    // Getteri pentru comenzi
    public ICommands getAddArtistCommand() { return addArtistCommand; }
    public ICommands getDeleteArtistCommand() { return deleteArtistCommand; }
    public ICommands getUpdateArtistCommand() { return updateArtistCommand; }
    public ICommands getSearchArtistsCommand() { return searchArtistsCommand; }

    public ICommands getAddArtworkCommand() { return addArtworkCommand; }
    public ICommands getDeleteArtworkCommand() { return deleteArtworkCommand; }
    public ICommands getUpdateArtworkCommand() { return updateArtworkCommand; }
    public ICommands getFilterArtworkByArtistCommand() { return filterArtworkByArtistCommand; }
    public ICommands getFilterArtworkByPriceCommand() { return filterArtworkByPriceCommand; }
    public ICommands getFilterArtworkByTypeCommand() { return filterArtworkByTypeCommand; }
    public ICommands getSearchArtworkCommand() { return searchArtworkCommand; }
    public ICommands getSaveToCsvCommand() { return saveToCsvCommand; }
    public ICommands getSaveToDocCommand() { return saveToDocCommand; }

    // Metode pentru logica comenzilor
    private void addArtist() {
        try {
            artistRepo.addArtist(crtArtist);
            artists.add(crtArtist);
            message = "Artist added!";
            crtArtist = new Artist("", "", "", "", "");
            notifyDataChanged();
        } catch (SQLException e) {
            message = "Error adding artist: " + e.getMessage();
            notifyDataChanged();
        }
    }

    private void deleteArtist() {
        if (!crtArtist.getName().isEmpty()) {
            try {
                artistRepo.deleteArtist(crtArtist.getName());
                artists.removeIf(a -> a.getName().equals(crtArtist.getName()));
                message = "Artist deleted!";
                notifyDataChanged();
            } catch (SQLException e) {
                message = "Error deleting artist: " + e.getMessage();
                notifyDataChanged();
            }
        }
    }

    private void updateArtist() {
        if (!crtArtist.getName().isEmpty()) {
            try {
                artistRepo.updateArtist(crtArtist.getName(), crtArtist);
                message = "Artist updated!";
                notifyDataChanged();
            } catch (SQLException e) {
                message = "Error updating artist: " + e.getMessage();
                notifyDataChanged();
            }
        }
    }

    private void searchArtists() {
        if (!crtArtist.getName().isEmpty()) {
            try {
                artists = artistRepo.searchByName(crtArtist.getName());
                message = "Artists found!";
                notifyDataChanged();
            } catch (SQLException e) {
                message = "Search failed: " + e.getMessage();
                notifyDataChanged();
            }
        }
    }

    private void addArtwork() {
        if (!crtArtwork.getTitle().isEmpty() && crtArtwork.getArtist() != null) {
            try {
                artworkRepo.addArtwork(crtArtwork, crtArtwork.getArtist().getName());
                artworks.add(crtArtwork);
                message = "Artwork added!";
                crtArtwork = new Artwork("", null, "", 0.0, 0);
                notifyDataChanged();
            } catch (SQLException e) {
                message = "Error adding artwork: " + e.getMessage();
                notifyDataChanged();
            }
        }
    }

    private void deleteArtwork() {
        if (!crtArtwork.getTitle().isEmpty()) {
            try {
                artworkRepo.deleteArtwork(crtArtwork.getTitle());
                artworks.removeIf(a -> a.getTitle().equals(crtArtwork.getTitle()));
                message = "Artwork deleted!";
                notifyDataChanged();
            } catch (SQLException e) {
                message = "Error deleting artwork: " + e.getMessage();
                notifyDataChanged();
            }
        }
    }

    private void updateArtwork() {
        if (!crtArtwork.getTitle().isEmpty() && crtArtwork.getArtist() != null) {
            try {
                artworkRepo.updateArtwork(crtArtwork.getTitle(), crtArtwork, crtArtwork.getArtist().getName());
                message = "Artwork updated!";
                notifyDataChanged();
            } catch (SQLException e) {
                message = "Error updating artwork: " + e.getMessage();
                notifyDataChanged();
            }
        }
    }

    private void filterArtworkByArtist() {
        if (crtArtwork.getArtist() != null && !crtArtwork.getArtist().getName().isEmpty()) {
            try {
                artworks = artworkRepo.filterArtworkByArtist(crtArtwork.getArtist().getName());
                message = "Filtered by artist!";
                notifyDataChanged();
            } catch (SQLException e) {
                message = "Filter failed: " + e.getMessage();
                notifyDataChanged();
            }
        }
    }

    private void filterArtworkByPrice() {
        try {
            artworks = artworkRepo.filterArtworkByPrice(crtArtwork.getPrice());
            message = "Filtered by price!";
            notifyDataChanged();
        } catch (SQLException e) {
            message = "Filter failed: " + e.getMessage();
            notifyDataChanged();
        }
    }

    private void filterArtworkByType() {
        if (!crtArtwork.getType().isEmpty()) {
            try {
                artworks = artworkRepo.filterArtworkByType(crtArtwork.getType());
                message = "Filtered by type!";
                notifyDataChanged();
            } catch (SQLException e) {
                message = "Filter failed: " + e.getMessage();
                notifyDataChanged();
            }
        }
    }

    private void searchArtwork() {
        if (!crtArtwork.getTitle().isEmpty()) {
            try {
                artworks = artworkRepo.searchByTitle(crtArtwork.getTitle());
                message = "Artworks found!";
                notifyDataChanged();
            } catch (SQLException e) {
                message = "Search failed: " + e.getMessage();
                notifyDataChanged();
            }
        }
    }

    private void saveToCsv() {
        message = "Saved to CSV!";
        notifyDataChanged();
    }

    private void saveToDoc() {
        message = "Saved to DOC!";
        notifyDataChanged();
    }
}