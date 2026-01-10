package rpm.ui.alerts;

import rpm.domain.PatientId;
import rpm.domain.VitalSnapshot;
import rpm.domain.alarm.AlarmState;
import rpm.simulation.PatientCard;
import rpm.ui.app.AppContext;
import rpm.ui.app.Router;

import java.util.List;
import java.util.stream.Collectors;

public final class AlertController {

    private final AppContext ctx;
    private final Router router;
    private final AlertGridView view;
    private final AlertAcknowledger acknowledger = new AlertAcknowledger();

    public AlertController(AppContext ctx, Router router, AlertGridView view) {
        this.ctx = ctx;
        this.router = router;
        this.view = view;

        view.setOnPatientClicked(router::showPatientDetail);
        view.setOnResolve(id -> {
            acknowledger.acknowledge(id, AlertRules.resolveDuration(ctx));
        });
    }

    public void refresh() {
        List<PatientId> all = ctx.ward.getPatientIds();
        int n = all.size();

        // Identify alerting patients
        List<PatientId> alerting = all.stream()
                .filter(this::isAlerting)
                .collect(Collectors.toList());

        boolean showOnlyAlerts = n > 8; // your requirement
        List<PatientId> toShow = showOnlyAlerts ? alerting : all;

        int perScreen = ctx.settings.getPatientsPerScreen(); // max 16
        List<AlertTileModel> tiles = toShow.stream()
                .limit(perScreen) // alert screen typically shows “all alerting” but cap to 16 for now
                .map(this::buildTile)
                .collect(Collectors.toList());

        view.setTiles(tiles, perScreen);
    }

    private boolean isAlerting(PatientId id) {
        AlarmState s = ctx.alarms.getState(id); // requires the tiny AlarmService getter
        return AlertRules.isAlertingRedOnly(s);
    }

    private AlertTileModel buildTile(PatientId id) {
        VitalSnapshot snap = ctx.ward.getPatientLatestSnapshot(id);
        PatientCard card = ctx.ward.getPatientCard(id);

        String name = (card != null && card.getLabel() != null && !card.getLabel().isEmpty())
                ? card.getLabel()
                : "Patient";

        boolean alerting = isAlerting(id);
        boolean acknowledged = acknowledger.isAcknowledged(id);

        return AlertTileModel.from(id, name, snap, alerting, acknowledged);
    }
}
