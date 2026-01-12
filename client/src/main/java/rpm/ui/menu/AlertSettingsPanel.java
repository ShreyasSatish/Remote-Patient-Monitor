package rpm.ui.menu;

import javafx.geometry.Insets;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import rpm.ui.app.AlertDuration;
import rpm.ui.app.AlertPreference;
import rpm.ui.app.AppContext;

public final class AlertSettingsPanel extends VBox {

    public AlertSettingsPanel(AppContext ctx) {
        setSpacing(8);
        setPadding(new Insets(12));
        setStyle(
                "-fx-border-color: #cccccc;" +
                        "-fx-border-radius: 10;" +
                        "-fx-background-radius: 10;" +
                        "-fx-background-color: white;"
        );


        Label title = new Label("Alert");
        title.setStyle("-fx-font-weight: bold; -fx-font-size: 14;");

        ComboBox<AlertPreference> pref = new ComboBox<>();
        pref.getItems().addAll(AlertPreference.VISUAL_ONLY, AlertPreference.AUDIO_AND_VISUAL);
        pref.setValue(ctx.settings.getAlertPreference());
        pref.valueProperty().addListener((obs, oldV, newV) -> {
            if (newV != null) ctx.settings.setAlertPreference(newV);
        });

        ComboBox<AlertDuration> dur = new ComboBox<>();
        dur.getItems().addAll(AlertDuration.SEC_10, AlertDuration.SEC_30, AlertDuration.MIN_1, AlertDuration.UNTIL_RESOLVED);
        dur.setValue(ctx.settings.getAlertDuration());
        dur.valueProperty().addListener((obs, oldV, newV) -> {
            if (newV != null) ctx.settings.setAlertDuration(newV);
        });

        HBox row1 = new HBox(10, new Label("Type:"), pref);
        HBox row2 = new HBox(10, new Label("Duration:"), dur);

        getChildren().addAll(title, row1, row2);
    }
}
