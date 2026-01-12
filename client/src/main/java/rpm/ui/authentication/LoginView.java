package rpm.ui.authentication;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import rpm.ui.app.AppContext;
import rpm.ui.app.NurseUser;
import rpm.ui.app.Router;

import java.util.HashMap;
import java.util.Map;

public final class LoginView extends VBox {

    private TextField usernameField;
    private PasswordField passwordField;
    private Button loginButton;
    private Label errorLabel;

    private final Map<String, String> userDatabase = new HashMap<>();
    private static final double CARD_WIDTH = 300;
    private static final double CARD_HEIGHT = 400;

    public LoginView(AppContext ctx, Router router) {

        userDatabase.put("juan", "abc");
        userDatabase.put("Holloway", "Nettles");
        userDatabase.put("nurse1", "securePassword1");
        userDatabase.put("nurse2", "securePassword2");
        userDatabase.put("nurse3", "securePassword3");
        userDatabase.put("doctor", "superSecurePassword");
        userDatabase.put("admin", "superDuperSecurePassword");

        this.setMaxWidth(CARD_WIDTH);
        this.setMaxHeight(CARD_HEIGHT);
        this.setAlignment(Pos.CENTER_LEFT);
        this.setSpacing(15);
        this.setStyle(
                "-fx-background-color: white;" +
                        "-fx-background-radius: 15;" +
                        "-fx-padding: 30;"
        );

        DropShadow dropShadow = new DropShadow();
        dropShadow.setRadius(10.0);
        dropShadow.setOffsetX(0.0);
        dropShadow.setOffsetY(5.0);
        dropShadow.setColor(Color.color(0, 0, 0, 0.3));
        this.setEffect(dropShadow);

        Label titleLabel = new Label("Login");
        titleLabel.setStyle("-fx-font-size: 34px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");
        titleLabel.setMaxWidth(Double.MAX_VALUE);
        titleLabel.setAlignment(Pos.CENTER);

        Label usernameLabel = new Label("Username");
        usernameLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #34495e;");
        usernameField = new TextField();
        usernameField.setPromptText("Enter username");
        usernameField.setStyle("-fx-background-radius: 5; -fx-padding: 8; -fx-font-size: 14px;");

        Label passwordLabel = new Label("Password");
        passwordLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #34495e;");
        passwordField = new PasswordField();
        passwordField.setPromptText("Enter password");
        passwordField.setStyle("-fx-background-radius: 5; -fx-padding: 8; -fx-font-size: 14px;");

        errorLabel = new Label("Invalid username or password");
        errorLabel.setTextFill(Color.RED);
        errorLabel.setStyle("-fx-font-size: 12px;");
        errorLabel.setVisible(false);

        loginButton = new Button("Login");
        loginButton.setMaxWidth(150);
        loginButton.setDisable(true); // 🔒 Disabled until fields are filled
        loginButton.setStyle(
                "-fx-background-color: #3498db;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-size: 16px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-background-radius: 5;" +
                        "-fx-cursor: hand;"
        );

        loginButton.setOnMouseEntered(e -> loginButton.setStyle(
                "-fx-background-color: #2980b9; -fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold; -fx-background-radius: 5;"
        ));
        loginButton.setOnMouseExited(e -> loginButton.setStyle(
                "-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold; -fx-background-radius: 5;"
        ));

        // Enable/disable button based on field content
        usernameField.textProperty().addListener((obs, o, n) -> updateLoginButtonState());
        passwordField.textProperty().addListener((obs, o, n) -> updateLoginButtonState());

        //  Mouse click login
        loginButton.setOnAction(e -> handleLogin(ctx, router));

        // ⏎ Enter key login (only if enabled)
        usernameField.setOnAction(e -> tryLogin());
        passwordField.setOnAction(e -> tryLogin());

        HBox buttonContainer = new HBox(10);
        buttonContainer.setAlignment(Pos.CENTER_LEFT);
        buttonContainer.getChildren().addAll(loginButton, errorLabel);

        getChildren().addAll(
                titleLabel,
                usernameLabel,
                usernameField,
                passwordLabel,
                passwordField,
                buttonContainer
        );
    }

    private void updateLoginButtonState() {
        boolean filled =
                !usernameField.getText().trim().isEmpty() &&
                        !passwordField.getText().trim().isEmpty();

        loginButton.setDisable(!filled);
    }

    private void tryLogin() {
        if (!loginButton.isDisable()) {
            loginButton.fire();
        }
    }

    private void handleLogin(AppContext ctx, Router router) {
        if (validateLogin()) {
            String user = usernameField.getText();
            ctx.session.setUser(new NurseUser(user, "Nurse", "User", "ID123"));
            router.showDashboard();
        }
    }

    private boolean validateLogin() {
        String user = usernameField.getText();
        String pass = passwordField.getText();

        if (userDatabase.containsKey(user) && userDatabase.get(user).equals(pass)) {
            errorLabel.setVisible(false);
            return true;
        } else {
            errorLabel.setVisible(true);
            passwordField.clear();
            return false;
        }
    }

    public String getUsername() { return usernameField.getText(); }
    public String getPassword() { return passwordField.getText(); }
    public Button getLoginButton() { return loginButton; }
}
