package Presenter;

import Model.Artist;
import Model.Artwork;
import java.util.List;

public interface IGalleryGUI {

    void displayArtists(List<Artist> artists);
    void displayArtistSearchResult(Artist artist);
    void displaySuccess(String message);


    void displayArtworks(List<Artwork> artworks);
    void displayArtworkSearchResult(Artwork artwork);
    void displayFilteredArtworks(List<Artwork> artworks);
    void confirmExportSuccess(String filePath);
    void confirmSuccess(String message);


    void showError(String message);
}