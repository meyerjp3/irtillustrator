package com.itemanalysis.irtillustrator;

import com.itemanalysis.psychometrics.distribution.UniformDistributionApproximation;
import com.itemanalysis.psychometrics.irt.model.Irm3PL;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.SnapshotParameters;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.image.WritableImage;
import javafx.stage.FileChooser;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
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
    private CheckBox showLegendCheckBox;

    @FXML
    private TextField titleTextField;

    @FXML
    private TextField xlabelTextField;

    @FXML
    private TextField xminTextField;

    @FXML
    private TextField xmaxTextField;

    @FXML
    private TextField ylabelTextField;

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

    UniformDistributionApproximation dist = new UniformDistributionApproximation(-7, 7, 300);

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
        defaultLineChart.setTitle("");

        double xLower = Double.parseDouble(xminTextField.getText().trim());
        double xUpper = Double.parseDouble(xmaxTextField.getText().trim());

        defaultXAxis.setAutoRanging(false);
        defaultXAxis.setLowerBound(xLower);
        defaultXAxis.setUpperBound(xUpper);
        defaultXAxis.setTickUnit(1);

//        double yLower = Double.parseDouble(yminTextField.getText().trim());
        double yUpper = Double.parseDouble(ymaxTextField.getText().trim());
        defaultYAxis.setAutoRanging(false);
//        defaultYAxis.setLowerBound(yLower);
        defaultYAxis.setUpperBound(yUpper);
        defaultYAxis.setTickUnit(0.1);

    }

    @FXML
    public void handleChartUpdate(){
        defaultLineChart.setTitle(titleTextField.getText().trim());
        defaultXAxis.setLabel(xlabelTextField.getText().trim());
        defaultYAxis.setLabel(ylabelTextField.getText().trim());

        defaultXAxis.setAutoRanging(false);
        defaultXAxis.setForceZeroInRange(false);
        defaultXAxis.setLowerBound(Double.parseDouble(xminTextField.getText().trim()));
        defaultXAxis.setUpperBound(Double.parseDouble(xmaxTextField.getText().trim()));

        defaultYAxis.setAutoRanging(false);
        defaultYAxis.setForceZeroInRange(false);
        defaultYAxis.setLowerBound(Double.parseDouble(yminTextField.getText().trim()));
        defaultYAxis.setUpperBound(Double.parseDouble(ymaxTextField.getText().trim()));
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

        defaultLineChart.setTitle("");
        defaultXAxis.setLabel("Theta");
        defaultYAxis.setLabel("Probability");

        defaultXAxis.setLowerBound(-6);
        defaultXAxis.setUpperBound(6);
        defaultYAxis.setLowerBound(0);
        defaultYAxis.setUpperBound(1);

        xminTextField.setText("-6.0");
        xmaxTextField.setText("6.0");
        yminTextField.setText("0");
        ymaxTextField.setText("1");

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

    @FXML
    private void handleShowHideLegend(){
        if(showLegendCheckBox.isSelected()){
            defaultLineChart.setLegendVisible(true);
        }else{
            defaultLineChart.setLegendVisible(false);
        }
    }

    @FXML
    private void handleSaveChartAsPNG(){
        WritableImage image = defaultLineChart.snapshot(new SnapshotParameters(), null);

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Image");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("PNG", "*.png"),
                new FileChooser.ExtensionFilter("JPG", "*.jpg")
        );

        File file = fileChooser.showSaveDialog(defaultLineChart.getScene().getWindow());

        //Save file as PNG if file type is not specified
        if(null!=file){
            String fileName = file.getAbsolutePath();
            if(!fileName.toLowerCase().endsWith(".png") && !fileName.toLowerCase().endsWith(".jpg")){
                file = new File(file.getAbsolutePath() + ".png");
            }

            fileName = file.getAbsolutePath();
            String suffix = fileName.substring(fileName.lastIndexOf(".")+1, fileName.length()).trim().toLowerCase();
            if(!suffix.equals("png") && !suffix.equals("jpg")){
                suffix = "png";
            }

            try{

                if(suffix.equals("jpg")){
                    BufferedImage image2 = SwingFXUtils.fromFXImage(image, null);
                    // Remove alpha-channel from buffered image: To prevent rendering in pink tones (bug in Javafx)
                    BufferedImage imageRGB = new BufferedImage(image2.getWidth(), image2.getHeight(), BufferedImage.OPAQUE);
                    Graphics2D graphics = imageRGB.createGraphics();
                    graphics.drawImage(image2, 0, 0, null);
                    ImageIO.write(imageRGB, suffix, file);
                    graphics.dispose();
                }else{
                    ImageIO.write(SwingFXUtils.fromFXImage(image, null), suffix, file);
                }


            }catch(IOException ex){
                ex.printStackTrace();
            }

        }



    }

}
