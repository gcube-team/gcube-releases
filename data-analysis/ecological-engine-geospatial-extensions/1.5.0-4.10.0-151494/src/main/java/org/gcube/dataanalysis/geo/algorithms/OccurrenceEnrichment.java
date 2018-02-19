package org.gcube.dataanalysis.geo.algorithms;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.contentmanagement.lexicalmatcher.utils.AnalysisLogger;
import org.gcube.dataanalysis.ecoengine.configuration.AlgorithmConfiguration;
import org.gcube.dataanalysis.ecoengine.configuration.INFRASTRUCTURE;
import org.gcube.dataanalysis.ecoengine.datatypes.ColumnType;
import org.gcube.dataanalysis.ecoengine.datatypes.DatabaseType;
import org.gcube.dataanalysis.ecoengine.datatypes.InputTable;
import org.gcube.dataanalysis.ecoengine.datatypes.OutputTable;
import org.gcube.dataanalysis.ecoengine.datatypes.PrimitiveType;
import org.gcube.dataanalysis.ecoengine.datatypes.PrimitiveTypesList;
import org.gcube.dataanalysis.ecoengine.datatypes.StatisticalType;
import org.gcube.dataanalysis.ecoengine.datatypes.enumtypes.PrimitiveTypes;
import org.gcube.dataanalysis.ecoengine.datatypes.enumtypes.TableTemplates;
import org.gcube.dataanalysis.ecoengine.interfaces.Transducerer;
import org.gcube.dataanalysis.ecoengine.utils.DatabaseFactory;
import org.gcube.dataanalysis.ecoengine.utils.DatabaseUtils;
import org.gcube.dataanalysis.ecoengine.utils.IOHelper;
import org.gcube.dataanalysis.ecoengine.utils.ResourceFactory;
import org.gcube.dataanalysis.ecoengine.utils.Tuple;
import org.gcube.dataanalysis.geo.connectors.wfs.WFS;
import org.gcube.dataanalysis.geo.interfaces.GISDataConnector;
import org.gcube.dataanalysis.geo.matrixmodel.RasterTable;
import org.gcube.dataanalysis.geo.matrixmodel.XYExtractor;
import org.gcube.dataanalysis.geo.utils.VectorOperations;
import org.hibernate.SessionFactory;

public class OccurrenceEnrichment implements Transducerer {

	static String OccurrencesTableNameParameter = "OccurrenceTable";
	static String LongitudeColumn = "LongitudeColumn";
	static String LatitudeColumn = "LatitudeColumn";
	static String ScientificNameColumn = "ScientificNameColumn";
	static String TimeColumn = "TimeColumn";
	static String OutputTableLabelParameter = "OutputTableName";
	static String OutputTableDBNameParameter = "OutputTableDBName";
	static String FilterParameter = "OptionalFilter";
	static String Resolution = "Resolution";
	static String Layers = "Layers";
	static String LayersNames = "FeaturesNames";
	static String yLL = "BBox_LowerLeftLat";
	static String xLL = "BBox_LowerLeftLong";
	static String yUR = "BBox_UpperRightLat";
	static String xUR = "BBox_UpperRightLong";

	AlgorithmConfiguration config;
	float status;

	private String[] layers;
	private String[] layersnames;
	private String occurrencesTableName;
	private String longitudeColumn;
	private String latitudeColumn;
	private String scientificnameColumn;
	private String timeColumn;
	private String filter;
	private float resolution;
	private String outputTableLabel;
	private String outputTableDBName;
	public LinkedHashMap<String, String> outputParameters = new LinkedHashMap<String, String>();

