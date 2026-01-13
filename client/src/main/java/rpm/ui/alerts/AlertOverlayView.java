package rpm.ui.alerts;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import rpm.domain.PatientId;

import java.util.List;
import java.util.function.Consumer;

// UI for newer alert popup system

public final class AlertOverlayView extends StackPane {

    private final VBox box = new VBox(10);
    private final VBox list = new VBox(8);
    private final Label title = new Label("ALERT");
    private final Button dismissAll = new Button("Resolve all");

    private Consumer<PatientId> onResolveOne = id -> {};
    private Runnable onResolveAll = () -> {};

    public AlertOverlayView() {

        setPickOnBounds(false);     // only capture clicks on the popup itself
        setMouseTransparent(true);
        box.setMouseTransparent(false);

        StackPane.setAlignment(box, Pos.CENTER);
        StackPane.setMargin(box, new Insets(12));

        box.getStyleClass().add("alert-popup");
        title.getStyleClass().add("alert-title");
        dismissAll.getStyleClass().add("resolve-btn");

        dismissAll.setOnAction(e -> onResolveAll.run());

        HBox header = new HBox(10, title, new Region(), dismissAll);
        HBox.setHgrow(header.getChildren().get(1), Priority.ALWAYS);
        header.setAlignment(Pos.CENTER_LEFT);

        box.setPadding(new Insets(12));
        box.setMaxWidth(520);

        list.setFillWidth(true);

        box.getChildren().addAll(header, list);
        getChildren().add(box);

        setVisible(false);
        setManaged(false);
    }

    public void setOnResolveOne(Consumer<PatientId> handler) {
        this.onResolveOne = handler != null ? handler : (id -> {});
    }

    public void setOnResolveAll(Runnable r) {
        this.onResolveAll = r != null ? r : () -> {};
    }

    // Display alerts on the UI
    public void showAlerts(List<AlertPopupItem> items, boolean showResolveButtons) {
        list.getChildren().clear();

        if (items == null || items.isEmpty()) {
            hide();
            return;
        }

        title.setText("ALERT (" + items.size() + ")");

        dismissAll.setVisible(showResolveButtons);
        dismissAll.setManaged(showResolveButtons);

        for (AlertPopupItem it : items) {
            list.getChildren().add(row(it, showResolveButtons));
        }

        setVisible(true);
        setManaged(true);

        setMouseTransparent(false);
        box.setMouseTransparent(false);
        toFront();
    }

    public void hide() {
        setVisible(false);
        setManaged(false);
        setMouseTransparent(true);
    }

    private HBox row(AlertPopupItem it, boolean showResolveButtons) {
        Label l = new Label(it.text());
        l.setWrapText(true);

        Button resolve = new Button("Resolve");
        resolve.getStyleClass().add("resolve-btn");
        resolve.setVisible(showResolveButtons);
        resolve.setManaged(showResolveButtons);
        resolve.setOnAction(e -> onResolveOne.accept(it.id));

        HBox row = new HBox(10, l, new Region(), resolve);
        HBox.setHgrow(row.getChildren().get(1), Priority.ALWAYS);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(8));

        row.getStyleClass().add("alert-row");
        l.getStyleClass().add("alert-row-text");

        box.setMaxWidth(320);
        box.setMaxHeight(220);
        list.setFillWidth(true);
        box.setMinSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);

        return row;
    }

    public static final class AlertPopupItem {
        public final PatientId id;
        public final String reason;

        public AlertPopupItem(PatientId id, String reason) {
            this.id = id;
            this.reason = reason;
        }

        public String text() {
            String bed = id.getDisplayName();
            String r = (reason == null || reason.isBlank()) ? "Out of range" : reason;
            return bed + " \u2013 " + r; // en dash
        }
    }

}
