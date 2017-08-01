package org.gcube.contentmanagement.graphtools.tests.old;


import org.gcube.contentmanagement.graphtools.data.BigSamplesTable;

import com.rapidminer.RapidMiner;
import com.rapidminer.example.ExampleSet;
import com.rapidminer.operator.preprocessing.sampling.AbsoluteSampling;
import com.rapidminer.tools.OperatorService;
import com.rapidminer.tools.math.MathFunctions;

public class ExampleMathFunctions {

	
	public static void main(String[] args) throws Exception{
		
		double variance = MathFunctions.variance(new double[] { 0.1, 0.1, 0.0, -0.1 }, Double.NEGATIVE_INFINITY);
		double covariance = MathFunctions.correlation(new double[] { 0.1, 0.2, -0.3, 0.0 }, new double[] { 0.0, 0.1, 0.1, -0.1 });
		double meanc = mean(new double[] { 0.1, 0.1, 0.0, -0.1 });
		
//		String pluginDirString = new File("C:/Users/coro/Desktop/WorkFolder/Workspace/RapidMinerPlugins/TextProcessing/Vega/lib/").getAbsolutePath();
//	    System.setProperty(RapidMiner.PROPERTY_RAPIDMINER_INIT_PLUGINS_LOCATION, pluginDirString);
		System.setProperty("rapidminer.init.operators","C:/Users/coro/Desktop/WorkFolder/Workspace/RapidMiner_Wasat/resources/operators.xml");
		
		RapidMiner.init ();
		
		BigSamplesTable bst = new BigSamplesTable();
		bst.addSampleRow("prova 1", 10, 12,13,14,15);
		bst.addSampleRow("prova 2", 20, 15,14,15);
		bst.addSampleRow("prova 3", 30, 11,110,150);
		System.out.println(bst.toString());
		
//		OperatorService.createOperator(ExampleSource.class);
//		as.apply(bst.generateExampleSet());
		
//		OperatorChain wvtoolOperator = (OperatorChain) OperatorService.createOperator(SingleTextInput.class);
		
		
//		Learner learner = (Learner)OperatorService .createOperator("J48");
		
		AbsoluteSampling as = (AbsoluteSampling)OperatorService .createOperator("AbsoluteSampling");
//		SimpleSampling ss = (SimpleSampling)OperatorService .createOperator("Sampling");
//		ss.setParameter("sample_ratio", "0.8");
		as.setParameter("sample_size", "2");
		as.setParameter("local_random_seed", "-1");
		
		ExampleSet exampleSet = bst.generateExampleSet();
		System.out.println(exampleSet.toResultString());
		
		
//		ExampleSet exampleSetOut = ss.apply(exampleSet);
		ExampleSet exampleSetOut = as.apply(exampleSet);
		System.out.println(exampleSetOut.toResultString());
		
		BigSamplesTable bstOut = new BigSamplesTable();
		bstOut.generateSampleTable(exampleSetOut);
		System.out.println(bstOut.toString());
		
//		as.apply(es);
		
		
		System.out.println("variance "+variance+" covariance "+covariance+" mean "+meanc);
		
		
		
	}
	
	
	//================================================= mean
	public static double mean(double[] p) {
	    double sum = 0;  // sum of all the elements
	    for (int i=0; i<p.length; i++) {
	        sum += p[i];
	    }
	    return sum / p.length;
	}//end method mean

	
}
