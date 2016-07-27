package org.gcube.dataanalysis.ecoengine.interfaces;

import java.util.List;

import org.gcube.dataanalysis.ecoengine.configuration.ALG_PROPS;
import org.gcube.dataanalysis.ecoengine.datatypes.StatisticalType;

public interface GenericAlgorithm {
			
			//defines the properties of this algorithm
			public ALG_PROPS[] getProperties();
			
			//defines the name of this algorithm
			public String getName();
			
			//gets the description of the algorithm
			public String getDescription();
			
			//set the input parameters for this generator
			public List<StatisticalType> getInputParameters();
			
			public StatisticalType getOutput();
			
}
