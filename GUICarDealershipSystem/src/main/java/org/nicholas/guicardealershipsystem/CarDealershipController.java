/**
 * Nicholas Dhannie
 * CEN 3024C - Software Development 1
 * March 29, 2024
 * CarDealershipSystemController.java
 * This class is focusing on all the logic of the application. This is what
 * will be listening to the users input with all options that they have available.
 * It will then handle all the interactions and show the changes or error messages.
 */

package org.nicholas.guicardealershipsystem;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;

import java.io.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class CarDealershipController {
    @FXML
    private TableView<Car> tableView;
    @FXML
    private Button addCarButton;
    @FXML
    private TextField yearFilterField;
    @FXML
    private TextField makeFilterField;
    @FXML
    private TextField modelFilterField;
    @FXML
    private TextField colorFilterField;
    private ObservableList<Car> allCars;


    /**
     * Name: getConnection
     *
     * This is where the program will connect to the database from
     * the url that is entered which is the path to your database.
     *
     * @return DriverManager.getConnection(url)
     * @throws SQLException
     */
    private Connection getConnection() throws SQLException {
        String url = "jdbc:sqlite:C:/sqlite/db/CarDealership.db";
        return DriverManager.getConnection(url);
    }

    public void initialize() {
        //The user cannot add a car without uploading a valid Database file
        addCarButton.setDisable(true);
    }

    /**
     * Name: loadCarsFile
     * <p>
     * This will load all the cars from the database file that the user chooses to upload
     * and sorts them into the category from the file. This will be then seen in
     * the tableView.
     */
    public void loadCarsFile() {
        // Connecting to the database based on the file that the user has uploaded
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM Cars");
             ResultSet resultSet = statement.executeQuery()) {

            ObservableList<Car> carList = FXCollections.observableArrayList();
            while (resultSet.next()) {
                //Getting the cars that are in the database and putting them based on the category
                int id = resultSet.getInt("id");
                int year = resultSet.getInt("year");
                String make = resultSet.getString("make");
                String model = resultSet.getString("model");
                String color = resultSet.getString("color");
                String engine = resultSet.getString("engine");
                String transmission = resultSet.getString("transmission");
                double price = resultSet.getDouble("price");
                boolean sold = resultSet.getBoolean("sold");

                Car car = new Car(id, year, make, model, color, engine, transmission, price);
                car.setSold(sold);
                carList.add(car);
            }
            // Populate the allCars list with the loaded cars
            allCars = FXCollections.observableArrayList(carList);
            // Set the TableView items to the allCars list
            tableView.setItems(allCars);

        } catch (SQLException e) {
            e.printStackTrace();
            // Show an error message
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Failed to Load Cars from Database");
            alert.setContentText("Please make sure the correct file has been uploaded, and make sure path is correct.");
            alert.showAndWait();
        }
    }

    /**
     * Name: removeSelectedCar
     *
     * This will remove the selected car that the user has selected from
     * the view on the file that was uploaded.
     */
    @FXML
    private void removeSelectedCar() {
        Car selectedCar = tableView.getSelectionModel().getSelectedItem();
        if (selectedCar != null) {
            try (Connection connection = getConnection();
                 //removing the car that the user has selected
                 PreparedStatement statement = connection.prepareStatement("DELETE FROM Cars WHERE id = ?")) {
                statement.setInt(1, selectedCar.getId());
                statement.executeUpdate();
                tableView.getItems().remove(selectedCar);
            } catch (SQLException e) {
                e.printStackTrace();
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Failed to Remove Car");
                alert.setContentText("An error occurred while removing the car from the database.");
                alert.showAndWait();
            }
        }
    }

    /**
     * Name: editCarDialog
     *
     * @param selectedCar This will allow the user to edit the car that they have selected
     *                    from the view. I made it to where the user has to enter numbers
     *                    for the year and price field. They will be able to save the changes
     *                    that were made or cancel them.
     */
    @FXML
    private void editCarDialog(Car selectedCar) {
        Dialog<Car> dialog = new Dialog<>();
        dialog.setTitle("Edit Car");
        dialog.setHeaderText("Edit Fields");

        // Save Button
        ButtonType saveButton = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButton, ButtonType.CANCEL);

        // input fields for each attribute
        TextField yearField = new TextField(Integer.toString(selectedCar.getYear()));
        TextField makeField = new TextField(selectedCar.getMake());
        TextField modelField = new TextField(selectedCar.getModel());
        TextField colorField = new TextField(selectedCar.getColor());
        TextField engineField = new TextField(selectedCar.getEngine());
        TextField transmissionField = new TextField(selectedCar.getTransmissionType());
        TextField priceField = new TextField(Double.toString(selectedCar.getPrice()));
        CheckBox soldCheckbox = new CheckBox("Sold");
        soldCheckbox.setSelected(selectedCar.isSold());

        // Make sure that year and price are numbers and not string
        yearField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                yearField.setText(newValue.replaceAll("\\D", ""));
            }
        });

        priceField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*(\\.\\d*)?")) {
                priceField.setText(oldValue);
            }
        });

        dialog.getDialogPane().setContent(new VBox(8, yearField, makeField, modelField, colorField, engineField, transmissionField, priceField, soldCheckbox));
        // Convert the result to a car object when the save button is clicked
        dialog.setResultConverter(buttonType -> {
            if (buttonType == saveButton) {
                if (yearField.getText().isEmpty() || makeField.getText().isEmpty() || modelField.getText().isEmpty() || colorField.getText().isEmpty() || engineField.getText().isEmpty() || transmissionField.getText().isEmpty() || priceField.getText().isEmpty()) {
                    // Show an error message
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setHeaderText("Incomplete Fields");
                    alert.setContentText("Please fill in all fields.");
                    alert.showAndWait();
                    return null;
                }
                try (Connection connection = getConnection();
                     PreparedStatement statement = connection.prepareStatement("UPDATE Cars SET year=?, make=?, model=?, color=?, engine=?, transmission=?, price=?, sold=? WHERE id=?")) {
                    int year = Integer.parseInt(yearField.getText());
                    String make = makeField.getText();
                    String model = modelField.getText();
                    String color = colorField.getText();
                    String engine = engineField.getText();
                    String transmissionType = transmissionField.getText();
                    double price = Double.parseDouble(priceField.getText());
                    boolean sold = soldCheckbox.isSelected();

                    //parameters for the prepared statement
                    statement.setInt(1, year);
                    statement.setString(2, make);
                    statement.setString(3, model);
                    statement.setString(4, color);
                    statement.setString(5, engine);
                    statement.setString(6, transmissionType);
                    statement.setDouble(7, price);
                    statement.setBoolean(8, sold);
                    statement.setInt(9, selectedCar.getId());

                    // Execute the update
                    statement.executeUpdate();

                    // Updating selected car object with the edited details
                    selectedCar.setYear(year);
                    selectedCar.setMake(make);
                    selectedCar.setModel(model);
                    selectedCar.setColor(color);
                    selectedCar.setEngine(engine);
                    selectedCar.setTransmissionType(transmissionType);
                    selectedCar.setPrice(price);
                    selectedCar.setSold(sold);

                    // Update TableView
                    tableView.refresh();
                } catch (SQLException | NumberFormatException e) {
                    // Show error message
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setHeaderText("Failed to Update Car");
                    alert.setContentText("An error occurred while updating the car details.");
                    alert.showAndWait();
                }
            }
            return null;
        });
        dialog.showAndWait();
    }

    /**
     * Name: uploadFile
     * <p>
     * This is what the user will be seeing when they select the upload File
     * button. It is made to where the user can only select a SQL or SQLite file.
     */
    @FXML
    private void uploadFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Car Data File");

        // Only allow SQL & SQLite files
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("SQL & SQLite files (*.sql *.db, *.sqlite)", "*.sql, SQLite files (*.db, *.sqlite)", "*.db", "*.sqlite");
        fileChooser.getExtensionFilters().add(extFilter);

        File selectedFile = fileChooser.showOpenDialog(null);

        if (selectedFile != null) {
            loadCarsFile();
            addCarButton.setDisable(false);
        }
    }


    /**
     * Name: addCarDialog
     * <p>
     * This will allow users to see the fields when they select the add car
     * button. This is basically the same as edit car.
     */
    @FXML
    private void addCarDialog() {
        Dialog<Car> dialog = new Dialog<>();
        dialog.setTitle("Add Car");
        dialog.setHeaderText("Enter Car Details");

        // Set the button types
        ButtonType addButton = new ButtonType("Add", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addButton, ButtonType.CANCEL);

        // Create text fields for each attribute
        TextField yearField = new TextField();
        TextField makeField = new TextField();
        TextField modelField = new TextField();
        TextField colorField = new TextField();
        TextField engineField = new TextField();
        TextField transmissionField = new TextField();
        TextField priceField = new TextField();

        yearField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                yearField.setText(newValue.replaceAll("\\D", ""));
            }
        });

        priceField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*(\\.\\d*)?")) {
                priceField.setText(oldValue);
            }
        });

        dialog.getDialogPane().setContent(new VBox(8, yearField, makeField, modelField, colorField, engineField, transmissionField, priceField)); // Add other text fields...

        dialog.setResultConverter(buttonType -> {
            if (buttonType == addButton) {
                // Validate that all fields are not empty
                if (yearField.getText().isEmpty() || makeField.getText().isEmpty() || modelField.getText().isEmpty() || colorField.getText().isEmpty() || engineField.getText().isEmpty() || transmissionField.getText().isEmpty() || priceField.getText().isEmpty()) {
                    // Show an error message
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setHeaderText("Incomplete Fields");
                    alert.setContentText("Please fill in all fields.");
                    alert.showAndWait();
                    return null;
                }
                try (Connection connection = getConnection();
                     PreparedStatement statement = connection.prepareStatement("INSERT INTO Cars (year, make, model, color, engine, transmission, price, sold) VALUES (?, ?, ?, ?, ?, ?, ?, ?)")) {
                    statement.setInt(1, Integer.parseInt(yearField.getText()));
                    statement.setString(2, makeField.getText());
                    statement.setString(3, modelField.getText());
                    statement.setString(4, colorField.getText());
                    statement.setString(5, engineField.getText());
                    statement.setString(6, transmissionField.getText());
                    statement.setDouble(7, Double.parseDouble(priceField.getText()));
                    statement.setInt(8, 0);

                    statement.executeUpdate();
                    loadCarsFile();
                } catch (SQLException e) {
                    e.printStackTrace();
                    // Show an error message
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setHeaderText("Failed to Add Car");
                    alert.setContentText("An error occurred while adding the car to the database.");
                    alert.showAndWait();
                }
            }
            return null;
        });
        dialog.showAndWait();
    }

    /**
     * Name: editSelectedCar
     *
     * this is to make sure that the user can be able to select
     * a car and edit it with the button. It will then go through
     * the editCarDialog in which the user has to fill out.
     */
    @FXML
    public void editSelectedCar() {
        Car selectedCar = tableView.getSelectionModel().getSelectedItem();
        if (selectedCar != null) {
            editCarDialog(selectedCar);
        }
    }

    /**
     * Name: removeFilteredCars
     *
     * This will allow the user to remove the car that they want by
     * entering something in one of the options for the filter
     * and be able to remove based on what they have entered.
     */
    @FXML
    private void removeFilteredCars() {
        String yearFilter = yearFilterField.getText().trim();
        String makeFilter = makeFilterField.getText().trim();
        String modelFilter = modelFilterField.getText().trim();
        String colorFilter = colorFilterField.getText().trim();

        // Check if at least one filter field contains a value
        if (yearFilter.isEmpty() && makeFilter.isEmpty() && modelFilter.isEmpty() && colorFilter.isEmpty()) {
            // Show a message that a filter needs to be applied
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Information");
            alert.setHeaderText("No Filters Applied");
            alert.setContentText("Please enter at least one filter criteria.");
            alert.showAndWait();
            return;
        }

        // Create a list to hold all filter predicates
        List<Predicate<Car>> filterPredicates = new ArrayList<>();
        // Add filter predicates based on the values in filter fields
        if (!yearFilter.isEmpty()) {
            filterPredicates.add(car -> String.valueOf(car.getYear()).equals(yearFilter));
        }

        if (!makeFilter.isEmpty()) {
            filterPredicates.add(car -> car.getMake().equalsIgnoreCase(makeFilter));
        }

        if (!modelFilter.isEmpty()) {
            filterPredicates.add(car -> car.getModel().equalsIgnoreCase(modelFilter));
        }

        if (!colorFilter.isEmpty()) {
            filterPredicates.add(car -> car.getColor().equalsIgnoreCase(colorFilter));
        }
        // Combine all predicates with AND logic
        Predicate<Car> combinedPredicate = filterPredicates.stream().reduce(Predicate::and).orElse(car -> true);

        // Apply the combined filter to get filtered cars
        ObservableList<Car> filteredCars = allCars.filtered(combinedPredicate);

        // Check if any cars match the filter
        if (!filteredCars.isEmpty()) {
            // Remove the filtered cars from the database
            try (Connection connection = getConnection()) {
                for (Car car : filteredCars) {
                    String deleteQuery = "DELETE FROM Cars WHERE id = ?";
                    try (PreparedStatement statement = connection.prepareStatement(deleteQuery)) {
                        statement.setInt(1, car.getId());
                        statement.executeUpdate();
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Failed to Remove Cars from Database");
                alert.setContentText("An error occurred while removing cars from the database.");
                alert.showAndWait();
                return;
            }

            // Remove the filtered cars from the existing list
            allCars.removeAll(filteredCars);

            // Update the TableView items
            tableView.setItems(allCars);
            // Clear the filter fields
            yearFilterField.clear();
            makeFilterField.clear();
            modelFilterField.clear();
            colorFilterField.clear();
        }
    }
}