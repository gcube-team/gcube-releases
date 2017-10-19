package org.gcube.dataanalysis.geo.algorithms;

import java.util.ArrayList;
import java.util.List;

import org.gcube.dataanalysis.ecoengine.configuration.AlgorithmConfiguration;
import org.gcube.dataanalysis.ecoengine.datatypes.ColumnType;
import org.gcube.dataanalysis.ecoengine.datatypes.DatabaseType;
import org.gcube.dataanalysis.ecoengine.datatypes.InputTable;
import org.gcube.dataanalysis.ecoengine.datatypes.PrimitiveType;
import org.gcube.dataanalysis.ecoengine.datatypes.enumtypes.PrimitiveTypes;
import org.gcube.dataanalysis.ecoengine.datatypes.enumtypes.TableTemplates;
import org.gcube.dataanalysis.geo.utils.GeospatialDataPublicationLevel;

public class SpeciesDistributionsMapsCreatorFromCsquares extends MapsCreator {

	@Override
	public String getDescription() {
		return "A transducer algorithm to produce a GIS map from a probability distribution associated to a set of csquare codes. A maximum of " + maxNPoints + " is allowed";
	}

	@Override
	public void init() throws Exception {
		log("MAPS_CREATOR");
		datastore = "timeseriesws";
		defaultStyle = "Species_prob";
		workspace = "aquamaps";
		username = "statistical.manager";
		purpose = "To Publish Geometric Layers for Species Distribution Maps";
		credits = "Generated via the Statistical Manager Service";
		keyword = "Species Probability Distribution";
	}

	@Override
	protected void setInputParameters() {
		try {
			PrimitiveType e = new PrimitiveType(Enum.class.getName(), GeospatialDataPublicationLevel.values(), PrimitiveTypes.ENUMERATED, publicationLevel, "The visibility level of the produced map",""+GeospatialDataPublicationLevel.PRIVATE);
			inputs.add(e);
		
			List<TableTemplates> templates = new ArrayList<TableTemplates>();
			addRemoteDatabaseInput(databaseParameterName, dburlParameterName, dbuserParameterName, dbpasswordParameterName, "driver", "dialect");
			templates.add(TableTemplates.GENERIC);
			addStringInput(layerNameParameter, "The name of the layer to produce", "Species Prob Distribution Csqr");
			InputTable tinput = new InputTable(templates, inputTableParameter, "The table information to geo-spatialize");
			ColumnType xColumn = new ColumnType(inputTableParameter, csquareParameter, "The column containing csquare codes", "", false);
			ColumnType probabilityDimension = new ColumnType(inputTableParameter, probabilityParameter, "The column containing probability information", "", false);

			inputs.add(tinput);
			inputs.add(xColumn);
			inputs.add(probabilityDimension);
			DatabaseType.addDefaultDBPars(inputs);

		} catch (Throwable e) {
			e.printStackTrace();
		}

	}

	public static void main(String[] args) throws Exception {
		AlgorithmConfiguration config = new AlgorithmConfiguration();
		config.setConfigPath("./cfg/");
		config.setPersistencePath("./cfg/");
		config.setGcubeScope("/gcube");
		config.setParam("DatabaseUserName", "utente");
		config.setParam("DatabasePassword", "d4science");
		config.setParam("DatabaseURL", "jdbc:postgresql://statistical-manager.d.d4science.research-infrastructures.eu/testdb");

		config.setParam(dburlParameterName, "jdbc:postgresql://geoserver-test.d4science-ii.research-infrastructures.eu/timeseriesgisdb");
		config.setParam(dbuserParameterName, "postgres");
		config.setParam(dbpasswordParameterName, "d4science2");

		config.setParam(inputTableParameter, "hspec_id_4cd0644e_46c5_4b33_b8fa_85b0c6b01982");
		config.setParam(csquareParameter, "csquarecode");
		config.setParam(probabilityParameter, "probability");

		config.setParam("ServiceUserName", "gianpaolo.coro");
		config.setParam(layerNameParameter, "Generic Species");

		MapsCreator maps = new SpeciesDistributionsMapsCreatorFromCsquares();
		maps.setConfiguration(config);
		maps.init();
		maps.compute();

	}
}