	@Override
	public List<StatisticalType> getInputParameters() {

		List<StatisticalType> inputs = new ArrayList<StatisticalType>();
		List<TableTemplates> template = new ArrayList<TableTemplates>();
		template.add(TableTemplates.OCCURRENCE_SPECIES);
		InputTable table = new InputTable(template, OccurrencesTableNameParameter, "A geospatial table containing occurrence records, following the template of the Species Products Discovery datasets", "");
		inputs.add(table);
		ColumnType p1 = new ColumnType(OccurrencesTableNameParameter, LongitudeColumn, "The column containing longitude values", "decimallongitude", false);
		inputs.add(p1);
		ColumnType p2 = new ColumnType(OccurrencesTableNameParameter, LatitudeColumn, "The column containing latitude values", "decimallatitude", false);
		inputs.add(p2);
		ColumnType p3 = new ColumnType(OccurrencesTableNameParameter, ScientificNameColumn, "The column containing Scientific Names", "scientificname", false);
		inputs.add(p3);
		ColumnType p4 = new ColumnType(OccurrencesTableNameParameter, TimeColumn, "The column containing time information", "eventdate", false);
		inputs.add(p4);

		IOHelper.addStringInput(inputs, FilterParameter, "A filter on one of the columns (e.g. basisofrecord='HumanObservation'). Optional", " ");
		IOHelper.addDoubleInput(inputs, Resolution, "The spatial resolution in degrees of the association between observations and environmental features", "0.5");
		IOHelper.addRandomStringInput(inputs, OutputTableDBNameParameter, "The database name of the table to produce", "enrich_");
		IOHelper.addStringInput(inputs, OutputTableLabelParameter, "The name of the output table", "enrich_");
		PrimitiveTypesList listEnvLayers = new PrimitiveTypesList(String.class.getName(), PrimitiveTypes.STRING, Layers, "The list of environmental layers to use for enriching the points. Each entry is a layer Title or UUID or HTTP link. E.g. the title or the UUID (preferred) of a layer indexed in the e-Infrastructure on GeoNetwork - You can retrieve it from GeoExplorer. Otherwise you can supply the direct HTTP link of the layer. The format will be guessed from the link. The default is GeoTiff. Supports several standards (NETCDF, WFS, WCS, ASC, GeoTiff )", false);
		inputs.add(listEnvLayers);

		PrimitiveTypesList listEnvLayersNames = new PrimitiveTypesList(String.class.getName(), PrimitiveTypes.STRING, LayersNames, "The list of names for the columns corresponding to the environmental layers. These will be the column names of the resulting table", false);
		inputs.add(listEnvLayersNames);

		DatabaseType.addDefaultDBPars(inputs);
		return inputs;
	}

	protected void getParameters() {

		layers = IOHelper.getInputParameter(config, Layers).trim().split(AlgorithmConfiguration.getListSeparator());
		String layernamesS = IOHelper.getInputParameter(config, LayersNames);
		if (layernamesS == null)
			layernamesS = "";
		layersnames = layernamesS.split(AlgorithmConfiguration.getListSeparator());
		AnalysisLogger.getLogger().debug("N. of Layers to take " + layers.length);
		occurrencesTableName = IOHelper.getInputParameter(config, OccurrencesTableNameParameter);
		longitudeColumn = IOHelper.getInputParameter(config, LongitudeColumn);
		latitudeColumn = IOHelper.getInputParameter(config, LatitudeColumn);
		scientificnameColumn = IOHelper.getInputParameter(config, ScientificNameColumn);
		timeColumn = IOHelper.getInputParameter(config, TimeColumn);
		filter = IOHelper.getInputParameter(config, FilterParameter);
		if (filter == null)
			filter = "";
		filter = filter.trim();

		resolution = IOHelper.getInputParameter(config, Resolution) == null ? 0.5f : Float.parseFloat(IOHelper.getInputParameter(config, Resolution));
		outputTableLabel = IOHelper.getInputParameter(config, OutputTableLabelParameter);
		outputTableDBName = IOHelper.getInputParameter(config, OutputTableDBNameParameter);

		AnalysisLogger.getLogger().debug("OccurrenceEnrichment->layers: " + layers);
		AnalysisLogger.getLogger().debug("OccurrenceEnrichment->layers names: " + layersnames);
		AnalysisLogger.getLogger().debug("OccurrenceEnrichment->occurrencesTableName: " + occurrencesTableName);
		AnalysisLogger.getLogger().debug("OccurrenceEnrichment->longitudeColumn: " + longitudeColumn);
		AnalysisLogger.getLogger().debug("OccurrenceEnrichment->latitudeColumn: " + latitudeColumn);
		AnalysisLogger.getLogger().debug("OccurrenceEnrichment->scientificnameColumn: " + scientificnameColumn);
		AnalysisLogger.getLogger().debug("OccurrenceEnrichment->timeColumn: " + timeColumn);
		AnalysisLogger.getLogger().debug("OccurrenceEnrichment->filter: " + filter);
		AnalysisLogger.getLogger().debug("OccurrenceEnrichment->resolution: " + resolution);
		AnalysisLogger.getLogger().debug("OccurrenceEnrichment->outputTableLabel: " + outputTableLabel);
		AnalysisLogger.getLogger().debug("OccurrenceEnrichment->outputTableDBName: " + outputTableDBName);

		String scope = config.getGcubeScope();
		AnalysisLogger.getLogger().debug("Extraction: Externally set scope " + scope);
		if (scope == null) {
			scope = ScopeProvider.instance.get();
			AnalysisLogger.getLogger().debug("Extraction: Internally set scope " + scope);
			config.setGcubeScope(scope);
		}

	}

