package View;

import Model.Artist;
import Model.Artwork;
import Presenter.GalleryPresenter;
import Presenter.IGalleryGUI;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;

import java.io.File;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;

public class GalleryGUI implements IGalleryGUI, Initializable {

    private final GalleryPresenter presenter;

    @FXML private Button artistButton;
    @FXML private Button artworkButton;
    @FXML private AnchorPane artistPane;
    @FXML private AnchorPane artworkPane;

    @FXML private TableView<Artist> artistTable;
    @FXML private TextField nameTextField;
    @FXML private TextField birthplaceTextField;
    @FXML private DatePicker birthdayDatePicker;
    @FXML private TextField nationalityTextField;
    @FXML private ImageView artistPhoto;
    @FXML private Button addArtistButton;
    @FXML private Button editArtistButton;
    @FXML private Button deleteArtistButton;
    @FXML private TextField searchArtistTextField;

    @FXML private TableView<Artwork> artworkTable;
    @FXML private TextField titleTextField;
    @FXML private ComboBox<String> artistComboBox;
    @FXML private ComboBox<String> typeComboBox;
    @FXML private TextField priceTextField;
    @FXML private TextField yearTextField;
    @FXML private TextField searchArtworkTextField;
    @FXML private ComboBox<String> filterByArtistBox;
    @FXML private ComboBox<String> filterByTypeBox;
    @FXML private TextField filterByPriceField;
    @FXML private Button addArtworkButton;
    @FXML private Button editArtworkButton;
    @FXML private Button deleteArtworkButton;
    @FXML private Button addImageButton;
    @FXML private Button saveToCsvButton;
    @FXML private Button saveToDocButton;
    @FXML private ImageView artworkImage1;
    @FXML private ImageView artworkImage2;

    @FXML private CheckBox addArtworkBox;
    @FXML private CheckBox editArtworkBox;
    @FXML private CheckBox addArtistBox;
    @FXML private CheckBox editArtistBox;

