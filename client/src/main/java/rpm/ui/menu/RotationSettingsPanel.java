package rpm.ui.menu;

import javafx.geometry.Insets;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import rpm.ui.app.AppContext;

public final class RotationSettingsPanel extends VBox {

    public RotationSettingsPanel(AppContext ctx) {
        setSpacing(8);
        setPadding(new Insets(12));
        setStyle(
                "-fx-border-color: #cccccc;" +
                        "-fx-border-radius: 10;" +
                        "-fx-background-radius: 10;" +
                        "-fx-background-color: white;"
        );

        Label title = new Label("Rotation");
        title.setStyle("-fx-font-weight: bold; -fx-font-size: 14;");

        CheckBox enabled = new CheckBox("Enable auto-rotation between pages");
        enabled.setSelected(ctx.settings.isRotationEnabled());
        enabled.selectedProperty().addListener((obs, oldV, newV) -> ctx.settings.setRotationEnabled(newV));

        ComboBox<Integer> seconds = new ComboBox<>();
        seconds.getItems().addAll(5, 10, 15, 30, 60);
        seconds.setValue(ctx.settings.getRotationSeconds());
        seconds.valueProperty().addListener((obs, oldV, newV) -> {
            if (newV != null) ctx.settings.setRotationSeconds(newV);
        });

        HBox row = new HBox(10, new Label("Rotate every:"), seconds, new Label("seconds"));
        row.setDisable(!enabled.isSelected());
        enabled.selectedProperty().addListener((obs, oldV, newV) -> row.setDisable(!newV));

        getChildren().addAll(title, enabled, row);
    }
}
