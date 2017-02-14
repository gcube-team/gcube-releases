package org.gcube.contentmanagement.graphtools.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.gcube.contentmanagement.graphtools.abstracts.SamplesTable;
import org.gcube.contentmanagement.graphtools.core.filters.Filter;
import org.gcube.contentmanagement.graphtools.data.GraphSamplesTable;
import org.gcube.contentmanagement.graphtools.data.conversions.GraphConverter2D;
import org.gcube.contentmanagement.graphtools.data.databases.CommonDBExtractor;
import org.gcube.contentmanagement.lexicalmatcher.analysis.core.LexicalEngineConfiguration;
import org.gcube.contentmanagement.lexicalmatcher.utils.AnalysisLogger;
import org.gcube.contentmanagement.lexicalmatcher.utils.DatabaseFactory;
import org.gcube.portlets.user.timeseries.charts.support.types.GraphData;
import org.gcube.portlets.user.timeseries.charts.support.types.GraphGroups;
import org.gcube.portlets.user.timeseries.charts.support.types.Point;
import org.hibernate.SessionFactory;

import com.rapidminer.RapidMiner;
import com.rapidminer.example.ExampleSet;
import com.rapidminer.operator.preprocessing.sampling.AbsoluteSampling;
import com.rapidminer.tools.OperatorService;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

public class StatisticsGenerator {

	public static void main(String[] args) throws Exception {
		String table = "ts_161efa00_2c32_11df_b8b3_aa10916debe6";
		String xDimension = "field5";
		String yDimension = "field6";
		String groupDimension = "field1";
		String speciesColumn = "field3";
		String filter1 = "Brown seaweeds";
		String filter2 = "River eels";
		StatisticsGenerator stg = new StatisticsGenerator();

		LexicalEngineConfiguration conf = new LexicalEngineConfiguration();
		// database Parameters
		conf.setDatabaseUserName("root");
		// conf.setDatabasePassword("password");
		conf.setDatabaseDriver("com.mysql.jdbc.Driver");
		conf.setDatabaseURL("jdbc:mysql://localhost/timeseries");
		conf.setDatabaseDialect("org.hibernate.dialect.MySQLDialect");
		conf.setDatabaseAutomaticTestTable("connectiontesttable");
		conf.setDatabaseIdleConnectionTestPeriod("3600");

		// stg.init("./cfg/");
		stg.init("./cfg/", conf);

		stg.generateGraphs(3, table, xDimension, yDimension, groupDimension, speciesColumn, filter1, filter2);
	}

	SessionFactory referenceDBSession;
	CommonDBExtractor extractor;
	private static final String LogFile = "ALog.properties";
	private static final String HibFile = "hibernate.cfg.xml";
	private static final String OperatorsFile = "operators.xml";
	private List<Filter> ColumnFilters;
	private Filter XRangeFilter;
	private Filter YRangeFilter;
	private XStream xStream;
	
	public void init(String cfgPath) throws Exception {
		init(cfgPath, null);
	}

	public SessionFactory getDBSession() {
		return this.referenceDBSession;
	}
	
	public void init(String cfgPath, LexicalEngineConfiguration config) throws Exception {
		AnalysisLogger.setLogger(cfgPath + "/" + LogFile);
		if (config == null)
			referenceDBSession = DatabaseFactory.initDBConnection(cfgPath + HibFile);
		else
			referenceDBSession = DatabaseFactory.initDBConnection(cfgPath + HibFile, config);

		ColumnFilters = new ArrayList<Filter>();

		extractor = new CommonDBExtractor(referenceDBSession);
		
		AnalysisLogger.getLogger().info("StatisticsGenerator->initialization complete");
		System.setProperty("rapidminer.init.operators", cfgPath + OperatorsFile);
		xStream = new XStream(new DomDriver());
		RapidMiner.init();
	}

	public void resetFilters(){
		ColumnFilters = new ArrayList<Filter>();
	}
	
