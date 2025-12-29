package rpm.ui.fx;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;

import java.net.URL;
import java.util.ResourceBundle;

public class PatientCardController implements Initializable {

    @FXML
    private Label hrLabel;
    @FXML
    private Label bpLabel;
    @FXML
    private Label rrLabel;
    @FXML
    private Label tempLabel;
    @FXML
    private LineChart<Number, Number> ecgChart;

    private XYChart.Series<Number, Number> ecgSeries;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }
}
