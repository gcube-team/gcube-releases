package org.gcube.dataanalysis.ecoengine.signals.ssa;

import Jama.EigenvalueDecomposition;
import Jama.Matrix;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import javax.swing.DefaultListModel;

import org.gcube.contentmanagement.lexicalmatcher.utils.AnalysisLogger;

public class SingularSpectrumAnalysis {

    /**
     * translation of the original time series into a sequence of multidimensional
     * vectors
     *
     * @param data data for analysis
     */
    public static void inclosure(SSADataset data) {
        int L = data.getL(); //the length of the window
        int K = data.getTimeSeries().size() - L + 1; //the number of vectors of attachments
        double inclosureMatrix[][] = new double[L][K]; //Matrix Orbital
        //form attachment vectors
        for (int i = 1; i <= K; i++) {
            int num = 0;
            for (int j = i - 1; j <= i + L - 2; j++) {
                inclosureMatrix[num][i - 1] = data.getTimeSeries().get(j);
                num++;
            }
        }
        data.setInclosureMatrix(inclosureMatrix);
    }

    /**
     * singular value decomposition
     *
     * @param data data for analysis
     */
    public static void singularDecomposition(SSADataset data) {
        double inclosureMatrix[][] = data.getInclosureMatrix();
        double transp[][] = transpositionMatrix(inclosureMatrix);
        Matrix S = new Matrix(inclosureMatrix).times(new Matrix(transp));
        //int d = new Matrix(inclosureMatrix).rank(); //rank of matrix attachment
        EigenvalueDecomposition decomposition = new EigenvalueDecomposition(S);
        Matrix eigenvalue = decomposition.getD();   //matrix with eigenvalues
        Matrix eigenvec = decomposition.getV();     //the matrix of eigenvectors
        List<Double> eigenvalueList = new ArrayList<Double>();
        //form the set of eigenvalues on the diagonal
        for (int i = 0; i < eigenvalue.getRowDimension(); i++) {
            for (int j = 0; j < eigenvalue.getRowDimension(); j++) {
                if (i == j) {
                    eigenvalueList.add(eigenvalue.get(i, j));
                }
            }
        }
        Comparator comparator = Collections.reverseOrder();
        /*
         * own values must be in descending order, so
         * We sort them in reverse order (initially ascending values
         * order)
         */
        Collections.sort(eigenvalueList, comparator);
        data.setEigenValueList(eigenvalueList);
        double sumValueList = 0;
        List<Double> percentList;
        List<Double> accruePercentList;
        for (int i = 0; i < data.getEigenValueList().size(); i++) {
            sumValueList = sumValueList + data.getEigenValueList().get(i);
        }
        //a percent of eigenvalues and accrued interest
        percentList = new ArrayList<Double>();
        accruePercentList = new ArrayList<Double>();
        double accruePercent = 0;
        for (int i = 0; i < data.getEigenValueList().size(); i++) {
            percentList.add(data.getEigenValueList().get(i) / sumValueList * 100);
            accruePercent += percentList.get(i);
            accruePercentList.add(accruePercent);
        }
        data.setAccruePercentList(accruePercentList);
        data.setPercentList(percentList);

        int size = eigenvec.getColumnDimension();
        Matrix V[] = new Matrix[size];
        Matrix U[] = new Matrix[size];
        Matrix X[] = new Matrix[size]; //Elementary matrix singular value decomposition
        ArrayList listSeries = new ArrayList();
        for (int j = 0; j < eigenvec.getColumnDimension(); j++) {
            double uVec[][] = new double[size][1];
            ArrayList series = new ArrayList();
            for (int k = 0; k < eigenvec.getRowDimension(); k++) {
                /*
                 * vectors must comply with its own number (!), so
                 * start with the last native vector
                 */
                uVec[k][0] = eigenvec.get(k, eigenvec.getColumnDimension() - j - 1);
                series.add(uVec[k][0]);
            }
            listSeries.add(series);
            U[j] = new Matrix(uVec);
            V[j] = new Matrix(transp).times(U[j]);
        }
        data.setEigenVectors(listSeries);
        for (int i = 0; i < V.length; i++) {
            for (int j = 0; j < V[i].getRowDimension(); j++) {
                for (int k = 0; k < V[i].getColumnDimension(); k++) {
                    double val = V[i].get(j, k) / Math.sqrt(eigenvalueList.get(i));
                    V[i].set(j, k, val);
                }
            }
        }
        data.setV(V);
        for (int i = 0; i < X.length; i++) {
            X[i] = U[i].times(V[i].transpose());
            for (int j = 0; j < X[i].getRowDimension(); j++) {
                for (int k = 0; k < X[i].getColumnDimension(); k++) {
                    double val = X[i].get(j, k) * Math.sqrt(eigenvalueList.get(i));
                    X[i].set(j, k, val);
                }
            }
        }
        data.setX(X);
    }

