package org.gcube.dataanalysis.geo.algorithms;

import java.util.ArrayList;
import java.util.List;

import org.gcube.dataanalysis.ecoengine.datatypes.ColumnType;
import org.gcube.dataanalysis.ecoengine.datatypes.DatabaseType;
import org.gcube.dataanalysis.ecoengine.datatypes.InputTable;
import org.gcube.dataanalysis.ecoengine.datatypes.PrimitiveType;
import org.gcube.dataanalysis.ecoengine.datatypes.enumtypes.PrimitiveTypes;
import org.gcube.dataanalysis.ecoengine.datatypes.enumtypes.TableTemplates;
import org.gcube.dataanalysis.geo.utils.GeospatialDataPublicationLevel;

public class PointsMapsCreator extends MapsCreator {

	@Override
	public String getDescription() {
		return "A transducer algorithm to produce a GIS map of points from a set of points with x,y coordinates indications. A maximum of " + maxNPoints + " is allowed";
	}

	@Override
	public void init() throws Exception {
		log("MAPS_CREATOR");
		datastore = "timeseriesws";
		defaultStyle = "point";
		workspace = "aquamaps";
		username = "statistical.manager";
		purpose = "To Publish Geometric Layers for Points Maps";
		credits = "Generated via the Statistical Manager Service";
		keyword = "Points Map";
	}

	@Override
	protected void setInputParameters() {
		try {
			PrimitiveType e = new PrimitiveType(Enum.class.getName(), GeospatialDataPublicationLevel.values(), PrimitiveTypes.ENUMERATED, publicationLevel, "The visibility level of the produced map",""+GeospatialDataPublicationLevel.PRIVATE);
			inputs.add(e);
		
			List<TableTemplates> templates = new ArrayList<TableTemplates>();
			addRemoteDatabaseInput(databaseParameterName, dburlParameterName, dbuserParameterName, dbpasswordParameterName, "driver", "dialect");
			templates.add(TableTemplates.GENERIC);
			addStringInput(layerNameParameter, "The name of the layer to produce", "Points Map");
			InputTable tinput = new InputTable(templates, inputTableParameter, "The table information to geo-spatialize");
			ColumnType xColumn = new ColumnType(inputTableParameter, xParameter, "The column containing longitude information", "", false);
			ColumnType yColumn = new ColumnType(inputTableParameter, yParameter, "The column containing latitude information", "", false);
			ColumnType customDimension = new ColumnType(inputTableParameter, infoParameter, "The column containing information you want to attach to each point", "", false);

			inputs.add(tinput);
			inputs.add(xColumn);
			inputs.add(yColumn);
			inputs.add(customDimension);
			DatabaseType.addDefaultDBPars(inputs);

		} catch (Throwable e) {
			e.printStackTrace();
		}

	}

}
