package org.gcube.dataanalysis.wps.statisticalmanager.synchserver.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.commons.lang.StringEscapeUtils;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class GML2CSV {

	public static void main(String[] args) throws Exception {
//		System.out.println(StringEscapeUtils.unescapeHtml("&lt;ciao"));
		
		parseGML("test.gml","test.csv");
	}
	
	public static void convertHTMLToXML(String gmlhtmlFile,String gmlxmlFile) throws Exception{
		BufferedWriter filew = new BufferedWriter(new FileWriter(new File(gmlxmlFile))); 
		BufferedReader filer = new BufferedReader(new FileReader(new File(gmlhtmlFile)));
		String line = filer.readLine();
		while (line !=null){
			filew.write(StringEscapeUtils.unescapeHtml(line)+"\n");
			line = filer.readLine();
		}
				
		filer.close();
		filew.close();
	}
	
	public static void parseGML(String gmlFile, String csvFile) {

		try {
			String gmlXMLFile = gmlFile+".xml";
			convertHTMLToXML(gmlFile, gmlXMLFile);
			gmlFile=gmlXMLFile;
			
			SAXParserFactory factory = SAXParserFactory.newInstance();
			SAXParser saxParser = factory.newSAXParser();
			final BufferedWriter filew = new BufferedWriter(new FileWriter(new File(csvFile))); 
			DefaultHandler handler = new DefaultHandler() {
				String lastTagContent = "";
				int tagcounter = 0;
				int maxtags = 0;
				boolean startrecording = false;
				boolean skiprecording = false;
				boolean headerwritten = false;
				ArrayList<String> row = new ArrayList<String>();
				ArrayList<String> header = new ArrayList<String>();
				
				public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
					if (qName.equals("gml:featureMember"))
						startrecording = true;
					
					if (startrecording && qName.equals("ogr:geometryProperty"))
						skiprecording = true;
					
					if (startrecording & !skiprecording) {
						// System.out.println("start qName:"+qName);
						tagcounter++;
						if (maxtags < tagcounter)
							maxtags = tagcounter;
					}
					

					
				}

				public void endElement(String uri, String localName, String qName) throws SAXException {
					// System.out.println("end qName:"+qName);
					if (startrecording && !skiprecording) {
						
						if (tagcounter == maxtags){
							
							if (lastTagContent!=null && lastTagContent.trim().length()>0){
								row.add(lastTagContent);
								String headerStr = qName.substring(qName.indexOf(":")+1);
								header.add(headerStr);
							}
						}
						else{
							//write the buffer
							try {
								if (row.size()>0){
									if (!headerwritten){
										String stringheader = Array2CSVString(header);
										filew.write(stringheader+"\n");
										headerwritten=true;
									}
										
									String stringrow = Array2CSVString(row);
									filew.write(stringrow+"\n");
									row = new ArrayList<String>();
								}
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
						tagcounter--;
					}
					if (qName.equals("ogr:geometryProperty"))
						skiprecording = false;
				}

				public void characters(char ch[], int start, int length) throws SAXException {
					if (startrecording & !skiprecording) {
						String element = new String(ch, start, length);
						lastTagContent = element;
						// System.out.println("chr element:"+element);
					}
				}

			};

			saxParser.parse(gmlFile, handler);
			filew.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static String Array2CSVString(ArrayList<String> row){
		StringBuffer sb = new StringBuffer();
		int rowl = row.size();
		//TODO check other conditions to quote
		for (int i=0;i<rowl;i++){
			String csvr = row.get(i).trim();
			if (csvr.contains(","))
				csvr = "\""+csvr+"\""; 
			sb.append(csvr);
			if (i<rowl-1)
				sb.append(",");
		}
		return sb.toString();
	}

}
