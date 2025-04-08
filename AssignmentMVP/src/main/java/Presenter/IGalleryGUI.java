package Presenter;

import java.util.List;

public interface IGalleryGUI {

    void displayArtists(List<String> artistData);
    void displaySuccess(String message);
    void displayArtworks(List<String> artworkData);
    void displayFilteredArtworks(List<String> artworkData);

    void populateArtistFields(String name, String birthDate, String birthPlace, String nationality, String photo);
    void populateArtworkFields(String title, String artistName, String type, String price, String year);
    void confirmExportSuccess(String filePath);
    void confirmSuccess(String message);
    void showError(String message);
    void clearArtistFields();
    void clearArtworkFields();
    void displayArtworkImages(List<String> imagePaths);
    void setArtistComboBoxItems(List<String> artistNames);
    void setFilterByArtistBoxItems(List<String> artistNames);
    void setFilterByTypeBoxItems(List<String> types);
    void setTypeComboBoxItems(List<String> types);
    void configureArtistTable();
    void configureArtworkTable();
    void setArtistFieldsEditable(boolean editable);
    void setArtworkFieldsEditable(boolean editable);
}