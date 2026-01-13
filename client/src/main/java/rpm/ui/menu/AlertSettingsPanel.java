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
        getStyleClass().add("panel-card");
        setSpacing(8);
        setPadding(new Insets(12));

        Label title = new Label("Alert");
        title.getStyleClass().add("panel-title");

        ComboBox<AlertPreference> pref = new ComboBox<>();
        pref.getItems().addAll(
                AlertPreference.VISUAL_ONLY,
                AlertPreference.AUDIO_AND_VISUAL
        );
        pref.setValue(ctx.settings.getAlertPreference());
        pref.setConverter(new javafx.util.StringConverter<AlertPreference>() {
            @Override
            public String toString(AlertPreference p) {
                if (p == null) return "";
                switch (p) {
                    case VISUAL_ONLY:
                        return "Visual only";
                    case AUDIO_AND_VISUAL:
                        return "Audio + visual";
                    default:
                        return "";
                }
            }

            @Override
            public AlertPreference fromString(String s) {
                return null;
            }
        });
        pref.valueProperty().addListener((obs, oldV, newV) -> {
            if (newV != null) ctx.settings.setAlertPreference(newV);
        });

        ComboBox<AlertDuration> dur = new ComboBox<>();
        dur.getItems().addAll(
                AlertDuration.SEC_10,
                AlertDuration.SEC_30,
                AlertDuration.MIN_1,
                AlertDuration.UNTIL_RESOLVED
        );
        dur.setValue(ctx.settings.getAlertDuration());
        dur.setConverter(new javafx.util.StringConverter<AlertDuration>() {
            @Override
            public String toString(AlertDuration d) {
                if (d == null) return "";
                switch (d) {
                    case SEC_10:
                        return "10 seconds";
                    case SEC_30:
                        return "30 seconds";
                    case MIN_1:
                        return "1 minute";
                    case UNTIL_RESOLVED:
                        return "Until resolved";
                    default:
                        return "";
                }
            }

            @Override
            public AlertDuration fromString(String s) {
                return null;
            }
        });
        dur.valueProperty().addListener((obs, oldV, newV) -> {
            if (newV != null) ctx.settings.setAlertDuration(newV);
        });

        HBox row1 = new HBox(10, new Label("Type:"), pref);
        HBox row2 = new HBox(10, new Label("Duration:"), dur);

        getChildren().addAll(title, row1, row2);
    }
}
