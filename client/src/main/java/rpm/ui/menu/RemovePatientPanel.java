package rpm.ui.menu;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import rpm.domain.PatientId;
import rpm.ui.app.AppContext;

import java.util.List;

public final class RemovePatientPanel extends VBox {

    private final ComboBox<PatientId> selector = new ComboBox<>();

    public RemovePatientPanel(AppContext ctx) {

        getStyleClass().add("panel-card");
        setSpacing(8);
        setPadding(new Insets(12));

        Label title = new Label("View");
        title.getStyleClass().add("panel-title");

        refreshIds(ctx);

        Button refresh = new Button("Refresh list");
        refresh.getStyleClass().add("banner-btn"); // or create "primary-btn"
        refresh.setMaxWidth(Double.MAX_VALUE);
        refresh.setOnAction(e -> refreshIds(ctx));

        Button add = new Button("Add patient");
        add.getStyleClass().add("banner-btn"); // or create "primary-btn"
        add.setMaxWidth(Double.MAX_VALUE);

        Button remove = new Button("Remove selected");
        remove.setOnAction(e -> {
            PatientId id = selector.getValue();
            if (id == null) return;
            boolean ok = ctx.ward.removePatient(id);
            if (!ok) {
                System.out.println("Remove failed (may be <= 8 beds or minimum patients).");
            }
            refreshIds(ctx);
        });

        getChildren().addAll(title, selector, remove, refresh,
                new Label("Note: Beds 01–08 cannot be removed."));
    }

    private void refreshIds(AppContext ctx) {
        List<PatientId> ids = ctx.ward.getPatientIds();
        selector.getItems().setAll(ids);
        if (!ids.isEmpty()) selector.setValue(ids.get(0));
    }
}
