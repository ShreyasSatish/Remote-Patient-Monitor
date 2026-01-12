package rpm.ui.alerts;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import rpm.domain.PatientId;

import java.util.List;
import java.util.function.Consumer;

public final class AlertOverlayView extends StackPane {

    private final VBox box = new VBox(10);
    private final VBox list = new VBox(8);
    private final Label title = new Label("ALERT");
    private final Button dismissAll = new Button("Resolve all");

    private Consumer<PatientId> onResolveOne = id -> {};
    private Runnable onResolveAll = () -> {};

    public AlertOverlayView() {
        setPickOnBounds(false);              // only capture clicks on the popup itself
        setMouseTransparent(true);
        box.setMouseTransparent(false);

        // Center-top container (you can change to bottom-right if you want)
        StackPane.setAlignment(box, Pos.TOP_CENTER);
        StackPane.setMargin(box, new Insets(12));

        title.setStyle("-fx-font-weight: bold; -fx-font-size: 16; -fx-text-fill: white;");

        dismissAll.setOnAction(e -> onResolveAll.run());

        HBox header = new HBox(10, title, new Region(), dismissAll);
        HBox.setHgrow(header.getChildren().get(1), Priority.ALWAYS);
        header.setAlignment(Pos.CENTER_LEFT);

        box.setPadding(new Insets(12));
        box.setMaxWidth(520);
        box.setStyle(
                "-fx-background-color: rgba(190,70,70,0.92);" +
                        "-fx-background-radius: 12;" +
                        "-fx-border-radius: 12;" +
                        "-fx-border-color: rgba(255,255,255,0.35);"
        );

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
        l.setStyle("-fx-text-fill: white; -fx-font-size: 13;");

        Button resolve = new Button("Resolve");
        resolve.setVisible(showResolveButtons);
        resolve.setManaged(showResolveButtons);
        resolve.setOnAction(e -> onResolveOne.accept(it.id));

        HBox row = new HBox(10, l, new Region(), resolve);
        HBox.setHgrow(row.getChildren().get(1), Priority.ALWAYS);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(8));
        row.setStyle("-fx-background-color: rgba(255,255,255,0.14); -fx-background-radius: 10;");

        return row;
    }

    public static final class AlertPopupItem {
        public final PatientId id;
        public final String displayName;
        public final String reason;

        public AlertPopupItem(PatientId id, String displayName, String reason) {
            this.id = id;
            this.displayName = displayName;
            this.reason = reason;
        }

        public String text() {
            String bed = id.getDisplayName();
            String r = (reason == null || reason.isBlank()) ? "Out of range vitals" : reason;
            return displayName + " (" + bed + "): " + r;
        }
    }
}
