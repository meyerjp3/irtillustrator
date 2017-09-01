import com.itemanalysis.psychometrics.distribution.ContinuousDistributionApproximation;
import com.itemanalysis.psychometrics.irt.model.IrmBinary;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.stage.Stage;


public class IccExample extends Application {

  @Override
  public void start(Stage stage) {
    final NumberAxis xAxis = new NumberAxis();
    final NumberAxis yAxis = new NumberAxis();
    final LineChart<Number,Number> lineChart =
        new LineChart<Number,Number>(xAxis,yAxis);
    XYChart.Series series = new XYChart.Series();

    IrmBinary model = new IrmBinary(1.6, -0.8);
    ContinuousDistributionApproximation dist =
        new ContinuousDistributionApproximation(51, -4.5, 4.5);

    double theta = 0;
    for(int i=0;i<51;i++){
        theta = dist.getPointAt(i);
        series.getData().add(new XYChart.Data(theta, model.probability(theta, 1)));
    }

    Scene scene  = new Scene(lineChart,800,600);
    lineChart.getData().add(series);
    lineChart.setLegendVisible(false);
    stage.setScene(scene);
    stage.show();
  }

  public static void main(String[] args) {
    launch(args);
  }
}
