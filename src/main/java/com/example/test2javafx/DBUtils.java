package com.example.test2javafx;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.*;
import java.sql.Date;
import java.util.*;

public class DBUtils {
    private static User currentUser;

    public static void setCurrentUser(User user) {
        currentUser = user;
    }

    public static User getCurrentUser() {
        return currentUser;
    }

    public static void logOutUser() {
        currentUser = null;
    }

    public static void changeScene(ActionEvent event, String fxmlFile, String title, String username) {
        Parent root = null;
        if (username != null) {
            try {
                FXMLLoader loader = new FXMLLoader(DBUtils.class.getResource(fxmlFile));
                root = loader.load();
                if (fxmlFile.equals("admin.fxml")) {
                    AdminController adminController = loader.getController();

                } else if (fxmlFile.equals("shop.fxml")) {
                    ShopController shopController = loader.getController();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            try {
                root = FXMLLoader.load(Objects.requireNonNull(DBUtils.class.getResource(fxmlFile)));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setTitle(title);
        if (fxmlFile.equals("log-in.fxml") || fxmlFile.equals("sign-up.fxml") || fxmlFile.equals("account-created.fxml")) {
            assert root != null;
            stage.setScene(new Scene(root, 600, 400));
        } else {
            if(fxmlFile.equals("changePassword.fxml")) {
                assert root != null;
                stage.setScene(new Scene(root, 300, 230));
                stage.setResizable(false);
            } else {
                assert root != null;
                stage.setScene(new Scene(root, 770, 430));
                stage.setResizable(false);
            }
        }
        stage.show();
    }


    public static void signUpUser(ActionEvent event, String firstName, String lastName, String username, String email, String password, String address, String phoneNumber, String userType) {
        PreparedStatement pstCheckUserExists = null;
        PreparedStatement pstInsert = null;
        ResultSet result = null;

        User newUser = new User(0, firstName, lastName, email, password, address, phoneNumber, username, User.setRoleByUserType(userType));
        if(!UserValidator.isValidUser(newUser)){
            return;
        }

        try (Connection conn = MySQLJDBCUtil.getConnection()) {
            String sql = "select * from user u where BINARY u.username = ?";
            pstCheckUserExists = conn.prepareStatement(sql);
            pstCheckUserExists.setString(1, username);
            result = pstCheckUserExists.executeQuery();
            if (result.isBeforeFirst()) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setContentText("You cannot use this username");
                alert.show();
            } else if (firstName != null && !firstName.trim().isEmpty() && lastName != null && !lastName.trim().isEmpty() && username != null && !username.trim().isEmpty() &&
                    email != null && !email.trim().isEmpty() && password != null && !password.trim().isEmpty() && address != null && !address.trim().isEmpty() &&
                    phoneNumber != null && !phoneNumber.trim().isEmpty() && userType != null && !userType.trim().isEmpty()) {
                String sqlInsert = "insert into user (firstName, lastName, email, password, address, phoneNumber, userType, username)" + "values (?, ?, ?, ?, ?, ?, ?, ?)";
                pstInsert = conn.prepareStatement(sqlInsert);
                pstInsert.setString(1, firstName);
                pstInsert.setString(2, lastName);
                pstInsert.setString(3, email);
                pstInsert.setString(4, password);
                pstInsert.setString(5, address);
                pstInsert.setString(6, phoneNumber);
                pstInsert.setString(7, userType);
                pstInsert.setString(8, username);
                pstInsert.executeUpdate();
                changeScene(event, "account-created.fxml", "Account created", null);
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setContentText("You must fill in all fields!");
                alert.show();
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } finally {
            if (result != null) {
                try {
                    result.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (pstCheckUserExists != null) {
                try {
                    pstCheckUserExists.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (pstInsert != null) {
                try {
                    pstInsert.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static User logInUser(ActionEvent event, String username, String password) {
        PreparedStatement pst = null;
        ResultSet resultSet = null;
        User user = null;
        try (Connection conn = MySQLJDBCUtil.getConnection()) {
            String sql = "Select * from user u where BINARY u.username = ?";
            pst = conn.prepareStatement(sql);
            pst.setString(1, username);
            resultSet = pst.executeQuery();
            if (!resultSet.isBeforeFirst()) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setContentText("provided credentials are incorrect!");
                alert.show();
            } else {
                while (resultSet.next()) {
                    String retrievedPassword = resultSet.getString("password");
                    if (retrievedPassword.equals(password)) {
                        user = new User(resultSet);
                        setCurrentUser(user);
                    } else {
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setContentText("The provided credentials are incorrect!");
                        alert.show();
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (resultSet != null) {
                try {
                    resultSet.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (pst != null) {
                try {
                    pst.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return user;
    }

        public static void clearCart(User currentUser) {
            try (Connection connection = MySQLJDBCUtil.getConnection()) {
                String sql = "DELETE FROM cartitem WHERE userId = ?";
                try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                    preparedStatement.setInt(1, currentUser.getUserId());
                    preparedStatement.executeUpdate();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }


    public static List<Product> getVisibleProductsInStockFromBD(int categoryId) {
        List<Product> productList = new ArrayList<>();
        try (Connection conn = MySQLJDBCUtil.getConnection()) {
            String sql = "select * from product p where p.categoryId = ? and p.isVisible = true and p.stockQuantity > 0";
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setInt(1, categoryId);
            ResultSet result = pst.executeQuery();
            while (result.next()) {
                int productId = result.getInt("productId");
                String name = result.getString("name");
                String brand = result.getString("brand");
                String description = result.getString("description");
                float price = result.getFloat("price");
                int stockQuantity = result.getInt("stockQuantity");
                int productCategoryId = result.getInt("categoryId");
                float avgRating = result.getFloat("avgRating");
                String imageUrl = result.getString("imageUrl");
                Boolean isVisible = result.getBoolean("isVisible");
                Product product = new Product(productId, name, brand, description, price, stockQuantity, productCategoryId, avgRating, imageUrl, isVisible);
                productList.add(product);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return productList;
    }


    public static List<Product> getAllVisibleProductsFromBD(int categoryId) {
        List<Product> productList = new ArrayList<>();
        try (Connection conn = MySQLJDBCUtil.getConnection()) {
            String sql = "select * from product p where p.categoryId = ? and p.isVisible = true";
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setInt(1, categoryId);
            ResultSet result = pst.executeQuery();
            while (result.next()) {
                int productId = result.getInt("productId");
                String name = result.getString("name");
                String brand = result.getString("brand");
                String description = result.getString("description");
                float price = result.getFloat("price");
                int stockQuantity = result.getInt("stockQuantity");
                int productCategoryId = result.getInt("categoryId");
                float avgRating = result.getFloat("avgRating");
                String imageUrl = result.getString("imageUrl");
                Boolean isVisible = result.getBoolean("isVisible");
                Product product = new Product(productId, name, brand, description, price, stockQuantity, productCategoryId, avgRating, imageUrl, isVisible);
                productList.add(product);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return productList;
    }


    public static List<User> getUsers() {
        List<User> usersList = new ArrayList<>();
        try (Connection conn = MySQLJDBCUtil.getConnection()) {
            String sql = "select * from user u";
            Statement stm = conn.createStatement();
            ResultSet result = stm.executeQuery(sql);
            while (result.next()) {
                int userId = result.getInt("userId");
                String username = result.getString("username");
                String userType = result.getString("userType");
                String email = result.getString("email");
                User user = new User(userId, username, userType, email);
                usersList.add(user);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return usersList;
    }

    public static void updateCartItemQuantity(CartItem cartItem, int newQuantity) {
        try (Connection conn = MySQLJDBCUtil.getConnection()) {
            String updateQuantitySql = "UPDATE cartitem SET quantity = ?, subtotal = ? WHERE cartItemId = ?";
            try (PreparedStatement updateQuantityStatement = conn.prepareStatement(updateQuantitySql)) {
                Product product = getProductById(cartItem.getProductId());
                float newSubtotal = calculateSubtotal(product.getPrice(), newQuantity);
                updateQuantityStatement.setInt(1, newQuantity);
                updateQuantityStatement.setFloat(2, newSubtotal);
                updateQuantityStatement.setInt(3, cartItem.getCartItemId());
                updateQuantityStatement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static float calculateSubtotal(float price, int quantity) {
        return price * quantity;
    }
    public static void addToCart(User user, Product product, int quantity) {
        try (Connection conn = MySQLJDBCUtil.getConnection()) {
        String checkItemSql = "SELECT * FROM cartitem WHERE productId = ? AND userId = ?";
        try (PreparedStatement checkItemStatement = conn.prepareStatement(checkItemSql)) {
            checkItemStatement.setInt(1, product.getProductId());
            checkItemStatement.setInt(2, user.getUserId());
            ResultSet resultSet = checkItemStatement.executeQuery();
            if (resultSet.next()) {
                int currentQuantity = resultSet.getInt("quantity");
                int newQuantity = currentQuantity + quantity;
                String updateQuantitySql = "UPDATE cartitem SET quantity = ? WHERE productId = ? AND userId = ?";
                try (PreparedStatement updateQuantityStatement = conn.prepareStatement(updateQuantitySql)) {
                    updateQuantityStatement.setInt(1, newQuantity);
                    updateQuantityStatement.setInt(2, product.getProductId());
                    updateQuantityStatement.setInt(3, user.getUserId());
                    updateQuantityStatement.executeUpdate();
                }
            } else {
                String insertSql = "INSERT INTO cartitem (productId, quantity, userId, subtotal) VALUES (?, ?, ?, ?)";
                try (PreparedStatement pst = conn.prepareStatement(insertSql)) {
                    pst.setInt(1, product.getProductId());
                    pst.setInt(2, quantity);
                    pst.setInt(3, user.getUserId());
                    pst.setFloat(4, calculateSubtotal(product.getPrice(), quantity));
                    pst.executeUpdate();
                }
            }
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
}

    public static List<CartItem> getCartItems(User user) {
        List<CartItem> cartItemsList = new ArrayList<>();
        try (Connection conn = MySQLJDBCUtil.getConnection()) {
            int userId = user.getUserId();
            String sql = "select * from cartItem c where c.userId = ?";
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setInt(1, userId);
            ResultSet result = pst.executeQuery();
            while (result.next()) {
                int cartItemId = result.getInt("cartItemId");
                int productId = result.getInt("productId");
                int quantity = result.getInt("quantity");
                float subtotal = result.getFloat("subtotal");
                CartItem cartItem = new CartItem(cartItemId, productId, quantity, userId, subtotal);
                cartItemsList.add(cartItem);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return cartItemsList;
    }

    public static Product getProductById(int productId) {
        Product product = null;
        try (Connection conn = MySQLJDBCUtil.getConnection()) {
            String sql = "select * from product p where p.productId = ?";
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setInt(1, productId);
            ResultSet result = pst.executeQuery();
            if(result.next()) {
                String name = result.getString("name");
                String brand = result.getString("brand");
                String description = result.getString("description");
                float price = result.getFloat("price");
                int stockQuantity = result.getInt("stockQuantity");
                int productCategoryId = result.getInt("categoryId");
                float avgRating = result.getFloat("avgRating");
                String imageUrl = result.getString("imageUrl");
                Boolean isVisible = result.getBoolean("isVisible");
                product = new Product(productId, name, brand, description, price, stockQuantity, productCategoryId, avgRating, imageUrl, isVisible);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return product;
    }

    public static boolean updatePasswordInDatabase(String oldPassword, String newPassword) {
        try (Connection conn = MySQLJDBCUtil.getConnection()) {
            // Check if the old password matches the current user's password
            String checkPasswordSql = "SELECT * FROM user WHERE userId = ? AND password = ?";
            try (PreparedStatement checkPasswordStatement = conn.prepareStatement(checkPasswordSql)) {
                checkPasswordStatement.setInt(1, currentUser.getUserId());
                checkPasswordStatement.setString(2, oldPassword);
                ResultSet resultSet = checkPasswordStatement.executeQuery();
                if (resultSet.next()) {
                    String updatePasswordSql = "UPDATE user SET password = ? WHERE userId = ?";
                    try (PreparedStatement updatePasswordStatement = conn.prepareStatement(updatePasswordSql)) {
                        updatePasswordStatement.setString(1, newPassword);
                        updatePasswordStatement.setInt(2, currentUser.getUserId());
                        updatePasswordStatement.executeUpdate();
                        currentUser.setPassword(newPassword); // Update the current user's password locally
                        return true;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static void updateUserInDatabase(String field, String newValue) {
        try (Connection conn = MySQLJDBCUtil.getConnection()) {
            String sql = "UPDATE user SET " + field + " = ? WHERE userId = ?";
            try (PreparedStatement pst = conn.prepareStatement(sql)) {
                pst.setString(1, newValue);
                pst.setInt(2, DBUtils.getCurrentUser().getUserId());
                int rowsAffected = pst.executeUpdate();
                if (rowsAffected > 0) {
                    System.out.println("Database updated successfully!");
                } else {
                    System.err.println("Failed to update the database.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static String getExistingValue(String field) {
        String existingValue = "";
        return existingValue;
    }


    public static void insertOrderItems(int orderId, User user, List<CartItem> cartItems) {
        try (Connection conn = MySQLJDBCUtil.getConnection()) {
            String insertOrderItemSQL = "INSERT INTO orderItem (orderId, userId, productId, quantity, subtotal) VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement pst = conn.prepareStatement(insertOrderItemSQL)) {
                for (CartItem cartItem : cartItems) {
                    pst.setInt(1, orderId);
                    pst.setInt(2, user.getUserId());
                    pst.setInt(3, cartItem.getProductId());
                    pst.setInt(4, cartItem.getQuantity());
                    pst.setDouble(5, cartItem.getSubtotal());
                    pst.executeUpdate();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    private static int getPaymentMethodId(Connection conn, String paymentMethod) throws SQLException {
        String getPaymentMethodIdSQL = "SELECT paymentMethodId FROM paymentmethod WHERE payment = ?";
        try (PreparedStatement pst = conn.prepareStatement(getPaymentMethodIdSQL)) {
            pst.setString(1, paymentMethod);
            ResultSet resultSet = pst.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt("paymentMethodId");
            } else {
                throw new SQLException("Payment method not found: " + paymentMethod);
            }
        }
    }
    public static int insertOrder(User user, float totalAmount, List<CartItem> cartItems, String paymentMethod) {
        try (Connection conn = MySQLJDBCUtil.getConnection()) {
            int orderId = getNewOrderId(conn);
            java.sql.Date date = new java.sql.Date(System.currentTimeMillis());
            String insertOrderSQL = "INSERT INTO `order` (orderId, date, totalAmount, userId, paymentMethodId) VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement pst = conn.prepareStatement(insertOrderSQL, Statement.RETURN_GENERATED_KEYS)) {
                pst.setInt(1, orderId);
                pst.setDate(2, date);
                pst.setFloat(3, totalAmount);
                pst.setInt(4, user.getUserId());
                pst.setInt(5, getPaymentMethodId(conn, paymentMethod));
                pst.executeUpdate();
                ResultSet generatedKeys = pst.getGeneratedKeys();
                if (generatedKeys.next()) {
                    orderId = generatedKeys.getInt(1);
                } else {
                    throw new SQLException("Failed to get the generated order ID.");
                }
            }
            insertOrderItems(orderId, user, cartItems);
            return orderId;
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }
    public static int getNewOrderId(Connection conn) throws SQLException {
        int maxOrderId = getMaxOrderId(conn);
        return maxOrderId + 1;
    }

    private static int getMaxOrderId(Connection conn) throws SQLException {
        String getMaxOrderIdSQL = "SELECT MAX(orderId) FROM `order`";
        try (PreparedStatement maxOrderIdStatement = conn.prepareStatement(getMaxOrderIdSQL)) {
            ResultSet resultSet = maxOrderIdStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt(1);
            } else {
                return 1;
            }
        }
    }

    public static ObservableList<OrderDetails> getOrderHistory(User user) throws SQLException {
        ObservableList<OrderDetails> orderHistory = FXCollections.observableArrayList();
        try (Connection connection = MySQLJDBCUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(
                     "SELECT o.orderId, o.date, o.totalAmount, o.paymentMethodId, p.productId, p.name, p.brand, p.price, oi.quantity, oi.subtotal " +
                             "FROM `order` o " +
                             "JOIN orderitem oi ON o.orderId = oi.orderId " +
                             "JOIN product p ON oi.productId = p.productId " +
                             "WHERE o.userId = ?")) {

            preparedStatement.setInt(1, user.getUserId());
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                int currentOrderId = -1;
                List<Product> products = new ArrayList<>();
                Map<Integer, Integer> productQuantities = new HashMap<>();
                while (resultSet.next()) {
                    int orderId = resultSet.getInt("orderId");
                    Date orderDate = resultSet.getDate("date");
                    float totalAmount = resultSet.getFloat("totalAmount");
                    int paymentMethod = resultSet.getInt("paymentMethodId");
                    if (currentOrderId != orderId) {
                        OrderDetails orderDetails = new OrderDetails(orderId, orderDate, totalAmount, new ArrayList<>(), new HashMap<>(), paymentMethod);
                        orderHistory.add(orderDetails);
                        products = orderDetails.getProducts();
                        productQuantities = orderDetails.getProductQuantities();
                        currentOrderId = orderId;
                    }
                    Product product = new Product();
                    product.setProductId(resultSet.getInt("productId"));
                    product.setName(resultSet.getString("name"));
                    product.setBrand(resultSet.getString("brand"));
                    product.setPrice(resultSet.getFloat("price"));
                    int quantity = resultSet.getInt("quantity");
                    productQuantities.put(product.getProductId(), quantity);
                    products.add(product);
                }
            }
        }

        return orderHistory;
    }

    public static void updateProductStockQuantity(int productId, int newStockQuantity) {
        try (Connection connection = MySQLJDBCUtil.getConnection()) {
            String updateQuery = "UPDATE product SET stockQuantity = ? WHERE productId = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(updateQuery)) {
                preparedStatement.setInt(1, newStockQuantity);
                preparedStatement.setInt(2, productId);
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static int getProductStockQuantity(int productId) {
        int stockQuantity = 0;
        String query = "SELECT stockQuantity FROM product WHERE productId = ?";
        try (Connection connection =  MySQLJDBCUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, productId);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    stockQuantity = resultSet.getInt("stockQuantity");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return stockQuantity;
    }

    public static List<Product> getProductsByCategory(int categoryId) {
        List<Product> productList = new ArrayList<>();
        try (Connection conn = MySQLJDBCUtil.getConnection()) {
            String sql = "SELECT * FROM product WHERE categoryId = ?";
            try (PreparedStatement pst = conn.prepareStatement(sql)) {
                pst.setInt(1, categoryId);
                try (ResultSet rs = pst.executeQuery()) {
                    while (rs.next()) {
                        int productId = rs.getInt("productId");
                        String name = rs.getString("name");
                        String brand = rs.getString("brand");
                        String description = rs.getString("description");
                        float price = rs.getFloat("price");
                        int stockQuantity = rs.getInt("stockQuantity");
                        float avgRating = rs.getFloat("avgRating");
                        String imageUrl = rs.getString("imageUrl");
                        Product product = new Product(productId, name, brand, description, price, stockQuantity, categoryId, avgRating, imageUrl, true);
                        productList.add(product);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return productList;
    }

    public static void deleteCartItem(CartItem cartItem) {
        try (Connection conn = MySQLJDBCUtil.getConnection()) {
            String deleteCartItemSQL = "DELETE FROM cartItem WHERE userId = ? AND productId = ?";
            try (PreparedStatement pst = conn.prepareStatement(deleteCartItemSQL)) {
                pst.setInt(1, cartItem.getUserId());
                pst.setInt(2, cartItem.getProductId());
                pst.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void saveProductReview(int orderId, ProductReview review) {
        String insertQuery = "INSERT INTO review (userId, productId, rating, comment, date) VALUES (?, ?, ?, ?, ?)";
        try (Connection connection = MySQLJDBCUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(insertQuery)) {
            preparedStatement.setInt(1, review.getUserId());
            preparedStatement.setInt(2, review.getProductId());
            preparedStatement.setFloat(3, review.getRating());
            preparedStatement.setString(4, review.getComment());
            preparedStatement.setDate(5, review.getDate());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void removeSelectedUsers(ObservableList<User> selectedUsers) {
        for (User user : selectedUsers) {
            deleteAllCartItemsOfUser(user);
            deleteAllOrderItemsOfUser(user);
            deleteAllOrdersOfUser(user);
            deleteUser(user);
        }
    }

    public static void deleteAllCartItemsOfUser(User user) {
        try (Connection conn = MySQLJDBCUtil.getConnection()) {
            String sql = "DELETE FROM cartItem c WHERE c.userId = ?";
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setInt(1, user.getUserId());
            pst.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void deleteAllOrderItemsOfUser(User user) {
        try (Connection conn = MySQLJDBCUtil.getConnection()) {
            String sql = "DELETE FROM orderitem WHERE userId = ?";
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setInt(1, user.getUserId());
            pst.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void deleteAllOrdersOfUser(User user) {
        try (Connection conn = MySQLJDBCUtil.getConnection()) {
            conn.createStatement().executeUpdate("SET foreign_key_checks = 0");
            String sql = "DELETE FROM `order` WHERE userId = ?";
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setInt(1, user.getUserId());
            pst.executeUpdate();
            conn.createStatement().executeUpdate("SET foreign_key_checks = 1");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void deleteUser(User user) {
        try (Connection conn = MySQLJDBCUtil.getConnection()) {
            conn.createStatement().executeUpdate("SET foreign_key_checks = 0");
            String sql = "DELETE FROM user WHERE userId = ?";
            try (PreparedStatement pst = conn.prepareStatement(sql)) {
                pst.setInt(1, user.getUserId());
                pst.executeUpdate();
            }
            conn.createStatement().executeUpdate("SET foreign_key_checks = 1");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public static String getUserPasswordFromDB(User user) {
        try(Connection conn = MySQLJDBCUtil.getConnection()) {
            String sql = "SELECT password FROM user WHERE userId = ?";
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setInt(1, user.getUserId());
            ResultSet resultSet = pst.executeQuery();
            if(resultSet.next()) {
                return resultSet.getString("password");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean validatePassword(String inputPassword, User user) {
        String password = getUserPasswordFromDB(user);
        assert password != null;
        return password.equals(inputPassword);
    }
}



