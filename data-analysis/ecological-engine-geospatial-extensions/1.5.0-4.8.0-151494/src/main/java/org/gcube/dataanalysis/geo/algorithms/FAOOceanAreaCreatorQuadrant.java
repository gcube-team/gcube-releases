package org.gcube.dataanalysis.geo.algorithms;

import java.util.ArrayList;
import java.util.List;

import org.gcube.contentmanagement.lexicalmatcher.utils.AnalysisLogger;
import org.gcube.dataanalysis.ecoengine.configuration.AlgorithmConfiguration;
import org.gcube.dataanalysis.ecoengine.datatypes.ColumnType;
import org.gcube.dataanalysis.ecoengine.datatypes.DatabaseType;
import org.gcube.dataanalysis.ecoengine.datatypes.InputTable;
import org.gcube.dataanalysis.ecoengine.datatypes.enumtypes.TableTemplates;
import org.gcube.dataanalysis.ecoengine.utils.IOHelper;
import org.gcube.dataanalysis.geo.utils.FAOOceanAreaConverter;

public class FAOOceanAreaCreatorQuadrant extends FAOOceanAreaCreator {

	static String quadrantDim = "Quadrant_Column";
	
	public FAOOceanAreaCreatorQuadrant(){
		super();
	}
	
	@Override
	protected void setInputParameters() {

		List<TableTemplates> templates = new ArrayList<TableTemplates>();
		templates.add(TableTemplates.GENERIC);
		InputTable tinput = new InputTable(templates, inputTableParameter, "The table to which the algorithm adds the csquare column");
		inputs.add(tinput);

		ColumnType xDimension = new ColumnType(inputTableParameter, xDim, "The column containing Longitude information", "x", false);
		ColumnType yDimension = new ColumnType(inputTableParameter, yDim, "The column containing Latitude information", "y", false);
		ColumnType quadrantDimension = new ColumnType(inputTableParameter, quadrantDim, "The column containing Quadrant information", "quadrant", false);
		
		inputs.add(xDimension);
		inputs.add(yDimension);
		inputs.add(quadrantDimension);
		IOHelper.addIntegerInput(inputs, resolutionParameter, "The resolution of the FAO Ocean Area codes", "5");
		IOHelper.addStringInput(inputs, outputTableParameter, "The name of the output table", "faooceanarea_");
		DatabaseType.addDefaultDBPars(inputs);

	}

	@Override
	public String getDescription() {
		return "An algorithm that adds a column containing the FAO Ocean Area codes associated to longitude, latitude and quadrant columns.";
	}

	@Override
	public String selectInformationForTransformation (AlgorithmConfiguration config, String table, int limit, int offset){
		
		String x = IOHelper.getInputParameter(config, xDim);
		String y = IOHelper.getInputParameter(config, yDim);
		String quadrant = IOHelper.getInputParameter(config, quadrantDim);
		
		String select = "select *," + x + " as loforcs01," + y +" as laforcs01,"+quadrant+ " from " + table + " limit " + limit + " offset " + offset;
		
		
		return select;
	}
	@Override
	public String rowToCode (Object[] rowArray){
		// take x and y
		String xValue = "" + rowArray[rowArray.length - 3];
		String yValue = "" + rowArray[rowArray.length - 2];
		String quadrantValue = "" + rowArray[rowArray.length - 1];
		
		// generate csquarecodes
		String code = "";
		try {
			double xV = Double.parseDouble(xValue);
			double yV = Double.parseDouble(yValue);
			int quadrantV = (int)(Double.parseDouble(quadrantValue));
			
			FAOOceanAreaConverter converter = new FAOOceanAreaConverter();
			code = converter.FAOOceanArea(quadrantV, xV, yV, (int)resolution); 
			
		} catch (Exception e) {
			
		}
		return code;
	}
	
}
