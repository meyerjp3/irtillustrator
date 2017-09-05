package com.itemanalysis.irtillustrator;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;

public class StepParameter {

    public SimpleDoubleProperty stepValue = new SimpleDoubleProperty();

    public StepParameter(double value){
        this.setStepValue(value);
    }

    public void setStepValue(Double stepValue){
        stepValueProperty().setValue(stepValue);
    }

    public Double getStepValue(){
        return stepValueProperty().getValue();
    }

    public DoubleProperty stepValueProperty(){
        if(null==stepValue) stepValue = new SimpleDoubleProperty(this, "StepParameter");
        return stepValue;
    }

    @Override
    public String toString(){
        return ((Double)stepValue.get()).toString();
    }

}
