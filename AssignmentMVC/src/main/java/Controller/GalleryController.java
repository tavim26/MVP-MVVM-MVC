package Controller;

import Model.ViewModel.GalleryViewModel;
import View.GalleryView;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.stage.FileChooser;

import java.io.File;
import java.util.List;
import java.util.Map;

public class GalleryController
{

    private final GalleryViewModel viewModel;
    private final GalleryView view;

    public GalleryController(GalleryViewModel viewModel, GalleryView view)
    {
        this.viewModel = viewModel;
        this.view = view;
        this.viewModel.addObserver(view);

        initializeEventHandlers();
    }


    private void initializeEventHandlers() {
        // Navigare între tab-uri
        view.getArtistButton().setOnAction(e -> showArtistPane());
        view.getArtworkButton().setOnAction(e -> showArtworkPane());
        view.getStatisticsButton().setOnAction(e -> showStatisticsPane());

        // Artist CRUD
        view.getAddArtistButton().setOnAction(e -> handleAddArtist());
        view.getEditArtistButton().setOnAction(e -> handleEditArtist());
        view.getDeleteArtistButton().setOnAction(e -> handleDeleteArtist());
        view.getClearArtistButton().setOnAction(e -> clearArtistFields());

        // Artwork CRUD
        view.getAddArtworkButton().setOnAction(e -> handleAddArtwork());
        view.getEditArtworkButton().setOnAction(e -> handleEditArtwork());
        view.getDeleteArtworkButton().setOnAction(e -> handleDeleteArtwork());
        view.getClearArtworkButton().setOnAction(e -> clearArtworkFields());

        // Export
        view.getSaveToCsvButton().setOnAction(e -> handleSaveToCsv());
        view.getSaveToDocButton().setOnAction(e -> handleSaveToDoc());


        // Imagini
        view.getAddImageButton().setOnAction(e -> handleAddImageToArtwork());

        // Filtrare/Căutare
        view.getSearchArtistTextField().textProperty().addListener((obs, old, val) -> searchArtist(val));
        view.getSearchArtworkTextField().textProperty().addListener((obs, old, val) -> searchArtwork(val));

        view.getFilterByArtistBox().valueProperty().addListener((obs, old, val) -> {
            if (val != null) filterArtworksByArtist(val);
        });
        view.getFilterByTypeBox().valueProperty().addListener((obs, old, val) -> {
            if (val != null) filterArtworksByType(val);
        });
        view.getPriceSlider().valueProperty().addListener((obs, old, val) -> {
            filterArtworksByPrice(val.doubleValue());
        });


    }


    private void showArtistPane() {
        view.getArtistPane().setVisible(true);
        view.getArtworkPane().setVisible(false);
        view.getStatisticsPane().setVisible(false);
    }

    private void showArtworkPane() {
        view.getArtistPane().setVisible(false);
        view.getArtworkPane().setVisible(true);
        view.getStatisticsPane().setVisible(false);
    }

    private void showStatisticsPane() {
        view.getArtistPane().setVisible(false);
        view.getArtworkPane().setVisible(false);
        view.getStatisticsPane().setVisible(true);
        generateStatistics();
    }

// === ARTIST ===

    private void handleAddArtist() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Artist Photo");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );
        File file = fileChooser.showOpenDialog(null);
        String photoPath = (file != null) ? file.toURI().toString() : "";

        viewModel.addArtist(
                view.getNameTextField().getText(),
                view.getBirthdayDatePicker().getValue() != null ? view.getBirthdayDatePicker().getValue().toString() : "",
                view.getBirthplaceTextField().getText(),
                view.getNationalityTextField().getText(),
                photoPath
        );
    }

    private void handleEditArtist() {
        String[] selected = view.getArtistTable().getSelectionModel().getSelectedItem();
        if (selected == null) return;

        viewModel.updateArtist(
                selected[0],
                view.getNameTextField().getText(),
                view.getBirthdayDatePicker().getValue() != null ? view.getBirthdayDatePicker().getValue().toString() : "",
                view.getBirthplaceTextField().getText(),
                view.getNationalityTextField().getText()
        );
    }

    private void handleDeleteArtist() {
        viewModel.deleteArtist(view.getNameTextField().getText());
    }

    private void clearArtistFields() {
        view.getNameTextField().clear();
        view.getBirthdayDatePicker().setValue(null);
        view.getBirthplaceTextField().clear();
        view.getNationalityTextField().clear();
        view.getSearchArtistTextField().clear();
        view.getArtistPhoto().setImage(null);
    }

    private void searchArtist(String keyword) {
        List<String[]> results = (keyword == null || keyword.isBlank())
                ? viewModel.getArtist()
                : viewModel.searchArtist(keyword);
        view.getArtistTable().setItems(javafx.collections.FXCollections.observableArrayList(results));
    }

