package org.gcube.contentmanagement.graphtools.plotting.graphs;


import org.gcube.contentmanagement.graphtools.abstracts.GenericStandaloneGraph;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.SpiderWebPlot;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.Dataset;

public class RadarGraph extends GenericStandaloneGraph{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public RadarGraph(String title) {
		super(title);
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

	@Override
	protected Dataset generateDataset() {
		 DefaultCategoryDataset dataset = new DefaultCategoryDataset();
         dataset.addValue(35.0, "S1", "C1");
         dataset.addValue(45.0, "S1", "C2");
         dataset.addValue(55.0, "S1", "C3");
         dataset.addValue(15.0, "S1", "C4");
         dataset.addValue(25.0, "S1", "C5");
         dataset.addValue(39.0, "S2", "C1");
         dataset.addValue(20.0, "S2", "C2");
         dataset.addValue(34.0, "S2", "C3");
         dataset.addValue(30.0, "S2", "C4");
         dataset.addValue(13.0, "S2", "C5");
        return dataset;
	}

	
	@Override
	protected JFreeChart createChart(Dataset dataset) {
		
		SpiderWebPlot plot = new SpiderWebPlot((DefaultCategoryDataset)dataset);
        JFreeChart chart = new JFreeChart(plot);

        return chart;
	}

	public static JFreeChart createStaticChart(Dataset dataset) {
		
		SpiderWebPlot plot = new SpiderWebPlot((DefaultCategoryDataset)dataset);
        JFreeChart chart = new JFreeChart(plot);

        return chart;
	}

	@Override
	protected GenericStandaloneGraph getInstance(String title) {
		return new RadarGraph(title);
	}

	

}
