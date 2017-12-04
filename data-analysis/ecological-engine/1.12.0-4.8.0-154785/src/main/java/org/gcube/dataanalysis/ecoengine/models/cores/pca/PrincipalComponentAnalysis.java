package org.gcube.dataanalysis.ecoengine.models.cores.pca;

import org.gcube.contentmanagement.graphtools.data.BigSamplesTable;
import org.gcube.contentmanagement.lexicalmatcher.utils.AnalysisLogger;
import org.gcube.dataanalysis.ecoengine.configuration.AlgorithmConfiguration;
import org.gcube.dataanalysis.ecoengine.utils.Operations;
import org.gcube.dataanalysis.ecoengine.utils.Transformations;

import com.rapidminer.example.ExampleSet;
import com.rapidminer.operator.IOContainer;
import com.rapidminer.operator.IOObject;
import com.rapidminer.operator.ModelApplier;
import com.rapidminer.operator.features.transformation.PCA;
import com.rapidminer.operator.features.transformation.PCAModel;
import com.rapidminer.tools.OperatorService;


public class PrincipalComponentAnalysis {

	
	public static void main(String[] args) throws Exception{
		
		
		AlgorithmConfiguration config = new AlgorithmConfiguration();
		config.setConfigPath("./cfg/");
		config.setPersistencePath("./cfg/");
		config.setNumberOfResources(1);
		config.setAgent("QUALITY_ANALYSIS");
		
		AnalysisLogger.setLogger(config.getConfigPath()+AlgorithmConfiguration.defaultLoggerFile);
		config.initRapidMiner();
		
		PrincipalComponentAnalysis pca = new PrincipalComponentAnalysis();
		
		/*random example
		int m= 20;
		int n = 10; 
		
		double values[][] = new double[m][n];
		
		for (int i=0;i<m;i++){
			for (int j=0;j<n;j++)
				if ((i==0)||(i==1))
					values[i][j]=1d;
				else
					values[i][j]=Math.random();
		}
		*/
		int m= 5;
		int n = 5; 
		double values[][] = new double[m][n];
		double val2[] = {1.000d,0.451d,0.511d,0.197d,0.162d};
		double val1[] = {0.451d,1.000d,0.445d,0.252d,0.238d};
		double val3[] = {0.511d,0.445d,1.000d,0.301d,0.227d};
		double val5[] = {0.197d,0.252d,0.301d,1.000d,0.620d};
		double val4[] = {0.162d,0.238d,0.227d,0.620d,1.000d};
		values[0] = val1;
		values[1] = val2;
		values[2] = val3;
		values[3] = val4;
		values[4] = val5;
		
		//calculates the PCA
		pca.calcPCA(values);
		PCAModel model = pca.getModel();
		
		int components = model.getMaximumNumberOfComponents();
		for (int i=0;i<components;i++){
			AnalysisLogger.getLogger().debug((i+1)+"->"+model.getEigenvalue(i));
			double[] eigen = model.getEigenvector(i);
			for (int j=0;j<eigen.length;j++)
				System.out.print(eigen[j]+" ");
			
			System.out.println();
		}
		
		double [][] componentsMatrix = pca.getComponentsMatrix(values); 
		
		System.exit(0);
	}
	
	public void init(AlgorithmConfiguration config){
		config.initRapidMiner();
	}
	
	
	PCAModel innermodel;
	int numberOfComponents;
	
	public PCAModel getModel(){
		return innermodel;
	}
	
	public double[] getEigenvector (int index){
		
		return innermodel.getEigenvector(index);
	}
	
	public double getEigenvalue (int index){
		
		return innermodel.getEigenvalue(index);
	}
	
	
	public double [] getEigenvalues (){
		double [] values = new double[numberOfComponents];
		for (int i=0;i<numberOfComponents;i++){
			values[i] = getEigenvalue(i);
		}

		return values;
	}

	public double [] getNormalizedEigenvalues (){
		double [] values = new double[numberOfComponents];
		
		for (int i=0;i<numberOfComponents;i++){
			values[i] = getEigenvalue(i);
		}

		double sumEigen = Operations.sumVector(values);
		
		for (int i=0;i<numberOfComponents;i++){
			values[i] = values[i]/sumEigen;
		}
		
		return values;
	}

	
	public double [] getInverseEigenvalues (){
		double [] values = new double[numberOfComponents];
		for (int i=0;i<numberOfComponents;i++){
			values[i] = 1d/getEigenvalue(i);
		}
		return values;
	}

	public double [] getInverseNormalizedEigenvalues (){
		double [] values = new double[numberOfComponents];
		double[] weightedEigens = getNormalizedEigenvalues();
		for (int i=0;i<numberOfComponents;i++){
			values[i] = 1d/weightedEigens[i];
		}
		return values;
	}
	
	public double[][] getComponentsMatrix(double[][] vectors) throws Exception{
		
		int nsamples=vectors.length;
		double[][] components = new double[nsamples][];
		
		for (int i=0;i<nsamples;i++){
			components[i] = getComponents(vectors[i]);
		}
		
		return components;
	}

	
	public double[] getComponents(double[] vector) throws Exception{
		
		double [] components = new double[numberOfComponents];
		for (int i=0;i<numberOfComponents;i++){
			components[i] = Operations.scalarProduct(vector, getEigenvector(i));
		}
		return components;
	}
	
	protected double[][] getPCA(double[][] sampleVectors) throws Exception{
		
		ExampleSet set = Transformations.matrix2ExampleSet(sampleVectors);
		ExampleSet outset = innermodel.apply(set);
		return Transformations.exampleSet2Matrix(outset);
		
	}
	
	public void calcPCA(double [][] sampleVectors) throws Exception{
		
		AnalysisLogger.getLogger().debug("STARTING PCA COMPUTATION");
		
		PCA pca = (PCA) OperatorService.createOperator("PCA");
		pca.setParameter("variance_threshold", "0.95");
		pca.setParameter("dimensionality_reduction", "keep variance");
		pca.setParameter("number_of_components", "-1");
		
		ExampleSet set = Transformations.matrix2ExampleSet(sampleVectors);
		
		IOContainer innerInput = new IOContainer(set);
		IOContainer output = pca.apply(innerInput);
		
		IOObject[] outputvector = output.getIOObjects();
		
//		ExampleSet setOut = (ExampleSet) outputvector[0];
		
		innermodel = (PCAModel) outputvector[1];
		numberOfComponents = innermodel.getMaximumNumberOfComponents();
		
		AnalysisLogger.getLogger().debug("MODEL APPLIED");
		
	}
	
	
}
