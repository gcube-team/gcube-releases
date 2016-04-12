package org.gcube.dataanalysis.ecoengine.transducers.charts;

import java.awt.Image;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.gcube.contentmanagement.graphtools.data.conversions.ImageTools;
import org.gcube.contentmanagement.graphtools.plotting.graphs.GaussianDistributionGraph;
import org.gcube.contentmanagement.graphtools.plotting.graphs.HistogramGraph;
import org.gcube.contentmanagement.graphtools.plotting.graphs.RadarGraph;
import org.gcube.contentmanagement.graphtools.plotting.graphs.ScatterGraphGeneric;
import org.gcube.contentmanagement.graphtools.utils.MathFunctions;
import org.gcube.contentmanagement.lexicalmatcher.utils.AnalysisLogger;
import org.gcube.contentmanagement.lexicalmatcher.utils.DatabaseFactory;
import org.gcube.dataanalysis.ecoengine.datatypes.ColumnTypesList;
import org.gcube.dataanalysis.ecoengine.datatypes.DatabaseType;
import org.gcube.dataanalysis.ecoengine.datatypes.InputTable;
import org.gcube.dataanalysis.ecoengine.datatypes.enumtypes.TableTemplates;
import org.gcube.dataanalysis.ecoengine.utils.IOHelper;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.function.NormalDistributionFunction2D;
import org.jfree.data.general.DatasetUtilities;
import org.jfree.data.xy.XYSeriesCollection;

public class QuantitiesAttributesChartsTransducerer extends AbstractChartsProducer {

	
	@Override
	protected void setInputParameters() {
		List<TableTemplates> templates = new ArrayList<TableTemplates>();
		templates.add(TableTemplates.GENERIC);
		InputTable tinput = new InputTable(templates, inputTableParameter, "The input table");
		inputs.add(tinput);
		ColumnTypesList p1 = new ColumnTypesList(inputTableParameter, attributesParameter, "The dimensions to consider in the charts", true);
		ColumnTypesList p2 = new ColumnTypesList(inputTableParameter, quantitiesParameter, "The numeric quantities to visualize ", false);
		IOHelper.addIntegerInput(inputs, topElementsNumber, "Max number of elements, with highest values, to visualize", "10");
		inputs.add(p1);
		inputs.add(p2);

		DatabaseType.addDefaultDBPars(inputs);
	}

	@Override
	public String getDescription() {
		return "An algorithm producing generic charts of attributes vs. quantities. Charts are displayed per quantity column. Histograms, Scattering and Radar charts are produced for the top ten quantities. A gaussian distribution reports overall statistics for the quantities.";
	}

	@Override
	public LinkedHashMap<String,Object> createCharts(String[] dimensions, String quantity, String time, List<Object> rows, boolean displaychart) {
		if (dimensions==null)
			dimensions=new String[0];
		
		DefaultCategoryDataset datasetHisto = new DefaultCategoryDataset();
		for (Object row : rows) {
			Object[] array = (Object[]) row;
			Double q = null;
			try {
					q = Double.parseDouble("" + array[array.length - 1]);
				} catch (Exception e) {
			}
			
			if (q != null) {
				int dimIdx = 0;
				for (String dimension : dimensions) {
					String dimensionValue = "";
					if (array[dimIdx]!=null)
						dimensionValue = ""+array[dimIdx];
					
					if (dimensionValue.length()>0)
						datasetHisto.addValue(q, dimensionValue, dimension);
					
					dimIdx++;
				}
			}
		}

		
//		List<Object> meanvar = DatabaseFactory.executeSQLQuery("select avg("+"( CAST ( " + quantity + " as real))"+"), variance("+"( CAST ( " + quantity + " as real))"+") from "+IOHelper.getInputParameter(config, inputTableParameter), connection);
		String selectMeanVar = "select avg(( CAST ( "+quantity+" as real))), variance(( CAST ( "+quantity+" as real))) from (select "+quantity+" from "+IOHelper.getInputParameter(config, inputTableParameter)+" where "+quantity+" IS NOT NULL and CAST("+quantity+" as character varying) <> '' ) as a";
		AnalysisLogger.getLogger().debug("QuantitiesAttributesCharts: select for mean and variance: "+selectMeanVar);
		List<Object> meanvar = DatabaseFactory.executeSQLQuery(selectMeanVar, connection);
		
		
		Object[] meanvarsrow = {0,0.1};
		
		try{
			meanvarsrow =  (Object[]) meanvar.get(0);}catch(Exception e){
				AnalysisLogger.getLogger().debug("QuantitiesAttributesCharts: cannot detect mean and variance for "+quantity);
		}
		
		double mean = MathFunctions.roundDecimal(Double.parseDouble(""+meanvarsrow[0]),2);
		double variance = MathFunctions.roundDecimal(Math.sqrt(Double.parseDouble(""+meanvarsrow[1])),2);
		
		AnalysisLogger.getLogger().debug("QuantitiesAttributesCharts: " + mean + " and variance:" + variance);
		
		NormalDistributionFunction2D normaldistributionfunction2d = new NormalDistributionFunction2D(mean, variance);
		org.jfree.data.xy.XYSeries gaussianxyseries = DatasetUtilities.sampleFunction2DToSeries(normaldistributionfunction2d, (mean - (2 * variance)), (mean + (2 * variance)), 121, "Distribution of "+quantity);
		XYSeriesCollection gaussianxyseriescollection = new XYSeriesCollection();
		gaussianxyseriescollection .addSeries(gaussianxyseries);
	
		Image charthisto = null;
		Image chartscattering = null;
		Image chartradar = null;

		if (dimensions.length>0){
			charthisto = ImageTools.toImage(HistogramGraph.createStaticChart(datasetHisto).createBufferedImage(1200, 960));
			chartscattering = ImageTools.toImage(ScatterGraphGeneric.createStaticChart(datasetHisto).createBufferedImage(1200, 960));
			chartradar = ImageTools.toImage(RadarGraph.createStaticChart(datasetHisto).createBufferedImage(1200, 960));
		}
		
		Image chartgaussian = ImageTools.toImage(GaussianDistributionGraph.createStaticChart(gaussianxyseriescollection, mean, variance).createBufferedImage(1200, 960));
		
		if (displaychart){
			HistogramGraph tsg = new HistogramGraph("QuantitiesAttributesHistogram");
			tsg.render(datasetHisto);
			
			ScatterGraphGeneric scatter = new ScatterGraphGeneric("QuantitiesAttributesScattering");
			scatter.render(datasetHisto);
			
			RadarGraph radar = new RadarGraph("QuantitiesAttributesRadar");
			radar.render(datasetHisto);
			
			GaussianDistributionGraph gaussian = new GaussianDistributionGraph("Gaussian");
			gaussian.mean=mean;
			gaussian.variance=variance;
			gaussian.render(gaussianxyseriescollection);
			
		}
		
		LinkedHashMap<String , Object> charts = new LinkedHashMap<String, Object>();
		charts.put("Histogram of the top ten quantities over the dimensions", charthisto);
		charts.put("Scattering of the top ten quantities over the dimensions", chartscattering);
		charts.put("Radar chart of the top ten quantities over the dimensions", chartradar);
		charts.put("Overall mean and standard deviation of the quantity", chartgaussian);
		
		return charts;
	}

}
