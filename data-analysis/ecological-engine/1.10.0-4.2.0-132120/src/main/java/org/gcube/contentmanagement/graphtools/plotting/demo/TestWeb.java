package org.gcube.contentmanagement.graphtools.plotting.demo;

import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.SpiderWebPlot;
import org.jfree.data.category.DefaultCategoryDataset;

public class TestWeb {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		testDrawWithNullInfo();
	}
	public static void testDrawWithNullInfo() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        dataset.addValue(35.0, "S1", "C1");
        dataset.addValue(45.0, "S1", "C2");
        dataset.addValue(55.0, "S1", "C3");
        dataset.addValue(15.0, "S1", "C4");
        dataset.addValue(25.0, "S1", "C5");
        SpiderWebPlot plot = new SpiderWebPlot(dataset);
        JFreeChart chart = new JFreeChart(plot);
        boolean success = false;
        try {
            BufferedImage image = new BufferedImage(200 , 100,
                    BufferedImage.TYPE_INT_RGB);
            Graphics2D g2 = image.createGraphics();
            chart.draw(g2, new Rectangle2D.Double(0, 0, 200, 100), null, null);
//            g2.dispose();
            
            success = true;
        }
        catch (Exception e) {
            success = false;
        }
    }

}
