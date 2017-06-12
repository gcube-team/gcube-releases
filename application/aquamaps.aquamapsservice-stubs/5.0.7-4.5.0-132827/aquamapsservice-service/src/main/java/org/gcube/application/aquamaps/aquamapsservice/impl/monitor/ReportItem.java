package org.gcube.application.aquamaps.aquamapsservice.impl.monitor;

public class ReportItem {
	private String valueName;
	private String time;
 private long actualValue;
 private long threshold;
 private long overcomesInLast10Hours;
 private long overcomesInLast24Hours;
 private long overcomesTotal;
 
public void setActualValue(long actualValue) {
	this.actualValue = actualValue;
}
public void setThreshold(long threshold) {
	this.threshold = threshold;
}
public void setOvercomesInLast10Hours(long overcomesInLast10Hours) {
	this.overcomesInLast10Hours = overcomesInLast10Hours;
}
public void setOvercomesInLast24Hours(long overcomesInLast24Hours) {
	this.overcomesInLast24Hours = overcomesInLast24Hours;
}
public void setOvercomesTotal(long overcomesTotal) {
	this.overcomesTotal = overcomesTotal;
}
public void setValueName(String valueName){
	this.valueName=valueName;
}
public void setTime(String time){
	this.time=time;
}
}