// === ARTWORK ===

    private void handleAddArtwork() {
        try {
            viewModel.addArtwork(
                    view.getTitleTextField().getText(),
                    view.getArtistComboBox().getValue(),
                    view.getTypeComboBox().getValue(),
                    Double.parseDouble(view.getPriceTextField().getText()),
                    Integer.parseInt(view.getYearTextField().getText())
            );
        } catch (NumberFormatException ex) {
            ex.printStackTrace(); // Poți adăuga alertă pentru UI
        }
    }

    private void handleEditArtwork() {
        String[] selected = view.getArtworkTable().getSelectionModel().getSelectedItem();
        if (selected == null) return;

        try {
            viewModel.updateArtwork(
                    selected[0],
                    view.getTitleTextField().getText(),
                    view.getArtistComboBox().getValue(),
                    view.getTypeComboBox().getValue(),
                    Double.parseDouble(view.getPriceTextField().getText()),
                    Integer.parseInt(view.getYearTextField().getText())
            );
        } catch (NumberFormatException ex) {
            ex.printStackTrace();
        }
    }

    private void handleDeleteArtwork() {
        viewModel.deleteArtwork(view.getTitleTextField().getText());
    }

    private void clearArtworkFields() {
        view.getTitleTextField().clear();
        view.getArtistComboBox().setValue(null);
        view.getTypeComboBox().setValue(null);
        view.getPriceTextField().clear();
        view.getYearTextField().clear();
        view.getSearchArtworkTextField().clear();
        view.getFilterByArtistBox().setValue(null);
        view.getFilterByTypeBox().setValue(null);
        view.getArtworkImage1().setImage(null);
        view.getArtworkImage2().setImage(null);
    }

    private void searchArtwork(String keyword) {
        List<String[]> results = viewModel.searchArtwork(keyword);
        view.getArtworkTable().setItems(javafx.collections.FXCollections.observableArrayList(results));
    }

    private void filterArtworksByArtist(String artistName) {
        List<String[]> results = viewModel.filterArtworkByArtist(artistName);
        view.getArtworkTable().setItems(javafx.collections.FXCollections.observableArrayList(results));
    }

    private void filterArtworksByType(String type) {
        List<String[]> results = viewModel.filterArtworkByType(type);
        view.getArtworkTable().setItems(javafx.collections.FXCollections.observableArrayList(results));
    }

    private void filterArtworksByPrice(double maxPrice) {
        List<String[]> results = viewModel.filterArtworkByPrice(maxPrice);
        view.getArtworkTable().setItems(javafx.collections.FXCollections.observableArrayList(results));
    }

    private void handleAddImageToArtwork() {
        String artworkTitle = view.getTitleTextField().getText();
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Image");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );
        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            viewModel.addImageToArtwork(artworkTitle, file.getAbsolutePath());
        }
    }

// === EXPORT ===

    private void handleSaveToCsv() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save as CSV");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
        File file = fileChooser.showSaveDialog(null);
        if (file != null) viewModel.saveToCsv(file.getAbsolutePath());
    }

    private void handleSaveToDoc() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save as Text Document");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text Files", "*.txt"));
        File file = fileChooser.showSaveDialog(null);
        if (file != null) viewModel.saveToDoc(file.getAbsolutePath());
    }

// === STATISTICS ===

    public void generateStatistics() {
        var typeData = viewModel.getArtworkCountsByType();
        var artistData = viewModel.getArtworkCountsByArtist();

        BarChart<String, Number> typeChart = view.getTypeChart();
        BarChart<String, Number> artistChart = view.getArtistChart();

        typeChart.getData().clear();
        artistChart.getData().clear();

        XYChart.Series<String, Number> typeSeries = new XYChart.Series<>();
        for (var entry : typeData.entrySet()) {
            typeSeries.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
        }
        typeChart.getData().add(typeSeries);

        XYChart.Series<String, Number> artistSeries = new XYChart.Series<>();
        for (var entry : artistData.entrySet()) {
            artistSeries.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
        }
        artistChart.getData().add(artistSeries);
    }



}
