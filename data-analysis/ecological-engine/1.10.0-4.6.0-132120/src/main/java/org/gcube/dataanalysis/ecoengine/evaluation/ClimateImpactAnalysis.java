package org.gcube.dataanalysis.ecoengine.evaluation;

import java.util.ArrayList;
import java.util.List;

import org.gcube.dataanalysis.ecoengine.datatypes.DatabaseType;
import org.gcube.dataanalysis.ecoengine.datatypes.InputTable;
import org.gcube.dataanalysis.ecoengine.datatypes.StatisticalType;
import org.gcube.dataanalysis.ecoengine.datatypes.enumtypes.TableTemplates;

public class ClimateImpactAnalysis extends DiscrepancyAnalysis {

	protected String SpeciesListTable = "ReferenceHSPEN";
	protected String LeftHSPEC = "LeftHSPEC";
	protected String RightHSPEC = "RightHSPEC";
	
	@Override
	public List<StatisticalType> getInputParameters() {
		
		List<TableTemplates> templatesHspen = new ArrayList<TableTemplates>();
		templatesHspen.add(TableTemplates.HSPEN);
		
		List<TableTemplates> templatesHspec = new ArrayList<TableTemplates>();
		templatesHspec.add(TableTemplates.HSPEC);
		
		InputTable hspec1 = new InputTable(templatesHspec,LeftHSPEC,"Left table containing a hspec distribution","hspen");
		InputTable hspec2 = new InputTable(templatesHspec,RightHSPEC,"Right table containing a hspec distribution","hspen");
		
		InputTable hspen = new InputTable(templatesHspen,SpeciesListTable,"Species List Table taken from envelopes","hspen");
		
		List<StatisticalType> stlist = new ArrayList<StatisticalType>();
		stlist.add(hspec1);
		stlist.add(hspec2);
		stlist.add(hspen);
		
		DatabaseType.addDefaultDBPars(stlist);
		
		return stlist;
	}
	
	
}