	@Override
	public void init() throws Exception {
		AnalysisLogger.getLogger().debug("Occurrence Enrichment Initialization");
	}

	@Override
	public void shutdown() {
		AnalysisLogger.getLogger().debug("Occurrence Enrichment Shutdown");
	}

	@Override
	public String getDescription() {
		return "An algorithm performing occurrences enrichment. Takes as input one table containing occurrence points for a set of species and a list of environmental layer, taken either from the e-infrastructure GeoNetwork (through the GeoExplorer application) or from direct HTTP links. Produces one table reporting the set of environmental values associated to the occurrence points.";
	}

	public static String generateEmptyValues(int nValues) {
		StringBuffer sb = new StringBuffer();
		for (int j = 0; j < nValues; j++) {
			sb.append("''");
			if (j < nValues - 1)
				sb.append(",");
		}
		return sb.toString();
	}
	@Override
	public void compute() throws Exception {

		SessionFactory dbconnection = null;
		try {
			long t0 = System.currentTimeMillis();
			status = 10;
			getParameters();
			dbconnection = DatabaseUtils.initDBSession(config);
			String columns = longitudeColumn + "," + latitudeColumn + "," + scientificnameColumn + "," + timeColumn;
			// (id serial, csquarecode character varying, x real, y real, z real, t real, fvalue real)
			String columnsTypes = "id serial, " + longitudeColumn + " real," + latitudeColumn + " real," + scientificnameColumn + " character varying," + timeColumn + " timestamp without time zone";

			// take min_max lat
			String query = "select min(" + longitudeColumn + ") as minlong, max(" + longitudeColumn + ") as maxlong,min(" + latitudeColumn + ") as minlat,max(" + latitudeColumn + ") as maxlat from " + occurrencesTableName;
			AnalysisLogger.getLogger().debug("OccurrenceEnrichment->Retrieving bounding box: " + query);
			List<Object> minmaxlonglat = (List<Object>) DatabaseFactory.executeSQLQuery(query, dbconnection);
			if (minmaxlonglat == null || minmaxlonglat.size() == 0)
				throw new Exception("Could not find min and max for occurrence data");
			status = 20;
			Object[] minmaxLongLat = (Object[]) minmaxlonglat.get(0);
			double BBxLL = Double.parseDouble("" + minmaxLongLat[0]);
			double BBxUR = Double.parseDouble("" + minmaxLongLat[1]);
			double BByLL = Double.parseDouble("" + minmaxLongLat[2]);
			double BByUR = Double.parseDouble("" + minmaxLongLat[3]);

			AnalysisLogger.getLogger().debug("OccurrenceEnrichment->Calculated Bounding Box: [" + BBxLL + "," + BByLL + ";" + BBxUR + "," + BByUR + "]");

			// take the occurrence points
			List<Object> rows = (List<Object>) DatabaseFactory.executeSQLQuery(DatabaseUtils.getDinstictElements(occurrencesTableName, columns, filter), dbconnection);
			if (rows == null || rows.size() == 0)
				throw new Exception("Could not find occurrence data");
			status = 30;
			AnalysisLogger.getLogger().debug("OccurrenceEnrichment->Rows Retrieved");
			List<Tuple<Double>> coordinates4d = new ArrayList<Tuple<Double>>();
			List<String[]> enrichment = new ArrayList<String[]>();

			int elementsFromOccurrences = 4;
			int elementstoreport = elementsFromOccurrences + layers.length;
			// take all the observations
			for (Object row : rows) {
				Object[] elements = (Object[]) row;
				double x = elements[0] == null ? 0 : Double.parseDouble("" + elements[0]);
				double y = elements[1] == null ? 0 : Double.parseDouble("" + elements[1]);
				String species = elements[2] == null ? "" : "" + elements[2];
				String time = elements[3] == null ? "NULL" : "" + elements[3];
				Tuple<Double> el = new Tuple<Double>(x, y, 0d, 0d);
				coordinates4d.add(el);

				String[] singlerow = new String[elementstoreport];
				singlerow[0] = "" + x;
				singlerow[1] = "" + y;
				singlerow[2] = species;
				singlerow[3] = time;

				enrichment.add(singlerow);
			}

			AnalysisLogger.getLogger().debug("OccurrenceEnrichment->Tuples Created. Assigning grid values to the tuples");
			status = 40;
			// take the layers matrices
			int layeridx = 0;
			float statusSteps = 50f / (float) layers.length;
			// for each layer, enrich observations with layers info
			for (String layerID : layers) {
				if (layerID.length() == 0)
					continue;

				AnalysisLogger.getLogger().debug("OccurrenceEnrichment->Assigning layer " + layerID + " Layer enumerator: " + layeridx);
				// extract xy information
				XYExtractor extractor = new XYExtractor(config);
				extractor.correctZ(0, layerID, resolution);
				double zmin = extractor.zmin;
				double zmax = extractor.zmax;
				double bestZ = Math.min(Math.abs(zmin), Math.abs(zmax));

				outputParameters.put("Matching Z value in layer " + (layeridx + 1), "" + bestZ);
				outputParameters.put("Min Z value in layer " + (layeridx + 1), "" + zmin);
				outputParameters.put("Max Z value in layer " + (layeridx + 1), "" + zmax);

				AnalysisLogger.getLogger().debug("OccurrenceEnrichment->Best Z for this reference layer: " + bestZ);
				// perform the closest extraction to the surface
				extractor.extractXYGrid(layerID, 0, BBxLL, BBxUR, BByLL, BByUR, bestZ, resolution, resolution);

				// retrieve the grid time values and tuples
				List<Double> gridValues = extractor.currentTimeValues;
				List<Tuple<Double>> grid3d = extractor.currentTuples;

				// use the layername as column name otherwise use a generic feature indication
				String layername = (layersnames.length > (layeridx) && layersnames[layeridx].trim().length() > 0) ? layersnames[layeridx].trim() : "feature" + (layeridx + 1);
				AnalysisLogger.getLogger().debug("OccurrenceEnrichment->Retrieved Layer Name: " + layername);
				
				

				AnalysisLogger.getLogger().debug("OccurrenceEnrichment->Assigning grid points to the occurrences");
				// make the association
				List<Double> enriched = VectorOperations.assignGridValuesToPoints2D(grid3d, gridValues, coordinates4d, resolution);
				int k = 0;
				AnalysisLogger.getLogger().debug("OccurrenceEnrichment->Assigning values to the column " + (elementsFromOccurrences + layeridx));
				HashMap<Double, Map<String, String>> polygonsFeatures = null;
				if (extractor.currentconnector instanceof WFS)
					polygonsFeatures = ((WFS) extractor.currentconnector).getPolygonsFeatures();

				
				
				boolean enrichWithEnvironmentalFeatures = true;
				Map<String, String> features=null;
				String emptyRow = "";
				if (polygonsFeatures != null && polygonsFeatures.size() > 0){
					AnalysisLogger.getLogger().debug("OccurrenceEnrichment->Managing Web Features");
					enrichWithEnvironmentalFeatures = false;
					features = polygonsFeatures.values().iterator().next();
					int ncolumns = features.keySet().size();
					emptyRow = generateEmptyValues(ncolumns);
					AnalysisLogger.getLogger().debug("OccurrenceEnrichment->Number of additional columns "+ncolumns);
					columnsTypes += ","+RasterTable.propertiesMapToColumnString(features, true);
					columns += ","+RasterTable.propertiesMapToColumnString(features, false);

				}	
				else{
					columns += ",\"" + layername + "\"";
					columnsTypes += ",\"" + layername + "\" real";
				}
				
				for (Double value : enriched) {
					String[] singlerow = enrichment.get(k);

					if (enrichWithEnvironmentalFeatures) {
						if (value == null || Double.isNaN(value) || Double.isInfinite(value))
							singlerow[elementsFromOccurrences + layeridx] = "-9999";
						else {
							singlerow[elementsFromOccurrences + layeridx] = "" + value;
						}
					}
					else{
						if (value == null || Double.isNaN(value) || Double.isInfinite(value))
							singlerow[elementsFromOccurrences + layeridx] = emptyRow;
						else {
							singlerow[elementsFromOccurrences + layeridx] = RasterTable.propertiesMapToDatabaseString(features);
						}
					}
					k++;
				}
				AnalysisLogger.getLogger().debug("OccurrenceEnrichment->Added values to the row");
				layeridx++;
				status = status + statusSteps;
			}//end for on the layer, switch to the next layer

			// write the complete association into the db
			AnalysisLogger.getLogger().debug("OccurrenceEnrichment->Dropping table " + outputTableDBName);
			try {
				DatabaseFactory.executeSQLUpdate(DatabaseUtils.dropTableStatement(outputTableDBName), dbconnection);
			} catch (Exception e) {
				AnalysisLogger.getLogger().debug("OccurrenceEnrichment->cannot drop table, does not exist: " + outputTableDBName);
			}
			String createquery = "create table " + outputTableDBName + " (" + columnsTypes + ")";
			AnalysisLogger.getLogger().debug("OccurrenceEnrichment->Creating table " + outputTableDBName + " query:" + createquery);
			DatabaseFactory.executeSQLUpdate(createquery, dbconnection);
			AnalysisLogger.getLogger().debug("OccurrenceEnrichment->Inserting chunks");
			DatabaseUtils.insertChunksIntoTable(outputTableDBName, columns, enrichment, 5000, dbconnection,false);
			AnalysisLogger.getLogger().debug("OccurrenceEnrichment->Whole process complete in " + ((double) (System.currentTimeMillis() - t0) / 1000f) + " s");
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			if (dbconnection != null)
				dbconnection.close();
			status = 100;
		}

	}

