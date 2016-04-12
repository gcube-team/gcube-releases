package org.gcube.contentmanagement.graphtools.tests.old;

import java.util.List;

import org.gcube.contentmanagement.graphtools.data.BigSamplesTable;
import org.gcube.contentmanagement.graphtools.data.conversions.GraphConverter2D;
import org.gcube.portlets.user.timeseries.charts.support.types.Point;


public class TestConverters {

	
	public static void main1(String[] args) throws Exception{
		BigSamplesTable bst = new BigSamplesTable();
		
		bst.addSampleRow("prova 1", 10, 15);
		bst.addSampleRow("prova 2", 10, 12);
		bst.addSampleRow("prova 3", 30, 11);
		bst.addSampleRow("prova 1", 10, 15);
		bst.addSampleRow("prova 2", 10, 12);
		bst.addSampleRow("prova 3", 30, 11);
		bst.addSampleRow("prova 1", 10, 15);
		bst.addSampleRow("prova 2", 10, 12);
		bst.addSampleRow("prova 3", 30, 11);
		bst.addSampleRow("prova 1", 10, 15);
		bst.addSampleRow("prova 2", 10, 12);
		bst.addSampleRow("prova 3", 30, 11);
		bst.addSampleRow("prova 1", 10, 15);
		bst.addSampleRow("prova 2", 10, 12);
		bst.addSampleRow("prova 3", 30, 11);
		bst.addSampleRow("prova 1", 10, 15);
		bst.addSampleRow("prova 2", 10, 12);
		bst.addSampleRow("prova 3", 30, 11);
		bst.addSampleRow("prova 1", 10, 15);
		bst.addSampleRow("prova 2", 10, 12);
		bst.addSampleRow("prova 3", 30, 11);
		
		
		System.out.println(bst.toString());
		
		
		List<Point<? extends Number, ? extends Number>> graphicus = GraphConverter2D.convert(bst);
		graphicus = GraphConverter2D.reduceDimension(graphicus);
		
		
		System.out.println("finished!");
		
	}

	
	public static void main(String[] args) throws Exception{
		BigSamplesTable bst = new BigSamplesTable();
		
		bst.addSampleRow("1;prova y1", 0, 0);
		bst.addSampleRow("1;prova y2", 0, 0);
		bst.addSampleRow("1;prova y3", 0, 0);
		bst.addSampleRow("prova 1;prova y1", 10, 15);
		bst.addSampleRow("prova 2;prova y2", 10, 12);
		bst.addSampleRow("prova 3;prova y2", 30, 11);
		bst.addSampleRow("prova 3;prova y1", 30, 10);
		
		System.out.println(bst.toString());
		
		List<Point<? extends Number, ? extends Number>> graphicus = GraphConverter2D.convert(bst);
		graphicus = GraphConverter2D.reduceDimension(graphicus);
		
		System.out.println("finished!");
		
	}
}
