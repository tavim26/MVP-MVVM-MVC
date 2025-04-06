package View;

import ViewModel.GalleryViewModel;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;

import java.time.LocalDate;
import java.util.List;

public class GalleryGUI
{
    @FXML private AnchorPane artistPane;
    @FXML private AnchorPane artworkPane;
    @FXML private Button artistButton;
    @FXML private Button artworkButton;

    // Artist Pane
    @FXML private TextField nameTextField;
    @FXML private TextField birthplaceTextField;
    @FXML private DatePicker birthdayDatePicker;
    @FXML private TextField nationalityTextField;
    @FXML private TextField searchArtistTextField;
    @FXML private Button addArtistButton;
    @FXML private Button editArtistButton;
    @FXML private Button deleteArtistButton;
    @FXML private Button clearArtistButton;
    @FXML private TableView<Object> artistTable;
    @FXML private TableColumn<Object, String> nameColumn;
    @FXML private TableColumn<Object, String> birthdayColumn;
    @FXML private TableColumn<Object, String> birthplaceColumn;
    @FXML private TableColumn<Object, String> nationalityColumn;
    @FXML private TableColumn<Object, String> artworkListColumn;
    @FXML private ImageView artistPhoto;

    // Artwork Pane
    @FXML private TextField titleTextField;
    @FXML private ComboBox<String> artistComboBox;
    @FXML private ComboBox<String> typeComboBox;
    @FXML private TextField priceTextField;
    @FXML private TextField yearTextField;
    @FXML private TextField searchArtworkTextField;
    @FXML private ComboBox<String> filterByArtistBox;
    @FXML private ComboBox<String> filterByTypeBox;
    @FXML private Slider priceSlider;
    @FXML private Button addArtworkButton;
    @FXML private Button editArtworkButton;
    @FXML private Button deleteArtworkButton;
    @FXML private Button addImageButton;
    @FXML private Button clearArtworkButton;
    @FXML private Button saveToCsvButton;
    @FXML private Button saveToDocButton;
    @FXML private TableView<Object> artworkTable;
    @FXML private TableColumn<Object, String> titleColumn;
    @FXML private TableColumn<Object, String> artistNameColumn;
    @FXML private TableColumn<Object, String> typeColumn;
    @FXML private TableColumn<Object, Double> priceColumn;
    @FXML private TableColumn<Object, Integer> creationYearColumn;
    @FXML private ImageView artworkImage1;
    @FXML private ImageView artworkImage2;

    private final GalleryViewModel viewModel;

    // Proprietăți pentru Artist
    private final StringProperty artistName = new SimpleStringProperty("");
    private final StringProperty artistBirthplace = new SimpleStringProperty("");
    private final ObjectProperty<LocalDate> artistBirthday = new SimpleObjectProperty<>();
    private final StringProperty artistNationality = new SimpleStringProperty("");
    private final StringProperty searchArtist = new SimpleStringProperty("");
    private final ObservableList<Object> artists = FXCollections.observableArrayList();

    // Proprietăți pentru Artwork
    private final StringProperty artworkTitle = new SimpleStringProperty("");
    private final StringProperty artworkArtist = new SimpleStringProperty("");
    private final StringProperty artworkType = new SimpleStringProperty("");
    private final StringProperty artworkPrice = new SimpleStringProperty("0.0");
    private final StringProperty artworkYear = new SimpleStringProperty("0");
    private final StringProperty searchArtwork = new SimpleStringProperty("");
    private final StringProperty filterArtist = new SimpleStringProperty("");
    private final StringProperty filterType = new SimpleStringProperty("");
    private final DoubleProperty filterPrice = new SimpleDoubleProperty(1000.0);
    private final ObservableList<Object> artworks = FXCollections.observableArrayList();
    private final ObservableList<String> artistNames = FXCollections.observableArrayList();
    private final ObservableList<String> artworkTypes = FXCollections.observableArrayList("Painting", "Sculpture");

    public GalleryGUI()
    {
        this.viewModel = new GalleryViewModel();
    }

