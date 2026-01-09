package rpm.ui.dashboard;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import rpm.domain.PatientId;

import java.util.List;
import java.util.function.Consumer;

public final class PatientGridView extends BorderPane {

    private static final int MAX_COLUMNS = 4;

    private final GridPane grid = new GridPane();
    private final ScrollPane scroll = new ScrollPane();

    private Consumer<PatientId> onPatientClicked = id -> {};
    private Runnable onNextPage = () -> {};
    private Runnable onPrevPage = () -> {};

    private final Button prevBtn = new Button("Prev");
    private final Button nextBtn = new Button("Next");
    private final Label pageLabel = new Label();

    public PatientGridView() {

        grid.setPadding(new Insets(16));
        grid.setHgap(16);
        grid.setVgap(16);
        grid.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

        scroll.setContent(grid);
        scroll.setFitToWidth(true);
        scroll.setFitToHeight(true);
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        setCenter(scroll);

        // Paging controls
        prevBtn.setOnAction(e -> onPrevPage.run());
        nextBtn.setOnAction(e -> onNextPage.run());

        HBox footer = new HBox(12, prevBtn, pageLabel, nextBtn);
        footer.setAlignment(Pos.CENTER);
        footer.setPadding(new Insets(10));

        setBottom(footer);
    }

    public void setOnPatientClicked(Consumer<PatientId> handler) {
        this.onPatientClicked = handler != null ? handler : (id -> {});
    }

    public void setOnNextPage(Runnable r) {
        this.onNextPage = r != null ? r : (() -> {});
    }

    public void setOnPrevPage(Runnable r) {
        this.onPrevPage = r != null ? r : (() -> {});
    }

    // Used by auto-rotation
    public void fireNextPage() {
        onNextPage.run();
    }

    public void setTiles(List<PatientTileModel> tiles, int pageIndex, int pageCount) {
        grid.getChildren().clear();
        grid.getColumnConstraints().clear();
        grid.getRowConstraints().clear();

        int count = (tiles == null) ? 0 : tiles.size();
        if (count <= 0) count = 1;

        int columns = Math.min(count, MAX_COLUMNS);
        int rows = (int) Math.ceil(count / (double) columns);

        double colPercent = 100.0 / columns;
        double rowPercent = 100.0 / rows;

        // Build columns
        for (int c = 0; c < columns; c++) {
            ColumnConstraints col = new ColumnConstraints();
            col.setPercentWidth(colPercent);
            col.setFillWidth(true);
            grid.getColumnConstraints().add(col);
        }

        // Build rows
        for (int r = 0; r < rows; r++) {
            RowConstraints row = new RowConstraints();
            row.setPercentHeight(rowPercent);
            row.setFillHeight(true);
            grid.getRowConstraints().add(row);
        }

        if (tiles != null) {
            for (int i = 0; i < tiles.size(); i++) {
                PatientTileModel t = tiles.get(i);
                PatientCardView card = new PatientCardView(t);

                // Fill grid cell completely
                card.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
                GridPane.setHgrow(card, Priority.ALWAYS);
                GridPane.setVgrow(card, Priority.ALWAYS);

                int col = i % columns;
                int row = i / columns;

                grid.add(card, col, row);

                card.setOnMouseClicked(e -> {
                    if (e.getButton() == MouseButton.PRIMARY) {
                        onPatientClicked.accept(t.id);
                    }
                });
            }
        }

        int current = pageIndex + 1;
        int total = Math.max(1, pageCount);
        pageLabel.setText("Page " + current + " / " + total);

        prevBtn.setDisable(pageIndex <= 0);
        nextBtn.setDisable(pageIndex >= total - 1);

        scroll.setVvalue(0);
    }
}
