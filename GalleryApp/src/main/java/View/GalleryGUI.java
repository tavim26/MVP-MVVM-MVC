package View;

import Model.Artist;
import Model.Artwork;
import Presenter.GalleryPresenter;
import Presenter.IGalleryGUI;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class GalleryGUI implements IGalleryGUI, Initializable {

    // Presenter
    private final GalleryPresenter presenter;

    @FXML
    private Button artistButton;

    @FXML
    private Button artworkButton;

    @FXML
    private AnchorPane artistPane;

    @FXML
    private AnchorPane artworkPane;

    @FXML
    private TableView<Artist> artistTable;

    @FXML
    private TextField nameTextField;

    @FXML
    private TextField birthplaceTextField;

    @FXML
    private DatePicker birthdayDatePicker;

    @FXML
    private TextField nationalityTextField;

    @FXML
    private ImageView artistPhoto;

    @FXML
    private Button addArtistButton;

    @FXML
    private Button editArtistButton;

    @FXML
    private Button deleteArtistButton;


    @FXML
    private TextField searchArtistTextField;

    @FXML
    private Button searchArtistButton;

    @FXML
    private TableView<Artwork> artworkTable;

    @FXML
    private TextField titleTextField;

    @FXML
    private ComboBox<String> artistComboBox;

    @FXML
    private ComboBox<String> typeComboBox;

    @FXML
    private TextField priceTextField;

    @FXML
    private TextField yearTextField;

    @FXML
    private TextField searchArtworkTextField;

    @FXML
    private Button searchArtworkButton;

    @FXML
    private ComboBox<?> filterByArtistBox;

    @FXML
    private ComboBox<?> filterByTypeBox;

    @FXML
    private TextField filterByPriceField;

    @FXML
    private Button addArtworkButton;

    @FXML
    private Button editArtworkButton;

    @FXML
    private Button deleteArtworkButton;

    @FXML
    private Button viewPhotosButton;

    @FXML
    private Button saveToCsvButton;

    @FXML
    private Button saveToDocButton;

    // Constructor pentru inițializarea presenterului
    public GalleryGUI() {
        this.presenter = new GalleryPresenter(this);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Configurare tabel pentru afișarea artiștilor
        if (artistTable != null) {
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
                List<Artwork> artworks = cellData.getValue().getArtworks();
                System.out.println("Artworks for " + cellData.getValue().getName() + ": " + artworks); // Log pentru depanare
                return new SimpleStringProperty(artworks.stream()
                        .map(Artwork::getTitle)
                        .collect(Collectors.joining(", ")));
            });

            artistTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
                if (newSelection != null) {
                    populateArtistFields(newSelection);
                }
            });

            presenter.getAllArtists();
        } else {
            System.err.println("artistTable is null in initialize method!");
        }

        // Configurare tabel pentru afișarea operelor de artă
        if (artworkTable != null) {
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

            artworkTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
                if (newSelection != null) {
                    populateArtworkFields(newSelection);
                }
            });

            presenter.getAllArtworks();
        } else {
            System.err.println("artworkTable is null in initialize method!");
        }

        // Populare combo box-uri
        populateArtistComboBox();
        populateTypeComboBox();
    }



    private void populateArtistComboBox() {
        if (artistComboBox != null) {
            // Așteaptă ca artistTable să fie populat după apelul getAllArtists()
            if (artistTable != null && artistTable.getItems() != null) {
                artistTable.getItems().forEach(artist -> artistComboBox.getItems().add(artist.getName()));
            } else {
                System.err.println("artistTable sau lista de artiști este null în populateArtistComboBox!");
            }
        } else {
            System.err.println("artistComboBox is null in populateArtistComboBox method!");
        }
    }

    // Metodă ajutătoare pentru popularea typeComboBox cu tipuri de opere de artă
    private void populateTypeComboBox() {
        if (typeComboBox != null) {
            typeComboBox.getItems().addAll("Painting", "Sculpture", "Photography");
        } else {
            System.err.println("typeComboBox is null in populateTypeComboBox method!");
        }
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



    // Metodă ajutătoare pentru popularea câmpurilor cu datele operei selectate
    private void populateArtworkFields(Artwork artwork) {
        titleTextField.setText(artwork.getTitle());
        artistComboBox.setValue(artwork.getArtist() != null ? artwork.getArtist().getName() : "");
        typeComboBox.setValue(artwork.getType());
        priceTextField.setText(String.valueOf(artwork.getPrice()));
        yearTextField.setText(String.valueOf(artwork.getCreationYear()));
    }

    // Metodă ajutătoare pentru resetarea câmpurilor operelor
    private void clearArtworkFields() {
        titleTextField.clear();
        artistComboBox.setValue(null);
        typeComboBox.setValue(null);
        priceTextField.clear();
        yearTextField.clear();
    }

    @FXML
    private void clickAddArtist() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg")
        );
        File selectedFile = fileChooser.showOpenDialog(addArtistButton.getScene().getWindow());

        if (selectedFile != null) {
            String photoPath = selectedFile.toURI().toString();
            Image image = new Image(photoPath);
            artistPhoto.setImage(image);

            // Obținerea datelor din câmpuri
            String name = nameTextField.getText();
            String birthPlace = birthplaceTextField.getText();
            String birthDate = birthdayDatePicker.getValue() != null ? birthdayDatePicker.getValue().toString() : "";
            String nationality = nationalityTextField.getText();

            // Afișează fereastra de confirmare
            Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
            confirmationAlert.setTitle("Confirm Add");
            confirmationAlert.setHeaderText(null);
            confirmationAlert.setContentText("Are you sure you want to add artist?");
            Optional<ButtonType> result = confirmationAlert.showAndWait();

            if (result.isPresent() && result.get() == ButtonType.OK) {
                // Apelarea metodei din presenter pentru adăugare
                presenter.addArtist(name, birthDate, birthPlace, nationality, photoPath);

                // Resetarea câmpurilor după adăugare
                clearArtistFields();
            }
        }
    }

    // Metodă ajutătoare pentru popularea câmpurilor cu datele artistului selectat
    private void populateArtistFields(Artist artist) {
        nameTextField.setText(artist.getName());
        birthplaceTextField.setText(artist.getBirthPlace());
        birthdayDatePicker.setValue(artist.getBirthDate().isEmpty() ? null : java.time.LocalDate.parse(artist.getBirthDate()));
        nationalityTextField.setText(artist.getNationality());
        if (artist.getPhoto() != null && !artist.getPhoto().isEmpty()) {
            artistPhoto.setImage(new Image(artist.getPhoto()));
        } else {
            artistPhoto.setImage(null);
        }
    }

    // Metodă ajutătoare pentru resetarea câmpurilor
    private void clearArtistFields() {
        nameTextField.clear();
        birthplaceTextField.clear();
        birthdayDatePicker.setValue(null);
        nationalityTextField.clear();
        artistPhoto.setImage(null);
    }

    @FXML
    private void clickEditArtist() {
        // Obține artistul selectat din tabel
        Artist selectedArtist = artistTable.getSelectionModel().getSelectedItem();
        if (selectedArtist == null) {
            showError("Selectați un artist pentru a-l edita!");
            return;
        }

        // Obținerea datelor noi din câmpuri
        String oldName = selectedArtist.getName(); // Numele original pentru identificare
        String newName = nameTextField.getText();
        String birthPlace = birthplaceTextField.getText();
        String birthDate = birthdayDatePicker.getValue() != null ? birthdayDatePicker.getValue().toString() : "";
        String nationality = nationalityTextField.getText();
        String photoPath = selectedArtist.getPhoto(); // Păstrează poza existentă dacă nu s-a schimbat

        // Verifică dacă poza a fost modificată
        if (artistPhoto.getImage() != null) {
            String currentPhotoPath = artistPhoto.getImage().getUrl();
            if (!currentPhotoPath.equals(photoPath)) {
                photoPath = currentPhotoPath;
            }
        }

        // Afișează fereastra de confirmare
        Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmationAlert.setTitle("Confirm Edit");
        confirmationAlert.setHeaderText(null);
        confirmationAlert.setContentText("Are you sure you want to edit artist?");
        Optional<ButtonType> result = confirmationAlert.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            // Apelarea metodei din presenter pentru actualizare
            presenter.updateArtist(oldName, newName, birthDate, birthPlace, nationality, photoPath);

            // Resetarea câmpurilor după editare
            clearArtistFields();
        }
    }

    @FXML
    private void clickDeleteArtist() {
        // Obține artistul selectat din tabel
        Artist selectedArtist = artistTable.getSelectionModel().getSelectedItem();
        if (selectedArtist == null) {
            showError("Selectați un artist pentru a-l șterge!");
            return;
        }

        // Afișează fereastra de confirmare
        Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmationAlert.setTitle("Confirm Delete");
        confirmationAlert.setHeaderText(null);
        confirmationAlert.setContentText("Are you sure you want to delete artist?");
        Optional<ButtonType> result = confirmationAlert.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            // Apelarea metodei din presenter pentru ștergere
            presenter.deleteArtist(selectedArtist.getName());

            // Resetarea câmpurilor după ștergere
            clearArtistFields();
        }
    }



    @FXML
    private void clickSearchArtist() {
        // Obține textul introdus în câmpul de căutare
        String searchText = searchArtistTextField.getText().trim();
        if (searchText.isEmpty()) {
            // Dacă textul este gol, afișează toți artiștii
            presenter.getAllArtists();
            return;
        }

        // Apelarea metodei din presenter pentru a căuta artistul
        presenter.searchArtist(searchText);
    }

    @FXML
    private void clickAddArtwork() {
        // Obține datele din câmpuri
        String title = titleTextField.getText().trim();
        String artistName = artistComboBox.getValue() != null ? artistComboBox.getValue().toString() : "";
        String type = typeComboBox.getValue() != null ? typeComboBox.getValue().toString() : "";
        double price;
        try {
            price = Double.parseDouble(priceTextField.getText().trim());
            if (price < 0) {
                showError("Prețul nu poate fi negativ!");
                return;
            }
        } catch (NumberFormatException e) {
            showError("Introduceți un preț valid!");
            return;
        }
        int creationYear;
        try {
            creationYear = Integer.parseInt(yearTextField.getText().trim());
        } catch (NumberFormatException e) {
            showError("Introduceți un an valid!");
            return;
        }

        // Afișează fereastra de confirmare
        Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmationAlert.setTitle("Confirm Add");
        confirmationAlert.setHeaderText(null);
        confirmationAlert.setContentText("Are you sure you want to add artwork?");
        Optional<ButtonType> result = confirmationAlert.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            // Apelarea metodei din presenter pentru adăugare
            presenter.addArtwork(title, artistName, type, price, creationYear);

            // Resetarea câmpurilor după adăugare
            clearArtworkFields();
        }
    }

    @FXML
    private void clickEditArtwork() {
        // Obține opera selectată din tabel
        Artwork selectedArtwork = artworkTable.getSelectionModel().getSelectedItem();
        if (selectedArtwork == null) {
            showError("Selectați o operă pentru a o edita!");
            return;
        }

        // Obține datele noi din câmpuri
        String oldTitle = selectedArtwork.getTitle(); // Titlul original pentru identificare
        String newTitle = titleTextField.getText().trim();
        String artistName = artistComboBox.getValue() != null ? artistComboBox.getValue().toString() : "";
        String type = typeComboBox.getValue() != null ? typeComboBox.getValue().toString() : "";
        double price;
        try {
            price = Double.parseDouble(priceTextField.getText().trim());
            if (price < 0) {
                showError("Prețul nu poate fi negativ!");
                return;
            }
        } catch (NumberFormatException e) {
            showError("Introduceți un preț valid!");
            return;
        }
        int creationYear;
        try {
            creationYear = Integer.parseInt(yearTextField.getText().trim());
        } catch (NumberFormatException e) {
            showError("Introduceți un an valid!");
            return;
        }

        // Afișează fereastra de confirmare
        Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmationAlert.setTitle("Confirm Edit");
        confirmationAlert.setHeaderText(null);
        confirmationAlert.setContentText("Are you sure you want to edit artwork?");
        Optional<ButtonType> result = confirmationAlert.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            // Apelarea metodei din presenter pentru actualizare
            presenter.updateArtwork(oldTitle, newTitle, artistName, type, price, creationYear);

            // Resetarea câmpurilor după editare
            clearArtworkFields();
        }
    }

    @FXML
    private void clickDeleteArtwork() {
        // Obține opera selectată din tabel
        Artwork selectedArtwork = artworkTable.getSelectionModel().getSelectedItem();
        if (selectedArtwork == null) {
            showError("Selectați o operă pentru a o șterge!");
            return;
        }

        // Afișează fereastra de confirmare
        Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmationAlert.setTitle("Confirm Delete");
        confirmationAlert.setHeaderText(null);
        confirmationAlert.setContentText("Are you sure you want to delete artwork?");
        Optional<ButtonType> result = confirmationAlert.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            // Apelarea metodei din presenter pentru ștergere
            presenter.deleteArtwork(selectedArtwork.getTitle());

            // Resetarea câmpurilor după ștergere
            clearArtworkFields();
        }
    }

    @FXML
    private void clickViewPhotos() {
        // Logica pentru butonul View Photos
    }

    @FXML
    private void clickSaveToCsv() {
        // Logica pentru butonul Save to CSV
    }

    @FXML
    private void clickSaveToDoc() {
        // Logica pentru butonul Save to Doc
    }

    // Implementarea interfeței IGalleryGUI
    @Override
    public void displayArtists(List<Artist> artists) {
        if (artistTable != null) {
            artistTable.setItems(FXCollections.observableArrayList(artists));
        } else {
            System.err.println("artistTable is null in displayArtists method!");
        }
    }

    @Override
    public void displayArtistSearchResult(Artist artist) {
        if (artistTable != null) {
            artistTable.getSelectionModel().clearSelection();
            artistTable.getItems().setAll(artist);
            populateArtistFields(artist);
        }
    }

    @Override
    public void displaySuccess(String message) {
        System.out.println("Succes: " + message);
        // Logica pentru afișarea mesajelor de succes (ex. Alert)
    }

    @Override
    public void displayArtworks(List<Artwork> artworks) {
        if (artworkTable != null) {
            artworkTable.setItems(FXCollections.observableArrayList(artworks));
        } else {
            System.err.println("artworkTable is null in displayArtworks method!");
        }
    }

    @Override
    public void displayArtworkSearchResult(Artwork artwork) {
        if (artworkTable != null) {
            artworkTable.getSelectionModel().clearSelection();
            artworkTable.getItems().setAll(artwork);
            populateArtworkFields(artwork);
        }
    }

    @Override
    public void displayFilteredArtworks(List<Artwork> artworks) {
        System.out.println("Opere filtrate: " + artworks);
        // Logica pentru afișarea operelor filtrate
    }

    @Override
    public void confirmExportSuccess(String filePath) {
        System.out.println("Export reușit în: " + filePath);
        // Logica pentru confirmarea exportului
    }

    @Override
    public void confirmSuccess(String message) {
        System.out.println("Succes: " + message);
        // Logica pentru confirmarea altor operațiuni
    }

    @Override
    public void showError(String message) {
        System.out.println("Eroare: " + message);
        // Logica pentru afișarea erorilor (ex. Alert)
    }
}