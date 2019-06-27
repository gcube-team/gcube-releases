package org.gcube.dataanalysis.geo.vti;

import java.util.ArrayList;
import java.util.List;

import org.gcube.contentmanagement.lexicalmatcher.utils.AnalysisLogger;
import org.gcube.dataanalysis.ecoengine.datatypes.ColumnType;
import org.gcube.dataanalysis.ecoengine.datatypes.DatabaseType;
import org.gcube.dataanalysis.ecoengine.datatypes.InputTable;
import org.gcube.dataanalysis.ecoengine.datatypes.enumtypes.TableTemplates;
import org.gcube.dataanalysis.ecoengine.utils.IOHelper;
import org.gcube.dataanalysis.geo.utils.CSquareCodesConverter;

public class CSquare2Coordinates extends GridCWP2Coordinates{

	@Override
	protected void setInputParameters() {

		List<TableTemplates> templates = new ArrayList<TableTemplates>();
		templates.add(TableTemplates.GENERIC);
		InputTable tinput = new InputTable(templates, inputTableParameter, "The table to which the algorithm will add information");
		inputs.add(tinput);

		ColumnType Dimension = new ColumnType(inputTableParameter, CodeColumn, "The column containing c-square codes", "GRID", false);
		
		inputs.add(Dimension);
		
		IOHelper.addStringInput(inputs, outputTableParameter, "The name of the output table", "csq_");
		DatabaseType.addDefaultDBPars(inputs);

	}
	
	@Override
	public String getDescription() {
		return "An algorithm that adds longitude, latitude and resolution columns analysing a column containing c-square codes.";
	}
	
	public void rowToCoords (Object[] rowArray) {
		// take x and y
		Object grid = null;
		try{
			grid=rowArray[rowArray.length - 1];
			String gridValue = ""+ grid;
			// generate csquarecodes
			CSquareCodesConverter converter = new CSquareCodesConverter();
			converter.parse(gridValue);
			
			currentLat= converter.getCurrentLat();
			currentLong=converter.getCurrentLong();
			currentRes=converter.getCurrentResolution();
		}catch(Exception e){
			AnalysisLogger.getLogger().debug("Error converting grid: "+grid+" - "+e.getLocalizedMessage());
			currentLat= 0;
			currentLong=0;
			currentRes=0;	
		}
	}
	
}
