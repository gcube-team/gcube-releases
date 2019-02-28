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

public class TestAlgorithm implements SpatialProbabilityDistributionGeneric{

	@Override
	public ALG_PROPS[] getProperties() {
		ALG_PROPS[] p = {ALG_PROPS.PHENOMENON_VS_GEOINFO};
		return p;
	}
	
	String pers;
	private String filename;
	@Override
	public void init(AlgorithmConfiguration config) {
		pers = config.getPersistencePath();
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
		List<Object> o = new ArrayList<Object>();
		o.add("pheno1");
		o.add("pheno2");
		return o;
	}

	@Override
	public List<Object> getGeographicalInfoObjects() {
		List<Object> o = new ArrayList<Object>();
		o.add("geo1");
		o.add("geo2");
		return o;
	}

	@Override
	public float calcProb(Object mainInfo, Object area) {
		String phen = (String) mainInfo;
		String geo = (String) area;
		System.out.println(phen+" vs "+geo);
		return 1;
	}

	@Override
	public void singleStepPreprocess(Object mainInfo, Object area) {
		
	}

	@Override
	public void singleStepPostprocess(Object mainInfo, Object allAreasInformation) {
		
	}

	@Override
	public void postProcess() {
		
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
        	AnalysisLogger.getLogger().debug("overall dimension of the distribution: "+distribution.size()+" X "+ysize);
            //Construct the LineNumberReader object
        	filename = pers+"testProb"+UUID.randomUUID();

        	AnalysisLogger.getLogger().debug(" Storing in "+filename);
            outputStream = new ObjectOutputStream(new FileOutputStream(filename));
            outputStream.writeObject(distribution);            
            AnalysisLogger.getLogger().debug("Stored");
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
		return 100;
	}

	@Override
	public String getMainInfoID(Object mainInfo) {
		
		return (String) mainInfo;
	}

	@Override
	public String getGeographicalID(Object geoInfo) {
		return (String) geoInfo;
	}

	@Override
	public String getName() {
		return "TEST";
	}

	@Override
	public String getDescription() {
		return "A performance test algorithm for the Statistical Manager - generates a constant probability distribution";
	}

	@Override
	public List<StatisticalType> getInputParameters() {
		return null;
	}

	@Override
	public StatisticalType getOutput() {
		PrimitiveType p = new PrimitiveType(File.class.getName(), new File(filename), PrimitiveTypes.FILE, "TestDistribution","Test Distribution File");
		return p;
	}
	
}
