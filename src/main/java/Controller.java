package com.itemanalysis.irt.charttool;

import com.itemanalysis.psychometrics.distribution.UniformDistributionApproximation;
import com.itemanalysis.psychometrics.irt.model.Irm3PL;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;


public class Controller implements Initializable{

    @FXML
    private ChoiceBox<String> modelChoiceBox;

    @FXML
    private TextField discriminationTextField;

    @FXML
    private TextField guessingTextField;

    @FXML
    private TextField difficultyTextField;

    @FXML
    private TextField slippingTextField;

    @FXML
    private CheckBox iccCheckBox;

    @FXML
    private CheckBox itemInfoCheckBox;

    @FXML
    private CheckBox stdErrorItemCheckBox;

    @FXML
    private CheckBox tccCheckBox;

    @FXML
    private CheckBox testInfoCheckBox;

    @FXML
    private CheckBox stdErrorTextCheckBox;

    @FXML
    private CheckBox observedPropCheckBox;

    @FXML
    private TextField titleTextField;

    @FXML
    private TextField xlabelTextFeild;

    @FXML
    private TextField xminTextField;

    @FXML
    private TextField xmaxTextField;

    @FXML
    private TextField ylabelTextFeild;

    @FXML
    private TextField yminTextField;

    @FXML
    private TextField ymaxTextField;

    @FXML
    private Button addButton;

    @FXML
    private Button clearButton;

    @FXML
    private Button updateButton;

    @FXML
    private Button previousButton;

    @FXML
    private Button nextButton;

    @FXML
    private NumberAxis defaultXAxis;

    @FXML
    private NumberAxis defaultYAxis;

    @FXML
    private LineChart<Number, Number> defaultLineChart;

    private XYChart.Series testInfoSeries = new XYChart.Series();

    private XYChart.Series tccSeries = new XYChart.Series();

    private XYChart.Series testStdErrorSeries = new XYChart.Series();

    private boolean isAnimated = false;

    UniformDistributionApproximation dist = new UniformDistributionApproximation(-5, 5, 250);

    public void initialize(URL fxmlFileLocation, ResourceBundle resources) {
        modelChoiceBox.getItems().clear();
        modelChoiceBox.setItems(FXCollections.observableArrayList("3PL", "4PL", "PCM", "GPCM", "GRM"));
        modelChoiceBox.setValue("3PL");

        for(int i=0;i<dist.getNumberOfPoints();i++){
            tccSeries.getData().add(new XYChart.Data(dist.getPointAt(i), 0));
            tccSeries.setName("TCC");
            testInfoSeries.getData().add(new XYChart.Data(dist.getPointAt(i), 0));
            testInfoSeries.setName("Test Information");
            testStdErrorSeries.getData().add(new XYChart.Data(dist.getPointAt(i), 0));
            testStdErrorSeries.setName("Standard Error (Test)");
        }

        defaultLineChart.setAnimated(isAnimated);

        defaultYAxis.setAutoRanging(false);
        defaultYAxis.setLowerBound(0.0);
        defaultYAxis.setUpperBound(1.0);
        defaultYAxis.setTickUnit(0.2);

    }

    public void handleChartUpdate(){
        defaultLineChart.setTitle(titleTextField.getText().trim());
    }

    @FXML
    private void handleClearChart(){
        defaultLineChart.getData().clear();
        tccSeries.getData().clear();
        testInfoSeries.getData().clear();
        testStdErrorSeries.getData().clear();

        for(int i=0;i<dist.getNumberOfPoints();i++){
            tccSeries.getData().add(new XYChart.Data(dist.getPointAt(i), 0));
            tccSeries.setName("TCC");

            testInfoSeries.getData().add(new XYChart.Data(dist.getPointAt(i), 0));
            testInfoSeries.setName("Test Information");

            testStdErrorSeries.getData().add(new XYChart.Data(dist.getPointAt(i), 0));
            testStdErrorSeries.setName("Standard Error (Test)");
        }

        defaultLineChart.setCreateSymbols(false);
    }

