package rpm.ui.layout;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import rpm.ui.app.AppContext;
import rpm.ui.app.Router;

public final class TopBanner extends HBox {

    private final Label userLabel = new Label();
    private final TextField searchField = new TextField();

    public TopBanner(AppContext ctx, Router router) {
        setSpacing(10);
        setPadding(new Insets(10));

        Button logoBtn = new Button("Home");
        logoBtn.setOnAction(e -> router.showDashboard());

        userLabel.setText(ctx.session.isLoggedIn()
                ? ctx.session.getUser().getName() + " (" + ctx.session.getUser().getUsername() + ")"
                : "");

        searchField.setPromptText("Search patient / bed...");
        HBox.setHgrow(searchField, Priority.ALWAYS);

        Button menuBtn = new Button("Menu");
        menuBtn.setOnAction(e -> router.showMenu());

        Button settingsBtn = new Button("⚙");
        settingsBtn.setOnAction(e -> SettingsPopup.show(settingsBtn, ctx, router));

        getChildren().addAll(logoBtn, userLabel, searchField, menuBtn, settingsBtn);
    }

    public TextField getSearchField() { return searchField; }
    public void setUserText(String s) { userLabel.setText(s); }
}
