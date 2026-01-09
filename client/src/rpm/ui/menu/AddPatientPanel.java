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
        setSpacing(8);
        setPadding(new Insets(12));
        setStyle("-fx-border-color: #cccccc; -fx-border-radius: 10; -fx-background-radius: 10;");

        Label title = new Label("Add patient");
        title.setStyle("-fx-font-weight: bold; -fx-font-size: 14;");

        TextField name = new TextField();
        name.setPromptText("Name (UI only for now)");

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

        /**
         Button add = new Button("Add patient");
         add.setOnAction(e -> {
         PatientId added = ctx.ward.addPatient();
         System.out.println("Added " + added.getDisplayName()
         + " | name=" + name.getText()
         + " | age=" + age.getValue()
         + " | condition=" + condition.getValue());

         // IMPORTANT:
         // Right now WardManager.addPatient() doesn't accept metadata.
         // For your UI demo, print it or store it in a UI-side map.
         // Later, we can add an overload in WardManager to store label/conditions in PatientCard.
         });

         **/


        getChildren().addAll(title,
                new Label("Name:"), name,
                new Label("Age:"), age,
                new Label("Condition:"), condition,
                add,
                new Label("Note: metadata is not yet stored in simulation; this is UI-ready."),
                new Label("Next step: add WardManager.addPatient(label, conditions) overload.")
        );
    }
}