    public GalleryGUI() {
        this.presenter = new GalleryPresenter(this);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        artistButton.setOnAction(event -> clickArtistButton());
        artworkButton.setOnAction(event -> clickArtworkButton());
        artistPane.setVisible(true);
        artworkPane.setVisible(false);

        configureArtistTable();
        configureArtworkTable();

        artistTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                presenter.populateArtistFields(newSelection);
            } else {
                presenter.clearArtistFields();
            }
        });
        artworkTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                presenter.populateArtworkFields(newSelection);
                try {
                    presenter.displayArtworkImages(newSelection.getTitle());
                } catch (SQLException e) {
                    showError("Error loading artwork images: " + e.getMessage());
                }
            } else {
                presenter.clearArtworkFields();
                artworkImage1.setImage(null);
                artworkImage2.setImage(null);
            }
        });

        searchArtistTextField.textProperty().addListener((obs, oldValue, newValue) ->
                presenter.searchArtistsByName(newValue));
        searchArtworkTextField.textProperty().addListener((obs, oldValue, newValue) ->
                presenter.searchArtworksByTitle(newValue));

        filterByArtistBox.valueProperty().addListener((obs, oldValue, newValue) ->
                presenter.applyFilters(filterByArtistBox.getValue(), filterByTypeBox.getValue(), filterByPriceField.getText()));
        filterByTypeBox.valueProperty().addListener((obs, oldValue, newValue) ->
                presenter.applyFilters(filterByArtistBox.getValue(), filterByTypeBox.getValue(), filterByPriceField.getText()));
        filterByPriceField.textProperty().addListener((obs, oldValue, newValue) ->
                presenter.applyFilters(filterByArtistBox.getValue(), filterByTypeBox.getValue(), filterByPriceField.getText()));

        addArtistBox.setOnAction(event -> presenter.handleAddArtistBox(addArtistBox.isSelected()));
        editArtistBox.setOnAction(event -> presenter.handleEditArtistBox(editArtistBox.isSelected()));
        addArtworkBox.setOnAction(event -> presenter.handleAddArtworkBox(addArtworkBox.isSelected()));
        editArtworkBox.setOnAction(event -> presenter.handleEditArtworkBox(editArtworkBox.isSelected()));

        presenter.initializeComboBoxItems();

        try {
            presenter.refreshArtists();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        try {
            presenter.refreshArtworks();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void configureArtistTable() {
        TableColumn<Artist, String> nameColumn = (TableColumn<Artist, String>) artistTable.getColumns().get(0);
        TableColumn<Artist, String> birthdateColumn = (TableColumn<Artist, String>) artistTable.getColumns().get(1);
        TableColumn<Artist, String> birthplaceColumn = (TableColumn<Artist, String>) artistTable.getColumns().get(2);
        TableColumn<Artist, String> nationalityColumn = (TableColumn<Artist, String>) artistTable.getColumns().get(3);
        TableColumn<Artist, String> artworksColumn = (TableColumn<Artist, String>) artistTable.getColumns().get(4);

        nameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getName()));
        birthdateColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getBirthDate()));
        birthplaceColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getBirthPlace()));
        nationalityColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getNationality()));
        artworksColumn.setCellValueFactory(cellData -> {
            try {
                return new SimpleStringProperty(String.join(", ", presenter.getArtworksByArtist(cellData.getValue().getName())));
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public void configureArtworkTable() {
        TableColumn<Artwork, String> titleColumn = (TableColumn<Artwork, String>) artworkTable.getColumns().get(0);
        TableColumn<Artwork, String> artistNameColumn = (TableColumn<Artwork, String>) artworkTable.getColumns().get(1);
        TableColumn<Artwork, String> typeColumn = (TableColumn<Artwork, String>) artworkTable.getColumns().get(2);
        TableColumn<Artwork, String> priceColumn = (TableColumn<Artwork, String>) artworkTable.getColumns().get(3);
        TableColumn<Artwork, String> creationYearColumn = (TableColumn<Artwork, String>) artworkTable.getColumns().get(4);

        titleColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTitle()));
        artistNameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getArtist().getName()));
        typeColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getType()));
        priceColumn.setCellValueFactory(cellData -> new SimpleStringProperty(String.valueOf(cellData.getValue().getPrice())));
        creationYearColumn.setCellValueFactory(cellData -> new SimpleStringProperty(String.valueOf(cellData.getValue().getCreationYear())));
    }

    @FXML
    private void clickArtistButton() {
        artistPane.setVisible(true);
        artworkPane.setVisible(false);
    }

    @FXML
    private void clickArtworkButton() {
        artistPane.setVisible(false);
        artworkPane.setVisible(true);
    }

    @FXML
    private void clickAddArtist() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg"));
        File selectedFile = fileChooser.showOpenDialog(addArtistButton.getScene().getWindow());
        String photoPath = selectedFile != null ? selectedFile.toURI().toString() : "";

        presenter.addArtist(
                nameTextField.getText(),
                birthdayDatePicker.getValue() != null ? birthdayDatePicker.getValue().toString() : "",
                birthplaceTextField.getText(),
                nationalityTextField.getText(),
                photoPath
        );
    }

    @FXML
    private void clickEditArtist() {
        Artist selectedArtist = artistTable.getSelectionModel().getSelectedItem();
        presenter.updateArtist(
                selectedArtist.getName(),
                nameTextField.getText(),
                birthdayDatePicker.getValue() != null ? birthdayDatePicker.getValue().toString() : "",
                birthplaceTextField.getText(),
                nationalityTextField.getText(),
                artistPhoto.getImage() != null ? artistPhoto.getImage().getUrl() : selectedArtist.getPhoto()
        );
    }

    @FXML
    private void clickDeleteArtist() {
        Artist selectedArtist = artistTable.getSelectionModel().getSelectedItem();
        presenter.deleteArtist(selectedArtist.getName());
    }

    @FXML
    private void clickAddArtwork() {
        presenter.addArtwork(
                titleTextField.getText().trim(),
                artistComboBox.getValue(),
                typeComboBox.getValue(),
                Double.parseDouble(priceTextField.getText()),
                Integer.parseInt(yearTextField.getText())
        );
    }

    @FXML
    private void clickEditArtwork() {
        Artwork selectedArtwork = artworkTable.getSelectionModel().getSelectedItem();
        presenter.updateArtwork(
                selectedArtwork.getTitle(),
                titleTextField.getText().trim(),
                artistComboBox.getValue(),
                typeComboBox.getValue(),
                Double.parseDouble(priceTextField.getText()),
                Integer.parseInt(yearTextField.getText())
        );
    }

    @FXML
    private void clickDeleteArtwork() {
        Artwork selectedArtwork = artworkTable.getSelectionModel().getSelectedItem();
        presenter.deleteArtwork(selectedArtwork.getTitle());
    }

    @FXML
    private void clickAddImage() {
        Artwork selectedArtwork = artworkTable.getSelectionModel().getSelectedItem();
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg"));
        File selectedFile = fileChooser.showOpenDialog(addImageButton.getScene().getWindow());
        String imagePath = selectedFile != null ? selectedFile.toURI().toString() : "";
        presenter.addArtworkImage(selectedArtwork.getTitle(), imagePath);
    }

    @FXML
    private void clickSaveToCsv() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
        File file = fileChooser.showSaveDialog(saveToCsvButton.getScene().getWindow());
        presenter.exportArtworksToCSV(file.getAbsolutePath());
    }

    @FXML
    private void clickSaveToDoc() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text Files", "*.txt"));
        File file = fileChooser.showSaveDialog(saveToDocButton.getScene().getWindow());
        presenter.exportArtworksToText(file.getAbsolutePath());
    }

    // IGalleryGUI methods
    @Override
    public void displayArtists(List<Artist> artists) {
        artistTable.setItems(FXCollections.observableArrayList(artists));
    }

    @Override
    public void displayArtistSearchResult(Artist artist) {
        artistTable.setItems(FXCollections.observableArrayList(artist));
    }

    @Override
    public void displaySuccess(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, message, ButtonType.OK);
        alert.setTitle("Success");
        alert.setHeaderText(null);
        alert.showAndWait();
    }

    @Override
    public void displayArtworks(List<Artwork> artworks) {
        artworkTable.setItems(FXCollections.observableArrayList(artworks));
    }

    @Override
    public void displayArtworkSearchResult(Artwork artwork) {
        artworkTable.setItems(FXCollections.observableArrayList(artwork));
    }

    @Override
    public void displayFilteredArtworks(List<Artwork> artworks) {
        artworkTable.setItems(FXCollections.observableArrayList(artworks));
    }

    @Override
    public void confirmExportSuccess(String filePath) {
        displaySuccess("Export was successful in the path: " + filePath);
    }

    @Override
    public void confirmSuccess(String message) {
        displaySuccess(message);
    }

    @Override
    public void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR, message, ButtonType.OK);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.showAndWait();
    }

    @Override
    public void populateArtistFields(Artist artist) {
        nameTextField.setText(artist.getName());
        birthplaceTextField.setText(artist.getBirthPlace());
        birthdayDatePicker.setValue(artist.getBirthDate().isEmpty() ? null : java.time.LocalDate.parse(artist.getBirthDate()));
        nationalityTextField.setText(artist.getNationality());
        artistPhoto.setImage(artist.getPhoto() != null && !artist.getPhoto().isEmpty() ? new Image(artist.getPhoto()) : null);
    }

    @Override
    public void clearArtistFields() {
        nameTextField.clear();
        birthplaceTextField.clear();
        birthdayDatePicker.setValue(null);
        nationalityTextField.clear();
        artistPhoto.setImage(null);
    }

    @Override
    public void populateArtworkFields(Artwork artwork) {
        titleTextField.setText(artwork.getTitle());
        artistComboBox.setValue(artwork.getArtist().getName());
        typeComboBox.setValue(artwork.getType());
        priceTextField.setText(String.valueOf(artwork.getPrice()));
        yearTextField.setText(String.valueOf(artwork.getCreationYear()));
    }

    @Override
    public void clearArtworkFields() {
        titleTextField.clear();
        artistComboBox.setValue(null);
        typeComboBox.setValue(null);
        priceTextField.clear();
        yearTextField.clear();
        artworkImage1.setImage(null);
        artworkImage2.setImage(null);
    }

    @Override
    public void displayArtworkImages(List<String> imagePaths) {
        artworkImage1.setImage(imagePaths.size() > 0 ? new Image(imagePaths.get(0)) : null);
        artworkImage2.setImage(imagePaths.size() > 1 ? new Image(imagePaths.get(1)) : null);
    }

    @Override
    public boolean confirmAction(String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, message, ButtonType.YES, ButtonType.NO);
        alert.setTitle("Confirmation");
        alert.setHeaderText(null);
        return alert.showAndWait().filter(ButtonType.YES::equals).isPresent();
    }

    @Override
    public void setArtistFieldsEditable(boolean editable) {
        nameTextField.setEditable(editable);
        birthplaceTextField.setEditable(editable);
        birthdayDatePicker.setEditable(editable);
        nationalityTextField.setEditable(editable);
    }

    @Override
    public void setArtworkFieldsEditable(boolean editable) {
        titleTextField.setEditable(editable);
        artistComboBox.setDisable(!editable);
        typeComboBox.setDisable(!editable);
        priceTextField.setEditable(editable);
        yearTextField.setEditable(editable);
    }

    @Override
    public void setArtistComboBoxItems(List<String> artistNames) {
        artistComboBox.setItems(FXCollections.observableArrayList(artistNames));
    }

    @Override
    public void setFilterByArtistBoxItems(List<String> artistNames) {
        filterByArtistBox.setItems(FXCollections.observableArrayList(artistNames));
    }

    @Override
    public void setFilterByTypeBoxItems(List<String> types) {
        filterByTypeBox.setItems(FXCollections.observableArrayList(types));
    }

    @Override
    public void setTypeComboBoxItems(List<String> types) {
        typeComboBox.setItems(FXCollections.observableArrayList(types));
    }

    public void checkAddArtwork(ActionEvent actionEvent) {
    }

    public void checkEditArtwork(ActionEvent actionEvent) {
    }

    public void checkAddArtist(ActionEvent actionEvent) {
    }

    public void checkEditArtist(ActionEvent actionEvent) {
    }
}