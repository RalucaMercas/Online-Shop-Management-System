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
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

public class OrderHistoryController implements Initializable {

    @FXML
    private ListView<OrderDetails> orderHistoryListView;

    @FXML
    private Button buttonBackToCart;

    @FXML
    private Button buttonAddReview;

    @FXML
    void handleButtonAddReview(ActionEvent event) {
        OrderDetails selectedOrder = orderHistoryListView.getSelectionModel().getSelectedItem();
        if (selectedOrder != null) {
            for (Product product : selectedOrder.getProducts()) {
                showReviewDialog(selectedOrder, product);
            }
        } else {
            showWarningDialog("Error", "Please select an order to add a review.");
        }
    }

    @FXML
    void handleButtonBackToCart(ActionEvent event) {
        Node source = (Node) event.getSource();
        Stage stageOrder = (Stage) source.getScene().getWindow();
        stageOrder.close();
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("shop.fxml"));
            Parent root = loader.load();
            ShopController shopController = loader.getController();
            shopController.switchToCartTab();
            Stage stage = new Stage();
            stage.setTitle("Shop");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        User currentUser = DBUtils.getCurrentUser();
        ObservableList<OrderDetails> orderHistory = FXCollections.observableArrayList();
        try {
            orderHistory.addAll(DBUtils.getOrderHistory(currentUser));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        setOrderHistoryItems(orderHistory);
    }

    public void setOrderHistoryItems(ObservableList<OrderDetails> orderHistory) {
        orderHistoryListView.setItems(orderHistory);
    }

    private void showReviewDialog(OrderDetails order, Product product) {
        Dialog<ProductReview> dialog = new Dialog<>();
        dialog.setTitle("Add Review");
        dialog.setHeaderText("Add a review for " + product.getName());

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField reviewField = new TextField();
        reviewField.setPromptText("Review for " + product.getName());

        ChoiceBox<Float> ratingChoiceBox = new ChoiceBox<>(FXCollections.observableArrayList(
                1.0f, 1.5f, 2.0f, 2.5f, 3.0f, 3.5f, 4.0f, 4.5f, 5.0f));
        ratingChoiceBox.setTooltip(new Tooltip("Select rating"));

        Label productNameLabel = new Label(product.getName() + " by " + product.getBrand());
        grid.add(productNameLabel, 0, 0);
        grid.add(new Label("Rating:"), 1, 0);
        grid.add(ratingChoiceBox, 2, 0);
        grid.add(new Label("Review:"), 3, 0);
        grid.add(reviewField, 4, 0);

        dialog.getDialogPane().setContent(grid);

        ButtonType addReviewButtonType = new ButtonType("Add review", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButtonType = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(addReviewButtonType, cancelButtonType);

        Platform.runLater(() -> reviewField.requestFocus());

        Button addReviewButton = (Button) dialog.getDialogPane().lookupButton(addReviewButtonType);

        addReviewButton.addEventFilter(ActionEvent.ACTION, event -> {
            if (!validateReviewInput(reviewField.getText(), ratingChoiceBox.getValue())) {
                event.consume(); // Consume the event to prevent closing the dialog
                showWarningDialog("Error", "A valid review is required. Please provide valid values.");
            }
        });

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == addReviewButtonType) {
                String reviewText = reviewField.getText();
                Float rating = ratingChoiceBox.getValue();
                if (!validateReviewInput(reviewText, rating)) {
                    showWarningDialog("Error", "A valid review is required. Please provide valid values.");
                    return null;
                }

                ProductReview review = new ProductReview(
                        DBUtils.getCurrentUser().getUserId(),
                        product.getProductId(),
                        rating,
                        reviewText,
                        new java.sql.Date(System.currentTimeMillis())
                );
                DBUtils.saveProductReview(order.getOrderId(), review);
                showInformationDialog("Review Added", "Review added successfully!");
                return review;
            }
            return null;
        });

        Optional<ProductReview> result = dialog.showAndWait();
        result.ifPresent(review -> {
//            showInformationDialog("Review Added", "Review added successfully!");
        });
    }
    private boolean validateReviewInput(String reviewText, Float rating) {
        return reviewText != null && !reviewText.trim().isEmpty() && rating != null;
    }

    private void showWarningDialog(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void showInformationDialog(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
