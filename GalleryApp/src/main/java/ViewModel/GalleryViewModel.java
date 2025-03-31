package ViewModel;

import Model.Artist;
import Model.Artwork;
import Model.ArtworkImage;
import Model.Repository.ArtistRepo;
import Model.Repository.ArtworkRepo;
import Model.Repository.ArtworkImageRepo;
import Command.GalleryCommands;
import Command.ICommands;

import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class GalleryViewModel implements INotifyPropertyChanged
{
    // Repository
    private final ArtistRepo artistRepo;
    private final ArtworkRepo artworkRepo;
    private final ArtworkImageRepo imageRepo;

    private List<Artist> artists;
    private List<Artwork> artworks;

    private List<List<String>> artistData;
    private List<List<String>> artworkData;
    private List<String> artistNames;
    private List<String> artworkTypes;


    private String selectedArtistName;
    private String selectedArtworkTitle;
    private String artistSearchQuery;
    private String artworkSearchQuery;
    private String message;

    private String filterArtist;
    private double filterPrice;
    private String filterType;

    // Imagini
    private List<String> artworkImagePaths;
    private String selectedArtistPhotoPath;

    // Proprietati campuri
    private String nameTextFieldValue;
    private String birthplaceTextFieldValue;
    private String birthdayDatePickerValue;
    private String nationalityTextFieldValue;
    private String titleTextFieldValue;
    private String typeComboBoxValue;
    private Double priceSpinnerValue;
    private Integer yearTextFieldValue;
    private String artistComboBoxValue;

    // Comenzi
    private final ICommands addArtistCommand;
    private final ICommands updateArtistCommand;
    private final ICommands deleteArtistCommand;
    private final ICommands addArtworkCommand;
    private final ICommands updateArtworkCommand;
    private final ICommands deleteArtworkCommand;
    private final ICommands searchArtistCommand;
    private final ICommands searchArtworkCommand;
    private final ICommands filterByArtistCommand;
    private final ICommands filterByPriceCommand;
    private final ICommands filterByTypeCommand;
    private final ICommands exportToCsvCommand;
    private final ICommands exportToDocCommand;
    private final ICommands addImageCommand;
    private final ICommands clearArtistCommand;
    private final ICommands clearArtworkCommand;

    // Gestionare listeneri pentru INotifyPropertyChanged
    private final List<PropertyChangedListener> listeners = new ArrayList<>();

    public GalleryViewModel()
    {
        artistRepo = new ArtistRepo();
        artworkRepo = new ArtworkRepo();
        imageRepo = new ArtworkImageRepo();

        // Inițializare liste și proprietati
        artists = new ArrayList<>();
        artworks = new ArrayList<>();
        artistData = new ArrayList<>();
        artworkData = new ArrayList<>();
        artistNames = new ArrayList<>();
        artworkTypes = new ArrayList<>();
        artworkImagePaths = new ArrayList<>();
        selectedArtistName = "";
        selectedArtworkTitle = "";
        artistSearchQuery = "";
        artworkSearchQuery = "";
        message = "";
        filterArtist = "";
        filterPrice = 0.0;
        filterType = "";
        selectedArtistPhotoPath = "";

        loadArtists();
        loadArtworks();

        // Inițializare comenzi
        addArtistCommand = new GalleryCommands(this::addArtist);
        updateArtistCommand = new GalleryCommands(this::updateArtist);
        deleteArtistCommand = new GalleryCommands(this::deleteArtist);
        addArtworkCommand = new GalleryCommands(this::addArtwork);
        updateArtworkCommand = new GalleryCommands(this::updateArtwork);
        deleteArtworkCommand = new GalleryCommands(this::deleteArtwork);
        searchArtistCommand = new GalleryCommands(this::searchArtists);
        searchArtworkCommand = new GalleryCommands(this::searchArtworks);
        filterByArtistCommand = new GalleryCommands(this::filterByArtist);
        filterByPriceCommand = new GalleryCommands(this::filterByPrice);
        filterByTypeCommand = new GalleryCommands(this::filterByType);
        exportToCsvCommand = new GalleryCommands(this::exportToCsv);
        exportToDocCommand = new GalleryCommands(this::exportToDoc);
        addImageCommand = new GalleryCommands(this::addImage);
        clearArtistCommand = new GalleryCommands(this::clearArtistFields);
        clearArtworkCommand = new GalleryCommands(this::clearArtworkFields);
    }


    // Implementare INotifyPropertyChanged
    @Override
    public void addPropertyChangedListener(PropertyChangedListener listener)
    {
        listeners.add(listener);
    }

    @Override
    public void removePropertyChangedListener(PropertyChangedListener listener)
    {
        listeners.remove(listener);
    }

    @Override
    public void firePropertyChanged(String propertyName)
    {
        for (PropertyChangedListener listener : listeners)
        {
            listener.propertyChanged(propertyName);
        }
    }




    private void loadArtists()
    {
        try {

            artists = artistRepo.getAllArtists();
            artistData = artists.stream().map(artist -> List.of(
                    artist.getName(),
                    artist.getBirthDate(),
                    artist.getBirthPlace(),
                    artist.getNationality(),
                    artist.getArtworks().stream().map(Artwork::getTitle).collect(Collectors.joining(", "))
            )).collect(Collectors.toList());

            artistNames = artists.stream().map(Artist::getName).collect(Collectors.toList());
            firePropertyChanged("artistData");
            firePropertyChanged("artistNames");
        } catch (SQLException e) {
            message = "Error loading artists: " + e.getMessage();
            firePropertyChanged("message");
        }
    }

    private void loadArtworks()
    {
        try {
            artworks = artworkRepo.getAllArtworks();
            artworkData = artworks.stream().map(artwork -> List.of(
                    artwork.getTitle(),
                    artwork.getArtist().getName(),
                    artwork.getType(),
                    String.valueOf(artwork.getPrice()),
                    String.valueOf(artwork.getCreationYear())
            )).collect(Collectors.toList());
            artworkTypes = artworks.stream().map(Artwork::getType).distinct().collect(Collectors.toList());
            firePropertyChanged("artworkData");
            firePropertyChanged("artworkTypes");
        } catch (SQLException e) {
            message = "Error loading artworks: " + e.getMessage();
            firePropertyChanged("message");
        }
    }

    private void loadArtworkImages()
    {
        if (selectedArtworkTitle != null && !selectedArtworkTitle.isEmpty())
        {
            try {
                Artwork selectedArtwork = artworks.stream()
                        .filter(a -> a.getTitle().equals(selectedArtworkTitle))
                        .findFirst()
                        .orElse(null);
                if (selectedArtwork != null) {
                    List<ArtworkImage> images = imageRepo.getImagesByArtwork(selectedArtworkTitle);
                    artworkImagePaths = images.stream().map(ArtworkImage::getImagePath).collect(Collectors.toList());
                    firePropertyChanged("artworkImagePaths");
                }
            } catch (SQLException e)
            {
                message = "Error loading images: " + e.getMessage();
                firePropertyChanged("message");
            }
        }
        else
        {
            artworkImagePaths = new ArrayList<>();
            firePropertyChanged("artworkImagePaths");
        }
    }


    // Metode pentru comenzi
    private void addArtist()
    {
        Artist newArtist = new Artist(
                getNameTextFieldValue(),
                getBirthdayDatePickerValue(),
                getBirthplaceTextFieldValue(),
                getNationalityTextFieldValue(),
                "default.jpg"
        );
        try {
            artistRepo.addArtist(newArtist);
            artists.add(newArtist);
            loadArtists();
            message = "Artist added successfully!";
            firePropertyChanged("message");
        } catch (SQLException e) {
            message = "Error adding artist: " + e.getMessage();
            firePropertyChanged("message");
        }
    }


    private void updateArtist()
    {
        if (selectedArtistName != null && !selectedArtistName.isEmpty())
        {
            Artist artist = artists.stream()
                    .filter(a -> a.getName().equals(selectedArtistName))
                    .findFirst()
                    .orElse(null);

            if (artist != null)
            {
                artist.setName(getNameTextFieldValue());
                artist.setBirthDate(getBirthdayDatePickerValue());
                artist.setBirthPlace(getBirthplaceTextFieldValue());
                artist.setNationality(getNationalityTextFieldValue());

                try {
                    artistRepo.updateArtist(selectedArtistName, artist);
                    loadArtists();
                    message = "Artist updated successfully!";
                    firePropertyChanged("message");
                } catch (SQLException e) {
                    message = "Error updating artist: " + e.getMessage();
                    firePropertyChanged("message");
                }
            }
            else
            {
                message = "No artist selected!";
                firePropertyChanged("message");
            }
        }
        else
        {
            message = "No artist selected!";
            firePropertyChanged("message");
        }
    }



    private void deleteArtist()
    {
        if (selectedArtistName != null && !selectedArtistName.isEmpty())
        {
            try {
                artistRepo.deleteArtist(selectedArtistName);
                artists.removeIf(a -> a.getName().equals(selectedArtistName));
                selectedArtistName = "";
                selectedArtistPhotoPath = "";
                loadArtists();
                message = "Artist deleted successfully!";
                firePropertyChanged("selectedArtistPhotoPath");
                firePropertyChanged("message");

            } catch (SQLException e) {
                message = "Error deleting artist: " + e.getMessage();
                firePropertyChanged("message");
            }
        }
        else
        {
            message = "No artist selected!";
            firePropertyChanged("message");
        }
    }

    private void addArtwork()
    {
        Artist artist = artists.stream()
                .filter(a -> a.getName().equals(getArtistComboBoxValue()))
                .findFirst()
                .orElse(null);

        if (artist != null) {
            Artwork newArtwork = new Artwork(
                    getTitleTextFieldValue(),
                    artist,
                    getTypeComboBoxValue(),
                    getPriceSpinnerValue(),
                    getYearTextFieldValue()
            );
            try {
                artworkRepo.addArtwork(newArtwork, artist.getName());
                artworks.add(newArtwork);
                artist.addArtwork(newArtwork);
                loadArtworks();
                loadArtists();
                message = "Artwork added successfully!";
                firePropertyChanged("message");
            } catch (SQLException e) {
                message = "Error adding artwork: " + e.getMessage();
                firePropertyChanged("message");
            }
        } else {
            message = "No artist selected!";
            firePropertyChanged("message");
        }
    }

    private void updateArtwork()
    {
        if (selectedArtworkTitle != null && !selectedArtworkTitle.isEmpty())
        {
            Artist artist = artists.stream()
                    .filter(a -> a.getName().equals(getArtistComboBoxValue()))
                    .findFirst()
                    .orElse(null);
            if (artist != null) {
                Artwork artwork = artworks.stream()
                        .filter(a -> a.getTitle().equals(selectedArtworkTitle))
                        .findFirst()
                        .orElse(null);
                if (artwork != null) {
                    artwork.setTitle(getTitleTextFieldValue());
                    artwork.setArtist(artist);
                    artwork.setType(getTypeComboBoxValue());
                    artwork.setPrice(getPriceSpinnerValue());
                    artwork.setCreationYear(getYearTextFieldValue());
                    try {
                        artworkRepo.updateArtwork(selectedArtworkTitle, artwork, artist.getName());
                        loadArtworks();
                        loadArtists();
                        message = "Artwork updated successfully!";
                        firePropertyChanged("message");
                    } catch (SQLException e) {
                        message = "Error updating artwork: " + e.getMessage();
                        firePropertyChanged("message");
                    }
                }
                else
                {
                    message = "No artwork selected!";
                    firePropertyChanged("message");
                }
            }
            else
            {
                message = "No artist selected!";
                firePropertyChanged("message");
            }
        }
        else
        {
            message = "No artwork selected!";
            firePropertyChanged("message");
        }
    }


    private void deleteArtwork()
    {
        if (selectedArtworkTitle != null && !selectedArtworkTitle.isEmpty())
        {
            try {
                artworkRepo.deleteArtwork(selectedArtworkTitle);
                artworks.removeIf(a -> a.getTitle().equals(selectedArtworkTitle));
                selectedArtworkTitle = "";
                loadArtworks();
                loadArtists();
                message = "Artwork deleted successfully!";
                firePropertyChanged("artworkImagePaths");
                firePropertyChanged("message");
            } catch (SQLException e) {
                message = "Error deleting artwork: " + e.getMessage();
                firePropertyChanged("message");
            }
        }
        else
        {
            message = "No artwork selected!";
            firePropertyChanged("message");
        }
    }


    private void searchArtists()
    {
        try {
            if (artistSearchQuery.isEmpty())
            {
                loadArtists();
            } else {
                artists = artistRepo.searchByName(artistSearchQuery);
                artistData = artists.stream().map(artist -> List.of(
                        artist.getName(),
                        artist.getBirthDate(),
                        artist.getBirthPlace(),
                        artist.getNationality(),
                        artist.getArtworks().stream().map(Artwork::getTitle).collect(Collectors.joining(", "))
                )).collect(Collectors.toList());
                artistNames = artists.stream().map(Artist::getName).collect(Collectors.toList());
                firePropertyChanged("artistData");
                firePropertyChanged("artistNames");
            }
        } catch (SQLException e) {
            message = "Error searching artists: " + e.getMessage();
            firePropertyChanged("message");
        }
    }

    private void searchArtworks()
    {
        try {
            if (artworkSearchQuery.isEmpty())
            {
                loadArtworks();
            }
            else
            {
                artworks = artworkRepo.searchByTitle(artworkSearchQuery);
                artworkData = artworks.stream().map(artwork -> List.of(
                        artwork.getTitle(),
                        artwork.getArtist().getName(),
                        artwork.getType(),
                        String.valueOf(artwork.getPrice()),
                        String.valueOf(artwork.getCreationYear())
                )).collect(Collectors.toList());
                artworkTypes = artworks.stream().map(Artwork::getType).distinct().collect(Collectors.toList());
                firePropertyChanged("artworkData");
                firePropertyChanged("artworkTypes");
            }
        } catch (SQLException e) {
            message = "Error searching artworks: " + e.getMessage();
            firePropertyChanged("message");
        }
    }

    private void filterByArtist()
    {
        try {
            if (filterArtist == null || filterArtist.isEmpty())
            {
                loadArtworks();
            } else {

                List<String> titles = artworkRepo.getArtworksByArtist(filterArtist);
                List<Artwork> filteredArtworks = new ArrayList<>();

                for (String title : titles)
                {
                    Artwork artwork = artworkRepo.findArtworkByTitle(title);

                    if (artwork != null)
                    {
                        filteredArtworks.add(artwork);
                    }
                }
                artworks = filteredArtworks;
                artworkData = artworks.stream().map(artwork -> List.of(
                        artwork.getTitle(),
                        artwork.getArtist().getName(),
                        artwork.getType(),
                        String.valueOf(artwork.getPrice()),
                        String.valueOf(artwork.getCreationYear())
                )).collect(Collectors.toList());
                artworkTypes = artworks.stream().map(Artwork::getType).distinct().collect(Collectors.toList());
                firePropertyChanged("artworkData");
                firePropertyChanged("artworkTypes");
                message = "Filtered by artist successfully!";
                firePropertyChanged("message");
            }
        } catch (SQLException e) {
            message = "Error filtering by artist: " + e.getMessage();
            firePropertyChanged("message");
        }
    }

    private void filterByPrice()
    {
        try {
            if (filterPrice <= 0)
            {
                loadArtworks();

            } else
            {
                artworks = artworkRepo.getAllArtworks().stream()
                        .filter(artwork -> artwork.getPrice() <= filterPrice)
                        .collect(Collectors.toList());

                artworkData = artworks.stream().map(artwork -> List.of(
                        artwork.getTitle(),
                        artwork.getArtist().getName(),
                        artwork.getType(),
                        String.valueOf(artwork.getPrice()),
                        String.valueOf(artwork.getCreationYear())
                )).collect(Collectors.toList());

                artworkTypes = artworks.stream().map(Artwork::getType).distinct().collect(Collectors.toList());
                firePropertyChanged("artworkData");
                firePropertyChanged("artworkTypes");
                message = "Filtered by price successfully!";
                firePropertyChanged("message");
            }
        } catch (SQLException e) {
            message = "Error filtering by price: " + e.getMessage();
            firePropertyChanged("message");
        }
    }

    private void filterByType()
    {
        try {

            if (filterType == null || filterType.isEmpty())
            {
                loadArtworks();

            }
            else
            {
                artworks = artworkRepo.getAllArtworks().stream()
                        .filter(artwork -> artwork.getType().equalsIgnoreCase(filterType))
                        .collect(Collectors.toList());
                artworkData = artworks.stream().map(artwork -> List.of(
                        artwork.getTitle(),
                        artwork.getArtist().getName(),
                        artwork.getType(),
                        String.valueOf(artwork.getPrice()),
                        String.valueOf(artwork.getCreationYear())
                )).collect(Collectors.toList());

                artworkTypes = artworks.stream().map(Artwork::getType).distinct().collect(Collectors.toList());
                firePropertyChanged("artworkData");
                firePropertyChanged("artworkTypes");
                message = "Filtered by type successfully!";
                firePropertyChanged("message");
            }
        } catch (SQLException e) {
            message = "Error filtering by type: " + e.getMessage();
            firePropertyChanged("message");
        }
    }

    private void exportToCsv()
    {
        try (FileWriter writer = new FileWriter("artworks.csv"))
        {
            writer.write("Title,Artist,Type,Price,Creation Year\n");
            for (Artwork artwork : artworks)
            {
                writer.write(String.format("%s,%s,%s,%.2f,%d\n",
                        artwork.getTitle(),
                        artwork.getArtist().getName(),
                        artwork.getType(),
                        artwork.getPrice(),
                        artwork.getCreationYear()));
            }
            message = "Exported to CSV successfully!";
            firePropertyChanged("message");

        } catch (IOException e) {
            message = "Error exporting to CSV: " + e.getMessage();
            firePropertyChanged("message");
        }
    }

    private void exportToDoc()
    {
        message = "Export to DOC not implemented yet! Use a library like Apache POI.";
        firePropertyChanged("message");
    }

    private void addImage()
    {
        if (selectedArtworkTitle != null && !selectedArtworkTitle.isEmpty())
        {
            Artwork selectedArtwork = artworks.stream()
                    .filter(a -> a.getTitle().equals(selectedArtworkTitle))
                    .findFirst()
                    .orElse(null);

            if (selectedArtwork != null)
            {
                ArtworkImage newImage = new ArtworkImage("path/to/image.jpg", selectedArtwork);
                try {
                    imageRepo.addImage(selectedArtworkTitle, newImage);
                    loadArtworkImages();
                    message = "Image added successfully!";
                    firePropertyChanged("message");
                } catch (SQLException e) {
                    message = "Error adding image: " + e.getMessage();
                    firePropertyChanged("message");
                }
            }
            else
            {
                message = "No artwork selected!";
                firePropertyChanged("message");
            }
        }
        else
        {
            message = "No artwork selected!";
            firePropertyChanged("message");
        }
    }

    private void clearArtistFields()
    {
        setNameTextFieldValue("");
        setBirthplaceTextFieldValue("");
        setBirthdayDatePickerValue("");
        setNationalityTextFieldValue("");
        message = "Artist fields cleared!";
        firePropertyChanged("message");
    }

    private void clearArtworkFields()
    {
        setTitleTextFieldValue("");
        setArtistComboBoxValue("");
        setTypeComboBoxValue("");
        setPriceSpinnerValue(0.0);
        setYearTextFieldValue(0);
        message = "Artwork fields cleared!";
        firePropertyChanged("message");
    }


    //metode auxiliare
    public String getNameTextFieldValue() {
        return nameTextFieldValue != null ? nameTextFieldValue : "";
    }

    public String getBirthplaceTextFieldValue() {
        return birthplaceTextFieldValue != null ? birthplaceTextFieldValue : "";
    }

    public String getBirthdayDatePickerValue() {
        return birthdayDatePickerValue != null ? birthdayDatePickerValue : "";
    }

    public String getNationalityTextFieldValue() {
        return nationalityTextFieldValue != null ? nationalityTextFieldValue : "";
    }

    public String getTitleTextFieldValue() {
        return titleTextFieldValue != null ? titleTextFieldValue : "";
    }

    public String getTypeComboBoxValue() {
        return typeComboBoxValue != null ? typeComboBoxValue : "";
    }

    public double getPriceSpinnerValue() {
        return priceSpinnerValue != null ? priceSpinnerValue : 0.0;
    }

    public int getYearTextFieldValue() {
        return yearTextFieldValue != null ? yearTextFieldValue : 0;
    }

    public String getArtistComboBoxValue() {
        return artistComboBoxValue != null ? artistComboBoxValue : "";
    }

    // getteri si setteri pentru view
    public List<List<String>> getArtistData() { return artistData; }
    public List<List<String>> getArtworkData() { return artworkData; }
    public List<String> getArtistNames() { return artistNames; }
    public List<String> getArtworkTypes() { return artworkTypes; }
    public List<String> getArtworkImagePaths() { return artworkImagePaths; }
    public String getSelectedArtistPhotoPath() { return selectedArtistPhotoPath; }

    public String getSelectedArtistName() { return selectedArtistName; }
    public void setSelectedArtistName(String name) {
        selectedArtistName = name;
        Artist artist = artists.stream()
                .filter(a -> a.getName().equals(name))
                .findFirst()
                .orElse(null);
        if (artist != null) {
            selectedArtistPhotoPath = artist.getPhoto();
            setNameTextFieldValue(artist.getName());
            setBirthplaceTextFieldValue(artist.getBirthPlace());
            setBirthdayDatePickerValue(artist.getBirthDate());
            setNationalityTextFieldValue(artist.getNationality());
        } else {
            selectedArtistPhotoPath = "";
        }
        firePropertyChanged("selectedArtistPhotoPath");
    }

    public String getSelectedArtworkTitle() { return selectedArtworkTitle; }

    public void setSelectedArtworkTitle(String title) {
        selectedArtworkTitle = title;
        Artwork artwork = artworks.stream()
                .filter(a -> a.getTitle().equals(title))
                .findFirst()
                .orElse(null);
        if (artwork != null) {
            setTitleTextFieldValue(artwork.getTitle());
            setArtistComboBoxValue(artwork.getArtist().getName());
            setTypeComboBoxValue(artwork.getType());
            setPriceSpinnerValue(artwork.getPrice());
            setYearTextFieldValue(artwork.getCreationYear());
            loadArtworkImages();
        } else {
            artworkImagePaths = new ArrayList<>();
            firePropertyChanged("artworkImagePaths");
        }
    }

    public String getArtistSearchQuery() { return artistSearchQuery; }
    public void setArtistSearchQuery(String artistSearchQuery) {
        this.artistSearchQuery = artistSearchQuery;
        firePropertyChanged("artistSearchQuery");
    }

    public String getArtworkSearchQuery() { return artworkSearchQuery; }
    public void setArtworkSearchQuery(String artworkSearchQuery) {
        this.artworkSearchQuery = artworkSearchQuery;
        firePropertyChanged("artworkSearchQuery");
    }

    public String getMessage() { return message; }
    public void setMessage(String message) {
        this.message = message;
        firePropertyChanged("message");
    }

    public String getFilterArtist() { return filterArtist; }
    public void setFilterArtist(String filterArtist) {
        this.filterArtist = filterArtist;
        firePropertyChanged("filterArtist");
    }

    public double getFilterPrice() { return filterPrice; }
    public void setFilterPrice(double filterPrice) {
        this.filterPrice = filterPrice;
        firePropertyChanged("filterPrice");
    }

    public String getFilterType() { return filterType; }
    public void setFilterType(String filterType) {
        this.filterType = filterType;
        firePropertyChanged("filterType");
    }

    public void setNameTextFieldValue(String value) {
        this.nameTextFieldValue = value;
        firePropertyChanged("nameTextFieldValue");
    }

    public void setBirthplaceTextFieldValue(String value) {
        this.birthplaceTextFieldValue = value;
        firePropertyChanged("birthplaceTextFieldValue");
    }

    public void setBirthdayDatePickerValue(String value) {
        this.birthdayDatePickerValue = value;
        firePropertyChanged("birthdayDatePickerValue");
    }

    public void setNationalityTextFieldValue(String value) {
        this.nationalityTextFieldValue = value;
        firePropertyChanged("nationalityTextFieldValue");
    }

    public void setTitleTextFieldValue(String value) {
        this.titleTextFieldValue = value;
        firePropertyChanged("titleTextFieldValue");
    }

    public void setTypeComboBoxValue(String value) {
        this.typeComboBoxValue = value;
        firePropertyChanged("typeComboBoxValue");
    }


    public void setPriceSpinnerValue(double value) {
        this.priceSpinnerValue = value;
        firePropertyChanged("priceSpinnerValue");
    }


    public void setYearTextFieldValue(int value) {
        this.yearTextFieldValue = value;
        firePropertyChanged("yearTextFieldValue");
    }


    public void setArtistComboBoxValue(String value) {
        this.artistComboBoxValue = value;
        firePropertyChanged("artistComboBoxValue");
    }



    // Getteri pentru comenzi
    public ICommands getAddArtistCommand() { return addArtistCommand; }
    public ICommands getUpdateArtistCommand() { return updateArtistCommand; }
    public ICommands getDeleteArtistCommand() { return deleteArtistCommand; }
    public ICommands getAddArtworkCommand() { return addArtworkCommand; }
    public ICommands getUpdateArtworkCommand() { return updateArtworkCommand; }
    public ICommands getDeleteArtworkCommand() { return deleteArtworkCommand; }
    public ICommands getSearchArtistCommand() { return searchArtistCommand; }
    public ICommands getSearchArtworkCommand() { return searchArtworkCommand; }
    public ICommands getFilterByArtistCommand() { return filterByArtistCommand; }
    public ICommands getFilterByPriceCommand() { return filterByPriceCommand; }
    public ICommands getFilterByTypeCommand() { return filterByTypeCommand; }
    public ICommands getExportToCsvCommand() { return exportToCsvCommand; }
    public ICommands getExportToDocCommand() { return exportToDocCommand; }
    public ICommands getAddImageCommand() { return addImageCommand; }
    public ICommands getClearArtistCommand() { return clearArtistCommand; }
    public ICommands getClearArtworkCommand() { return clearArtworkCommand; }
}