    /**
     * restoration of the time series (group stage)
     *
     * a JList model @param (group list)
     * @param data data for analysis
     */
    public static void grouping(List<SSAGroupList> model, SSADataset data) {
        Matrix grouX[] = new Matrix[model.size()];
        for (int i = 0; i < model.size(); i++) {
            SSAGroupList obj = (SSAGroupList) model.get(i);
            for (int j = 0; j < obj.getGroups().size(); j++) {
                SSAUnselectList unselect = (SSAUnselectList) obj.getGroups().get(j);
                if (j == 0) {
                    grouX[i] = data.getX()[unselect.getIndex()];
                } else {
                    grouX[i] = grouX[i].plus(data.getX()[unselect.getIndex()]);
                }
            }
        }
        data.setGroupX(grouX);
    }

    /**
     * restoration of the time series (the stage diagonal averaging)
     *
     * @param data for analysis
     */
    public static void diagonalAveraging(SSADataset data) {
        int L;
        int K;
        int N;
        List<List> list = new ArrayList<List>();
        for (int i = 0; i < data.getGroupX().length; i++) {
            if (data.getGroupX()[i].getRowDimension() < data.getGroupX()[i].getColumnDimension()) {
                L = data.getGroupX()[i].getRowDimension();
                K = data.getGroupX()[i].getColumnDimension();
            } else {
                K = data.getGroupX()[i].getRowDimension();
                L = data.getGroupX()[i].getColumnDimension();
            }
            N = data.getGroupX()[i].getRowDimension() + data.getGroupX()[i].getColumnDimension() - 1;
            List series = new ArrayList();
            double element;
            for (int k = 0; k <= N - 1; k++) {
                element = 0;
                if (k >= 0 && k < L - 1) {
                    for (int m = 0; m <= k; m++) {
                        if (data.getGroupX()[i].getRowDimension() <= data.getGroupX()[i].getColumnDimension()) {
                            element += data.getGroupX()[i].get(m, k - m);
                        } else if (data.getGroupX()[i].getRowDimension() > data.getGroupX()[i].getColumnDimension()) {
                            element += data.getGroupX()[i].get(k - m, m);
                        }
                    }
                    element = element * (1.0 / (k + 1));
                    series.add(element);
                }
                if (k >= L - 1 && k < K - 1) {
                    for (int m = 0; m <= L - 2; m++) {
                        if (data.getGroupX()[i].getRowDimension() <= data.getGroupX()[i].getColumnDimension()) {
                            element += data.getGroupX()[i].get(m, k - m);
                        } else if (data.getGroupX()[i].getRowDimension() > data.getGroupX()[i].getColumnDimension()) {
                            element += data.getGroupX()[i].get(k - m, m);
                        }
                    }
                    element = element * (1.0 / L);
                    series.add(element);
                }
                if (k >= K - 1 && k < N) {
                    for (int m = k - K + 1; m <= N - K; m++) {
                        if (data.getGroupX()[i].getRowDimension() <= data.getGroupX()[i].getColumnDimension()) {
                            element += data.getGroupX()[i].get(m, k - m);
                        } else if (data.getGroupX()[i].getRowDimension() > data.getGroupX()[i].getColumnDimension()) {
                            element += data.getGroupX()[i].get(k - m, m);
                        }
                    }
                    element = element * (1.0 / (N - k));
                    series.add(element);
                }
            }
            list.add(series);
        }
        double sum;
        //We summarize the series and get the original number
        List<Double> reconstructionList = new ArrayList<Double>();
        for (int j = 0; j < list.get(0).size(); j++) {
            sum = 0;
            for (int i = 0; i < list.size(); i++) {
                sum += (Double) list.get(i).get(j);
            }
            reconstructionList.add(sum);
        }
        //added by Gianpaolo Coro
        /*
        double reconstructionratio = 1;
        double ratiosum = 0;
        int tssize = data.getTimeSeries().size();
        for (int j = 0; j < tssize ; j++) {
        	double ratio = data.getTimeSeries().get(j)/reconstructionList.get(j);
        	ratiosum=ratiosum+ratio;
        }
        
        reconstructionratio = ratiosum/tssize;
        System.out.println("Reconstruction ratio: "+reconstructionratio);
        for (int j = 0; j < tssize ; j++) {
        	reconstructionList.set(j,reconstructionratio*reconstructionList.get(j));
        }
        */
        data.setReconstructionList(reconstructionList);
    }

