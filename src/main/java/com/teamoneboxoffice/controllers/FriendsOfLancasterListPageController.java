package com.teamoneboxoffice.controllers;

import com.teamoneboxoffice.entities.Friends_Of_Lancaster;
import com.teamoneboxoffice.services.implementations.DAOs.FriendsOfLancasterDAO;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Controller for the Friends of Lancaster List page.
 * Loads and displays friend data in a TableView.
 */
public class FriendsOfLancasterListPageController implements Initializable {

    @FXML private TableView<Friends_Of_Lancaster> friendsTableView;
    @FXML private TableColumn<Friends_Of_Lancaster, Integer> idColumn;
    @FXML private TableColumn<Friends_Of_Lancaster, String> nameColumn;
    @FXML private TableColumn<Friends_Of_Lancaster, String> emailColumn;
    @FXML private TableColumn<Friends_Of_Lancaster, String> phoneColumn;
    @FXML private Label statusLabel;

    private FriendsOfLancasterDAO friendsDAO;
    private ObservableList<Friends_Of_Lancaster> friendsList;

    /**
     * Initializes the controller class. This method is automatically called
     * after the fxml file has been loaded. Sets up the table columns and loads data.
     *
     * @param url The location used to resolve relative paths for the root object, or null if not known.
     * @param resourceBundle The resources used to localize the root object, or null if not known.
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        friendsDAO = new FriendsOfLancasterDAO();
        setupTableColumns();
        loadFriendsData();
    }

    /**
     * Configures the TableView columns to bind to the Friends_Of_Lancaster entity properties.
     */
    private void setupTableColumns() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("friendID"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        phoneColumn.setCellValueFactory(new PropertyValueFactory<>("phoneNumber"));
    }

    /**
     * Loads the list of Friends of Lancaster from the database using the DAO
     * and populates the TableView.
     */
    private void loadFriendsData() {
        statusLabel.setText("Loading Friends list...");
        List<Friends_Of_Lancaster> fetchedFriends = friendsDAO.getAllFriends();

        if (fetchedFriends != null) {
            friendsList = FXCollections.observableArrayList(fetchedFriends);
            friendsTableView.setItems(friendsList);
            statusLabel.setText("Showing " + friendsList.size() + " Friends of Lancaster.");
        } else {
            friendsTableView.getItems().clear();
            statusLabel.setText("Failed to load Friends list.");
        }
        friendsTableView.setPlaceholder(new Label("No Friends of Lancaster found in the database."));
    }
}