	@Override
	public StatisticalType getOutput() {
		List<TableTemplates> templateHspec = new ArrayList<TableTemplates>();
		templateHspec.add(TableTemplates.GENERIC);
		OutputTable p = new OutputTable(templateHspec, outputTableLabel, outputTableDBName, "Output table");
		LinkedHashMap<String, StatisticalType> map = new LinkedHashMap<String, StatisticalType>();

		for (String key : outputParameters.keySet()) {
			String value = outputParameters.get(key);
			PrimitiveType val = new PrimitiveType(String.class.getName(), "" + value, PrimitiveTypes.STRING, key, key);
			map.put(key, val);
		}

		map.put("OutputTable", p);
		PrimitiveType outputm = new PrimitiveType(HashMap.class.getName(), map, PrimitiveTypes.MAP, "ResultsMap", "Results Map");
		return outputm;
	}

	@Override
	public float getStatus() {
		return status;
	}

	@Override
	public INFRASTRUCTURE getInfrastructure() {
		return INFRASTRUCTURE.LOCAL;
	}

	@Override
	public void setConfiguration(AlgorithmConfiguration config) {
		this.config = config;
	}

	protected ResourceFactory resourceManager;

	public String getResourceLoad() {
		if (resourceManager == null)
			resourceManager = new ResourceFactory();
		return resourceManager.getResourceLoad(1);
	}

	public String getResources() {
		return ResourceFactory.getResources(100f);
	}

}
