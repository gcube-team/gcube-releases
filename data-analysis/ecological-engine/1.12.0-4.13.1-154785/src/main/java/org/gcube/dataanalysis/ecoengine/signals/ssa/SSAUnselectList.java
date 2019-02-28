package org.gcube.dataanalysis.ecoengine.signals.ssa;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class SSAUnselectList{

    public int getIndex() {
        return index;
    }

    private int index;
    private double percent;

    public SSAUnselectList(int index, double percent) {
        this.index = index;
        this.percent = percent;
    }

    public String toString() {
        String value = "";
        BigDecimal per = new BigDecimal(percent);
        double num = per.setScale(4, RoundingMode.HALF_EVEN).doubleValue();
        value = value + (index + 1) + "(" + num + "%)";
        return value;
    }
}