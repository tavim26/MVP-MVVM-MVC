package Presenter;

import Model.Artist;
import Model.Artwork;
import java.util.List;

public interface IGalleryGUI {
    // Metode pentru artiști
    void displayArtists(List<Artist> artists);
    void displayArtistSearchResult(Artist artist);
    void displaySuccess(String message);

    // Metode pentru opere de artă
    void displayArtworks(List<Artwork> artworks);
    void displayArtworkSearchResult(Artwork artwork);
    void displayFilteredArtworks(List<Artwork> artworks);
    void confirmExportSuccess(String filePath);
    void confirmSuccess(String message);

    // Metodă comună pentru erori
    void showError(String message);
}