package com.example.test2javafx;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.Spinner;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.util.StringConverter;

import javafx.beans.value.ObservableValue;
import javafx.scene.control.SpinnerValueFactory;


public class SpinnerListCell extends ListCell<CartItem> {
    private Spinner<Integer> spinner;
    private Label subtotalLabel;

    public SpinnerListCell() {
        initSpinner();
    }

    private void initSpinner() {
        spinner = new Spinner<>(0, 150, 1);
        spinner.setEditable(true);
        spinner.setPrefWidth(70);

        spinner.setOnMouseClicked(event -> handleSpinnerChange());
    }
    private boolean isUpdatingSpinner = false;
    private void handleSpinnerChange() {
        if (!isUpdatingSpinner) {
            CartItem cartItem = getItem();
            if (cartItem != null) {
                int stockQuantity = getProductStockQuantity(cartItem.getProductId());
                String name = getProductName(cartItem.getProductId());
                int newValue = spinner.getValue();
                if (newValue == 0) {
                    getListView().getItems().remove(cartItem);
                    getListView().refresh();
                    DBUtils.deleteCartItem(cartItem);
                } else if (newValue <= stockQuantity) {
                    updateCartItem(cartItem, newValue);
                } else {
                    showErrorDialog("Error", "Not enough stock available for '" +
                            name + "'. Maximum available quantity: " + stockQuantity);
                    isUpdatingSpinner = true;
                    spinner.getValueFactory().setValue(stockQuantity);
                    isUpdatingSpinner = false;
                    updateCartItem(cartItem, stockQuantity);
                }
            }
        }
    }

    private void updateCartItem(CartItem cartItem, int newQuantity) {
        cartItem.setQuantity(newQuantity);
        updateSubtotal(cartItem);
        DBUtils.updateCartItemQuantity(cartItem, newQuantity);
    }
    private int getProductStockQuantity(int productId) {
        Product product = DBUtils.getProductById(productId);
        if (product != null) {
            return product.getStockQuantity();
        }
        return 0;
    }

    private String getProductName(int productId) {
        Product product = DBUtils.getProductById(productId);
        if (product != null) {
            return product.getName();
        }
        return null;
    }

    private void showErrorDialog(String title, String content) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(content);
            alert.showAndWait();
        });
    }
    private void updateSubtotal(CartItem cartItem) {
        float productPrice = getProductPrice(cartItem.getProductId());
        cartItem.setSubtotal(cartItem.getQuantity() * productPrice);
        if (subtotalLabel != null) {
            subtotalLabel.setText(String.format("Subtotal: %.2f$", cartItem.getSubtotal()));
        }
    }

    @Override
    protected void updateItem(CartItem item, boolean empty) {
        super.updateItem(item, empty);
        if (empty || item == null) {
            setText(null);
            setGraphic(null);
        } else {
            spinner.getValueFactory().setValue(item.getQuantity());
            float productPrice = getProductPrice(item.getProductId());
            item.setSubtotal(item.getQuantity() * productPrice);
            GridPane gridPane = new GridPane();
            gridPane.setAlignment(Pos.CENTER_LEFT);
            gridPane.setPadding(new Insets(0, 10, 0, 0));
            Label productLabel = new Label(item.toString());
            gridPane.add(productLabel, 0, 0);
            double spaceWidth = calculateSpaceWidth(item.toString());
            Region spacer1 = new Region();
            GridPane.setHgrow(spacer1, Priority.ALWAYS);
            spacer1.setMinWidth(spaceWidth);
            gridPane.add(spacer1, 1, 0);
            Label quantityLabel = new Label("Quantity:");
            gridPane.add(quantityLabel, 2, 0);
            gridPane.add(spinner, 3, 0);
            ColumnConstraints col4 = new ColumnConstraints();
            col4.setPercentWidth(20);
            gridPane.getColumnConstraints().add(col4);
            subtotalLabel = new Label(String.format("Subtotal: %.2f$", item.getSubtotal()));
            gridPane.add(subtotalLabel, 4, 0);
            ColumnConstraints col1 = new ColumnConstraints();
            col1.setPercentWidth(0);
            ColumnConstraints col2 = new ColumnConstraints();
            col2.setPercentWidth(3);
            ColumnConstraints col3 = new ColumnConstraints();
            col3.setPercentWidth(5);
            ColumnConstraints col5 = new ColumnConstraints();
            col5.setPercentWidth(5);
            gridPane.getColumnConstraints().addAll(col1, col2, col3, col5);
            setGraphic(gridPane);
        }
    }


    private float getProductPrice(int productId) {
        Product product = DBUtils.getProductById(productId);
        if (product != null) {
            return product.getPrice();
        }
        return 0.0f;
    }

    private double calculateSpaceWidth(String text) {
        return Math.min(text.length() * 50, 50);
    }
}