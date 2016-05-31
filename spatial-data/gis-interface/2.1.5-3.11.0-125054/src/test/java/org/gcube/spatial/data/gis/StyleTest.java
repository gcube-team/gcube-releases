package org.gcube.spatial.data.gis;

import java.awt.Color;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import net.sf.csv4j.CSVLineProcessor;
import net.sf.csv4j.CSVReaderProcessor;
import net.sf.csv4j.ParseException;
import net.sf.csv4j.ProcessingException;

import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.spatial.data.gis.symbology.ClassStyleDef;
import org.gcube.spatial.data.gis.symbology.StyleUtils;

public class StyleTest {

	
	public static void main(String[] args) throws Exception{
		ScopeProvider.instance.set("/gcube/devsec");
		String name="TDM_Style_"+System.currentTimeMillis();
		String attribute="fbokpc";
		Color c1=Color.RED;
		Color c2=Color.YELLOW;
		List<ClassStyleDef> classes=loadFromCSV("/home/fabio/distinct.sql");
		String sld=StyleUtils.createStyle(name, attribute, (ArrayList<ClassStyleDef>) classes, c1, c2);
		GISInterface gis= GISInterface.get();		
		System.out.println(gis.publishStyle(sld, name));		
		System.out.println("Published "+name+" on "+gis.getCurrentGeoServerDescriptor());
	}
	
	
	private static List<ClassStyleDef> loadFromCSV(String csvPath) throws ParseException, FileNotFoundException, IOException, ProcessingException{
		final ArrayList<ClassStyleDef> toReturn=new ArrayList<>();
		
		//starting csv processing
				CSVReaderProcessor csvReaderProcessor = new CSVReaderProcessor();
				csvReaderProcessor.setDelimiter(',');
				csvReaderProcessor.setHasHeader(true);
				csvReaderProcessor.processStream(new InputStreamReader(new FileInputStream(csvPath)), new CSVLineProcessor() {
					
					@Override
					public void processHeaderLine(int arg0, List<String> arg1) {
						// TODO Auto-generated method stub
						
					}
					
					@Override
					public void processDataLine(int arg0, List<String> arg1) {
						String featureValue=arg1.get(0);
						toReturn.add(new ClassStyleDef(featureValue));
					}
					
					@Override
					public boolean continueProcessing() {
						return true;
					}
				});
		
		return toReturn;
	}
	
}
