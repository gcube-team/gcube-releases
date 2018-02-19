package org.gcube.dataanalysis.ecoengine.evaluation.bioclimate;

import java.awt.Color;
import java.awt.Image;

import org.gcube.contentmanagement.graphtools.abstracts.GenericStandaloneGraph;
import org.gcube.contentmanagement.graphtools.data.conversions.ImageTools;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.Dataset;
import org.jfree.ui.RectangleInsets;

public class BioClimateGraph extends GenericStandaloneGraph {

	private static final long serialVersionUID = 1L;
	double max;
	double min;
	public BioClimateGraph(String title,double max,double min) {
		super(title);
		this.max= max;
		this.min = min;
	}

	protected Dataset generateDataset() {
		 // row keys...
        String series1 = "First";
        String series2 = "Second";
        String series3 = "Third";

        // column keys...
        String type1 = "Type 1";
        String type2 = "Type 2";
        String type3 = "Type 3";
        String type4 = "Type 4";
        String type5 = "Type 5";
        String type6 = "Type 6";
        String type7 = "Type 7";
        String type8 = "Type 8";

        // create the dataset...
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        dataset.addValue(1.0, series1, type1);
        dataset.addValue(4.0, series1, type2);
        dataset.addValue(3.0, series1, type3);
        dataset.addValue(5.0, series1, type4);
        dataset.addValue(5.0, series1, type5);
        dataset.addValue(7.0, series1, type6);
        dataset.addValue(7.0, series1, type7);
        dataset.addValue(8.0, series1, type8);

        dataset.addValue(5.0, series2, type1);
        dataset.addValue(7.0, series2, type2);
        dataset.addValue(6.0, series2, type3);
        dataset.addValue(8.0, series2, type4);
        dataset.addValue(4.0, series2, type5);
        dataset.addValue(4.0, series2, type6);
        dataset.addValue(2.0, series2, type7);
        dataset.addValue(1.0, series2, type8);

        dataset.addValue(4.0, series3, type1);
        dataset.addValue(3.0, series3, type2);
        dataset.addValue(2.0, series3, type3);
        dataset.addValue(3.0, series3, type4);
        dataset.addValue(6.0, series3, type5);
        dataset.addValue(3.0, series3, type6);
        dataset.addValue(4.0, series3, type7);
        dataset.addValue(3.0, series3, type8);
        return dataset;
	}

	
	public static Image renderStaticImgObject(int width, int height, Dataset set, String title, double max, double min) {
		
		JFreeChart chart = createStaticChart(set,max,min,title);
		
		/*
		JPanel jp = new ChartPanel(chart);

		this.setContentPane(jp);
		this.pack();
		 */
//		Image image = this.createImage(width, height);
		
		Image image = ImageTools.toImage(chart.createBufferedImage(width, height));
		
		return image;
	}

	
	protected static JFreeChart createStaticChart(Dataset dataset, double max, double min, String title) {

		 // create the chart...
       JFreeChart chart = ChartFactory.createLineChart(
           title,       // chart title
           "",                    // domain axis label
           "",                   // range axis label
           (DefaultCategoryDataset)dataset,                   // data
           PlotOrientation.VERTICAL,  // orientation
           true,                      // include legend
           true,                      // tooltips
           false                      // urls
       );
       chart.setBackgroundPaint(Color.white);
       
       CategoryPlot plot = chart.getCategoryPlot();
//      plot.setBackgroundPaint(Color.white);
       plot.setRangeGridlinePaint(Color.white);
       plot.setDomainCrosshairVisible(true);
       plot.setDomainGridlinesVisible(true);
       plot.setRangeCrosshairVisible(true);
       plot.setRenderer(new LineAndShapeRenderer(true,true));

       CategoryAxis categoryaxis1 = plot.getDomainAxis(0);
       categoryaxis1.setCategoryLabelPositions(CategoryLabelPositions.DOWN_45);
       
       plot.mapDatasetToDomainAxis(0, 0);
         
       if (max!=min){
       plot.getRangeAxis().setAutoRange(false);
       plot.getRangeAxis().setUpperBound(max);
       plot.getRangeAxis().setLowerBound(min);
       double avg = min+((max-min)/2d);
       plot.getRangeAxis().centerRange(avg);
       }
       
       return chart;
	}
	
	protected JFreeChart createChart(Dataset dataset) {

		 // create the chart...
        JFreeChart chart = ChartFactory.createLineChart(
            this.getTitle(),       // chart title
            "",                    // domain axis label
            "",                   // range axis label
            (DefaultCategoryDataset)dataset,                   // data
            PlotOrientation.VERTICAL,  // orientation
            true,                      // include legend
            true,                      // tooltips
            false                      // urls
        );
        chart.setBackgroundPaint(Color.white);
        
        CategoryPlot plot = chart.getCategoryPlot();
//       plot.setBackgroundPaint(Color.white);
        plot.setRangeGridlinePaint(Color.white);
        plot.setDomainCrosshairVisible(true);
        plot.setDomainGridlinesVisible(true);
        plot.setRangeCrosshairVisible(true);
        plot.setRenderer(new LineAndShapeRenderer(true,true));

        CategoryAxis categoryaxis1 = plot.getDomainAxis(0);
        categoryaxis1.setCategoryLabelPositions(CategoryLabelPositions.DOWN_45);
        
        plot.mapDatasetToDomainAxis(0, 0);
        
        
//        plot.zoomRangeAxes(0.1,10d,null,null);
//       
        if (max!=min){
        plot.getRangeAxis().setAutoRange(false);
        plot.getRangeAxis().setUpperBound(max);
        plot.getRangeAxis().setLowerBound(min);
        double avg = min+((max-min)/2d);
        plot.getRangeAxis().centerRange(avg);
        }
        //deprecated
        /*
        LineAndShapeRenderer renderer = (LineAndShapeRenderer) plot.getRenderer();
        renderer.setShapesVisible(true);
        renderer.setDrawOutlines(true);
        renderer.setUseFillPaint(true);
        renderer.setFillPaint(Color.white);
*/
        
        

        
		return chart;
	}

	@Override
	protected GenericStandaloneGraph getInstance(String title) {
		return new BioClimateGraph(title,max,min);
	}

}
