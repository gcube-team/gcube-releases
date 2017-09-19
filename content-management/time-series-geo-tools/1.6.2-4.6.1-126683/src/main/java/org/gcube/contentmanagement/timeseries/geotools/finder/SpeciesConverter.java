package org.gcube.contentmanagement.timeseries.geotools.finder;

import java.util.List;

import org.gcube.contentmanagement.lexicalmatcher.utils.AnalysisLogger;
import org.gcube.contentmanagement.timeseries.geotools.databases.ConnectionsManager;

public class SpeciesConverter {

	ConnectionsManager connManager;
	String reference_species_table;
	private static final String conversionQuery = "select %1$s from codeconversiontable where %2$s = '%3$s' or %2$s = '%4$s' or %2$s = '%5$s'";
	private static final String fromNameToScientificNameQuery = "select scientific_name from %ref_species% where alpha_3_code = '%1$s' or scientific_name = '%1$s' or name_en = '%1$s' or name_fr = '%1$s' or name_es = '%1$s';";
	
	public SpeciesConverter(ConnectionsManager connManager,String reference_species_table){
		this.connManager = connManager;
		this.reference_species_table = reference_species_table;
	}
	
	public String speciesName2FishCode(String fishName) throws Exception {
		
//		AnalysisLogger.getLogger().trace("speciesName2FishCode->GETTING SCIENTIFIC NAME: "+String.format(fromNameToScientificNameQuery, fishName));
		List<Object> scientific_names = connManager.AquamapsQuery(String.format(fromNameToScientificNameQuery.replace("%ref_species%", reference_species_table), fishName));
		String name = (String) scientific_names.get(0);
		String name1 = ""+name.charAt(0);
		name1 = name1.toUpperCase()+name.substring(1);
		String query = String.format(conversionQuery,"namecode","name",name, name1,name.toLowerCase());
//		AnalysisLogger.getLogger().warn("speciesName2FishCode->CONVERSION TO FISH CODE: "+query);
		List<Object> out = connManager.AquamapsQuery(query);
		String code = (String)out.get(0); 
		return code;
	}
	
	public String fishCode2SpeciesName(String fishCode) throws Exception {
		
		String query = String.format(conversionQuery,"name","namecode",fishCode,fishCode,fishCode);
		List<Object> out = connManager.AquamapsQuery(query);
		String name = ""+out.get(0); 
		return name;
	}

}