    @FXML
    private void handleAddItem(){

        double a = 1;
        double b = 0;
        double c = 0;
        double u = 1;

        String aText = discriminationTextField.getText().trim();
        if(!"".equals(aText)) a = Double.parseDouble(aText);

        String bText = difficultyTextField.getText().trim();
        if(!"".equals(bText)) b = Double.parseDouble(bText);

        String cText = guessingTextField.getText().trim();
        if(!"".equals(cText)) c = Double.parseDouble(cText);

        String uText = slippingTextField.getText().trim();
        if(!"".equals(uText)) u = Double.parseDouble(uText);

//        Irm4PL model = new Irm4PL(a, b, c, u, 1.0);
        Irm3PL model = new Irm3PL(a, b, c, 1.0);


        int count = defaultLineChart.getData().size();
        XYChart.Series series = new XYChart.Series();
        series.setName("Item " + (count+1));

        for(int i=0;i<dist.getNumberOfPoints();i++){
            series.getData().add(new XYChart.Data(dist.getPointAt(i), model.probability(dist.getPointAt(i), 1)));
        }

        ObservableList<XYChart.Data<Number,Number>> newData = series.getData();
        ObservableList<XYChart.Data<Number,Number>> tccData = tccSeries.getData();
        ObservableList<XYChart.Data<Number,Number>> tInfoData = testInfoSeries.getData();
        ObservableList<XYChart.Data<Number,Number>> stdErrorTestData = testStdErrorSeries.getData();

        Number tccValue = 0;
        Number testInfoValue = 0;
        Number seTestValue = 0;
        for(int i=0;i<newData.size();i++){
            tccValue = tccData.get(i).getYValue().doubleValue() + newData.get(i).getYValue().doubleValue();
            tccData.get(i).setYValue(tccValue);

            testInfoValue = tInfoData.get(i).getYValue().doubleValue() + model.itemInformationAt(dist.getPointAt(i));
            tInfoData.get(i).setYValue(testInfoValue);

            seTestValue = 1.0/Math.sqrt(testInfoValue.doubleValue());
            stdErrorTestData.get(i).setYValue(seTestValue);


        }

        defaultLineChart.getData().add(series);
        defaultLineChart.setCreateSymbols(false);

        discriminationTextField.clear();
        difficultyTextField.clear();
        guessingTextField.clear();
        slippingTextField.clear();

    }

    @FXML
    private void handleTccSelected(){
        if(tccCheckBox.isSelected()){
            defaultLineChart.setAnimated(true);
            defaultLineChart.getData().add(tccSeries);
            defaultYAxis.setLabel("Value");
        }else{
            for(int i=0;i<defaultLineChart.getData().size();i++){
                if("TCC".equals(defaultLineChart.getData().get(i).getName())){
                    defaultLineChart.getData().remove(i);
                }
            }
            if(!testInfoCheckBox.isSelected() || !stdErrorTextCheckBox.isSelected()){
                defaultLineChart.setAnimated(false);
                defaultYAxis.setLabel("Probability");
            }
        }
    }

    @FXML
    private void handleTestInfoSelected(){
        if(testInfoCheckBox.isSelected()){
            defaultLineChart.setAnimated(true);
            defaultLineChart.getData().add(testInfoSeries);
            defaultYAxis.setLabel("Value");
        }else{
            for(int i=0;i<defaultLineChart.getData().size();i++){
                if("Test Information".equals(defaultLineChart.getData().get(i).getName())){
                    defaultLineChart.getData().remove(i);
                }
            }
            if(!tccCheckBox.isSelected() || !stdErrorTextCheckBox.isSelected()){
                defaultLineChart.setAnimated(false);
                defaultYAxis.setLabel("Probability");
            }
        }
    }

    @FXML
    private void handleStdErrorTest(){
        if(stdErrorTextCheckBox.isSelected()){
            defaultLineChart.setAnimated(true);
            defaultLineChart.getData().add(testStdErrorSeries);
            defaultYAxis.setLabel("Value");
        }else{
            for(int i=0;i<defaultLineChart.getData().size();i++){
                if("Standard Error (Test)".equals(defaultLineChart.getData().get(i).getName())){
                    defaultLineChart.getData().remove(i);
                }
            }
            if(!tccCheckBox.isSelected() || !testInfoCheckBox.isSelected()){
                defaultLineChart.setAnimated(false);
                defaultYAxis.setLabel("Probability");
            }
        }
    }


}
