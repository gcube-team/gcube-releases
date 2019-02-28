package org.gcube.dataanalysis.geo.algorithms;

import java.util.ArrayList;
import java.util.List;

import org.gcube.dataanalysis.ecoengine.configuration.AlgorithmConfiguration;
import org.gcube.dataanalysis.ecoengine.datatypes.ColumnType;
import org.gcube.dataanalysis.ecoengine.datatypes.DatabaseType;
import org.gcube.dataanalysis.ecoengine.datatypes.InputTable;
import org.gcube.dataanalysis.ecoengine.datatypes.enumtypes.TableTemplates;
import org.gcube.dataanalysis.ecoengine.utils.IOHelper;
import org.gcube.dataanalysis.geo.utils.FAOOceanAreaConverter;

public class FAOOceanAreaCreator extends CSquaresCreator {
	
	public FAOOceanAreaCreator(){
		resolutionParameter = "Resolution";
		codecolumnName = "fao_ocean_area";
	}
	@Override
	protected void setInputParameters() {

		List<TableTemplates> templates = new ArrayList<TableTemplates>();
		templates.add(TableTemplates.GENERIC);
		InputTable tinput = new InputTable(templates, inputTableParameter, "The table to which the algorithm adds the csquare column");
		inputs.add(tinput);

		ColumnType xDimension = new ColumnType(inputTableParameter, xDim, "The column containing Longitude information", "x", false);
		ColumnType yDimension = new ColumnType(inputTableParameter, yDim, "The column containing Latitude information", "y", false);

		inputs.add(xDimension);
		inputs.add(yDimension);
		IOHelper.addIntegerInput(inputs, resolutionParameter, "The resolution of the FAO Ocean Area codes", "5");
		IOHelper.addStringInput(inputs, outputTableParameter, "The name of the output table", "faooceanarea_");
		DatabaseType.addDefaultDBPars(inputs);

	}

	@Override
	public String getDescription() {
		return "An algorithm that adds a column containing the FAO Ocean Area codes associated to longitude and latitude columns.";
	}

	public String selectInformationForTransformation (AlgorithmConfiguration config, String table, int limit, int offset){
		
		String x = IOHelper.getInputParameter(config, xDim);
		String y = IOHelper.getInputParameter(config, yDim);
		
		String select = "select *," + x + " as loforcs01," + y + " as laforcs01 from " + table + " limit " + limit + " offset " + offset;
		return select;
	}
	
	public String rowToCode (Object[] rowArray){
		// take x and y
		String xValue = "" + rowArray[rowArray.length - 2];
		String yValue = "" + rowArray[rowArray.length - 1];
		
		// generate csquarecodes
		String code = "";
		try {
			double xV = Double.parseDouble(xValue);
			double yV = Double.parseDouble(yValue);
			FAOOceanAreaConverter converter = new FAOOceanAreaConverter();
			code = converter.FAOOceanArea(-1, xV, yV, (int)resolution); 
			
		} catch (Exception e) {
		}
		return code;
	}
	
}
