package View;

import Controller.GalleryController;

import Model.Observer.Observable;
import Model.Observer.Observer;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.BubbleChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;

import java.io.File;
import java.time.LocalDate;
import java.util.List;

public class GalleryView implements Observer
{

    private GalleryController controller;

    public void setController(GalleryController controller)
    {
        this.controller = controller;
    }

    // Left pane buttons
    @FXML private Button artistButton;
    @FXML private Button artworkButton;
    @FXML private Button statisticsButton;

    // Artist pane components
    @FXML private AnchorPane artistPane;
    @FXML private TableView<String[]> artistTable;
    @FXML private TableColumn<String[], String> nameColumn;
    @FXML private TableColumn<String[], String> birthdayColumn;
    @FXML private TableColumn<String[], String> birthplaceColumn;
    @FXML private TableColumn<String[], String> nationalityColumn;
    @FXML private TableColumn<String[], String> artworkListColumn;
    @FXML private TextField nameTextField;
    @FXML private TextField birthplaceTextField;
    @FXML private DatePicker birthdayDatePicker;
    @FXML private TextField nationalityTextField;
    @FXML private TextField searchArtistTextField;
    @FXML private Button addArtistButton;
    @FXML private Button editArtistButton;
    @FXML private Button deleteArtistButton;
    @FXML private Button clearArtistButton;
    @FXML private ImageView artistPhoto;

    // Artwork pane components
    @FXML private AnchorPane artworkPane;
    @FXML private TableView<String[]> artworkTable;
    @FXML private TableColumn<String[], String> titleColumn;
    @FXML private TableColumn<String[], String> artistNameColumn;
    @FXML private TableColumn<String[], String> typeColumn;
    @FXML private TableColumn<String[], String> priceColumn;
    @FXML private TableColumn<String[], String> creationYearColumn;
    @FXML private TextField titleTextField;
    @FXML private ComboBox<String> artistComboBox;
    @FXML private ComboBox<String> typeComboBox;
    @FXML private TextField priceTextField;
    @FXML private TextField yearTextField;
    @FXML private TextField searchArtworkTextField;
    @FXML private ComboBox<String> filterByArtistBox;
    @FXML private ComboBox<String> filterByTypeBox;
    @FXML private Button addArtworkButton;
    @FXML private Button editArtworkButton;
    @FXML private Button deleteArtworkButton;
    @FXML private Button addImageButton;
    @FXML private Button clearArtworkButton;
    @FXML private Button saveToCsvButton;
    @FXML private Button saveToDocButton;
    @FXML private Slider priceSlider;
    @FXML private ImageView artworkImage1;
    @FXML private ImageView artworkImage2;

    // Statistics pane components
    @FXML private AnchorPane statisticsPane;
    @FXML private BarChart<?, ?> typeChart;
    @FXML private CategoryAxis artworkTypeAxis;
    @FXML private NumberAxis nrOfArtworksAxis;
    @FXML private BubbleChart<?, ?> priceChart;
    @FXML private NumberAxis numberOfArtworksAxis;
    @FXML private NumberAxis priceAxis;


    @FXML
    public void initialize()
    {
        setupArtistTable();
        setupArtworkTable();
        setupSearch();
        setupFilter();
        setupComboBoxes();
    }



