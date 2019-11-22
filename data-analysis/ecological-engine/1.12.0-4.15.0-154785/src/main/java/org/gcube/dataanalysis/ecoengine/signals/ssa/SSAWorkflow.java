package org.gcube.dataanalysis.ecoengine.signals.ssa;

import java.util.ArrayList;
import java.util.List;

import org.gcube.contentmanagement.lexicalmatcher.utils.AnalysisLogger;
import org.gcube.dataanalysis.ecoengine.signals.SignalProcessing;

public class SSAWorkflow {

	public static SSADataset applyCompleteWorkflow(List<Double> timeseries, int analysisWindowLength, float eigenValuesPercentageThreshold, int nPointsToForecast, boolean reportReconstructedSignal){
		
		SSADataset data = new SSADataset();
		data.setTimeSeries(timeseries);
		data.setL(analysisWindowLength);
		data.setPercThreshold(eigenValuesPercentageThreshold);
		// step 1: Embedding of time series in a LxK matrix
		// L = the length of the window
		// K = timeseries.size() - L + 1 the number of vectors of attachments
		SingularSpectrumAnalysis.inclosure(data);
		// apply SVD and get a number of eigenvectors equal to the rank of the
		// embedding matrix
		System.gc();
		SingularSpectrumAnalysis.singularDecomposition(data);
		// calculate averages for each frame of the time series
		System.gc();
		SingularSpectrumAnalysis.setMovingAverage(data);
		// Diagonal averaging of the covariance matrix
		System.gc();
		SingularSpectrumAnalysis.averagedCovariance(data);
		// store the logs and the sqrts of the eigenvalues
		System.gc();
		SingularSpectrumAnalysis.functionEigenValue(data);
		//build groups of indices
		List<SSAGroupList> groupsModel = new ArrayList<SSAGroupList>();
		List<SSAUnselectList> groups = new ArrayList<SSAUnselectList>();
		AnalysisLogger.getLogger().debug("Listing All the Eigenvalues");
		for (int i = 0; i < data.getPercentList().size(); i++) {
			double currentperc = data.getPercentList().get(i); 
			AnalysisLogger.getLogger().debug("Eigenvalue: Number: "+i+" Percentage: "+currentperc);
			if (currentperc>eigenValuesPercentageThreshold)
				groups.add(new SSAUnselectList(i, currentperc));
		}
		
		groupsModel.add(new SSAGroupList(groups));
		//build a matrix which is the sum of the groups matrices
		SingularSpectrumAnalysis.grouping(groupsModel, data);
		// restoration of the time series (the diagonal averaging)
		SingularSpectrumAnalysis.diagonalAveraging(data);
		double[] signal = new double[data.getTimeSeries().size()];
		for(int i = 0; i < data.getTimeSeries().size(); i++) signal[i] = data.getTimeSeries().get(i);
		
		SingularSpectrumAnalysis.forecast(data,nPointsToForecast,reportReconstructedSignal);
		
		double[] rsignal = new double[data.getForecastList().size()];
		for(int i = 0; i < data.getForecastList().size(); i++) rsignal[i] = data.getForecastList().get(i);
		
		data.setReconstructedSignal(rsignal);
		data.setForecastSignal(rsignal);
		
//		SignalProcessing.displaySignalWithGenericTime(signal, 0, 1, "signal");
//		SignalProcessing.displaySignalWithGenericTime(rsignal, 0, 1, "reconstructed signal");
		
		AnalysisLogger.getLogger().debug("SSA workflow DONE");
		return data;
	}

}
