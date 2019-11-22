package org.gcube.dataanalysis.ecoengine.transducers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import org.gcube.dataanalysis.ecoengine.configuration.AlgorithmConfiguration;
import org.gcube.dataanalysis.ecoengine.configuration.INFRASTRUCTURE;
import org.gcube.dataanalysis.ecoengine.datatypes.DatabaseType;
import org.gcube.dataanalysis.ecoengine.datatypes.InputTable;
import org.gcube.dataanalysis.ecoengine.datatypes.OutputTable;
import org.gcube.dataanalysis.ecoengine.datatypes.PrimitiveType;
import org.gcube.dataanalysis.ecoengine.datatypes.StatisticalType;
import org.gcube.dataanalysis.ecoengine.datatypes.TablesList;
import org.gcube.dataanalysis.ecoengine.datatypes.enumtypes.PrimitiveTypes;
import org.gcube.dataanalysis.ecoengine.datatypes.enumtypes.TableTemplates;
import org.gcube.dataanalysis.ecoengine.evaluation.bioclimate.InterpolateTables;
import org.gcube.dataanalysis.ecoengine.evaluation.bioclimate.InterpolateTables.INTERPOLATIONFUNCTIONS;
import org.gcube.dataanalysis.ecoengine.interfaces.Transducerer;
import org.gcube.dataanalysis.ecoengine.utils.ResourceFactory;

public class InterpolationTransducer implements Transducerer{
	
	protected AlgorithmConfiguration config;
	protected InterpolateTables interp;
	private String[] producedtables;
	
	protected float status = 0;
	
	@Override
	public INFRASTRUCTURE getInfrastructure() {
		return INFRASTRUCTURE.LOCAL;
	}
	
	
	@Override
	public void init() throws Exception {
		interp = new InterpolateTables(config.getConfigPath(), config.getPersistencePath(), config.getParam("DatabaseURL"),config.getParam("DatabaseUserName"), config.getParam("DatabasePassword"));
	}

	@Override
	public void setConfiguration(AlgorithmConfiguration config) {
		this.config=config;
	}

	@Override
	public void shutdown() {
		
	}

	@Override
	public float getStatus() {
		if ((status>0)&&(status<100)){
			return Math.min(interp.getStatus(),95f);
		}
		else
			return status;
	}

	@Override
	public String getDescription() {
		return "Evaluates the climatic changes impact on species presence";
	}

	@Override
	public List<StatisticalType> getInputParameters() {
		List<StatisticalType> parameters = new ArrayList<StatisticalType>();
		List<TableTemplates> templates = new ArrayList<TableTemplates>();
		templates.add(TableTemplates.HCAF);
		
		InputTable p7 = new InputTable(templates, "FirstHCAF", "the HCAF table representing the starting scenario", "hcaf_d");
		InputTable p8 = new InputTable(templates, "SecondHCAF", "the HCAF table representing the ending scenario", "hcaf_d_2050");
		PrimitiveType p9 = new PrimitiveType(Integer.class.getName(), null, PrimitiveTypes.NUMBER, "YearStart", "the year associated to the FirstHCAF parameter","2012");
		PrimitiveType p10 = new PrimitiveType(Integer.class.getName(), null, PrimitiveTypes.NUMBER, "YearEnd", "the year associated to the SecondHCAF parameter","2050");
		PrimitiveType p11 = new PrimitiveType(Integer.class.getName(), null, PrimitiveTypes.NUMBER, "NumberOfInterpolations", "number of Intermediate Interpolation points","2");
		
		PrimitiveType p12 = new PrimitiveType(Enum.class.getName(), InterpolateTables.INTERPOLATIONFUNCTIONS.values(), PrimitiveTypes.ENUMERATED, "InterpolationFunction", "The interpolation Function to use",""+InterpolateTables.INTERPOLATIONFUNCTIONS.LINEAR);
				
		parameters.add(p7);
		parameters.add(p8);
		parameters.add(p9);
		parameters.add(p10);
		parameters.add(p11);
		parameters.add(p12);
		
		DatabaseType.addDefaultDBPars(parameters);
		return parameters;
	}

	@Override
	public StatisticalType getOutput() {
		LinkedHashMap<String, StatisticalType> map = new LinkedHashMap<String, StatisticalType>();
		
		
		List<TableTemplates> template = new ArrayList<TableTemplates>();
		template.add(TableTemplates.HCAF);
//		TablesList p = new TablesList(template, "INTEPOLATED_HCAF_TABLE_LIST", "List of HCAF tables produced by the interpolation", false);
		
		if ((producedtables!=null) &&(producedtables.length>0)){
			int i=1;
			for (String table:producedtables){
//				p.add(new OutputTable(template,table,table,"Interpolation number "+i));
				map.put("Interpolation "+i+" ("+table+")", new OutputTable(template,"Interpolation number "+i,table,"Interpolation number "+i));
				i++;
				
			}
		}
		
		PrimitiveType output = new PrimitiveType(LinkedHashMap.class.getName(), map, PrimitiveTypes.MAP, "INTEPOLATED_HCAF_TABLE_LIST", "List of HCAF tables produced by the interpolation");
		
		
		return output;
	
	}

	@Override
	public void compute() throws Exception {
		
		status = 0.1f;
		try{
		int nInterpolations = Integer.parseInt(config.getParam("NumberOfInterpolations"))+2;
		String interpolationType = config.getParam("InterpolationFunction");
		INTERPOLATIONFUNCTIONS fun = INTERPOLATIONFUNCTIONS.valueOf(interpolationType);
		int year1 = Integer.parseInt(config.getParam("YearStart"));
		int year2 = Integer.parseInt(config.getParam("YearEnd"));
		
		interp.interpolate( config.getParam("FirstHCAF"),  config.getParam("SecondHCAF"), nInterpolations, fun, year1, year2);
		producedtables = interp.getInterpolatedTables();
		
		}catch(Exception e){
			e.printStackTrace();
			throw e;
		}
		finally{
			status = 100f;
		}
	}


	ResourceFactory resourceManager;
	public String getResourceLoad() {
		if (resourceManager==null)
			resourceManager = new ResourceFactory();
		return resourceManager.getResourceLoad(1);
	}


	@Override
	public String getResources() {
		return ResourceFactory.getResources(100f);
	}

}