    /**
     * the transpose of a matrix
     *
     * the original matrix matrix @param
     * @return the resulting matrix
     */
    private static double[][] transpositionMatrix(double matrix[][]) {
    	AnalysisLogger.getLogger().debug("SSA->Building a matrix with dimensions: "+matrix[0].length+" X "+matrix.length);
        double transpMatrix[][] = new double[matrix[0].length][matrix.length];
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[i].length; j++) {
                transpMatrix[j][i] = matrix[i][j];
            }
        }
        return transpMatrix;
    }

    /**
     * formation of moving averages
     *
     * @param data data for analysis
     */
    public static void setMovingAverage(SSADataset data) {
        List<Double> SMA = new ArrayList<Double>();
        int m = data.getTimeSeries().size() - data.getL() + 1; //период осреднения
        for (int i = 0; i < data.getL(); i++) {
            double sum = 0;
            double avg = 0;
            for (int j = i; j < m + i; j++) {
                sum += data.getTimeSeries().get(j);
            }
            avg = sum / m;
            SMA.add(avg);
            data.setSMA(SMA);
        }
    }
    
    /**
     * the diagonal of the covariance matrix averaging * (on the side diagonal)
     * @param data data for analysis
     */
    public static void averagedCovariance(SSADataset data) {
        double avg;
        double K = data.getTimeSeries().size() - data.getL() + 1; //the number of vectors of attachments
        List<Double> covarianceList = new ArrayList<Double>();
        double transp[][] = transpositionMatrix(data.getInclosureMatrix());
        Matrix S = new Matrix(data.getInclosureMatrix()).times(new Matrix(transp));
        S = S.times(1.0 / K); //covariance matrix
        int size = S.getColumnDimension();
        int N = size + size - 1;
        int n;
        for (int k = 0; k < N; k++) {
            if ((k % 2) == 0) {
                if (k >= 0 && k < size) {
                    avg = 0;
                    n = 0;
                    for (int m = 0; m <= k; m++) {
                        avg += S.get(m, size - 1 - (k - m));
                        n++;
                    }
                    avg = avg / (n);
                    covarianceList.add(avg);
                }
                if (k >= size && k < N) {
                    avg = 0;
                    n = 0;
                    for (int m = k - size + 1; m <= N - size; m++) {
                        avg += S.get(m, size - 1 - (k - m));
                        n++;
                    }
                    avg = avg / (n);
                    covarianceList.add(avg);
                }
            }
        }
        data.setCov(covarianceList);
    }

    /**
     *formation of the functions eigenvalues
     * @param data data for analysis
     */
    public static void functionEigenValue(SSADataset data) {
        List<Double> lgList = new ArrayList<Double>();
        List<Double> sqrtList = new ArrayList<Double>();
        for (int i = 0; i < data.getEigenValueList().size(); i++) {
            lgList.add((Double) Math.log(data.getEigenValueList().get(i)));
            sqrtList.add(Math.sqrt(data.getEigenValueList().get(i)));
        }
        data.setLgEigenValue(lgList);
        data.setSqrtEigenValue(sqrtList);
    }
    
    /**
     * author Gianpaolo Coro
     * @param data
     */
    public static void forecast(SSADataset data, int nPointsToForecast, boolean reconstructedSignal){
    	if (nPointsToForecast==0){
    		data.setForecastList(data.getReconstructionList());
    		return;
    	}
//    	List eigenvectors = data.getEigenVectors().subList(0, 11);
    	int nTotalEigenV = data.getPercentList().size();
    	int bestEigenVectors = nTotalEigenV;
    	//find the best number of eigenvectors to use for the forecast
    	for (int i=0;i<nTotalEigenV;i++){
    		double currentperc = data.getPercentList().get(i);
    		if (currentperc<data.getPercThreshold()){
    			bestEigenVectors=i+1;
    			break;
    		}
    	}
    	
    	List eigenvectors = data.getEigenVectors().subList(0, bestEigenVectors);
    	int L = data.getL();
    	int lastcoordinate = L-1;
    	AnalysisLogger.getLogger().debug("SSA: value for L: "+L);
    	int nEigenVectors = eigenvectors.size();
    	AnalysisLogger.getLogger().debug("Number of Selected Eigenvectors For Reconstruction: "+nEigenVectors);
    	double[] p = new double[nEigenVectors];
    	for (int i = 0;i<nEigenVectors;i++){
    		p[i] = (Double)((List)eigenvectors.get(i)).get(lastcoordinate);
    	}
    	double[][] P = new double[nEigenVectors][L-1];    	
    	
    	for (int i = 0;i<nEigenVectors;i++){
    		List<Double> evec = (List)eigenvectors.get(i);
    		for (int j =0;j<(L-1);j++)
    			P[i][j] = evec.get(j); 
    	}
    	
    	double ni_sqr = 0d;
    	for (int i = 0;i<nEigenVectors;i++){
    		ni_sqr = ni_sqr+(p[i]*p[i]); 
    	}
    	
    	double [] R = new double[L-1];
    	
    	for (int j=0;j<L-1;j++){
    		double rj = 0d;
    		for (int i=0;i<nEigenVectors;i++){
    			rj = rj+(p[i]*P[i][j]);
    		}
//    		R[i] = (1d/(1d-ni_sqr))*ri;
    		R[j] = rj/(1-ni_sqr);
    	}
    	
    	int M = nPointsToForecast;
    	List<Double> y = new ArrayList<Double>();
    	int signalSize = data.getTimeSeries().size();
    	for (int j =0 ;j<(signalSize+M);j++){
    		if (j<signalSize){
    			if (reconstructedSignal)
    				y.add(j,data.getReconstructionList().get(j));
    			else
    				y.add(j,data.getTimeSeries().get(j));
    		}
    		else
    		{
    			double sumprec = 0;
    			for (int g=0;g<L-1;g++){
    				double ag = R[L-2-g];
    				double yj_g = y.get(j-g-1);
    				sumprec=sumprec+ag*yj_g;
    			}
    			y.add(j, sumprec);
//    			System.out.println("Forecast: "+y.get(j));
    			
    		}
    	}
    	
    	AnalysisLogger.getLogger().debug("Length of the original signal: "+signalSize+" Length of the reconstructed signal: "+y.size());
    	
    	data.setForecastList(y);
    	
    }
    
 
}
