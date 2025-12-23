package rpm.ui.dashboard;

import javafx.scene.control.TextField;
import rpm.domain.PatientId;
import rpm.domain.VitalSnapshot;
import rpm.simulation.PatientCard;
import rpm.ui.app.AppContext;
import rpm.ui.app.Router;
import rpm.ui.layout.TopBanner;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public final class DashboardController {

    private final AppContext ctx;
    private final Router router;
    private final PatientGridView grid;
    private final TopBanner banner;

    private List<PatientId> ids = new ArrayList<>();
    private int pageIndex = 0;

    public DashboardController(AppContext ctx, Router router, PatientGridView grid, TopBanner banner) {
        this.ctx = ctx;
        this.router = router;
        this.grid = grid;
        this.banner = banner;

        // Banner search behaviour: simple bed number search or "contains" label
        TextField search = banner.getSearchField();
        search.setOnAction(e -> {
            String q = search.getText().trim();
            if (q.isEmpty()) return;

            PatientId found = findPatient(q);
            if (found != null) router.showPatientDetail(found);
        });

        grid.setOnPatientClicked(router::showPatientDetail);
        grid.setOnNextPage(() -> { pageIndex++; renderPage(); });
        grid.setOnPrevPage(() -> { pageIndex = Math.max(0, pageIndex - 1); renderPage(); });
    }

    public void refreshPatients() {
        ids = ctx.ward.getPatientIds();
        int perScreen = ctx.settings.getPatientsPerScreen();
        int maxPage = Math.max(0, (ids.size() - 1) / perScreen);
        if (pageIndex > maxPage) pageIndex = maxPage;
    }

    public void renderPage() {
        int perScreen = ctx.settings.getPatientsPerScreen();

        int from = pageIndex * perScreen;
        int to = Math.min(ids.size(), from + perScreen);

        List<PatientTileModel> tiles = ids.subList(from, to).stream()
                .map(this::buildTile)
                .collect(Collectors.toList());

        grid.setTiles(tiles, pageIndex, pageCount(perScreen));
    }

    private int pageCount(int perScreen) {
        if (ids.isEmpty()) return 1;
        return (int) Math.ceil(ids.size() / (double) perScreen);
    }

    private PatientTileModel buildTile(PatientId id) {
        VitalSnapshot snap = ctx.ward.getPatientLatestSnapshot(id);
        PatientCard card = ctx.ward.getPatientCard(id);

        String name = (card != null && card.getLabel() != null && !card.getLabel().isEmpty())
                ? card.getLabel()
                : "Patient";

        // Alarm state (if you’ve got AlarmEngine state per patient)
        // If your AlarmEngine has getState(PatientId), use it:
        // AlarmState alarmState = ctx.alarmsEngine.getState(id);
        // For now we’ll leave it null, and the tile can stay normal:
        return PatientTileModel.from(id, name, snap);
    }

    private PatientId findPatient(String q) {
        // try bed number
        try {
            int bed = Integer.parseInt(q);
            PatientId id = new PatientId(bed);
            if (ctx.ward.getPatientIds().contains(id)) return id;
        } catch (NumberFormatException ignored) {}

        // search by label
        for (PatientId id : ctx.ward.getPatientIds()) {
            PatientCard c = ctx.ward.getPatientCard(id);
            if (c != null && c.getLabel() != null) {
                if (c.getLabel().toLowerCase().contains(q.toLowerCase())) return id;
            }
        }
        return null;
    }
}