	public void addColumnFilter(String column, String element, String operator) {
		ColumnFilters.add(new Filter(column, element, operator));
	}

	public void addColumnFilter(String column, String element) {
		ColumnFilters.add(new Filter(column, element));
	}

	public void addXRangeFilter(String xmin, String xmax) {
		XRangeFilter = new Filter(xmin, xmax);
	}

	public void addYRangeFilter(String ymin, String ymax) {
		YRangeFilter = new Filter(ymin, ymax);
	}

		
	public GraphGroups generateGraphs(int maxElements, String timeSeriesTable, String xDimension, String yDimension, String groupDimension, String speciesColumn, String... filters) throws Exception {

		Map<String, SamplesTable> samplesMap = extractor.getMultiDimTemporalTables(ColumnFilters, YRangeFilter, timeSeriesTable, xDimension, groupDimension, yDimension, speciesColumn, filters);

		AnalysisLogger.getLogger().info("StatisticsGenerator-> samplesMap has been generated");
		AnalysisLogger.getLogger().trace(samplesMap.toString());
		// setup Absolute Sampling operator
		AbsoluteSampling asop = (AbsoluteSampling) OperatorService.createOperator("AbsoluteSampling");
		asop.setParameter("sample_size", "" + maxElements);
		asop.setParameter("local_random_seed", "-1");

		// setup graphgroups
		GraphGroups graphgroups = new GraphGroups();

		int i = 1;
		// for each samples table perform processing
		for (String key : samplesMap.keySet()) {
			// get samples table
			SamplesTable stable = samplesMap.get(key);
			// transform samples table into a list of points
			List<Point<? extends Number, ? extends Number>> singlegraph = GraphConverter2D.transformTable(stable);

			// filter XRange if necessary
			if (XRangeFilter != null) {
				singlegraph = GraphConverter2D.filterXRange(singlegraph, XRangeFilter.getFirstElement(), XRangeFilter.getSecondElement());
			}

			// setup the graph samples table to perform mining processing
			GraphSamplesTable graphSamples = new GraphSamplesTable(singlegraph);
			// if there are too many samples, perform downsampling
			if (graphSamples.getNumOfDataRows() > maxElements) {
				// generate an Example Set for Rapid Miner
				ExampleSet es = graphSamples.generateExampleSet();
				// apply Sampling
				es = asop.apply(es);
				// generate a new graph samples table
				graphSamples = new GraphSamplesTable();
				graphSamples.generateSampleTable(es);

				// get the points list from the graph samples table
				singlegraph = graphSamples.getGraph();
				AnalysisLogger.getLogger().trace("Regeneration\n" + graphSamples.toString());
			}

			// reorder the elements of the points list
			// this steps performs re-enumeration and reordering of the rows after the sampling operations
			singlegraph = GraphConverter2D.reorder(singlegraph);

			AnalysisLogger.getLogger().trace("Reordering\n" + singlegraph.toString());
			if ((singlegraph != null)&&(singlegraph.size()>0)) {
				// build up the GraphData for visualization
				GraphData grd = new GraphData(singlegraph, true);

				// calculate the bounds of the graph
				graphSamples.calculateBounds();

				// set the bounds
				grd.setMaxY(graphSamples.maxY);
				grd.setMinY(graphSamples.minY);

				// add the points list
				graphgroups.addGraph("Distribution for " + key, grd);

				AnalysisLogger.getLogger().trace("StatisticsGenerator-> graphgroup " + i + " generated with key: " + key);
				i++;
			}
		}

		AnalysisLogger.getLogger().info("StatisticsGenerator-> graphgroups have been generated");

		return graphgroups;

	}

	public String generateStringGraphs(int maxElements, String timeSeriesTable, String xDimension, String yDimension, String groupDimension, String speciesColumn, String... filters) throws Exception {
		GraphGroups gg = generateGraphs(maxElements, timeSeriesTable, xDimension, yDimension, groupDimension, speciesColumn, filters);
		
		return xStream.toXML(gg);
	}
	
	
}