    @FXML
    public void initialize()
    {
        // Binding bidirecțional UI -> Proprietăți
        nameTextField.textProperty().bindBidirectional(artistName);
        birthplaceTextField.textProperty().bindBidirectional(artistBirthplace);
        birthdayDatePicker.valueProperty().bindBidirectional(artistBirthday);
        nationalityTextField.textProperty().bindBidirectional(artistNationality);
        searchArtistTextField.textProperty().bindBidirectional(searchArtist);

        titleTextField.textProperty().bindBidirectional(artworkTitle);
        artistComboBox.valueProperty().bindBidirectional(artworkArtist);
        typeComboBox.valueProperty().bindBidirectional(artworkType);
        priceTextField.textProperty().bindBidirectional(artworkPrice);
        yearTextField.textProperty().bindBidirectional(artworkYear);
        searchArtworkTextField.textProperty().bindBidirectional(searchArtwork);
        filterByArtistBox.valueProperty().bindBidirectional(filterArtist);
        filterByTypeBox.valueProperty().bindBidirectional(filterType);
        priceSlider.valueProperty().bindBidirectional(filterPrice);

        // Binding unidirecțional pentru liste
        artistTable.itemsProperty().bind(new SimpleObjectProperty<>(artists));
        artworkTable.itemsProperty().bind(new SimpleObjectProperty<>(artworks));
        artistComboBox.itemsProperty().bind(new SimpleObjectProperty<>(artistNames));
        typeComboBox.itemsProperty().bind(new SimpleObjectProperty<>(artworkTypes));
        filterByArtistBox.itemsProperty().bind(new SimpleObjectProperty<>(artistNames));
        filterByTypeBox.itemsProperty().bind(new SimpleObjectProperty<>(artworkTypes));





        // Configurare tabele
        nameColumn.setCellValueFactory(cellData -> new SimpleStringProperty((String) ((List<?>) cellData.getValue()).get(0)));
        birthdayColumn.setCellValueFactory(cellData -> new SimpleStringProperty((String) ((List<?>) cellData.getValue()).get(1)));
        birthplaceColumn.setCellValueFactory(cellData -> new SimpleStringProperty((String) ((List<?>) cellData.getValue()).get(2)));
        nationalityColumn.setCellValueFactory(cellData -> new SimpleStringProperty((String) ((List<?>) cellData.getValue()).get(3)));
        artworkListColumn.setCellValueFactory(cellData -> new SimpleStringProperty((String) ((List<?>) cellData.getValue()).get(4)));

        titleColumn.setCellValueFactory(cellData -> new SimpleStringProperty((String) ((List<?>) cellData.getValue()).get(0)));
        artistNameColumn.setCellValueFactory(cellData -> new SimpleStringProperty((String) ((List<?>) cellData.getValue()).get(1)));
        typeColumn.setCellValueFactory(cellData -> new SimpleStringProperty((String) ((List<?>) cellData.getValue()).get(2)));
        priceColumn.setCellValueFactory(cellData -> new SimpleDoubleProperty((Double) ((List<?>) cellData.getValue()).get(3)).asObject());
        creationYearColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty((Integer) ((List<?>) cellData.getValue()).get(4)).asObject());

        // Sincronizare cu ViewModel
        viewModel.setOnDataChanged(() -> {

            artists.setAll(viewModel.getArtists().stream()
                    .map(a -> List.of(a.getName(), a.getBirthDate(), a.getBirthPlace(), a.getNationality(),
                            a.getArtworks().stream().map(Artwork::getTitle).collect(Collectors.joining(", "))))
                    .toList());
            artworks.setAll(viewModel.getArtworks().stream()
                    .map(a -> List.of(a.getTitle(), a.getArtist().getName(), a.getType(), a.getPrice(), a.getCreationYear()))
                    .toList());
            artistNames.setAll(viewModel.getArtists().stream().map(Artist::getName).toList());
            if (viewModel.getMessage() != null) {
                new Alert(Alert.AlertType.INFORMATION, viewModel.getMessage()).showAndTell();
            }

        });

