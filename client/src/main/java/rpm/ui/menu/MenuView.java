package rpm.ui.menu;

import javafx.geometry.Insets;
import javafx.scene.layout.*;
import rpm.ui.app.AppContext;
import rpm.ui.app.Router;

public final class MenuView extends BorderPane {


    public MenuView(AppContext ctx, Router router) {

        VBox left = new VBox(12,
                new ViewSettingsPanel(ctx),
                new RotationSettingsPanel(ctx),
                new AlertSettingsPanel(ctx)
        );
        left.setPadding(new Insets(15));
        left.setFillWidth(true);

        VBox right = new VBox(12,
                new RemovePatientPanel(ctx),
                new AddPatientPanel(ctx)
        );
        right.setPadding(new Insets(15));
        right.setFillWidth(true);

        HBox content = new HBox(15, left, right);
        content.setPadding(new Insets(10));

        HBox.setHgrow(left, Priority.ALWAYS);
        HBox.setHgrow(right, Priority.ALWAYS);
        left.setPrefWidth(520);
        right.setPrefWidth(520);

        setCenter(content);

    }
}
