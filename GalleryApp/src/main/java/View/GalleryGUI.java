package View;

import Model.Artist;
import Model.Artwork;
import Model.ArtworkImage;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

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
    @FXML private Button searchArtworkButton;
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


    public GalleryGUI()
    {
        this.presenter = new GalleryPresenter(this);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle)
    {
        artistButton.setOnAction(event -> clickArtistButton());
        artworkButton.setOnAction(event -> clickArtworkButton());
        artistPane.setVisible(true);
        artworkPane.setVisible(false);

        configureArtistTable();
        artistTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                populateArtistFields(newSelection);
            }
        });

        configureArtworkTable();
        artworkTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                populateArtworkFields(newSelection);
                displayArtworkImages(newSelection);
            }
        });

        populateArtistComboBox();
        populateTypeComboBox();
        populateFilterBoxes();

        searchArtistTextField.textProperty().addListener((obs, oldValue, newValue) -> {
            if (newValue.isEmpty()) {
                presenter.refreshArtists();
            } else {
                presenter.searchArtistsByName(newValue);
            }
        });

        searchArtworkTextField.textProperty().addListener((obs, oldValue, newValue) -> {
            if (newValue.isEmpty()) {
                presenter.refreshArtworks();
            } else {
                presenter.searchArtworksByTitle(newValue);
            }
        });

        filterByArtistBox.valueProperty().addListener((obs, oldValue, newValue) -> applyFilters());
        filterByTypeBox.valueProperty().addListener((obs, oldValue, newValue) -> applyFilters());
        filterByPriceField.textProperty().addListener((obs, oldValue, newValue) -> applyFilters());

        presenter.refreshArtists();
        presenter.refreshArtworks();
        applyFilters();
        initializeArtistFieldsState();
        initializeArtworkFieldsState();
    }

    private void applyFilters()
    {
        String artistName = filterByArtistBox.getValue();
        String type = filterByTypeBox.getValue();
        String priceText = filterByPriceField.getText().trim();

        List<Artwork> filteredArtworks = new ArrayList<>(presenter.getArtworksTable());

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

        if (!priceText.isEmpty()) {
            double maxPrice = parseDouble(priceText, "Preț maxim");
            if (maxPrice >= 0) {
                filteredArtworks = filteredArtworks.stream()
                        .filter(artwork -> artwork.getPrice() < maxPrice)
                        .collect(Collectors.toList());
            }
        }

        displayFilteredArtworks(filteredArtworks);
    }



    private void configureArtistTable() {
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
                List<String> titles = presenter.getArtworksByArtist(cellData.getValue().getName());
                return new SimpleStringProperty(String.join(", ", titles));
            } catch (Exception e) {
                return new SimpleStringProperty("Eroare la incarcare");
            }
        });
    }

    private void configureArtworkTable() {
        TableColumn<Artwork, String> titleColumn = (TableColumn<Artwork, String>) artworkTable.getColumns().get(0);
        TableColumn<Artwork, String> artistNameColumn = (TableColumn<Artwork, String>) artworkTable.getColumns().get(1);
        TableColumn<Artwork, String> typeColumn = (TableColumn<Artwork, String>) artworkTable.getColumns().get(2);
        TableColumn<Artwork, String> priceColumn = (TableColumn<Artwork, String>) artworkTable.getColumns().get(3);
        TableColumn<Artwork, String> creationYearColumn = (TableColumn<Artwork, String>) artworkTable.getColumns().get(4);

        titleColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTitle()));
        artistNameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getArtist() != null ? cellData.getValue().getArtist().getName() : ""));
        typeColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getType()));
        priceColumn.setCellValueFactory(cellData -> new SimpleStringProperty(String.valueOf(cellData.getValue().getPrice())));
        creationYearColumn.setCellValueFactory(cellData -> new SimpleStringProperty(String.valueOf(cellData.getValue().getCreationYear())));
    }






    private void populateArtistComboBox()
    {
        List<String> artistNames = new ArrayList<>(presenter.getArtistsTable().stream()
                .map(Artist::getName).collect(Collectors.toList()));
        artistNames.add(0, "All");
        artistComboBox.setItems(FXCollections.observableArrayList(artistNames));
        artistComboBox.setValue("All");
    }

    private void populateTypeComboBox() {
        typeComboBox.getItems().addAll("Painting", "Sculpture", "Photography");
    }

    private void populateFilterBoxes() {
        List<String> artistNames = new ArrayList<>(presenter.getArtistsTable().stream()
                .map(Artist::getName).collect(Collectors.toList()));
        artistNames.add(0, "All");
        filterByArtistBox.setItems(FXCollections.observableArrayList(artistNames));
        filterByArtistBox.setValue("All");

        List<String> types = new ArrayList<>(List.of("Painting", "Sculpture", "Photography"));
        types.add(0, "All");
        filterByTypeBox.setItems(FXCollections.observableArrayList(types));
        filterByTypeBox.setValue("All");
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
        if (!addArtistBox.isSelected()) {
            showError("Activate Add checkbox!");
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg"));
        File selectedFile = fileChooser.showOpenDialog(addArtistButton.getScene().getWindow());

        String photoPath = selectedFile != null ? selectedFile.toURI().toString() : "";
        if (selectedFile != null) {
            artistPhoto.setImage(new Image(photoPath));
        }

        String name = nameTextField.getText();
        String birthPlace = birthplaceTextField.getText();
        String birthDate = birthdayDatePicker.getValue() != null ? birthdayDatePicker.getValue().toString() : "";
        String nationality = nationalityTextField.getText();

        if (confirmAction("Are you sure you want to add artist?")) {
            presenter.addArtist(name, birthDate, birthPlace, nationality, photoPath);
            clearArtistFields();
            addArtistBox.setSelected(false); // Dezactivează după adăugare
        }
    }

    @FXML
    private void clickEditArtist() {
        if (!editArtistBox.isSelected()) {
            showError("Activate Edit checkbox!");
            return;
        }

        Artist selectedArtist = artistTable.getSelectionModel().getSelectedItem();
        if (selectedArtist == null) {
            showError("Select and artist first!");
            return;
        }

        String oldName = selectedArtist.getName();
        String newName = nameTextField.getText();
        String birthPlace = birthplaceTextField.getText();
        String birthDate = birthdayDatePicker.getValue() != null ? birthdayDatePicker.getValue().toString() : "";
        String nationality = nationalityTextField.getText();
        String photoPath = artistPhoto.getImage() != null ? artistPhoto.getImage().getUrl() : selectedArtist.getPhoto();

        if (confirmAction("Are you sure you want to edit artist?")) {
            presenter.updateArtist(oldName, newName, birthDate, birthPlace, nationality, photoPath);
            clearArtistFields();
            editArtistBox.setSelected(false);
        }
    }

    @FXML
    private void clickDeleteArtist() {
        Artist selectedArtist = artistTable.getSelectionModel().getSelectedItem();
        if (selectedArtist == null) {
            showError("Select an artist first!");
            return;
        }

        if (confirmAction("Are you sure you want to delete artist?")) {
            presenter.deleteArtist(selectedArtist.getName());
            clearArtistFields();
        }
    }







    @FXML
    private void clickAddArtwork() {
        if (!addArtworkBox.isSelected()) {
            showError("Activate Add checkbox");
            return;
        }

        String title = titleTextField.getText().trim();
        String artistName = artistComboBox.getValue();
        String type = typeComboBox.getValue();
        double price = parseDouble(priceTextField.getText(), "Preț");
        int creationYear = parseInt(yearTextField.getText(), "An");

        if (price >= 0 && creationYear >= 0 && confirmAction("Are you sure you want to add artwork?")) {
            presenter.addArtwork(title, artistName, type, price, creationYear);
            clearArtworkFields();
            addArtworkBox.setSelected(false);
        }
    }

    @FXML
    private void clickEditArtwork() {
        if (!editArtworkBox.isSelected()) {
            showError("Activate Edit checkbox!");
            return;
        }

        Artwork selectedArtwork = artworkTable.getSelectionModel().getSelectedItem();
        if (selectedArtwork == null) {
            showError("Select an artwork first!");
            return;
        }

        String oldTitle = selectedArtwork.getTitle();
        String newTitle = titleTextField.getText().trim();
        String artistName = artistComboBox.getValue();
        String type = typeComboBox.getValue();
        double price = parseDouble(priceTextField.getText(), "Preț");
        int creationYear = parseInt(yearTextField.getText(), "An");

        if (price >= 0 && creationYear >= 0 && confirmAction("Are you sure you want to edit artwork?")) {
            presenter.updateArtwork(oldTitle, newTitle, artistName, type, price, creationYear);
            clearArtworkFields();
            editArtworkBox.setSelected(false);
        }
    }

    @FXML
    private void clickDeleteArtwork() {
        Artwork selectedArtwork = artworkTable.getSelectionModel().getSelectedItem();
        if (selectedArtwork == null) {
            showError("Select and artwork first!");
            return;
        }

        if (confirmAction("Are you sure you want to delete artwork?")) {
            presenter.deleteArtwork(selectedArtwork.getTitle());
            clearArtworkFields();
        }
    }





    @FXML
    private void clickAddImage() {
        Artwork selectedArtwork = artworkTable.getSelectionModel().getSelectedItem();
        if (selectedArtwork == null) {
            showError("Select an artwork first!");
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg"));
        File selectedFile = fileChooser.showOpenDialog(addImageButton.getScene().getWindow());

        if (selectedFile != null) {
            String imagePath = selectedFile.toURI().toString();
            presenter.addArtworkImage(selectedArtwork.getTitle(), imagePath);
            displayArtworkImages(selectedArtwork);
        }
    }

    @FXML
    private void clickSaveToCsv() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
        File file = fileChooser.showSaveDialog(saveToCsvButton.getScene().getWindow());
        if (file != null) {
            presenter.exportArtworksToCSV(file.getAbsolutePath());
        }
    }

    @FXML
    private void clickSaveToDoc() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text Files", "*.txt"));
        File file = fileChooser.showSaveDialog(saveToDocButton.getScene().getWindow());
        if (file != null) {
            presenter.exportArtworksToText(file.getAbsolutePath());
        }
    }







    //metode ajutatoare

    private void populateArtistFields(Artist artist) {
        nameTextField.setText(artist.getName());
        birthplaceTextField.setText(artist.getBirthPlace());
        birthdayDatePicker.setValue(artist.getBirthDate().isEmpty() ? null : java.time.LocalDate.parse(artist.getBirthDate()));
        nationalityTextField.setText(artist.getNationality());
        artistPhoto.setImage(artist.getPhoto() != null && !artist.getPhoto().isEmpty() ? new Image(artist.getPhoto()) : null);
    }

    private void clearArtistFields() {
        nameTextField.clear();
        birthplaceTextField.clear();
        birthdayDatePicker.setValue(null);
        nationalityTextField.clear();
        artistPhoto.setImage(null);
    }

    private void populateArtworkFields(Artwork artwork) {
        titleTextField.setText(artwork.getTitle());
        artistComboBox.setValue(artwork.getArtist() != null ? artwork.getArtist().getName() : null);
        typeComboBox.setValue(artwork.getType());
        priceTextField.setText(String.valueOf(artwork.getPrice()));
        yearTextField.setText(String.valueOf(artwork.getCreationYear()));
    }

    private void clearArtworkFields() {
        titleTextField.clear();
        artistComboBox.setValue(null);
        typeComboBox.setValue(null);
        priceTextField.clear();
        yearTextField.clear();
        artworkImage1.setImage(null);
        artworkImage2.setImage(null);
    }

    private void displayArtworkImages(Artwork artwork) {
        try {
            List<ArtworkImage> images = presenter.getImagesForArtwork(artwork.getTitle());
            artworkImage1.setImage(images.size() > 0 ? new Image(images.get(0).getImagePath()) : null);
            artworkImage2.setImage(images.size() > 1 ? new Image(images.get(1).getImagePath()) : null);
        } catch (SQLException e) {
            showError("Load image error " + e.getMessage());
        }
    }

    private double parseDouble(String value, String fieldName) {
        try {
            double result = Double.parseDouble(value.trim());
            if (result < 0) {
                showError(fieldName + " cannot be negative!");
                return -1;
            }
            return result;
        } catch (NumberFormatException e) {
            showError("Introduce a valid  " + fieldName.toLowerCase());
            return -1;
        }
    }

    private int parseInt(String value, String fieldName) {
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException e) {
            showError("Introduce a valid  " + fieldName.toLowerCase());
            return -1;
        }
    }

    private boolean confirmAction(String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, message, ButtonType.YES, ButtonType.NO);
        alert.setTitle("Confirmation");
        alert.setHeaderText(null);
        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == ButtonType.YES;
    }



    @Override
    public void displayArtists(List<Artist> artists) {
        artistTable.setItems(FXCollections.observableArrayList(artists));
        populateArtistComboBox();
        populateFilterBoxes();
    }

    @Override
    public void displayArtistSearchResult(Artist artist) {
        artistTable.setItems(FXCollections.observableArrayList(artist));
        populateArtistFields(artist);
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
        populateArtworkFields(artwork);
        displayArtworkImages(artwork);
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

    private void initializeArtistFieldsState() {
        nameTextField.setEditable(false);
        birthplaceTextField.setEditable(false);
        birthdayDatePicker.setEditable(false);
        nationalityTextField.setEditable(false);

        addArtistBox.setOnAction(event -> {
            if (addArtistBox.isSelected()) {
                editArtistBox.setSelected(false);
                clearArtistFields();
            }
        });

        editArtistBox.setOnAction(event -> {
            if (editArtistBox.isSelected()) {
                addArtistBox.setSelected(false);
                nameTextField.setEditable(true);
                birthplaceTextField.setEditable(true);
                birthdayDatePicker.setEditable(true);
                nationalityTextField.setEditable(true);
            } else {
                nameTextField.setEditable(false);
                birthplaceTextField.setEditable(false);
                birthdayDatePicker.setEditable(false);
                nationalityTextField.setEditable(false);
            }
        });
    }

    private void initializeArtworkFieldsState() {
        titleTextField.setEditable(false);
        artistComboBox.setDisable(true);
        typeComboBox.setDisable(true);
        priceTextField.setEditable(false);
        yearTextField.setEditable(false);

        addArtworkBox.setOnAction(event -> {
            if (addArtworkBox.isSelected()) {
                editArtworkBox.setSelected(false);
                clearArtworkFields();
            }
        });

        editArtworkBox.setOnAction(event -> {
            if (editArtworkBox.isSelected()) {
                addArtworkBox.setSelected(false);
                titleTextField.setEditable(true);
                artistComboBox.setDisable(false);
                typeComboBox.setDisable(false);
                priceTextField.setEditable(true);
                yearTextField.setEditable(true);
            } else {
                titleTextField.setEditable(false);
                artistComboBox.setDisable(true);
                typeComboBox.setDisable(true);
                priceTextField.setEditable(false);
                yearTextField.setEditable(false);
            }
        });
    }

    public void checkAddArtist(ActionEvent actionEvent) {
    }

    public void checkEditArtist(ActionEvent actionEvent) {
    }

    public void checkAddArtwork(ActionEvent actionEvent) {
    }

    public void checkEditArtwork(ActionEvent actionEvent) {
    }


}