package org.gcube.contentmanagement.graphtools.examples.graphsTypes;

import java.applet.Applet;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.List;

import javax.imageio.ImageIO;

import org.gcube.contentmanagement.graphtools.core.StatisticsGenerator;
import org.gcube.contentmanagement.graphtools.data.conversions.ImageTools;
import org.gcube.contentmanagement.graphtools.plotting.graphs.GaussianDistributionGraph;
import org.gcube.contentmanagement.graphtools.plotting.graphs.ScatterGraphGeneric;
import org.gcube.contentmanagement.graphtools.plotting.graphs.ScatterGraphNumeric;
import org.gcube.contentmanagement.lexicalmatcher.utils.AnalysisLogger;
import org.gcube.portlets.user.timeseries.charts.support.types.GraphGroups;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;



public class ExampleImage extends Applet {

	public void paint1(Graphics g) {
		BufferedImage bgimg = loadImage();
		g.drawImage(bgimg, 0, 0, this);
//		g.fillRect(0, 0, 10, 10);
		
	}
	static int width = 320;
	static int height = 280;
	
    private BufferedImage loadImage() {
        String imgFileName = "C:/Users/coro/Desktop/WorkFolder/Workspace/StatisticsExtractor/weather-cloud.png";
        
        BufferedImage img = null;
        try {
            img =  ImageIO.read(new File(imgFileName));
        } catch (Exception e) {
        	e.printStackTrace();
        }
        return img;
    }

    public void init() {
        
    	
        setBackground( Color.LIGHT_GRAY );
        setSize(width, height);
     }

    
    
//	public void paint(Graphics g){
    public static void main(String[] args){
		
		try{
			
		String table = "ts_161efa00_2c32_11df_b8b3_aa10916debe6";
		String xDimension = "field5";
		String yDimension = "field6";
		String groupDimension = "field1";
		String speciesColumn = "field3";
		String filter1 = "Brown seaweeds";
		
		StatisticsGenerator stg = new StatisticsGenerator();
		stg.init("C:/Users/coro/Desktop/WorkFolder/Workspace/StatisticsExtractor/cfg/");
		GraphGroups gg = stg.generateGraphs(200, table, xDimension, yDimension, groupDimension, speciesColumn, filter1);
		
//		ScatterGraphNumeric series = new ScatterGraphNumeric("");
		
//		GaussianDistributionGraph series = new GaussianDistributionGraph("");
		ScatterGraphGeneric series = new ScatterGraphGeneric("");
		
		series.renderImages("./saved",width,height,gg);
		
		AnalysisLogger.getLogger().debug("finished");
		
		System.exit(0);
		
		/* OLD CODE
		List<Image> image = series.renderGraphGroupImage(width,height,gg);
		
		Image singleimage = image.get(1);
		
		
		BufferedImage bimage = ImageTools.toBufferedImage(singleimage);
		
		XStream xStream = new XStream(new DomDriver()); 
		
		String xmlimage = xStream.toXML(singleimage);
		
		System.out.println(xmlimage);
		
		File outputfile = new File("saved.png");
		
		ImageIO.write(bimage, "png", outputfile);
		*/
		
		}catch(Exception e){
			
			e.printStackTrace();
			
		}
		
		
	}
    
}
