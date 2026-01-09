package rpm.ui.alerts;

import javafx.geometry.Insets;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import rpm.domain.PatientId;

import java.util.List;
import java.util.function.Consumer;

public final class AlertGridView extends VBox {

    private final GridPane grid = new GridPane();
    private Consumer<PatientId> onPatientClicked = id -> {};
    private Consumer<PatientId> onResolve = id -> {};

    public AlertGridView() {
        setPadding(new Insets(20));
        grid.setHgap(15);
        grid.setVgap(15);
        getChildren().add(grid);
    }

    public void setOnPatientClicked(Consumer<PatientId> c) { onPatientClicked = c != null ? c : id -> {}; }
    public void setOnResolve(Consumer<PatientId> c) { onResolve = c != null ? c : id -> {}; }

    public void setTiles(List<AlertTileModel> tiles, int perScreen) {
        grid.getChildren().clear();

        int cols = (int) Math.ceil(Math.sqrt(Math.max(1, tiles.size())));
        cols = Math.max(2, Math.min(cols, 4)); // keep it up to 4 cols for 16 max

        int r = 0, c = 0;
        for (AlertTileModel t : tiles) {
            AlertCardView card = new AlertCardView();
            card.setModel(t);

            card.setOnMouseClicked(e -> onPatientClicked.accept(t.id));
            card.setResolveHandler(() -> onResolve.accept(t.id));

            grid.add(card, c, r);

            c++;
            if (c >= cols) { c = 0; r++; }
        }
    }
}
