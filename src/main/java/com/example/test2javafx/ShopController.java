package com.example.test2javafx;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
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
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;

public class ShopController implements Initializable {
    private static Stage clientStage;
    private static Stage shopStage;

    @FXML
    private Button buttonOrderHistory;

    @FXML
    private TabPane tabPane;

    @FXML
    private Button buttonDeleteAccount;

    @FXML
    private Button buttonPlaceOrder;

    @FXML
    private Button button_bodyCare;

    @FXML
    private Button button_hairCare;

    @FXML
    private Button button_makeUp;

    @FXML
    private Button button_menPerfumes;

    @FXML
    private Button button_nailCare;

    @FXML
    private Button button_womenPerfumes;

    @FXML
    private Button button_logout;

    @FXML
    private BorderPane mainPane;

    @FXML
    private Tab tab_cart;

    @FXML
    private Tab tab_details;

    @FXML
    private Tab tab_products;

    @FXML
    private ListView<CartItem> cartListView;

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
    private ImageView emptyCartImage;

    @FXML
    void handleButtonOrderHistory(ActionEvent event) {
        Node source = (Node) event.getSource();
        Stage stageOrder = (Stage) source.getScene().getWindow();
        stageOrder.close();
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("orderHistory.fxml"));
            Parent root = loader.load();
            OrderHistoryController orderHistoryController = loader.getController();
            User currentUser = DBUtils.getCurrentUser();
            ObservableList<OrderDetails> orderHistory = FXCollections.observableArrayList();
            try {
                orderHistory.addAll(DBUtils.getOrderHistory(currentUser));
            } catch (SQLException e) {
                e.printStackTrace();
            }
            orderHistoryController.setOrderHistoryItems(orderHistory);
            Stage stage = new Stage();
            stage.setTitle("Order History");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void handleButtonPlaceOrderOnAction(ActionEvent event) {
        System.out.println("\n\nYou clicked on Place Order button\n");
        User currentUser = DBUtils.getCurrentUser();
        ArrayList<CartItem> cartItems = new ArrayList<>(DBUtils.getCartItems(currentUser));
        if (cartItems.isEmpty()) {
            showWarning_dialog("Empty Cart", "Your cart is empty. Add products to your cart before placing an order.");
            return;
        }
        Node source = (Node) event.getSource();
        Stage stageSop = (Stage) source.getScene().getWindow();
        stageSop.close();

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("order.fxml"));
            Parent root = loader.load();
            OrderController orderController = loader.getController();
            orderController.initializeData(cartListView.getItems());
            Stage stage = new Stage();
            stage.setTitle("Order");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void showWarning_dialog(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    @FXML
    void handleChangePassword(ActionEvent event) {
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
    }


    @FXML
    void handleButtonMakeUpOnAction() {
        ShopFxmlLoader loader = new ShopFxmlLoader();
        Pane view = loader.getPage("makeup");
        if (view != null) {
            mainPane.setCenter(view);
        }
    }

    @FXML
    void handleButtonNailCareOnAction() {
        ShopFxmlLoader loader = new ShopFxmlLoader();
        Pane view = loader.getPage("nailCare");
        if (view != null) {
            mainPane.setCenter(view);
        }
    }

    @FXML
    void handleButtonWomenPerfumeryOnAction() {
        ShopFxmlLoader loader = new ShopFxmlLoader();
        Pane view = loader.getPage("womenPerfumery");
        if (view != null) {
            mainPane.setCenter(view);
        }
    }

    @FXML
    void handleButtonMenPerfumeryOnAction() {
        ShopFxmlLoader loader = new ShopFxmlLoader();
        Pane view = loader.getPage("menPerfumery");
        if (view != null) {
            mainPane.setCenter(view);
        }
    }

    @FXML
    void handleButtonHairCareOnAction() {
        ShopFxmlLoader loader = new ShopFxmlLoader();
        Pane view = loader.getPage("hairCare");
        if (view != null) {
            mainPane.setCenter(view);
        }
    }

    private ImageView addGraphic(String imageFile) {
        ImageView imageView = null;
        Image image = new Image(getClass().getResourceAsStream(imageFile));
        imageView = new ImageView(image);
        imageView.setFitHeight(22);
        imageView.setFitWidth(22);
        return imageView;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        shopStage = new Stage();
        clientStage = new Stage();
        clientStage.setTitle("Admin Window");
        clientStage.setOnCloseRequest(event -> clientStage = null);

        ImageView viewProducts = addGraphic("cosmetic.png");
        tab_products.setGraphic(viewProducts);
        ImageView viewCart = addGraphic("shopping-cart.png");
        tab_cart.setGraphic(viewCart);
        ImageView viewDetails = addGraphic("people.png");
        tab_details.setGraphic(viewDetails);

        button_logout.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                DBUtils.logOutUser();
                DBUtils.changeScene(event, "log-in.fxml", "Login", null);
            }
        });

        if(DBUtils.getCurrentUser() != null){
            User currentUser = DBUtils.getCurrentUser();
            getUsernameLabel.setText(currentUser.getUsername());
            getFirstNameLabel.setText(currentUser.getFirstName());
            getlastNameLabel.setText(currentUser.getLastName());
            getEmailLabel.setText(currentUser.getEmail());
            getAddressLabel.setText(currentUser.getAddress());
            getPNumberLabel.setText(currentUser.getPhoneNumber());
        }

        if (cartListView != null) {
            ///adaugam spinner ul
            cartListView.setCellFactory(param -> new SpinnerListCell());
            cartListView.getItems().addAll(DBUtils.getCartItems(DBUtils.getCurrentUser()));
            cartListView.getSelectionModel().setSelectionMode(javafx.scene.control.SelectionMode.MULTIPLE);
        } else {
            System.err.println("productListView is null");
        }


        tab_cart.setOnSelectionChanged(event -> {
            if (tab_cart.isSelected()) {
                updateCartListView();
                checkProductVisibility();
                deleteCartItemsWithNotVisibleProducts();
                updateCartListView();
            }
        });


        buttonDeleteAccount.setOnAction(event -> {
            showDeleteAccountDialog(event);
        });

        // Set edit icons
        setEditIcon(editUsernameIcon, getUsernameLabel);
        setEditIcon(editFirstNameIcon, getFirstNameLabel);
        setEditIcon(editLastNameIcon, getlastNameLabel);
        setEditIcon(editEmailIcon, getEmailLabel);
        setEditIcon(editAddressIcon, getAddressLabel);
        setEditIcon(editPhoneNumberIcon, getPNumberLabel);

    }

    private void deleteCartItemsWithNotVisibleProducts() {
        List<CartItem> cartItems = DBUtils.getCartItems(DBUtils.getCurrentUser());
        for(CartItem cartItem : cartItems) {
            Product product = cartItem.getProduct();
            if(!product.getVisible() || (product.getVisible() && product.getStockQuantity() <= 0)) {
                deleteProductFromCartItem(product);
            }
        }
    }

    private void updateCartListView() {
        ObservableList<CartItem> cartItemsList = FXCollections.observableArrayList(DBUtils.getCartItems(DBUtils.getCurrentUser()));
        cartListView.setItems(cartItemsList);
        if (cartItemsList.isEmpty()) {
            Image image = new Image(getClass().getResourceAsStream("/com/example/test2javafx/emptyCart.jpeg"));
            emptyCartImage.setImage(image);
            emptyCartImage.setVisible(true);
        } else {
            emptyCartImage.setVisible(false);
        }
    }

    private void checkProductVisibility() {
        List<CartItem> cartItems = DBUtils.getCartItems(DBUtils.getCurrentUser());
        for (CartItem cartItem : cartItems) {
            Product product = cartItem.getProduct();
            if (!product.getVisible()) {
                showProductNotAvailableMessage(product);
            }
            if(product.getVisible() && product.getStockQuantity() <= 0) {
                showProductNotInStockMessage(product);
            }
        }
    }

    private void showProductNotInStockMessage(Product product) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Product Out of stock");
        alert.setHeaderText(null);
        alert.setContentText("We are sorry, the product '" + product.getName() +
                "' is currently out of stock.");
        alert.showAndWait();
    }

    private void showProductNotAvailableMessage(Product product) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Product Not Available");
        alert.setHeaderText(null);
        alert.setContentText("We are sorry, the product '" + product.getName() +
                "' was deleted by an administrator and is no longer available.");
        alert.showAndWait();
    }

    private void deleteProductFromCartItem(Product product) {
        try(Connection conn = MySQLJDBCUtil.getConnection()){
            String sql = "DELETE FROM cartItem WHERE productId = ?";
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setInt(1, product.getProductId());
            int rowsAffected = pst.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Product deleted from cartItem successfully for all users!");
            } else {
                System.out.println("Product not found in cartItem.");
            }
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }

    private void setEditIcon(ImageView icon, Label label) {
        Image editImage = new Image(getClass().getResourceAsStream("edit_icon.png"));
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
                            UserValidator.showInvalidAlert("Invalid username!");
                            showEditDialog(label);
                        } else {
                            label.setText(newText);
                            DBUtils.updateUserInDatabase("username", newText);
                        }
                        break;
                    case "getFirstNameLabel":
                        if(!UserValidator.isValidFirstName(newText)) {
                            UserValidator.showInvalidAlert("Invalid first name!");
                            showEditDialog(label);
                        } else {
                            label.setText(newText);
                            DBUtils.updateUserInDatabase("firstName", newText);
                        }
                        break;
                    case "getlastNameLabel":
                        if(!UserValidator.isValidLastName(newText)) {
                            UserValidator.showInvalidAlert("Invalid last name!");
                            showEditDialog(label);
                        } else {
                            label.setText(newText);
                            DBUtils.updateUserInDatabase("lastName", newText);
                        }
                        break;
                    case "getEmailLabel":
                        if(!UserValidator.isValidEmail(newText)) {
                            UserValidator.showInvalidAlert("Invalid email!");
                            showEditDialog(label);
                        } else {
                            label.setText(newText);
                            DBUtils.updateUserInDatabase("email", newText);
                        }
                        break;
                    case "getAddressLabel":
                        if(!UserValidator.isValidAddress(newText)) {
                            UserValidator.showInvalidAlert("Invalid address!");
                            showEditDialog(label);
                        } else {
                            label.setText(newText);
                            DBUtils.updateUserInDatabase("address", newText);
                        }
                        break;
                    case "getPNumberLabel":
                        if(!UserValidator.isValidPhoneNumber(newText)){
                            UserValidator.showInvalidAlert("Invalid phone number");
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

    public static Stage getClientStage() {
        return clientStage;
    }

    private void showErrorAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
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

        passwordField.textProperty().addListener((observable, oldValue, newValue) -> {
            deleteButton.setDisable(newValue.trim().isEmpty());
        });

        dialog.getDialogPane().setContent(grid);

        Platform.runLater(() -> passwordField.requestFocus());

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
                alert.setContentText("Incorrect password. Please try again.");
                alert.showAndWait();
            }
        });
    }


    public static Stage getShopStage() {
        return shopStage;
    }

    public void switchToCartTab() {
        tabPane.getSelectionModel().select(tab_cart);
    }

    public void switchToAccountTab() {
        tabPane.getSelectionModel().select(tab_details);
    }

    public void switchToProductsTab() {
        tabPane.getSelectionModel().select(tab_products);
    }

}
