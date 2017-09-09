package com.itemanalysis.irtillustrator;

import com.itemanalysis.psychometrics.distribution.UniformDistributionApproximation;
import com.itemanalysis.psychometrics.irt.model.*;
import com.sun.org.apache.xerces.internal.impl.xpath.XPath;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.SnapshotParameters;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.WritableImage;
import javafx.stage.FileChooser;
import javafx.util.Callback;
import javafx.util.converter.DoubleStringConverter;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
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
    private TextField addStepParameterTextField;

    @FXML
    private Button addButton;

    @FXML
    private Button clearButton;

    @FXML
    private Button updateButton;

    @FXML
    private Button stepAddButton;

    @FXML
    private Button stepClearButton;

    @FXML
    private Button previousButton;

    @FXML
    private Button nextButton;

    @FXML
    private TableView<StepParameter> stepParameterTableView;

    @FXML
    private TableColumn<StepParameter, Double> stepTableColumn;

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

    private UniformDistributionApproximation dist = new UniformDistributionApproximation(-7, 7, 300);

    private ObservableList<StepParameter> data = null;

    private String[] models = {"Binary Model", "PCM", "GPCM", "GRM"};

    private String selectedModel = models[0];

    private double[] tcc = null;

    private double[] tinfo = null;

    private int itemCount = 0;

    private ArrayList<XYChart.Series> allItemSeries = new ArrayList<XYChart.Series>();

    private ArrayList<XYChart.Series> allItemInfoSeries = new ArrayList<XYChart.Series>();
    private ArrayList<XYChart.Series> allItemStdErrorSeries = new ArrayList<XYChart.Series>();

    private final String VERSION = "2017.1";

    private final String BUILD_DATE = "September 7, 2017";

    public void initialize(URL fxmlFileLocation, ResourceBundle resources) {
        modelChoiceBox.getItems().clear();
        modelChoiceBox.setItems(FXCollections.observableArrayList(models));
        modelChoiceBox.setValue("Binary Model");
        modelChoiceBox.getSelectionModel().selectedItemProperty().addListener(
                (ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
                    selectedModel = newValue;

                    if(models[1].equals(selectedModel)){
                        stepParameterTableView.setDisable(false);
                        discriminationTextField.setDisable(true);
                        difficultyTextField.setDisable(false);
                        guessingTextField.setDisable(true);
                        slippingTextField.setDisable(true);

                        addStepParameterTextField.setDisable(false);
                        stepAddButton.setDisable(false);
                        stepClearButton.setDisable(false);
                    }else if(models[2].equals(selectedModel) || models[3].equals(selectedModel)){
                        stepParameterTableView.setDisable(false);
                        discriminationTextField.setDisable(false);
                        difficultyTextField.setDisable(true);
                        guessingTextField.setDisable(true);
                        slippingTextField.setDisable(true);

                        addStepParameterTextField.setDisable(false);
                        stepAddButton.setDisable(false);
                        stepClearButton.setDisable(false);
                    }else{
                        stepParameterTableView.setDisable(true);
                        discriminationTextField.setDisable(false);
                        difficultyTextField.setDisable(false);
                        guessingTextField.setDisable(false);
                        slippingTextField.setDisable(false);

                        addStepParameterTextField.setDisable(true);
                        stepAddButton.setDisable(true);
                        stepClearButton.setDisable(true);
                    }

                }


        );



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

        //Add data to table - trying it out
        stepTableColumn.setCellValueFactory(new PropertyValueFactory<>("stepValue"));
        data = FXCollections.observableArrayList();

        stepParameterTableView.setDisable(true);//disabled on startup, changes with choice in ChoiceBox
        stepParameterTableView.setEditable(true);
        stepParameterTableView.setItems(data);

        stepTableColumn.setCellValueFactory(new PropertyValueFactory<StepParameter, Double>("stepValue"));
        stepTableColumn.setCellFactory(TextFieldTableCell.forTableColumn(new DoubleStringConverter()));
        stepTableColumn.setOnEditCommit(
                new EventHandler<TableColumn.CellEditEvent<StepParameter, Double>>(){
                    @Override
                    public void handle(TableColumn.CellEditEvent<StepParameter, Double> t){
                        ((StepParameter)t.getTableView().getItems().get(
                                t.getTablePosition().getRow())
                        ).setStepValue(t.getNewValue());

                    }
                }
        );


        tcc = new double[dist.getNumberOfPoints()];
        tinfo = new double[dist.getNumberOfPoints()];

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
        stepParameterTableView.setDisable(true);
        discriminationTextField.setDisable(false);
        difficultyTextField.setDisable(false);
        guessingTextField.setDisable(false);
        slippingTextField.setDisable(false);

        addStepParameterTextField.setDisable(true);
        stepAddButton.setDisable(true);
        stepClearButton.setDisable(true);

        modelChoiceBox.getSelectionModel().selectFirst();
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

        iccCheckBox.setSelected(true);
        itemInfoCheckBox.setSelected(false);
        stdErrorItemCheckBox.setSelected(false);
        tccCheckBox.setSelected(false);
        testInfoCheckBox.setSelected(false);
        stdErrorTextCheckBox.setSelected(false);
        showLegendCheckBox.setSelected(true);

        data = FXCollections.observableArrayList();
        stepParameterTableView.setItems(data);

        tcc = new double[dist.getNumberOfPoints()];
        tinfo = new double[dist.getNumberOfPoints()];
        allItemSeries = new ArrayList<>();
        allItemInfoSeries = new ArrayList<XYChart.Series>();
        allItemStdErrorSeries = new ArrayList<XYChart.Series>();
        itemCount = 0;
    }

    @FXML
    private void handleAddStep(){
        String text = addStepParameterTextField.getText().trim();
        if(!"".equals(text)){
            double value = Double.parseDouble(text);
            data.add(new StepParameter(value));
            addStepParameterTextField.clear();
        }
    }

    @FXML
    private void handleClearStep(){
        data = FXCollections.observableArrayList();
        stepParameterTableView.setItems(data);
    }

    private int checkDiscriminationValue(double value){
        if(value<=0){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Invalid Parameter Value");
            alert.setHeaderText(null);
            alert.setContentText("The discrimination parameter must be larger than zero.");
            alert.showAndWait();
            return 1;
        }
        return 0;
    }

    private int checkGuessingValue(double value){
        if(value<0 || value > 1){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Invalid Parameter Value");
            alert.setHeaderText(null);
            alert.setContentText("The guessing parameter must be between 0 and 1.");
            alert.showAndWait();
            return 1;
        }
        return 0;
    }

    private int checkSlippingValue(double value, double guessing){
        if(value <= guessing || value > 1){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Invalid Parameter Value");
            alert.setHeaderText(null);
            alert.setContentText("The slipping parameter must be between the guessing parameter and 1.");
            alert.showAndWait();
            return 1;
        }
        return 0;
    }

    private int checkThresholdParameters(double[] steps){
        double sum = 0;
        for(int i=0;i<steps.length;i++){
            sum += steps[i];
        }

        if(sum!=0){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Invalid Parameter Value");
            alert.setHeaderText(null);
            alert.setContentText("The threshold (step) parameters must sum to zero in the partial credit model.");
            alert.showAndWait();
            return 1;
        }
        return 0;
    }

    @FXML
    private void handleAddItem(){
        int invalidCount = 0;
        double a = 1;
        double b = 0;
        double c = 0;
        double u = 1;
        double[] steps = null;

        String aText = discriminationTextField.getText().trim();
        if(!"".equals(aText)){
            a = Double.parseDouble(aText);
            invalidCount += checkDiscriminationValue(a);
        }

        String bText = difficultyTextField.getText().trim();
        if(!"".equals(bText)) b = Double.parseDouble(bText);

        String cText = guessingTextField.getText().trim();
        if(!"".equals(cText)){
            c = Double.parseDouble(cText);
            invalidCount += checkGuessingValue(c);
        }

        String uText = slippingTextField.getText().trim();
        if(!"".equals(uText)){
            u = Double.parseDouble(uText);
            invalidCount += checkSlippingValue(u, c);
        }

        //Get Step parameters
        if(!models[0].equals(selectedModel)){
            int size = stepParameterTableView.getItems().size();
            steps = new double[size];
            for(int i=0;i<size;i++){
                steps[i] = stepParameterTableView.getItems().get(i).getStepValue();
            }
        }

        //check constraints on partial credit model - steps must sum to zero
        if(models[1].equals(selectedModel)){
            checkThresholdParameters(steps);
        }

        if(invalidCount == 0){

            ArrayList<XYChart.Series> categorySeries = new ArrayList<XYChart.Series>();
            XYChart.Series series = null;
            ItemResponseModel model = null;


            if(models[1].equals(selectedModel)){
                //partial credit model

                //create the model
                model = new IrmPCM(b, steps, 1.0);

                //add all series
                for(int k=0;k<model.getNcat();k++){
                    series = new XYChart.Series();
                    series.setName("Item " + (itemCount+1) + " Cat " + k);
                    categorySeries.add(series);
                    allItemSeries.add(series);
                }

                //compute probabilities
                for(int i=0;i<dist.getNumberOfPoints();i++){
                    for(int k=0;k<model.getNcat();k++){
                        categorySeries.get(k).getData().add(
                                new XYChart.Data(dist.getPointAt(i),
                                model.probability(dist.getPointAt(i), k)));
                    }
                }

                for(int k=0;k<model.getNcat();k++){
                    defaultLineChart.getData().add(categorySeries.get(k));
                }


            }else if(models[2].equals(selectedModel)){
                //generalized partial credit model

                //First step should be zero
                double[] steps2 = new double[steps.length+1];
                steps2[0] = 0;
                for(int i=0;i<steps.length;i++){
                    steps2[i+1]=steps[i];
                }

                //create the model
                model = new IrmGPCM(a, steps2, 1.0);

                //add all series
                for(int k=0;k<model.getNcat();k++){
                    series = new XYChart.Series();
                    series.setName("Item " + (itemCount+1) + " Cat " + k);
                    categorySeries.add(series);
                    allItemSeries.add(series);
                }

                //compute probabilities
                for(int i=0;i<dist.getNumberOfPoints();i++){
                    for(int k=0;k<model.getNcat();k++){
                        categorySeries.get(k).getData().add(
                                new XYChart.Data(dist.getPointAt(i),
                                        model.probability(dist.getPointAt(i), k)));
                    }
                }

                for(int k=0;k<model.getNcat();k++){
                    defaultLineChart.getData().add(categorySeries.get(k));
                }

            }else if(models[3].equals(selectedModel)){
                //Graded response model

                //create the model
                model = new IrmGRM(a, steps, 1.0);

                //add all series
                for(int k=0;k<model.getNcat()-1;k++){
                    series = new XYChart.Series();
                    series.setName("Item " + (itemCount+1) + " Cat " + (k+1));
                    categorySeries.add(series);
                    allItemSeries.add(series);
                }

                //compute probabilities
                for(int i=0;i<dist.getNumberOfPoints();i++){
                    for(int k=0;k<model.getNcat()-1;k++){
                        categorySeries.get(k).getData().add(
                                new XYChart.Data(dist.getPointAt(i),
                                        model.cumulativeProbability(dist.getPointAt(i), k+1)));
                    }
                }

                for(int k=0;k<model.getNcat()-1;k++){
                    defaultLineChart.getData().add(categorySeries.get(k));
                }


            }else{
                //binary item model

                model = new Irm4PL(a, b, c, u, 1.0);

                series = new XYChart.Series();
                series.setName("Item " + (itemCount+1));

                for(int i=0;i<dist.getNumberOfPoints();i++){
                    series.getData().add(new XYChart.Data(dist.getPointAt(i), model.probability(dist.getPointAt(i), 1)));
                }

                defaultLineChart.getData().add(series);
                allItemSeries.add(series);

            }

            defaultLineChart.setCreateSymbols(false);

            XYChart.Series itemInfoSeries = new XYChart.Series();
            itemInfoSeries.setName("Item Info " + (itemCount+1));
            XYChart.Series itemStdErrorSeries = new XYChart.Series();
            itemStdErrorSeries.setName("Item S.E. " + (itemCount+1));

            for(int i=0;i<dist.getNumberOfPoints();i++){
                tcc[i] = tcc[i]+model.expectedValue(dist.getPointAt(i));
                tinfo[i] = tinfo[i]+model.itemInformationAt(dist.getPointAt(i));

                itemInfoSeries.getData().add(new XYChart.Data(dist.getPointAt(i), model.itemInformationAt(dist.getPointAt(i))));
                itemStdErrorSeries.getData().add(new XYChart.Data(dist.getPointAt(i), Math.sqrt(1/model.itemInformationAt(dist.getPointAt(i)))));
            }

            allItemInfoSeries.add(itemInfoSeries);
            allItemStdErrorSeries.add(itemStdErrorSeries);

            updateTestInformation();
            updateTCC();

            //Reset text fields
            discriminationTextField.clear();
            difficultyTextField.clear();
            guessingTextField.clear();
            slippingTextField.clear();
            data = FXCollections.observableArrayList();
            stepParameterTableView.setItems(data);

            itemCount++;
        }

    }

    private void updateTCC(){
        ObservableList<XYChart.Data<Number,Number>> tccData = tccSeries.getData();
        for(int i=0;i<dist.getNumberOfPoints();i++){
            tccData.get(i).setYValue(tcc[i]);
        }
    }

    @FXML
    private void handleTccSelected(){
        if(tccCheckBox.isSelected()){
            defaultLineChart.setAnimated(true);

            updateTCC();

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

    private void updateTestInformation(){
        ObservableList<XYChart.Data<Number,Number>> tInfoData = testInfoSeries.getData();
        for(int i=0;i<dist.getNumberOfPoints();i++){
            tInfoData.get(i).setYValue(tinfo[i]);
        }
    }

    @FXML
    private void handleTestInfoSelected(){
        if(testInfoCheckBox.isSelected()){
            defaultLineChart.setAnimated(true);
            updateTestInformation();

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

            ObservableList<XYChart.Data<Number,Number>> tSEData = testStdErrorSeries.getData();
            for(int i=0;i<dist.getNumberOfPoints();i++){
                tSEData.get(i).setYValue(1/Math.sqrt(tinfo[i]));
            }

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
    private void handleSaveChartAsPNG() {
        WritableImage image = defaultLineChart.snapshot(new SnapshotParameters(), null);

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Image");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("PNG", "*.png"),
                new FileChooser.ExtensionFilter("JPG", "*.jpg")
        );

        File file = fileChooser.showSaveDialog(defaultLineChart.getScene().getWindow());

        //Save file as PNG if file type is not specified
        if (null != file) {
            String fileName = file.getAbsolutePath();
            if (!fileName.toLowerCase().endsWith(".png") && !fileName.toLowerCase().endsWith(".jpg")) {
                file = new File(file.getAbsolutePath() + ".png");
            }

            fileName = file.getAbsolutePath();
            String suffix = fileName.substring(fileName.lastIndexOf(".") + 1, fileName.length()).trim().toLowerCase();
            if (!suffix.equals("png") && !suffix.equals("jpg")) {
                suffix = "png";
            }

            try {

                if (suffix.equals("jpg")) {
                    BufferedImage image2 = SwingFXUtils.fromFXImage(image, null);
                    // Remove alpha-channel from buffered image: To prevent rendering in pink tones (bug in Javafx)
                    BufferedImage imageRGB = new BufferedImage(image2.getWidth(), image2.getHeight(), BufferedImage.OPAQUE);
                    Graphics2D graphics = imageRGB.createGraphics();
                    graphics.drawImage(image2, 0, 0, null);
                    ImageIO.write(imageRGB, suffix, file);
                    graphics.dispose();
                } else {
                    ImageIO.write(SwingFXUtils.fromFXImage(image, null), suffix, file);
                }


            } catch (IOException ex) {
                ex.printStackTrace();
            }

        }
    }

    @FXML
    private void handleAboutMenuSelection(){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("About");
        alert.setHeaderText("IRT Illustrator");

        String aboutText = "IRT Illustrator " + VERSION + "\n" +
                "Built on " + BUILD_DATE + "\n" +
                "Copyright (c) 2017 J. Patrick Meyer.\n" +
                "All rights reserved.\n" +
                "www.ItemAnalysis.com\n\n" +
                "Icon made by Prosymbols from www.flaticon.com";

        alert.setContentText(aboutText);
        alert.show();

    }

    @FXML
    private void handleShowICC(){
        if(iccCheckBox.isSelected()){
            defaultLineChart.setAnimated(true);

            for(XYChart.Series s : allItemSeries){
                defaultLineChart.getData().add(s);
            }

            if(!tccCheckBox.isSelected() || !stdErrorTextCheckBox.isSelected()){
                defaultYAxis.setLabel("Probability");
            }
        }else{
            for(int i=0;i<defaultLineChart.getData().size();i++){
                for(XYChart.Series s : allItemSeries){
                    defaultLineChart.getData().remove(s);
                }
            }
            defaultYAxis.setLabel("Value");
        }
    }

    @FXML
    private void handleShowItemInfo(){
        if(itemInfoCheckBox.isSelected()){
            defaultLineChart.setAnimated(true);

            for(XYChart.Series s : allItemInfoSeries){
                defaultLineChart.getData().add(s);
            }
            defaultYAxis.setLabel("Value");
        }else{
            for(int i=0;i<defaultLineChart.getData().size();i++){
                for(XYChart.Series s : allItemInfoSeries){
                    defaultLineChart.getData().remove(s);
                }
            }
            if(iccCheckBox.isSelected() && !(tccCheckBox.isSelected() || stdErrorTextCheckBox.isSelected())){
                defaultYAxis.setLabel("Probability");
            }else{
                defaultYAxis.setLabel("Value");
            }
        }
    }

    @FXML
    private void handleShowItemStdError(){
        if(stdErrorItemCheckBox.isSelected()){
            defaultLineChart.setAnimated(true);

            for(XYChart.Series s : allItemStdErrorSeries){
                defaultLineChart.getData().add(s);
            }
            defaultYAxis.setLabel("Value");
        }else{
            for(int i=0;i<defaultLineChart.getData().size();i++){
                for(XYChart.Series s : allItemStdErrorSeries){
                    defaultLineChart.getData().remove(s);
                }
            }
            if(iccCheckBox.isSelected() && !(tccCheckBox.isSelected() || stdErrorTextCheckBox.isSelected())){
                defaultYAxis.setLabel("Probability");
            }else{
                defaultYAxis.setLabel("Value");
            }
        }
    }

}
