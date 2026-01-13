package rpm.ui.layout;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import rpm.ui.app.AppContext;
import rpm.ui.app.Router;

public final class TopBanner extends HBox {

    private static final String LOGO_RESOURCE = "/rpm/ui/assets/rancho-logo.png";

    private final Button homeBtn = new Button();
    private final Label userLabel = new Label();
    private final TextField searchField = new TextField();
    private final Button menuBtn = new Button("Menu");
    private final Button powerBtn = new Button("⏻");

    public TopBanner(AppContext ctx, Router router) {
        getStyleClass().add("top-banner");
        setAlignment(Pos.CENTER_LEFT);
        setSpacing(12);
        setPadding(new Insets(10, 14, 10, 14));

        homeBtn.getStyleClass().add("banner-home");
        homeBtn.setOnAction(e -> router.showDashboard());

        homeBtn.setGraphic(buildLogo(34));     // <- was 22
        homeBtn.setText("Rancho");
        homeBtn.setGraphicTextGap(10);
        homeBtn.setContentDisplay(ContentDisplay.LEFT);
        homeBtn.setMinHeight(40);

        userLabel.getStyleClass().add("banner-user");
        userLabel.setText("");

        searchField.getStyleClass().add("banner-search");
        searchField.setPromptText("Search patient / bed…");
        searchField.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(searchField, Priority.ALWAYS);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        menuBtn.getStyleClass().add("banner-btn");
        menuBtn.setOnAction(e -> router.showMenu());
        menuBtn.setMinHeight(38);

        powerBtn.getStyleClass().add("banner-btn");
        powerBtn.setOnAction(e -> router.powerOffApp());
        powerBtn.setMinHeight(38);

        getChildren().addAll(homeBtn, userLabel, searchField, spacer, menuBtn, powerBtn);
    }

    public TextField getSearchField() {
        return searchField;
    }

    public void setUserText(String text) {
        userLabel.setText(text == null ? "" : text);
    }

    private ImageView buildLogo(double width) {
        var url = getClass().getResource(LOGO_RESOURCE);
        if (url == null) return new ImageView();

        ImageView iv = new ImageView(new Image(url.toExternalForm(), true));
        iv.setPreserveRatio(true);
        iv.setFitWidth(width);
        iv.setSmooth(true);
        return iv;
    }
}
