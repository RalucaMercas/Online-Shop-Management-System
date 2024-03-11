package com.example.test2javafx;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.*;

public class AdminController implements Initializable {
    private static Stage adminStage;

    @FXML
    private Button button_add_user;

    @FXML
    private Button button_remove_user;

    @FXML
    private Button buttonDeleteAccount;

    @FXML
    private Button button_bodyCare;

    @FXML
    private Button button_hairCare;

    @FXML
    private Button button_makeUp;

    @FXML
    private Button button_menPerfume;

    @FXML
    private Button button_nailCare;

    @FXML
    private Button button_womenPerfume;

    @FXML
    private Button button_logout;

    @FXML
    private BorderPane mainPane;

    @FXML
    private Tab tab_users;

    @FXML
    private Tab tab_details;

    @FXML
    private Tab tab_products;

    @FXML
    private ListView<User> usersListView;

    @FXML
    private Label getAddressLabel;

    @FXML
    private Label getEmailLabel;

    @FXML
    private Label getFirstNameLabel;

    @FXML
    private Label getPNumberLabel;

    @FXML
    private Label getUsernameLabel;

    @FXML
    private Label getlastNameLabel;

    @FXML
    private Button buttonChangePassword;

    @FXML
    private Button buttonSwitchToClientAccount;

    @FXML
    private ImageView editUsernameIcon;

    @FXML
    private ImageView editFirstNameIcon;

    @FXML
    private ImageView editLastNameIcon;

    @FXML
    private ImageView editEmailIcon;

    @FXML
    private ImageView editAddressIcon;

    @FXML
    private ImageView editPhoneNumberIcon;

    @FXML
    private ImageView editProduct;

