package org.gcube.contentmanagement.graphtools.abstracts;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

import org.gcube.contentmanagement.graphtools.data.conversions.ImageTools;
import org.gcube.contentmanagement.lexicalmatcher.utils.AnalysisLogger;
import org.gcube.portlets.user.timeseries.charts.support.types.GraphData;
import org.gcube.portlets.user.timeseries.charts.support.types.GraphGroups;
import org.gcube.portlets.user.timeseries.charts.support.types.Point;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.Dataset;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

/*
 * Converts a GraphData into a graphicable structure DataSet
 * GenericStandaloneGraph: GraphData -> DataSet
 */
public abstract class GenericStandaloneGraph extends ApplicationFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected boolean big;

	public GenericStandaloneGraph(String title) {
		super(title);
		
		big = false;
	}
	
	
	abstract protected Dataset generateDataset();

	abstract protected JFreeChart createChart(Dataset dataset);

	abstract protected GenericStandaloneGraph getInstance(String title);

	public void render(Dataset set) {

		render(-1, -1, set);
	}

	public void render(double x, double y, Dataset set) {
		JFreeChart chart = createChart(set);
		JPanel jp = new ChartPanel(chart);

		this.setContentPane(jp);
		this.pack();
		if (big)
			this.setBounds(0, 0, (int) this.getBounds().getWidth() * 2, (int) this.getBounds().getHeight() * 2);

		if ((x == -1) || (y == -1))
			RefineryUtilities.centerFrameOnScreen(this);
		else
			RefineryUtilities.positionFrameOnScreen(this, x, y);

		this.setVisible(true);

	}

	public List<Image> renderGraphGroupImage(int width, int height, GraphGroups graphgroups) {

		ArrayList<Image> images = new ArrayList<Image>();

		Map<String, GraphData> graphmap = graphgroups.getGraphs();
		double x = 0;
		double y = 0;
		double max = 1;
		// int numberOfGraphs = graphmap.size();

		for (String key : graphmap.keySet()) {

			GenericStandaloneGraph graph = getInstance(key);
			Dataset datas = graph.convert2Dataset(graphmap.get(key));
			images.add(graph.renderImgObject(width, height, datas));

			x += 0.1;
			y += 0.1;
			if (x > max || y > max) {
				x = 0;
				y = 0;
			}
		}

		return images;

	}

	public void renderImages(String filePath, int width, int height, GraphGroups graphgroups) {

		List<Image> images = renderGraphGroupImage(width,height,graphgroups);
		int i=0;
		for (Image img:images){
			BufferedImage bimage = ImageTools.toBufferedImage(img);
			File outputfile = new File(filePath+"_"+i+".png");
			try{
			ImageIO.write(bimage, "png", outputfile);
			}catch(Exception e){
				AnalysisLogger.getLogger().error("renderImages->Error in writing files ",e);
			}
			i++;
		}
		
	}
	
	public Image renderImgObject(int width, int height, Dataset set) {
		JFreeChart chart = createChart(set);
		
		/*
		JPanel jp = new ChartPanel(chart);

		this.setContentPane(jp);
		this.pack();
		 */
//		Image image = this.createImage(width, height);
		
		Image image = ImageTools.toImage(chart.createBufferedImage(width, height));
		
		return image;
	}

	
	public void renderGraphGroup(GraphGroups graphgroups) {

		Map<String, GraphData> graphmap = graphgroups.getGraphs();
		double x = 0;
		double y = 0;
		double max = 1;
		// int numberOfGraphs = graphmap.size();

		for (String key : graphmap.keySet()) {

			GenericStandaloneGraph graph = getInstance(key);
			Dataset datas = graph.convert2Dataset(graphmap.get(key));
			graph.render(x, y, datas);

			x += 0.1;
			y += 0.1;
			if (x > max || y > max) {
				x = 0;
				y = 0;
			}
		}
	}

	protected Dataset convert2Dataset(GraphData st) {

		DefaultCategoryDataset dataset = new DefaultCategoryDataset();
		List<Point<? extends Number, ? extends Number>> pointslist = st.getData();

		// NOTE: after the graph generation graphs are inverted in x and y
		int numbOfRows = pointslist.size();
		if (numbOfRows > 0) {
			int numbOfCols = pointslist.get(0).getEntries().size();

			for (int x = 0; x < numbOfRows; x++) {

				String xlabel = pointslist.get(x).getLabel();

				for (int y = 0; y < numbOfCols; y++) {

					double value = pointslist.get(x).getEntries().get(y).getValue().doubleValue();
					String ylabel = pointslist.get(x).getEntries().get(y).getLabel();

					// System.out.println("ADDING : "+value+" , "+ylabel+" , "+xlabel);

					dataset.addValue(value, xlabel, ylabel);
				}
			}

		}
		return dataset;
	}

	
}
