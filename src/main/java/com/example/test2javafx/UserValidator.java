package com.example.test2javafx;

import javafx.scene.control.Alert;

public class UserValidator {
    public static void showInvalidAlert(String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public static boolean isValidUser(User user) {
        String firstName = user.getFirstName();
        String lastName = user.getLastName();
        String email = user.getEmail();
        String password = user.getPassword();
        String address = user.getAddress();
        String phoneNumber = user.getPhoneNumber();
        UserRole userRole = user.getRole();
        String username = user.getUsername();

        if(!isValidFirstName(firstName)) {
            showInvalidAlert("Oops, your first name is invalid. Make sure your first name only contains letters and optional hyphens!");
        }
        if(!isValidLastName(lastName)) {
            showInvalidAlert("Oops, your last name is invalid. Make sure your last name only contains letters and optional hyphens!");
        }
        if(!isValidEmail(email)) {
            showInvalidAlert("Oops, your email format is invalid. Please try again!");
        }
        if(!isValidPassword(password)) {
            showInvalidAlert("Oops, your password is invalid. Please make sure you include uppercase letters, lowercase letters, numbers and symbols!");
        }
        if(!isValidAddress(address)){
            showInvalidAlert("Oops, your address cannot be empty. Please try again!");
        }
        if(!isValidPhoneNumber(phoneNumber)) {
            showInvalidAlert("Oops, your phone number format is invalid. Please try again!");
        }
        if(!isValidUserType(userRole)) {
            showInvalidAlert("Oops, your user role is invalid. Please try again");
        }
        if(!isValidUsername(username)) {
            showInvalidAlert("Oops, your username cannot start with a digit or a special character. Please try again!");
        }
        return isValidFirstName(firstName) && isValidLastName(lastName) && isValidEmail(email)
                && isValidPassword(password) && isValidAddress(address) && isValidPhoneNumber(phoneNumber)
                && isValidUserType(userRole) && isValidUsername(username);
    }


    public static boolean isValidFirstName(String firstName) {
        return firstName != null && firstName.matches("^[a-zA-Z]+(-[a-zA-Z]+)?$");
    }
    public static boolean isValidLastName(String lastName) {
        return lastName != null && lastName.matches("^[a-zA-Z]+(-[a-zA-Z]+)?$");
    }

    public static boolean isValidEmail(String email) {
        String emailRegex = "^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,}$";
        return email.matches(emailRegex);
    }

    public static boolean isValidPassword(String password) {
        return password != null && password.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]+$");
    }

    public static boolean isValidAddress(String address) {
        return address != null && !address.trim().isEmpty();
    }

    public static boolean isValidUserType(UserRole userRole) {
        return (userRole == UserRole.CLIENT || userRole == UserRole.ADMIN);
    }

    public static boolean isValidPhoneNumber(String phoneNumber) {
        String phoneRegex = "^0[72][0-9]{8}$";
        return phoneNumber.matches(phoneRegex);
    }

    public static boolean isValidUsername(String username) {
        return username != null && !username.matches("^[\\d\\W].*");
    }


}
