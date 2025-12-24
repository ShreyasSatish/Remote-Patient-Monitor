package com.beginsecure.remotepatientmonitor;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ChoiceBox;

import java.net.URL;
import java.util.ResourceBundle;

public class SettingsController implements Initializable {
    @FXML
    private ChoiceBox<String> choiceNumPatients;

    private String[] numPatientsChoices = {"2 Patients", "4 Patients", "8 Patients"};

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        choiceNumPatients.getItems().addAll(numPatientsChoices);

    }
}
