package rpm.ui.dashboard;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import rpm.domain.PatientId;

import java.util.List;
import java.util.function.Consumer;

public final class PatientGridView extends BorderPane {

    private final GridPane grid = new GridPane();
    private final Button prevBtn = new Button("◀");
    private final Button nextBtn = new Button("▶");
    private final Label pageLabel = new Label("Page 1/1");

    private Consumer<PatientId> onPatientClicked = id -> {};
    private Runnable onPrev = () -> {};
    private Runnable onNext = () -> {};

    public PatientGridView() {
        setPadding(new Insets(20));

        grid.setHgap(15);
        grid.setVgap(15);

        prevBtn.setOnAction(e -> onPrev.run());
        nextBtn.setOnAction(e -> onNext.run());

        HBox footer = new HBox(10, prevBtn, pageLabel, nextBtn);
        footer.setAlignment(Pos.CENTER);
        footer.setPadding(new Insets(10));

        setCenter(grid);
        setBottom(footer);
    }

    public void setOnPatientClicked(Consumer<PatientId> c) { this.onPatientClicked = c != null ? c : id -> {}; }
    public void setOnPrevPage(Runnable r) { this.onPrev = r != null ? r : () -> {}; }
    public void setOnNextPage(Runnable r) { this.onNext = r != null ? r : () -> {}; }

    public void setTiles(List<PatientTileModel> tiles, int pageIndex, int pageCount) {
        grid.getChildren().clear();

        // Simple layout: make a grid with 2 columns by default; if 16 tiles, it becomes 4x4 naturally.
        int cols = (int) Math.ceil(Math.sqrt(Math.max(1, tiles.size())));
        if (cols < 2) cols = 2;
        int r = 0, c = 0;

        for (PatientTileModel t : tiles) {
            PatientCardView card = new PatientCardView(t);
            card.setOnMouseClicked(e -> onPatientClicked.accept(t.id));
            grid.add(card, c, r);

            c++;
            if (c >= cols) { c = 0; r++; }
        }

        pageLabel.setText("Page " + (pageIndex + 1) + "/" + Math.max(1, pageCount));
    }
}