    private void setupArtistTable() {
        nameColumn.setCellValueFactory(c -> new SimpleStringProperty(c.getValue()[0]));
        birthdayColumn.setCellValueFactory(c -> new SimpleStringProperty(c.getValue()[1]));
        birthplaceColumn.setCellValueFactory(c -> new SimpleStringProperty(c.getValue()[2]));
        nationalityColumn.setCellValueFactory(c -> new SimpleStringProperty(c.getValue()[3]));

        artistTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            if (newSel != null) populateArtistFields(newSel);
        });
    }

    private void setupArtworkTable() {
        titleColumn.setCellValueFactory(c -> new SimpleStringProperty(c.getValue()[0]));
        artistNameColumn.setCellValueFactory(c -> new SimpleStringProperty(c.getValue()[1]));
        typeColumn.setCellValueFactory(c -> new SimpleStringProperty(c.getValue()[2]));
        priceColumn.setCellValueFactory(c -> new SimpleStringProperty(c.getValue()[3]));
        creationYearColumn.setCellValueFactory(c -> new SimpleStringProperty(c.getValue()[4]));

        artworkTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            if (newSel != null) populateArtworkFields(newSel);
        });
    }


    private void populateArtistFields(String[] data) {
        nameTextField.setText(data[0]);
        birthdayDatePicker.setValue(LocalDate.parse(data[1]));
        birthplaceTextField.setText(data[2]);
        nationalityTextField.setText(data[3]);
        artistPhoto.setImage(data[4] != null && !data[4].isEmpty() ? new Image(data[4]) : null);
    }

    private void populateArtworkFields(String[] data) {
        titleTextField.setText(data[0]);
        artistComboBox.setValue(data[1]);
        typeComboBox.setValue(data[2]);
        priceTextField.setText(data[3]);
        yearTextField.setText(data[4]);
        displayArtworkImages(data[0]);
    }


    public void setupSearch()
    {
        searchArtistTextField.textProperty().addListener((obs, oldVal, newVal) ->
                controller.searchArtist(newVal)
        );

        searchArtworkTextField.textProperty().addListener((obs, oldVal, newVal) ->
                controller.searchArtwork(newVal)
        );
    }



    private void setupFilter()
    {

        filterByArtistBox.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && !newVal.isEmpty()) {
                controller.filterArtworksByArtist(newVal);
            }
        });

        filterByTypeBox.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && !newVal.isEmpty()) {
                controller.filterArtworksByType(newVal);
            }
        });

        priceSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            controller.filterArtworksByPrice(newVal.doubleValue());
        });
    }


    private void setupComboBoxes()
    {
        typeComboBox.getItems().addAll("Painting", "Sculpture", "Photography");
        filterByTypeBox.getItems().addAll("Painting", "Sculpture", "Photography");
    }





    // === Observer implementation ===
    @Override
    public void update(Observable obs)
    {
        controller.refreshView();
    }


    // Public update methods (invoked from controller)
    public void updateArtistList(List<String[]> artists) {
        artistTable.setItems(FXCollections.observableArrayList(artists));
    }

    public void updateArtworkList(List<String[]> artworks) {
        artworkTable.setItems(FXCollections.observableArrayList(artworks));
    }


    public void updateArtistComboBoxes(List<String> artistNames) {
        artistComboBox.setItems(FXCollections.observableArrayList(artistNames));
        filterByArtistBox.setItems(FXCollections.observableArrayList(artistNames));
    }




    private void displayArtworkImages(String artworkTitle)
    {
        List<String> imagePaths = controller.getArtworkImagePaths(artworkTitle);

        artworkImage1.setImage(imagePaths.size() > 0 ? new Image(imagePaths.get(0)) : null);
        artworkImage2.setImage(imagePaths.size() > 1 ? new Image(imagePaths.get(1)) : null);
    }






    // === Button handlers ===

    @FXML
    private void clickArtistButton(ActionEvent event)
    {
        artistPane.setVisible(true);
        artworkPane.setVisible(false);
        statisticsPane.setVisible(false);
    }

    @FXML
    private void clickArtworkButton(ActionEvent event)
    {
        artistPane.setVisible(false);
        artworkPane.setVisible(true);
        statisticsPane.setVisible(false);
    }

    @FXML
    private void clickStatisticsButton(ActionEvent event)
    {
        artistPane.setVisible(false);
        artworkPane.setVisible(false);
        statisticsPane.setVisible(true);
    }


    // === ARTIST BUTTON ACTIONS ===

    @FXML
    private void clickAddArtist(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Artist Photo");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );

        File file = fileChooser.showOpenDialog(null);
        String photoPath = (file != null) ? file.toURI().toString() : "";

        controller.addArtist(
                nameTextField.getText(),
                birthdayDatePicker.getValue() != null ? birthdayDatePicker.getValue().toString() : "",
                birthplaceTextField.getText(),
                nationalityTextField.getText(),
                photoPath
        );
    }



    @FXML
    private void clickEditArtist(ActionEvent event)
    {
        String[] selected = artistTable.getSelectionModel().getSelectedItem();
        controller.updateArtist(
                selected[0],
                nameTextField.getText(),
                birthdayDatePicker.getValue() != null ? birthdayDatePicker.getValue().toString() : "",
                birthplaceTextField.getText(),
                nationalityTextField.getText(),
                ""
            );

    }

    @FXML
    private void clickDeleteArtist(ActionEvent event)
    {

        controller.deleteArtist(nameTextField.getText());

    }




    // === ARTWORK BUTTON ACTIONS ===

    @FXML
    private void clickAddArtwork(ActionEvent event)
    {
        controller.addArtwork(
                titleTextField.getText(),
                artistComboBox.getValue(),
                typeComboBox.getValue(),
                Double.parseDouble(priceTextField.getText()),
                Integer.parseInt(yearTextField.getText())
        );

    }

    @FXML private void clickEditArtwork(ActionEvent event)
    {
        String[] selected = artworkTable.getSelectionModel().getSelectedItem();

        controller.updateArtwork(
                selected[0],
                titleTextField.getText(),
                artistComboBox.getValue(),
                typeComboBox.getValue(),
                Double.parseDouble(priceTextField.getText()),
                Integer.parseInt(yearTextField.getText())
            );

    }

    @FXML private void clickDeleteArtwork(ActionEvent event)
    {
        controller.deleteArtwork(titleTextField.getText());

    }



    @FXML
    public void clickAddImage(ActionEvent event) {
        String artworkTitle = titleTextField.getText();

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Image");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );

        File file = fileChooser.showOpenDialog(null); // poate fi înlocuit cu stage dacă e nevoie

        controller.addImageToArtwork(artworkTitle, file.getAbsolutePath());

    }


    @FXML
    public void clickSaveToCsv(ActionEvent actionEvent)
    {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save as CSV");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
        File file = fileChooser.showSaveDialog(null);

        controller.saveToCsv(file.getAbsolutePath());

    }

    @FXML
    public void clickSaveToDoc(ActionEvent actionEvent)
    {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save as Text Document");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text Files", "*.txt"));
        File file = fileChooser.showSaveDialog(null);

        controller.saveToDoc(file.getAbsolutePath());

    }






    // === HELPER BUTTON ACTIONS ===


    @FXML
    private void clickClearArtist(ActionEvent event)
    {
        nameTextField.clear();
        birthplaceTextField.clear();
        birthdayDatePicker.setValue(null);
        nationalityTextField.clear();
        searchArtistTextField.clear();
    }


    @FXML private void clickClearArtwork(ActionEvent event)
    {
        titleTextField.clear();
        artistComboBox.setValue(null);
        typeComboBox.setValue(null);
        priceTextField.clear();
        yearTextField.clear();
        searchArtworkTextField.clear();
        filterByArtistBox.setValue(null);
        filterByTypeBox.setValue(null);
    }






}
