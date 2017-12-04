package org.gcube.dataanalysis.ecoengine.signals.ssa;

import java.util.ArrayList;
import java.util.List;

import org.jfree.chart.ChartPanel;

import Jama.Matrix;

public class SSADataset {

    private List<Double> timeSeries;	//the original time series
    private int L;						//length of window
    private double inclosureMatrix [][]; //matrix attachment
    private Matrix X [];			//Basic Matrix singular decomposition
    private Matrix groupX [];    //the resulting matrix for each of the groups
    private Matrix V [];         //the main components of singular decomposition
    private List<Double> reconstructionList; 
    private List<Double> forecastList; 
    
    private double[] reconstructedSignal; 
    private double[] forecastSignal; 
    
	private List <Double> SMA;			//moving averages
    private List <Double> cov;			//averaging the diagonal covariance
    private List <Double> eigenValueList;//eigenvalues
    private List <Double> lgEigenValue;  //log of the eigenvalues
    private List <Double> sqrtEigenValue;//roots of eigenvalues
    private List eigenVectors;			//eigenvectors
    private List <Double> percentList;   //capital/interest/numbers
    private List<Double> accruePercentList; //accrued interest eigenvalues
    private double percThreshold=1;
    
	/*
     * for a cascading display InternalFrame
     */
    private int nextFrameX;
    private int nextFrameY;
    private int frameDistance;
    private int eigenFuncPage;
    private int mainCompPage;
    private List<ChartPanel> eigenVecListCharts;
    private List<ChartPanel> mainCompListCharts;

    public SSADataset() {
        timeSeries = new ArrayList<Double>();
        L = 2;
    }

    public List getEigenVectors() {
        return eigenVectors;
    }

    public void setEigenVectors(List eigenVectors) {
        this.eigenVectors = eigenVectors;
    }

    public Matrix[] getV() {
        return V;
    }

    public void setV(Matrix[] V) {
        this.V = V;
    }

    public List<Double> getTimeSeries() {
        return timeSeries;
    }

    public void setTimeSeries(List<Double> timeSeries) {
        this.timeSeries = timeSeries;
    }

    public int getL() {
        return L;
    }

    public void setL(int L) {
        this.L = L;
    }

    public double[][] getInclosureMatrix() {
        return inclosureMatrix;
    }

    public void setInclosureMatrix(double matrix[][]) {
        inclosureMatrix = matrix;
    }

    public Matrix[] getX() {
        return X;
    }

    public void setX(Matrix X[]) {
        this.X = X;
    }

    public List<Double> getReconstructionList() {
        return reconstructionList;
    }

    public void setReconstructionList(List<Double> reconstructionList) {
        this.reconstructionList = reconstructionList;
    }

    public List<Double> getSMA() {
        return SMA;
    }

    public void setSMA(List<Double> SMA) {
        this.SMA = SMA;
    }

    public List<Double> getCov() {
        return cov;
    }

    public void setCov(List<Double> cov) {
        this.cov = cov;
    }

    public void setLgEigenValue(List<Double> lgEigenValue) {
        this.lgEigenValue = lgEigenValue;
    }

    public List<Double> getLgEigenValue() {
        return lgEigenValue;
    }

    public void setSqrtEigenValue(List<Double> sqrtEigenValue) {
        this.sqrtEigenValue = sqrtEigenValue;
    }

    public List<Double> getSqrtEigenValue() {
        return sqrtEigenValue;
    }

    public List<Double> getEigenValueList() {
        return eigenValueList;
    }

    public void setEigenValueList(List<Double> eigenValueList) {
        this.eigenValueList = eigenValueList;
    }

    public List<Double> getAccruePercentList() {
        return accruePercentList;
    }

    public void setAccruePercentList(List<Double> accruePercentList) {
        this.accruePercentList = accruePercentList;
    }

    public List<Double> getPercentList() {
        return percentList;
    }

    public void setPercentList(List<Double> percentList) {
        this.percentList = percentList;
    }

    public void setFrameDistance(int frameDistance) {
        this.frameDistance = frameDistance;
    }

    public void setNextFrameX(int nextFrameX) {
        this.nextFrameX = nextFrameX;
    }

    public void setNextFrameY(int nextFrameY) {
        this.nextFrameY = nextFrameY;
    }

    public int getFrameDistance() {
        return frameDistance;
    }

    public int getNextFrameX() {
        return nextFrameX;
    }

    public int getNextFrameY() {
        return nextFrameY;
    }

    public int getEigenFuncPage() {
        return eigenFuncPage;
    }

    public void setEigenFuncPage(int eigenFuncPage) {
        this.eigenFuncPage = eigenFuncPage;
    }

    public List<ChartPanel> getEigenVecListCharts() {
        return eigenVecListCharts;
    }

    public void setEigenVecListCharts(List<ChartPanel> eigenVecListCharts) {
        this.eigenVecListCharts = eigenVecListCharts;
    }

    public List<ChartPanel> getMainCompListCharts() {
        return mainCompListCharts;
    }

    public void setMainCompListCharts(List<ChartPanel> mainCompListCharts) {
        this.mainCompListCharts = mainCompListCharts;
    }

    public int getMainCompPage() {
        return mainCompPage;
    }

    public void setMainCompPage(int mainCompPage) {
        this.mainCompPage = mainCompPage;
    }

    public Matrix[] getGroupX() {
        return groupX;
    }

    public void setGroupX(Matrix[] groupX) {
        this.groupX = groupX;
    }
    
    public double getPercThreshold() {
		return percThreshold;
	}

	public void setPercThreshold(double percThreshold) {
		this.percThreshold = percThreshold;
	}

    public List<Double> getForecastList() {
		return forecastList;
	}

	public void setForecastList(List<Double> forecastList) {
		this.forecastList = forecastList;
	}

	public double[] getReconstructedSignal() {
		return reconstructedSignal;
	}

	public void setReconstructedSignal(double[] reconstructedSignal) {
		this.reconstructedSignal = reconstructedSignal;
	}

	public double[] getForecastSignal() {
		return forecastSignal;
	}

	public void setForecastSignal(double[] forecastSignal) {
		this.forecastSignal = forecastSignal;
	}

	
}
