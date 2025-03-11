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

    void populateArtistFields(Artist artist);

    void clearArtistFields();

    void populateArtworkFields(Artwork artwork);

    void clearArtworkFields();

    void displayArtworkImages(List<String> imagePaths);

    boolean confirmAction(String message);

    void setArtistFieldsEditable(boolean editable);

    void setArtworkFieldsEditable(boolean editable);

    void setArtistComboBoxItems(List<String> artistNames);

    void setFilterByArtistBoxItems(List<String> artistNames);

    void setFilterByTypeBoxItems(List<String> types);

    void setTypeComboBoxItems(List<String> types);

    void configureArtistTable();

    void configureArtworkTable();

}