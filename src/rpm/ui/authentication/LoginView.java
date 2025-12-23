package rpm.ui.authentication;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import rpm.ui.app.AppContext;
import rpm.ui.app.NurseUser;
import rpm.ui.app.Router;

public final class LoginView extends VBox {

    public LoginView(AppContext ctx, Router router) {
        setSpacing(10);
        setPadding(new Insets(30));

        TextField username = new TextField();
        username.setPromptText("Username");

        PasswordField password = new PasswordField();
        password.setPromptText("Password");

        Button loginBtn = new Button("Log in");
        loginBtn.setOnAction(e -> {
            // TEMP: accept anything for now
            ctx.session.setUser(new NurseUser(username.getText(), "Nurse", "User", "ID123"));
            router.showDashboard();
        });

        Button signupBtn = new Button("Sign up");
        signupBtn.setOnAction(e -> {
            // next screen later
        });

        getChildren().addAll(username, password, loginBtn, signupBtn);
    }
}
