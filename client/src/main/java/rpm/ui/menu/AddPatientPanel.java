package rpm.ui.menu;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import rpm.domain.PatientId;
import rpm.ui.app.AppContext;
import rpm.simulation.ChronicCondition;
import java.util.EnumSet;

public final class AddPatientPanel extends VBox {

    public AddPatientPanel(AppContext ctx) {

        getStyleClass().add("panel-card");
        setSpacing(10);
        setPadding(new Insets(12));

        Label title = new Label("Add patient");
        title.getStyleClass().add("panel-title");

        TextField name = new TextField();
        name.setPromptText("Name");

        Spinner<Integer> age = new Spinner<>(0, 120, 30);
        age.setEditable(true);

        ComboBox<String> condition = new ComboBox<>();
        condition.getItems().addAll(
                "Healthy",
                "COPD",
                "Heart failure",
                "Hypertension",
                "Post-op",
                "Sepsis risk"
        );
        condition.setValue("Healthy");

        Button add = new Button("Add patient");
        add.getStyleClass().add("banner-btn"); // or create "primary-btn"
        add.setMaxWidth(Double.MAX_VALUE);

        add.setOnAction(e -> {
            String label = name.getText().trim();
            if (label.isEmpty()) label = "Patient";

            EnumSet<ChronicCondition> conditions = EnumSet.noneOf(ChronicCondition.class);

            String c = condition.getValue();
            if (c != null) {
                switch (c) {
                    case "COPD":
                        conditions.add(ChronicCondition.COPD_TENDENCY);
                        break;
                    case "Heart failure":
                        conditions.add(ChronicCondition.HEART_FAILURE_RISK);
                        break;
                    case "Hypertension":
                        conditions.add(ChronicCondition.HYPERTENSION);
                        break;
                    case "Bradycardia":
                        conditions.add(ChronicCondition.BRADYCARDIA_TENDENCY);
                        break;
                    case "Arrhythmia":
                        conditions.add(ChronicCondition.ARRHYTHMIA_TENDENCY);
                        break;
                    case "Infection risk":
                        conditions.add(ChronicCondition.INFECTION_RISK);
                        break;
                    default:
                        break;
                }
            }

            PatientId added = ctx.ward.addPatient(label, conditions);

            System.out.println("Added " + added.getDisplayName()
                    + " | name=" + label
                    + " | age=" + age.getValue()
                    + " | condition=" + c);
        });

        getChildren().addAll(title,
                new Label("Name:"), name,
                new Label("Age:"), age,
                new Label("Condition:"), condition,
                add
        );
    }
}