    @FXML
    void handleChangePassword() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("changePassword.fxml"));
        Parent root;
        try {
            root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Change Password");
            stage.setScene(new Scene(root, 300, 230));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void handleButtonBodyCareOnAction() {
        ShopFxmlLoader loader = new ShopFxmlLoader();
        Pane view = loader.getPage("bodyCare");
        if (view != null) {
            mainPane.setCenter(view);
        }
        List<Product> bodyProducts = DBUtils.getProductsByCategory(1);
        for (Product product : bodyProducts) {
            if (product.getStockQuantity() == 0) {
                showRestockAlertForProduct(product);
            }
        }
    }

    @FXML
    void handleButtonMakeUpOnAction() {
        ShopFxmlLoader loader = new ShopFxmlLoader();
        Pane view = loader.getPage("makeup");
        if (view != null) {
            mainPane.setCenter(view);
        }
        List<Product> makeupProducts = DBUtils.getProductsByCategory(3);
        for (Product product : makeupProducts) {
            if (product.getStockQuantity() == 0) {
                showRestockAlertForProduct(product);
            }
        }
    }

    @FXML
    void handleButtonNailCareOnAction() {
        System.out.println("You clicked on nail care button!");
        ShopFxmlLoader loader = new ShopFxmlLoader();
        Pane view = loader.getPage("nailCare");
        if (view != null) {
            mainPane.setCenter(view);
        }
        List<Product> nailProducts = DBUtils.getProductsByCategory(2);
        for (Product product : nailProducts) {
            if (product.getStockQuantity() == 0) {
                showRestockAlertForProduct(product);
            }
        }
    }

    @FXML
    void handleButtonWomenPerfumeryOnAction() {
        ShopFxmlLoader loader = new ShopFxmlLoader();
        Pane view = loader.getPage("womenPerfumery");
        if (view != null) {
            mainPane.setCenter(view);
        }
        List<Product> womenPerfumeryProducts = DBUtils.getProductsByCategory(4);
        for (Product product : womenPerfumeryProducts) {
            if (product.getStockQuantity() == 0) {
                showRestockAlertForProduct(product);
            }
        }
    }

    @FXML
    void handleButtonMenPerfumeryOnAction() {
        ShopFxmlLoader loader = new ShopFxmlLoader();
        Pane view = loader.getPage("menPerfumery");
        if (view != null) {
            mainPane.setCenter(view);
        }
        List<Product> menPerfumeryProducts = DBUtils.getProductsByCategory(5);
        for (Product product : menPerfumeryProducts) {
            if (product.getStockQuantity() == 0) {
                showRestockAlertForProduct(product);
            }
        }
    }

    @FXML
    void handleButtonHairCareOnAction(ActionEvent event) {
        ShopFxmlLoader loader = new ShopFxmlLoader();
        Pane view = loader.getPage("hairCare");
        if (view != null) {
            mainPane.setCenter(view);
        }
        List<Product> hairProducts = DBUtils.getProductsByCategory(6);
        for (Product product : hairProducts) {
            if (product.getStockQuantity() == 0) {
                showRestockAlertForProduct(product);
            }
        }
    }

    private ImageView addGraphic(String imageFile) {
        Image image = new Image(Objects.requireNonNull(getClass().getResourceAsStream(imageFile)));
        ImageView imageView = new ImageView(image);
        imageView.setFitHeight(22);
        imageView.setFitWidth(22);
        return imageView;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        adminStage = new Stage();
        adminStage.setTitle("Admin Window");
        adminStage.setOnCloseRequest(event -> adminStage = null);
        ImageView viewProducts = addGraphic("cosmetic.png");
        tab_products.setGraphic(viewProducts);
        ImageView viewUsers = addGraphic("users.png");
        tab_users.setGraphic(viewUsers);
        ImageView viewDetails = addGraphic("people.png");
        tab_details.setGraphic(viewDetails);

        button_logout.setOnAction(event -> {
            DBUtils.logOutUser();
            DBUtils.changeScene(event, "log-in.fxml", "Login", null);
        });

        if (DBUtils.getCurrentUser() != null) {
            User currentUser = DBUtils.getCurrentUser();
            getUsernameLabel.setText(currentUser.getUsername());
            getFirstNameLabel.setText(currentUser.getFirstName());
            getlastNameLabel.setText(currentUser.getLastName());
            getEmailLabel.setText(currentUser.getEmail());
            getAddressLabel.setText(currentUser.getAddress());
            getPNumberLabel.setText(currentUser.getPhoneNumber());
        }

        if (usersListView != null) {
            usersListView.getItems().addAll(DBUtils.getUsers());
            usersListView.getSelectionModel().setSelectionMode(javafx.scene.control.SelectionMode.MULTIPLE);
        } else {
            System.err.println("usersListView is null");
        }

        button_add_user.setOnAction(event -> showAddUserDialog());

        button_remove_user.setOnAction(event -> {
            ObservableList<User> selectedUsers = usersListView.getSelectionModel().getSelectedItems();

            if (!selectedUsers.isEmpty()) {
                if (isCurrentUserSelected(selectedUsers)) {
                    showErrorAlert(); // Display an error message if the admin is trying to delete their own account
                } else {
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setTitle("Confirmation");
                    alert.setHeaderText("Remove Users");
                    alert.setContentText("Are you sure you want to remove the selected user(s)?");

                    Optional<ButtonType> result = alert.showAndWait();
                    if (result.isPresent() && result.get() == ButtonType.OK) {
                        DBUtils.removeSelectedUsers(selectedUsers);
                        updateUsersListView();
                    }
                }
            } else {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Information");
                alert.setHeaderText(null);
                alert.setContentText("Please select user(s) to remove.");
                alert.showAndWait();
            }
        });

        buttonDeleteAccount.setOnAction(this::showDeleteAccountDialog);

        setEditIcon(editUsernameIcon, getUsernameLabel);
        setEditIcon(editFirstNameIcon, getFirstNameLabel);
        setEditIcon(editLastNameIcon, getlastNameLabel);
        setEditIcon(editEmailIcon, getEmailLabel);
        setEditIcon(editAddressIcon, getAddressLabel);
        setEditIcon(editPhoneNumberIcon, getPNumberLabel);

        buttonSwitchToClientAccount.setOnAction(event -> {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmation");
            alert.setHeaderText("Switch to Client Account");
            alert.setContentText("Are you sure you want to permanently switch to your Client account? This action cannot be undone.");

            ButtonType switchButtonType = new ButtonType("Switch");
            ButtonType cancelButtonType = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
            alert.getButtonTypes().setAll(switchButtonType, cancelButtonType);

            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == switchButtonType) {
                DBUtils.updateUserInDatabase("userType", "client");
                DBUtils.logOutUser();
                DBUtils.changeScene(event, "log-in.fxml", "Login", null);
            }
        });

    }

    private void setEditIcon(ImageView icon, Label label) {
        Image editImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream("edit_icon.png")));
        icon.setImage(editImage);
        icon.setFitHeight(16);
        icon.setFitWidth(16);
        icon.setOnMouseClicked(event -> showEditDialog(label));
    }

    private void showEditDialog(Label label) {
        TextInputDialog dialog = new TextInputDialog(label.getText());
        dialog.setTitle("Edit Information");
        switch (label.getId()) {
            case "getUsernameLabel":
                dialog.setHeaderText("Edit username");
                dialog.setContentText("New username:");
                break;
            case "getFirstNameLabel":
                dialog.setHeaderText("Edit first name");
                dialog.setContentText("New first name:");
                break;
            case "getlastNameLabel":
                dialog.setHeaderText("Edit last name");
                dialog.setContentText("New last name:");
                break;
            case "getEmailLabel":
                dialog.setHeaderText("Edit email");
                dialog.setContentText("New email:");
                break;
            case "getAddressLabel":
                dialog.setHeaderText("Edit address");
                dialog.setContentText("New address:");
                break;
            case "getPNumberLabel":
                dialog.setHeaderText("Edit phone number");
                dialog.setContentText("New phone number:");
                break;
            default:
                break;
        }

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(newText -> {
            if (newText.trim().isEmpty()) {
                showWarningDialog("Input cannot be empty.");
                return;
            }
            User currentUser = DBUtils.getCurrentUser();
            if (currentUser != null) {
                String existingValue = DBUtils.getExistingValue(label.getId());
                if (existingValue.equals(newText)) {
                    showWarningDialog("New input must be different from the existing information.");
                    return;
                }
                switch (label.getId()) {
                    case "getUsernameLabel":
                        if(!UserValidator.isValidUsername(newText)) {
                            UserValidator.showInvalidAlert("Oops, the username cannot start with a digit or a special character. Please try again!");
                            showEditDialog(label);
                        } else {
                            label.setText(newText);
                            DBUtils.updateUserInDatabase("username", newText);
                        }
                        break;
                    case "getFirstNameLabel":
                        if(!UserValidator.isValidFirstName(newText)) {
                            UserValidator.showInvalidAlert("Oops, invalid first name. Make sure it only contains letters and optional hyphens!");
                            showEditDialog(label);
                        } else {
                            label.setText(newText);
                            DBUtils.updateUserInDatabase("firstName", newText);
                        }
                        break;
                    case "getlastNameLabel":
                        if(!UserValidator.isValidLastName(newText)) {
                            UserValidator.showInvalidAlert("Oops, invalid last name. Make sure it only contains letters and optional hyphens!");
                            showEditDialog(label);
                        } else {
                            label.setText(newText);
                            DBUtils.updateUserInDatabase("lastName", newText);
                        }
                        break;
                    case "getEmailLabel":
                        if(!UserValidator.isValidEmail(newText)) {
                            UserValidator.showInvalidAlert("Oops, invalid email format. Please try again!");
                            showEditDialog(label);
                        } else {
                            label.setText(newText);
                            DBUtils.updateUserInDatabase("email", newText);
                        }
                        break;
                    case "getAddressLabel":
                        if(!UserValidator.isValidAddress(newText)) {
                            UserValidator.showInvalidAlert("Oops, the address cannot be empty. Please try again!");
                            showEditDialog(label);
                        } else {
                            label.setText(newText);
                            DBUtils.updateUserInDatabase("address", newText);
                        }
                        break;
                    case "getPNumberLabel":
                        if(!UserValidator.isValidPhoneNumber(newText)){
                            UserValidator.showInvalidAlert("Oops, invalid phone number format. Please try again!");
                            showEditDialog(label);
                        } else {
                            label.setText(newText);
                            DBUtils.updateUserInDatabase("phoneNumber", newText);
                        }
                        break;
                    default:
                        break;
                }
            }
        });
    }

    private void showWarningDialog(String content) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private boolean isCurrentUserSelected(ObservableList<User> selectedUsers) {
        User currentUser = DBUtils.getCurrentUser();
        return selectedUsers.contains(currentUser);
    }

    public static Stage getAdminStage() {
        return adminStage;
    }

    private void showErrorAlert() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText("Cannot delete your own account.");
        alert.showAndWait();
    }

    private void showAddUserDialog() {
        Dialog<User> dialog = new Dialog<>();
        dialog.setTitle("Add New User");

        ButtonType addButton = new ButtonType("Add", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addButton, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new javafx.geometry.Insets(20, 150, 10, 10));

        TextField firstName = new TextField();
        TextField lastName = new TextField();
        TextField username = new TextField();
        TextField email = new TextField();
        PasswordField password = new PasswordField();
        TextField address = new TextField();
        TextField phoneNumber = new TextField();
        ChoiceBox<String> userType = new ChoiceBox<>();
        userType.getItems().addAll("admin", "client");

        grid.add(new Label("First Name:"), 0, 0);
        grid.add(firstName, 1, 0);
        grid.add(new Label("Last Name:"), 0, 1);
        grid.add(lastName, 1, 1);
        grid.add(new Label("Username:"), 0, 2);
        grid.add(username, 1, 2);
        grid.add(new Label("Email:"), 0, 3);
        grid.add(email, 1, 3);
        grid.add(new Label("Password:"), 0, 4);
        grid.add(password, 1, 4);
        grid.add(new Label("Address:"), 0, 5);
        grid.add(address, 1, 5);
        grid.add(new Label("Phone Number:"), 0, 6);
        grid.add(phoneNumber, 1, 6);
        grid.add(new Label("User Type:"), 0, 7);
        grid.add(userType, 1, 7);
        Node addButtonNode = dialog.getDialogPane().lookupButton(addButton);
        addButtonNode.setDisable(true);
        List<TextField> requiredFields = Arrays.asList(firstName, lastName, email, password, address, phoneNumber);
        for (TextField field : requiredFields) {
            field.textProperty().addListener((observable, oldValue, newValue) -> addButtonNode.setDisable(requiredFields.stream().anyMatch(tf -> tf.getText().trim().isEmpty()) || userType.getValue() == null));
        }
        userType.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> addButtonNode.setDisable(requiredFields.stream().anyMatch(tf -> tf.getText().trim().isEmpty()) || newValue == null));
        dialog.getDialogPane().setContent(grid);
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == addButton) {
                User newUser = new User(0, firstName.getText(), lastName.getText(), email.getText(), password.getText(),
                        address.getText(), phoneNumber.getText(), username.getText(), User.setRoleByUserType(userType.getValue()));
                if(!UserValidator.isValidUser(newUser)) {
                    try {
                        dialog.wait();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
                return newUser;
            }
            return null;
        });
        dialog.showAndWait().ifPresent(this::addUserToDatabase);
    }

    private void showDeleteAccountDialog(ActionEvent event) {
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Delete Account");
        dialog.setHeaderText("Are you sure you want to delete your account?");
        ButtonType deleteButtonType = new ButtonType("Delete", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(deleteButtonType, ButtonType.CANCEL);
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Enter your password");
        grid.add(new Label("Password:"), 0, 0);
        grid.add(passwordField, 1, 0);
        Node deleteButton = dialog.getDialogPane().lookupButton(deleteButtonType);
        deleteButton.setDisable(true);
        passwordField.textProperty().addListener((observable, oldValue, newValue) -> deleteButton.setDisable(newValue.trim().isEmpty()));
        dialog.getDialogPane().setContent(grid);
        Platform.runLater(passwordField::requestFocus);
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == deleteButtonType) {
                return passwordField.getText();
            }
            return null;
        });

        Optional<String> result = dialog.showAndWait();

        result.ifPresent(password -> {
            if (DBUtils.validatePassword(password, DBUtils.getCurrentUser())) {
                ObservableList<User> user = FXCollections.observableArrayList(DBUtils.getCurrentUser());
                DBUtils.removeSelectedUsers(user);
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setContentText("Account deleted successfully!");
                alert.showAndWait();
                DBUtils.logOutUser();
                DBUtils.changeScene(event, "log-in.fxml", "Login", null);
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Invalid Password");
                alert.setHeaderText(null);
                alert.setContentText("Oops, your password is invalid. Please make sure you include uppercase letters, lowercase letters, numbers and symbols!");
                alert.showAndWait();
            }
        });
    }

    private void addUserToDatabase(User user) {
        try (Connection conn = MySQLJDBCUtil.getConnection()) {
            String sql = "INSERT INTO user (firstName, lastName, email, password, address, phoneNumber, userType, username) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement pst = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            pst.setString(1, user.getFirstName());
            pst.setString(2, user.getLastName());
            pst.setString(3, user.getEmail());
            pst.setString(4, user.getPassword());
            pst.setString(5, user.getAddress());
            pst.setString(6, user.getPhoneNumber());
            pst.setString(7, user.getUserTypeByRole(user.getRole()));
            pst.setString(8, user.getUsername());
            int rowsAffected = pst.executeUpdate();
            if (rowsAffected > 0) {
                try (ResultSet generatedKeys = pst.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int userId = generatedKeys.getInt(1);
                        user.setUserId(userId);
                        updateUsersListView();
                    } else {
                        System.err.println("Failed to retrieve userId.");
                    }
                }
            } else {
                System.err.println("Failed to add user to the database.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void updateUsersListView() {
        if (usersListView != null) {
            usersListView.getItems().clear();
            usersListView.getItems().addAll(DBUtils.getUsers());
        } else {
            System.err.println("usersListView is null");
        }
    }

    private void showRestockAlertForProduct(Product product) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Restock Required");
        alert.setHeaderText(null);
        alert.setContentText("Product '" + product.getName() + "' is out of stock. Please restock before proceeding.");
        alert.showAndWait();
    }
}