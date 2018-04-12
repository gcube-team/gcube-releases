/**
 * 
 */
package org.gcube.common.homelibrary.performance.tool;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import javax.imageio.ImageIO;

/**
 * @author Federico De Faveri defaveri@isti.cnr.it
 *
 */
public class MeasurementGraphGenerator {
	
	/**
	 * @param args not used.
	 * @throws IOException if an error occurs.
	 */
	public static void main(String[] args) throws IOException
	{
		MeasurementChannel channel = new MeasurementChannel("MyTestChannel", new MeasurementSession("test"));
		
		Random random = new Random();
		long time = random.nextInt(1500);
		for (int i = 0; i<100; i++){
			time += random.nextInt(100);
			channel.addData(new MeasurementData(time, i));
			System.out.println("generating i: "+i+" time: "+time);
		}
		
		generateChannelGraph(channel, 800, 200);
	}
	
	/**
	 * @param channel the channel.
	 * @param width graph width.
	 * @param height graph height.
	 * @throws IOException if an error occurs.
	 */
	public static void generateChannelGraph(MeasurementChannel channel, int width, int height) throws IOException
	{
		System.out.println();
		int padding = 10;
				
		BufferedImage off_Image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

		Graphics2D g2 = off_Image.createGraphics();
		
		//background
		g2.setColor(Color.WHITE);
		g2.fillRect(0, 0, width, height);
		

		Rectangle graphArea = new Rectangle(padding, height-padding, width-padding, height-padding);

		drawGraph(g2, channel.getData(), graphArea);
		
        File outputfile = new File("saved.png");
        ImageIO.write(off_Image, "png", outputfile);

	}
	
	protected static void drawGraph(Graphics2D g2, List<MeasurementData> datas, Rectangle graphArea)
	{
		System.out.println("drawGraph graphArea: "+graphArea);
		
		Point zero = graphArea.getLocation();
		int width = graphArea.width;
		int height = graphArea.height;
		System.out.println("zero: "+zero+" width: "+width+" height:"+height);
		
		g2.setColor(Color.BLACK);
		g2.drawLine(zero.x, zero.y, zero.x, zero.y-height);
		g2.drawLine(zero.x, zero.y, zero.x+width, zero.y);
		
		long minTime = Long.MAX_VALUE;
		long maxTime = Long.MIN_VALUE;
		long minValue = Long.MAX_VALUE;
		long maxValue = Long.MIN_VALUE;
		
		for (MeasurementData data:datas){
			minTime = Math.min(minTime, data.getTime());
			maxTime = Math.max(maxTime, data.getTime());
			maxValue = Math.max(maxValue, data.getValue());
			minValue = Math.min(minValue, data.getValue());
		}
		
		System.out.println("minTime: "+minTime);
		System.out.println("maxTime: "+maxTime);
		System.out.println("minValue: "+minValue);
		System.out.println("maxValue: "+maxValue);
				
		
		double deltaValue = maxValue-minValue;
		double deltaTime = maxTime-minTime;
		
		int n = 10;
		double widthStep = width/n;
		for (int i = 0; i<n; i++){
			int x = (int)(zero.x+i*widthStep);
			g2.drawLine(x, zero.y, x, zero.y+3);
			g2.rotate(Math.PI/2, x, zero.y);
			g2.drawString("t"+x, x+2, zero.y);
			g2.rotate(-Math.PI/2, x, zero.y);
		}
		
		Collections.sort(datas);
		
		MeasurementData prev = datas.get(0);
		int y = (int)(zero.y - ((prev.getTime()-minTime)*height)/deltaTime);
		int x = (int)(zero.x + ((prev.getValue()-minValue)*width)/deltaValue);
		Point prevPoint = new Point(x, y);
		
		g2.setColor(Color.RED);
		for (int i = 1; i<datas.size(); i++){
			
			MeasurementData curr = datas.get(i);
			
			y = (int)(zero.y - ((curr.getTime()-minTime)*height)/deltaTime);
			x = (int)(zero.x + ((curr.getValue()-minValue)*width)/deltaValue);
			Point currPoint = new Point(x, y);
			
			System.out.println("line from "+prevPoint+" to "+currPoint);
			g2.drawLine(prevPoint.x, prevPoint.y, currPoint.x, currPoint.y);
			
			prevPoint = currPoint;
		}
		
	}

}
