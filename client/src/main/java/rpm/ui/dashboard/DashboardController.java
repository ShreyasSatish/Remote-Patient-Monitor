package rpm.ui.dashboard;

import javafx.scene.control.TextField;
import rpm.domain.PatientId;
import rpm.domain.VitalSnapshot;
import rpm.domain.alarm.AlarmState;
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
    private boolean overrideActive = false;
    private List<PatientId> overrideIds = new ArrayList<>();
    private int savedPageIndex = 0;
    private long overrideEndsAtMs = 0; // epoch millis in sim time

    private int rotationCounter = 0;

    // audio
    private final rpm.ui.alerts.AudioAlertManager audio;


    private List<PatientId> ids = new ArrayList<>();
    private int pageIndex = 0;

    public DashboardController(AppContext ctx, Router router, PatientGridView grid, TopBanner banner) {
        this.ctx = ctx;
        this.router = router;
        this.grid = grid;
        this.banner = banner;
        this.audio = new rpm.ui.alerts.AudioAlertManager(ctx.settings);


        // banner search behaviour for now: bed number search or "contains" label
        TextField search = banner.getSearchField();
        search.setOnAction(e -> {
            String q = search.getText().trim();
            if (q.isEmpty()) return;

            PatientId found = findPatient(q);
            if (found != null) router.showPatientDetail(found);
        });

        ctx.alarms.addListener(new rpm.domain.alarm.AlarmListener() {
            @Override
            public void onAlarmTransition(rpm.domain.alarm.AlarmTransition t) {
                // Treat any transition TO non-green as "red-only alert"
                if (t.getTo() != rpm.domain.alarm.AlarmLevel.GREEN) {
                    javafx.application.Platform.runLater(() -> onAlertTriggered());
                }
            }

            @Override
            public void onAlarmState(PatientId id, java.time.Instant time, AlarmState state) {}
        });

        grid.setOnPatientClicked(router::showPatientDetail);
        grid.setOnNextPage(() -> {
            int perScreen = ctx.settings.getPatientsPerScreen();
            int maxPage = pageCount(perScreen) - 1;

            if (pageIndex >= maxPage) {
                pageIndex = 0;          // wrap back to first page
            } else {
                pageIndex++;
            }

            renderPage();
        });

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

        List<PatientId> source = overrideActive ? overrideIds : ids;

        int from, to;
        if (overrideActive) {
            from = 0;
            to = Math.min(source.size(), perScreen); // show all alerting up to perScreen
        } else {
            from = pageIndex * perScreen;
            to = Math.min(source.size(), from + perScreen);
        }

        List<PatientTileModel> tiles = source.subList(from, to).stream()
                .map(this::buildTile)
                .collect(Collectors.toList());

        grid.setTiles(tiles, overrideActive ? 0 : pageIndex, pageCount(perScreen));
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

        AlarmState s = ctx.alarms.getState(id);
        boolean alerting = (s != null && s.getOverall() != rpm.domain.alarm.AlarmLevel.GREEN);

        return PatientTileModel.from(id, name, snap, alerting);
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

    private void onAlertTriggered() {
        int perScreen = ctx.settings.getPatientsPerScreen();
        int total = ctx.ward.getPatientCount();

        // If everything fits on screen, don't override page—just let tiles turn red.
        if (perScreen >= total) {
            // still start audio for duration if enabled
            audio.startFor(ctx.clock.getSimTime().toEpochMilli());
            return;
        }

        // compute alerting patient ids
        List<PatientId> alerting = ctx.ward.getPatientIds().stream()
                .filter(this::isAlerting)
                .collect(Collectors.toList());

        if (alerting.isEmpty()) return;

        // start audio
        audio.startFor(ctx.clock.getSimTime().toEpochMilli());

        // activate override
        if (!overrideActive) savedPageIndex = pageIndex; // preserve current page once

        overrideActive = true;
        overrideIds = alerting;

        // set when to end override
        java.time.Duration d = ctx.settings.getAlertDuration().toDurationOrNull();
        if (d == null) {
            overrideEndsAtMs = Long.MAX_VALUE; // until resolved
        } else {
            overrideEndsAtMs = ctx.clock.getSimTime().toEpochMilli() + d.toMillis();
        }
    }

    public void onUiTick() {
        long now = ctx.clock.getSimTime().toEpochMilli();

        // stop audio if time passed
        audio.tick(now);

        // end override if duration elapsed OR no alerts remain (unless UNTIL_RESOLVED)
        if (overrideActive) {
            boolean anyStillAlerting = overrideIds.stream().anyMatch(this::isAlerting);

            boolean durationElapsed = now >= overrideEndsAtMs;
            boolean untilResolved = (ctx.settings.getAlertDuration().toDurationOrNull() == null);

            if ((!untilResolved && durationElapsed) || (untilResolved && !anyStillAlerting)) {
                overrideActive = false;
                overrideIds = new ArrayList<>();
                pageIndex = savedPageIndex;
                // also stop audio when resolved
                if (untilResolved) audio.stop();
            }
        }

        // rotation: only when enabled and NOT overriding
        if (!overrideActive && ctx.settings.isRotationEnabled()) {
            // simplest: just advance page every rotationSeconds using a counter
            rotationCounter++;
            if (rotationCounter >= ctx.settings.getRotationSeconds()) {
                rotationCounter = 0;
                pageIndex++;
                int perScreen = ctx.settings.getPatientsPerScreen();
                int maxPage = Math.max(0, (ids.size() - 1) / perScreen);
                if (pageIndex > maxPage) pageIndex = 0;
            }
        }
    }

    private boolean isAlerting(PatientId id) {
        AlarmState s = ctx.alarms.getState(id);
        return rpm.ui.alerts.AlertRules.isAlertingRedOnly(s);
        // or: return s != null && s.getOverall() != AlarmLevel.GREEN;
    }



}
