package org.gcube.dataanalysis.geo.algorithms;

import java.util.ArrayList;
import java.util.List;

import org.gcube.dataanalysis.ecoengine.datatypes.ColumnType;
import org.gcube.dataanalysis.ecoengine.datatypes.DatabaseType;
import org.gcube.dataanalysis.ecoengine.datatypes.InputTable;
import org.gcube.dataanalysis.ecoengine.datatypes.PrimitiveType;
import org.gcube.dataanalysis.ecoengine.datatypes.StatisticalType;
import org.gcube.dataanalysis.ecoengine.datatypes.enumtypes.PrimitiveTypes;
import org.gcube.dataanalysis.ecoengine.datatypes.enumtypes.TableTemplates;
import org.gcube.dataanalysis.ecoengine.utils.IOHelper;
import org.gcube.dataanalysis.geo.connectors.table.TableMatrixRepresentation;

public class XYExtractionTable extends XYExtraction{

	@Override
	public String getDescription() {
		return "An algorithm to extract values associated to a table containing geospatial features (e.g. Vessel Routes, Species distribution maps etc. ). A grid of points at a certain resolution is specified by the user and values are associated to the points from the environmental repository. " + 
	"It accepts as one geospatial table " + "and the specification about time and space. The algorithm produces one table containing the values associated to the selected bounding box.";
	}

	@Override
	public List<StatisticalType> getInputParameters() {
		
		List<StatisticalType> inputs = new ArrayList<StatisticalType>();
		
		List<TableTemplates> template= new ArrayList<TableTemplates>();
		template.add(TableTemplates.GENERIC);
		InputTable table = new InputTable(template,TableMatrixRepresentation.tableNameParameter,"A geospatial table containing at least x,y information","");
		inputs.add(table);
		ColumnType columnx = new ColumnType(TableMatrixRepresentation.tableNameParameter, TableMatrixRepresentation.xDimensionColumnParameter, "The column containing x (longitude) information", "x", false);
		inputs.add(columnx);
		ColumnType columny = new ColumnType(TableMatrixRepresentation.tableNameParameter, TableMatrixRepresentation.yDimensionColumnParameter, "The column containing y (latitude) information", "y", false);
		inputs.add(columny);
		ColumnType columnvalue = new ColumnType(TableMatrixRepresentation.tableNameParameter, TableMatrixRepresentation.valueDimensionColumnParameter, "A column containing real valued features", "value", false);
		inputs.add(columnvalue);
		inputs.add(new PrimitiveType(String.class.getName(), null, PrimitiveTypes.STRING, TableMatrixRepresentation.filterParameter, "A filter on one of the columns (e.g. speed=2)", " "));
		
		IOHelper.addStringInput(inputs, TableMatrixRepresentation.zDimensionColumnParameter, "The column containing z (altitude or depth) information (optional)", "z");
		IOHelper.addStringInput(inputs, TableMatrixRepresentation.timeDimensionColumnParameter, "The column containing time (otional)", "datetime");
		
		List<StatisticalType> previnputs = super.getInputParameters();
		inputs.add(previnputs.get(1));
		inputs.add(previnputs.get(2));
		inputs.add(previnputs.get(3));
		inputs.add(previnputs.get(4));
		inputs.add(previnputs.get(5));
		inputs.add(previnputs.get(6));
		inputs.add(previnputs.get(7));
		inputs.add(previnputs.get(8));
		inputs.add(previnputs.get(9));
		inputs.add(previnputs.get(10));
		
		DatabaseType.addDefaultDBPars(inputs);
		
		return inputs;
	}
	
	
	
}
