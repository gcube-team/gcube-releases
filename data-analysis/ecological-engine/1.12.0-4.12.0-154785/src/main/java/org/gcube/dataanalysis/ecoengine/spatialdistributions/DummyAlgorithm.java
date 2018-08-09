package org.gcube.dataanalysis.ecoengine.spatialdistributions;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.gcube.contentmanagement.lexicalmatcher.utils.AnalysisLogger;
import org.gcube.dataanalysis.ecoengine.configuration.ALG_PROPS;
import org.gcube.dataanalysis.ecoengine.configuration.AlgorithmConfiguration;
import org.gcube.dataanalysis.ecoengine.datatypes.PrimitiveType;
import org.gcube.dataanalysis.ecoengine.datatypes.StatisticalType;
import org.gcube.dataanalysis.ecoengine.datatypes.enumtypes.PrimitiveTypes;
import org.gcube.dataanalysis.ecoengine.interfaces.SpatialProbabilityDistributionGeneric;

public class DummyAlgorithm implements SpatialProbabilityDistributionGeneric{

	List<String> randomElements;
	String persistence;
	private String filename;
	static String persistedFilePrefix = "dummyfile";
	
	public static void main (String[] args){
			String s = toString(330.6499f);
			
			System.out.println(s);
			System.out.println(fromString(s));
	}

	private static String toString(float number){
		String s = ""+number;
		int m = s.length();
		String res = "";
		for (int i=0;i<m;i++){
			int k = 0;
			if (s.charAt(i)=='.')
				k =  (int)s.charAt(i);
			else
				k = Integer.parseInt(""+s.charAt(i));
			res+=(char) (k+65);
		}
		return res;
	}
	
	
	private static float fromString(String alphanumeric){
		
		int m = alphanumeric.length();
		String res = "";
		for (int i=0;i<m;i++){
			int k = (int)alphanumeric.charAt(i) - 65;
			
			if (k == (int)'.')
				res+=".";
			else	
				res+= k;
		}
		return Float.parseFloat(res);
	}
	
	@Override
	public void init(AlgorithmConfiguration config) {
		AnalysisLogger.getLogger().trace("Dummy INIT");
		randomElements = new ArrayList<String>();
		for (int i=0;i<170000;i++)
		{
			randomElements.add(""+(100*Math.random()));
		}
		persistence = config.getPersistencePath();
	}

	@Override
	public String getMainInfoType() {
		return String.class.getName();
	}

	@Override
	public String getGeographicalInfoType() {
		return String.class.getName();
	}

	@Override
	public List<Object> getMainInfoObjects() {
		
		List<Object> randomElements = new ArrayList<Object>();
		for (int i=0;i<20;i++)
		{
			randomElements.add(toString((float)(100f*Math.random())));
		}
		return randomElements;
	}

	@Override
	public List<Object> getGeographicalInfoObjects() {
		AnalysisLogger.getLogger().trace("Dummy TAKING RANDOMS");
		List<Object> randomElements = new ArrayList<Object>();
		for (int i=0;i<170000;i++)
		{
			randomElements.add(""+(100*Math.random()));
		}
		return randomElements;
	}

	@Override
	public float calcProb(Object mainInfo, Object area) {
//		AnalysisLogger.getLogger().debug("Calculation Probability");
		Float f1 = fromString((String) mainInfo);
		Float f2 = Float.valueOf((String) area);
		return (float) 100f*f1*f2;
	}

	@Override
	public void singleStepPreprocess(Object mainInfo, Object area) {
		AnalysisLogger.getLogger().trace("Dummy SINGLE PREPROCESSING Step");
	}

	@Override
	public void singleStepPostprocess(Object mainInfo, Object allAreasInformation) {
		AnalysisLogger.getLogger().trace("Dummy SINGLE POSTPROCESSING Step");
	}

	@Override
	public void postProcess() {
		AnalysisLogger.getLogger().trace("Dummy POSTPROCESS");
	}

	@Override
	public void storeDistribution(Map<Object, Map<Object, Float>> distribution) {
		ObjectOutputStream outputStream = null;
        
        try {
        	int ysize = 0;
        	for (Object s:distribution.keySet()){
        		ysize = distribution.get(s).size();
        		break;
        	}
        	AnalysisLogger.getLogger().debug("Dummy overall dimension of the distribution: "+distribution.size()+" X "+ysize);
            //Construct the LineNumberReader object
        	filename = persistence+persistedFilePrefix+UUID.randomUUID();

        	AnalysisLogger.getLogger().debug("Dummy Storing in "+filename);
            outputStream = new ObjectOutputStream(new FileOutputStream(persistence+persistedFilePrefix+"_"+UUID.randomUUID()));
            outputStream.writeObject(distribution);            
            AnalysisLogger.getLogger().debug("Dummy Stored");
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            try {
                if (outputStream != null) {
                    outputStream.flush();
                    outputStream.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
	}

	@Override
	public float getInternalStatus() {
		return 100f;
	}

	@Override
	public String getMainInfoID(Object mainInfo) {
		return (String)mainInfo;
	}

	@Override
	public String getGeographicalID(Object geoInfo) {
		return (String)geoInfo;
	}

	@Override
	public ALG_PROPS[] getProperties() {
		ALG_PROPS [] p = {ALG_PROPS.PHENOMENON_VS_GEOINFO};
		return p;
	}

	@Override
	public String getName() {
		return "DUMMY";
	}

	@Override
	public String getDescription() {

		return "a testing algorithm for statistical service work performances - calculates a random probability distribution and stores on a file";
	}

	@Override
	public List<StatisticalType> getInputParameters() {
		
		return null;
	}
	
	@Override
	public StatisticalType getOutput() {
		PrimitiveType p = new PrimitiveType(File.class.getName(), new File(filename), PrimitiveTypes.FILE, "DummyDistribution","Dummy Distribution File");
		return p;
	}
}
