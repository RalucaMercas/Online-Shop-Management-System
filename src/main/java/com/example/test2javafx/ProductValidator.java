package com.example.test2javafx;

import javafx.scene.control.Alert;

public class ProductValidator {

    public static void showInvalidAlert(String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public static boolean isValidProduct(Product product) {
        String name = product.getName();
        String brand = product.getBrand();
        String description = product.getDescription();
        float price = product.getPrice();
        int stockQuantity = product.getStockQuantity();
        String image = product.getStringImageUrl();

        if(!isValidName(name)) {
            showInvalidAlert("Oops, make sure the name of the product contains at least one letter. Please try again!");
        }
        if(!isValidBrand(brand)) {
            showInvalidAlert("Oops, make sure the brand name contains at least one letter. Please try again!");
        }
        if(!isValidDescription(description)) {
            showInvalidAlert("Oops, make sure the description contains at least one letter. Please try again!");
        }
        if(!isValidPrice(price)) {
            showInvalidAlert("Oops, the price should be greater than zero. Please try again!");
        }
        if(!isValidStockQuantity(stockQuantity)){
            showInvalidAlert("Oops, the stock quantity should not be less than zero. Please try again!");
        }
        if(!isValidImage(image)) {
            showInvalidAlert("Oops, make sure the image Url has one of these extensions: \"png\", \"jpg\", \"jpeg\"");
        }

        return isValidName(name) && isValidBrand(brand) && isValidDescription(description)
                && isValidPrice(price) && isValidStockQuantity(stockQuantity) && isValidImage(image);
    }


    private static boolean isValidName(String name) {
        return name != null && !name.trim().isEmpty() && name.matches(".*[a-zA-Z].*");    }

    private static boolean isValidBrand(String brand) {
        return brand != null && !brand.trim().isEmpty() && brand.matches(".*[a-zA-Z].*");
    }

    private static boolean isValidDescription(String description) {
        return description != null && !description.trim().isEmpty() && description.matches(".*[a-zA-Z].*");
    }

    private static boolean isValidPrice(float price) {
        return price > 0;
    }

    private static boolean isValidStockQuantity(int stockQuantity) {
        return stockQuantity >= 0;
    }

    private static boolean isValidImage(String image) {
        String[] validExtensions = {"png", "jpg", "jpeg"};
        if (image != null && !image.trim().isEmpty()) {
            String[] parts = image.split("\\.");
            if (parts.length > 1) {
                String extension = parts[parts.length - 1].toLowerCase();
                for (String validExtension : validExtensions) {
                    if (extension.equals(validExtension)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
