package View;

import ViewModel.GalleryViewModel;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.beans.property.SimpleStringProperty;

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
    @FXML private TableView<List<String>> artistTable;
    @FXML private TableColumn<List<String>, String> nameColumn;
    @FXML private TableColumn<List<String>, String> birthdayColumn;
    @FXML private TableColumn<List<String>, String> birthplaceColumn;
    @FXML private TableColumn<List<String>, String> nationalityColumn;
    @FXML private TableColumn<List<String>, String> artworkListColumn;
    @FXML private ImageView artistPhoto;

    // Artwork Pane
    @FXML private TextField titleTextField;
    @FXML private ComboBox<String> artistComboBox;
    @FXML private ComboBox<String> typeComboBox;
    @FXML private Spinner<Double> priceSpinner;
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
    @FXML private TableView<List<String>> artworkTable;
    @FXML private TableColumn<List<String>, String> titleColumn;
    @FXML private TableColumn<List<String>, String> artistNameColumn;
    @FXML private TableColumn<List<String>, String> typeColumn;
    @FXML private TableColumn<List<String>, String> priceColumn;
    @FXML private TableColumn<List<String>, String> creationYearColumn;
    @FXML private ImageView artworkImage1;
    @FXML private ImageView artworkImage2;

    // ViewModel
    private GalleryViewModel viewModel;

    private ObservableList<List<String>> artistObservableList;
    private ObservableList<List<String>> artworkObservableList;
    private ObservableList<String> artistNamesObservable;
    private ObservableList<String> artworkTypesObservable;

    @FXML
    public void initialize()
    {
        viewModel = new GalleryViewModel();

        artistObservableList = FXCollections.observableArrayList();
        artworkObservableList = FXCollections.observableArrayList();
        artistNamesObservable = FXCollections.observableArrayList();
        artworkTypesObservable = FXCollections.observableArrayList();

        setupArtistTable();
        setupArtworkTable();

        setupComboBoxes();

        priceSpinner.setValueFactory(new SpinnerValueFactory.DoubleSpinnerValueFactory(0, 100000, 0, 100));

        bindProperties();
        bindCommands();

        viewModel.addPropertyChangedListener(this::handlePropertyChanged);

        artistPane.setVisible(true);
        artworkPane.setVisible(false);
    }

    private void setupArtistTable()
    {
        nameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().get(0)));
        birthdayColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().get(1)));
        birthplaceColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().get(2)));
        nationalityColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().get(3)));
        artworkListColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().get(4)));

        artistObservableList.setAll(viewModel.getArtistData());
        artistTable.setItems(artistObservableList);

        artistTable.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldSelection, newSelection) ->
                        viewModel.setSelectedArtistName(newSelection != null ? newSelection.get(0) : "")
        );
    }

    private void setupArtworkTable()
    {
        titleColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().get(0)));
        artistNameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().get(1)));
        typeColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().get(2)));
        priceColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().get(3)));
        creationYearColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().get(4)));

        artworkObservableList.setAll(viewModel.getArtworkData());
        artworkTable.setItems(artworkObservableList);

        artworkTable.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldSelection, newSelection) ->
                        viewModel.setSelectedArtworkTitle(newSelection != null ? newSelection.get(0) : "")
        );
    }

    private void setupComboBoxes()
    {
        artistNamesObservable.setAll(viewModel.getArtistNames());
        artworkTypesObservable.setAll(viewModel.getArtworkTypes());

        artistComboBox.setItems(artistNamesObservable);
        typeComboBox.setItems(artworkTypesObservable);
        filterByArtistBox.setItems(artistNamesObservable);
        filterByTypeBox.setItems(artworkTypesObservable);

        // Binding bidirecțional pentru artistComboBox
        artistComboBox.setValue(viewModel.getArtistComboBoxValue());
        artistComboBox.valueProperty().addListener((obs, oldValue, newValue) ->
                viewModel.setArtistComboBoxValue(newValue));

        // Binding bidirecțional pentru typeComboBox
        typeComboBox.setValue(viewModel.getTypeComboBoxValue());
        typeComboBox.valueProperty().addListener((obs, oldValue, newValue) ->
                viewModel.setTypeComboBoxValue(newValue));

        // Binding pentru filtre
        filterByArtistBox.setValue(viewModel.getFilterArtist());
        filterByArtistBox.valueProperty().addListener((obs, oldValue, newValue) -> {
            viewModel.setFilterArtist(newValue);
            viewModel.getFilterByArtistCommand().execute();
        });

        filterByTypeBox.setValue(viewModel.getFilterType());
        filterByTypeBox.valueProperty().addListener((obs, oldValue, newValue) -> {
            viewModel.setFilterType(newValue);
            viewModel.getFilterByTypeCommand().execute();
        });
    }

    private void bindProperties()
    {
        // nameTextField
        nameTextField.setText(viewModel.getNameTextFieldValue());
        nameTextField.textProperty().addListener((obs, oldValue, newValue) ->
                viewModel.setNameTextFieldValue(newValue));

        // birthplaceTextField
        birthplaceTextField.setText(viewModel.getBirthplaceTextFieldValue());
        birthplaceTextField.textProperty().addListener((obs, oldValue, newValue) ->
                viewModel.setBirthplaceTextFieldValue(newValue));

        // birthdayDatePicker
        String dateValue = viewModel.getBirthdayDatePickerValue();
        birthdayDatePicker.setValue(dateValue.isEmpty() ? null : LocalDate.parse(dateValue));
        birthdayDatePicker.valueProperty().addListener((obs, oldValue, newValue) ->
                viewModel.setBirthdayDatePickerValue(newValue != null ? newValue.toString() : ""));

        // nationalityTextField
        nationalityTextField.setText(viewModel.getNationalityTextFieldValue());
        nationalityTextField.textProperty().addListener((obs, oldValue, newValue) ->
                viewModel.setNationalityTextFieldValue(newValue));

        // searchArtistTextField
        searchArtistTextField.setText(viewModel.getArtistSearchQuery());
        searchArtistTextField.textProperty().addListener((obs, oldValue, newValue) -> {
            viewModel.setArtistSearchQuery(newValue);
            viewModel.getSearchArtistCommand().execute();
        });





        // titleTextField
        titleTextField.setText(viewModel.getTitleTextFieldValue());
        titleTextField.textProperty().addListener((obs, oldValue, newValue) ->
                viewModel.setTitleTextFieldValue(newValue));

        // yearTextField
        yearTextField.setText(String.valueOf(viewModel.getYearTextFieldValue()));
        yearTextField.textProperty().addListener((obs, oldValue, newValue) -> {
            try {
                viewModel.setYearTextFieldValue(newValue.isEmpty() ? 0 : Integer.parseInt(newValue));
            } catch (NumberFormatException e) {
                viewModel.setYearTextFieldValue(0);
            }
        });

        // priceSpinner
        priceSpinner.getValueFactory().setValue(viewModel.getPriceSpinnerValue());
        priceSpinner.valueProperty().addListener((obs, oldValue, newValue) ->
                viewModel.setPriceSpinnerValue(newValue));

        // searchArtworkTextField
        searchArtworkTextField.setText(viewModel.getArtworkSearchQuery());
        searchArtworkTextField.textProperty().addListener((obs, oldValue, newValue) -> {
            viewModel.setArtworkSearchQuery(newValue);
            viewModel.getSearchArtworkCommand().execute();
        });

        // priceSlider
        priceSlider.setValue(viewModel.getFilterPrice());
        priceSlider.valueProperty().addListener((obs, oldValue, newValue) -> {
            viewModel.setFilterPrice(newValue.doubleValue());
            viewModel.getFilterByPriceCommand().execute();
        });
    }

    private void bindCommands()
    {
        // Butoane meniu
        artistButton.setOnAction(event -> {
            artistPane.setVisible(true);
            artworkPane.setVisible(false);
        });
        artworkButton.setOnAction(event -> {
            artistPane.setVisible(false);
            artworkPane.setVisible(true);
        });

        // Artist Pane
        addArtistButton.setOnAction(event -> viewModel.getAddArtistCommand().execute());
        editArtistButton.setOnAction(event -> viewModel.getUpdateArtistCommand().execute());
        deleteArtistButton.setOnAction(event -> viewModel.getDeleteArtistCommand().execute());
        clearArtistButton.setOnAction(event -> viewModel.getClearArtistCommand().execute());

        // Artwork Pane
        addArtworkButton.setOnAction(event -> viewModel.getAddArtworkCommand().execute());
        editArtworkButton.setOnAction(event -> viewModel.getUpdateArtworkCommand().execute());
        deleteArtworkButton.setOnAction(event -> viewModel.getDeleteArtworkCommand().execute());
        addImageButton.setOnAction(event -> viewModel.getAddImageCommand().execute());
        clearArtworkButton.setOnAction(event -> viewModel.getClearArtworkCommand().execute());
        saveToCsvButton.setOnAction(event -> viewModel.getExportToCsvCommand().execute());
        saveToDocButton.setOnAction(event -> viewModel.getExportToDocCommand().execute());
    }

    private void handlePropertyChanged(String propertyName)
    {
        switch (propertyName) {
            case "artistData":
                artistObservableList.setAll(viewModel.getArtistData());
                break;
            case "artworkData":
                artworkObservableList.setAll(viewModel.getArtworkData());
                break;
            case "artistNames":
                artistNamesObservable.setAll(viewModel.getArtistNames());
                break;
            case "artworkTypes":
                artworkTypesObservable.setAll(viewModel.getArtworkTypes());
                break;
            case "selectedArtistPhotoPath":
                String photoPath = viewModel.getSelectedArtistPhotoPath();
                artistPhoto.setImage(photoPath != null && !photoPath.isEmpty() ? new Image(photoPath) : null);
                break;
            case "artworkImagePaths":
                updateArtworkImages();
                break;
            case "nameTextFieldValue":
                nameTextField.setText(viewModel.getNameTextFieldValue());
                break;
            case "birthplaceTextFieldValue":
                birthplaceTextField.setText(viewModel.getBirthplaceTextFieldValue());
                break;
            case "birthdayDatePickerValue":
                String dateValue = viewModel.getBirthdayDatePickerValue();
                birthdayDatePicker.setValue(dateValue.isEmpty() ? null : LocalDate.parse(dateValue));
                break;
            case "nationalityTextFieldValue":
                nationalityTextField.setText(viewModel.getNationalityTextFieldValue());
                break;
            case "titleTextFieldValue":
                titleTextField.setText(viewModel.getTitleTextFieldValue());
                break;
            case "artistComboBoxValue":
                artistComboBox.setValue(viewModel.getArtistComboBoxValue());
                break;
            case "typeComboBoxValue":
                typeComboBox.setValue(viewModel.getTypeComboBoxValue());
                break;
            case "priceSpinnerValue":
                priceSpinner.getValueFactory().setValue(viewModel.getPriceSpinnerValue());
                break;
            case "yearTextFieldValue":
                yearTextField.setText(String.valueOf(viewModel.getYearTextFieldValue()));
                break;
            case "message":
                System.out.println(viewModel.getMessage());
                break;
        }
    }

    private void updateArtworkImages()
    {
        List<String> imagePaths = viewModel.getArtworkImagePaths();
        artworkImage1.setImage(imagePaths.size() > 0 ? new Image(imagePaths.get(0)) : null);
        artworkImage2.setImage(imagePaths.size() > 1 ? new Image(imagePaths.get(1)) : null);
    }
}