        // Binding bidirecțional Proprietăți -> ViewModel
        artistName.addListener((obs, old, newVal) -> viewModel.getCrtArtist().setName(newVal));
        artistBirthplace.addListener((obs, old, newVal) -> viewModel.getCrtArtist().setBirthPlace(newVal));
        artistBirthday.addListener((obs, old, newVal) -> viewModel.getCrtArtist().setBirthDate(newVal != null ? newVal.toString() : ""));
        artistNationality.addListener((obs, old, newVal) -> viewModel.getCrtArtist().setNationality(newVal));
        searchArtist.addListener((obs, old, newVal) -> viewModel.getCrtArtist().setName(newVal));

        artworkTitle.addListener((obs, old, newVal) -> viewModel.getCrtArtwork().setTitle(newVal));
        artworkArtist.addListener((obs, old, newVal) -> viewModel.getCrtArtwork().setArtist(
                viewModel.getArtists().stream().filter(a -> a.getName().equals(newVal)).findFirst().orElse(null)));
        artworkType.addListener((obs, old, newVal) -> viewModel.getCrtArtwork().setType(newVal));
        artworkPrice.addListener((obs, old, newVal) -> {
            try {
                viewModel.getCrtArtwork().setPrice(newVal.isEmpty() ? 0.0 : Double.parseDouble(newVal));
            } catch (NumberFormatException e) {
                viewModel.getCrtArtwork().setPrice(0.0);
            }
        });
        artworkYear.addListener((obs, old, newVal) -> {
            try {
                viewModel.getCrtArtwork().setCreationYear(newVal.isEmpty() ? 0 : Integer.parseInt(newVal));
            } catch (NumberFormatException e) {
                viewModel.getCrtArtwork().setCreationYear(0);
            }
        });
        searchArtwork.addListener((obs, old, newVal) -> viewModel.getCrtArtwork().setTitle(newVal));
        filterArtist.addListener((obs, old, newVal) -> viewModel.getCrtArtwork().setArtist(
                viewModel.getArtists().stream().filter(a -> a.getName().equals(newVal)).findFirst().orElse(null)));
        filterType.addListener((obs, old, newVal) -> viewModel.getCrtArtwork().setType(newVal));
        filterPrice.addListener((obs, old, newVal) -> viewModel.getCrtArtwork().setPrice(newVal.doubleValue()));

        // Comenzi
        addArtistButton.setOnAction(e -> viewModel.getAddArtistCommand().execute());
        editArtistButton.setOnAction(e -> viewModel.getUpdateArtistCommand().execute());
        deleteArtistButton.setOnAction(e -> viewModel.getDeleteArtistCommand().execute());
        searchArtistTextField.setOnAction(e -> viewModel.getSearchArtistsCommand().execute());

        addArtworkButton.setOnAction(e -> viewModel.getAddArtworkCommand().execute());
        editArtworkButton.setOnAction(e -> viewModel.getUpdateArtworkCommand().execute());
        deleteArtworkButton.setOnAction(e -> viewModel.getDeleteArtworkCommand().execute());
        searchArtworkTextField.setOnAction(e -> viewModel.getSearchArtworkCommand().execute());
        filterByArtistBox.setOnAction(e -> viewModel.getFilterArtworkByArtistCommand().execute());
        filterByTypeBox.setOnAction(e -> viewModel.getFilterArtworkByTypeCommand().execute());
        priceSlider.setOnMouseReleased(e -> viewModel.getFilterArtworkByPriceCommand().execute());

        saveToCsvButton.setOnAction(e -> viewModel.getSaveToCsvCommand().execute());
        saveToDocButton.setOnAction(e -> viewModel.getSaveToDocCommand().execute());

        // Navigare pane-uri
        artistButton.setOnAction(e -> { artistPane.setVisible(true); artworkPane.setVisible(false); });
        artworkButton.setOnAction(e -> { artistPane.setVisible(false); artworkPane.setVisible(true); });

        // Inițializare
        viewModel.setOnDataChanged(viewModel.getOnDataChanged()); // Trigger inițial
    }
}