package com.cookandroid.healthscanner.ui.food.foodgraph;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.text.DecimalFormat;

public class ValueFormatter implements IValueFormatter {
    private DecimalFormat decimalFormat;
    public ValueFormatter(){
        decimalFormat = new DecimalFormat("#####.0");
    }
    @Override
    public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
        return decimalFormat.format(value);
    }
}
