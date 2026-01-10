package rpm.ui.dashboard;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
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
    private Consumer<PatientId> onResolve = id -> {};
    private Runnable onNextPage = () -> {};
    private Runnable onPrevPage = () -> {};

    private final Button prevBtn = new Button("Prev");
    private final Button nextBtn = new Button("Next");
    private final Label pageLabel = new Label();

    public PatientGridView() {

        // Blue background behind the grid area
        setStyle("-fx-background-color: #A0C1D1;");

        grid.setPadding(new Insets(16));
        grid.setHgap(16);
        grid.setVgap(16);
        grid.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        grid.setStyle("-fx-background-color: transparent;");

        scroll.setContent(grid);
        scroll.setFitToWidth(true);
        scroll.setFitToHeight(true);
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        // ✅ Use CSS (reliable) to kill viewport white background
        scroll.getStyleClass().add("dashboard-scroll");
        scroll.setPannable(false);

        setCenter(scroll);

        prevBtn.setOnAction(e -> onPrevPage.run());
        nextBtn.setOnAction(e -> onNextPage.run());

        HBox footer = new HBox(12, prevBtn, pageLabel, nextBtn);
        footer.setAlignment(Pos.CENTER);
        footer.setPadding(new Insets(10));
        footer.setStyle("-fx-background-color: #A0C1D1;");

        setBottom(footer);
    }

    public void setOnPatientClicked(Consumer<PatientId> handler) {
        this.onPatientClicked = handler != null ? handler : (id -> {});
    }

    public void setOnResolve(Consumer<PatientId> handler) {
        this.onResolve = handler != null ? handler : (id -> {});
    }

    public void setOnNextPage(Runnable r) {
        this.onNextPage = r != null ? r : (() -> {});
    }

    public void setOnPrevPage(Runnable r) {
        this.onPrevPage = r != null ? r : (() -> {});
    }

    public void fireNextPage() {
        onNextPage.run();
    }

    public void setTiles(List<PatientTileModel> tiles,
                         int pageIndex,
                         int pageCount,
                         boolean showResolve) {

        grid.getChildren().clear();
        grid.getColumnConstraints().clear();
        grid.getRowConstraints().clear();

        int count = (tiles == null) ? 0 : tiles.size();
        if (count <= 0) count = 1;

        int columns = Math.min(count, MAX_COLUMNS);
        int rows = (int) Math.ceil(count / (double) columns);

        double colPercent = 100.0 / columns;
        double rowPercent = 100.0 / rows;

        for (int i = 0; i < columns; i++) {
            ColumnConstraints col = new ColumnConstraints();
            col.setPercentWidth(colPercent);
            col.setFillWidth(true);
            col.setHgrow(Priority.ALWAYS);
            grid.getColumnConstraints().add(col);
        }

        for (int i = 0; i < rows; i++) {
            RowConstraints row = new RowConstraints();
            row.setPercentHeight(rowPercent);
            row.setFillHeight(true);
            row.setVgrow(Priority.ALWAYS);
            grid.getRowConstraints().add(row);
        }

        if (tiles != null) {
            int c = 0, r = 0;

            for (PatientTileModel t : tiles) {
                PatientCardView card = new PatientCardView(t);
                card.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
                GridPane.setHgrow(card, Priority.ALWAYS);
                GridPane.setVgrow(card, Priority.ALWAYS);

                card.setOnMouseClicked(e -> onPatientClicked.accept(t.id));
                card.setOnResolve(() -> onResolve.accept(t.id));

                grid.add(card, c, r);

                c++;
                if (c >= columns) { c = 0; r++; }
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
