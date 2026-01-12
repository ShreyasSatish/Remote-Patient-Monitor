package rpm.ui.menu;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.VBox;
import rpm.ui.app.AppContext;

public final class ViewSettingsPanel extends VBox {

    public ViewSettingsPanel(AppContext ctx) {
        setSpacing(8);
        setPadding(new Insets(12));
        setStyle(
                "-fx-border-color: #cccccc;" +
                        "-fx-border-radius: 10;" +
                        "-fx-background-radius: 10;" +
                        "-fx-background-color: white;"
        );


        Label title = new Label("View");
        title.setStyle("-fx-font-weight: bold; -fx-font-size: 14;");

        Label current = new Label();

        Slider slider = new Slider(1, 16, ctx.settings.getPatientsPerScreen());
        slider.setMajorTickUnit(1);
        slider.setMinorTickCount(0);
        slider.setSnapToTicks(true);
        slider.setShowTickLabels(true);
        slider.setShowTickMarks(true);

        current.setText("Patients per screen: " + ctx.settings.getPatientsPerScreen());

        slider.valueProperty().addListener((obs, oldV, newV) -> {
            int v = (int) Math.round(newV.doubleValue());
            ctx.settings.setPatientsPerScreen(v);
            current.setText("Patients per screen: " + v);
        });

        getChildren().addAll(title, current, slider);
    }
}
