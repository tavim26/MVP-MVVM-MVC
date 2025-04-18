package Controller;

import Model.ViewModel.GalleryViewModel;
import View.GalleryView;

import java.util.List;
import java.util.Map;

public class GalleryController {

    private final GalleryViewModel viewModel;
    private final GalleryView view;

    public GalleryController(GalleryViewModel viewModel, GalleryView view) {
        this.viewModel = viewModel;
        this.view = view;
        this.view.setController(this);

        this.viewModel.addObserver(view);
    }

    public void refreshView() {
        view.updateArtistList(viewModel.getArtist());
        view.updateArtworkList(viewModel.getArtwork());
        view.updateArtistComboBoxes(viewModel.getArtistNames());
    }

    // === ARTIST ===

    public void addArtist(String name, String birthDate, String birthPlace, String nationality, String photo) {
        viewModel.addArtist(name, birthDate, birthPlace, nationality, photo);
    }

    public void updateArtist(String oldName, String name, String birthDate, String birthPlace, String nationality) {
        viewModel.updateArtist(oldName, name, birthDate, birthPlace, nationality);
    }

    public void deleteArtist(String name) {
        viewModel.deleteArtist(name);
    }

    public void searchArtist(String keyword) {
        List<String[]> results = (keyword == null || keyword.isBlank())
                ? viewModel.getArtist()
                : viewModel.searchArtist(keyword);
        view.updateArtistList(results);
    }

    // === ARTWORK ===

    public void addArtwork(String title, String artistName, String type, double price, int year) {
        viewModel.addArtwork(title, artistName, type, price, year);
    }

    public void updateArtwork(String oldTitle, String title, String artistName, String type, double price, int year) {
        viewModel.updateArtwork(oldTitle, title, artistName, type, price, year);
    }

    public void deleteArtwork(String title) {
        viewModel.deleteArtwork(title);
    }

    public void searchArtwork(String keyword) {
        List<String[]> results = viewModel.searchArtwork(keyword);
        view.updateArtworkList(results);
    }

    public void filterArtworksByType(String type) {
        view.updateArtworkList(viewModel.filterArtworkByType(type));
    }

    public void filterArtworksByArtist(String artistName) {
        view.updateArtworkList(viewModel.filterArtworkByArtist(artistName));
    }

    public void filterArtworksByPrice(double maxPrice) {
        view.updateArtworkList(viewModel.filterArtworkByPrice(maxPrice));
    }

    // === IMAGINI ===

    public void addImageToArtwork(String artworkTitle, String imagePath) {
        viewModel.addImageToArtwork(artworkTitle, imagePath);
    }

    public List<String> getArtworkImagePaths(String artworkTitle) {
        return viewModel.getArtworkImagePaths(artworkTitle);
    }

    // === EXPORT ===

    public void saveToCsv(String path) {
        viewModel.saveToCsv(path);
    }

    public void saveToDoc(String path) {
        viewModel.saveToDoc(path);
    }


    public void generateStatistics()
    {
        Map<String, Integer> typeData = viewModel.getArtworkCountsByType();
        Map<String, Integer> artistData = viewModel.getArtworkCountsByArtist();
        view.displayStatistics(typeData, artistData);
    }
}
