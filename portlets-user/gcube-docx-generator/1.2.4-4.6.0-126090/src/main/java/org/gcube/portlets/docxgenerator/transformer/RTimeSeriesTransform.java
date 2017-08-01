package org.gcube.portlets.docxgenerator.transformer;



import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.sf.csv4j.ParseException;
import net.sf.csv4j.ProcessingException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.gcube.portlets.d4sreporting.common.shared.BasicComponent;
import org.gcube.portlets.d4sreporting.common.shared.RepTimeSeries;
import org.gcube.portlets.docxgenerator.content.Content;
import org.gcube.portlets.docxgenerator.content.PContent;
import org.gcube.portlets.docxgenerator.content.RContent;
import org.gcube.portlets.docxgenerator.content.TableContent;

public class RTimeSeriesTransform implements Transformer{

	private static final String pathCsvFile = "src/test/resources/fileCsv.csv";
	private static final Log log = LogFactory.getLog(TableTransformer.class);
	@Override
	public ArrayList<Content> transform(BasicComponent component,
			WordprocessingMLPackage wmlPack) {
		
		
		try {
			
			RepTimeSeries rtimeseries = (RepTimeSeries)component.getPossibleContent();
			CSVTimeSeriesParser csvtimeSeriesParser = new CSVTimeSeriesParser();
			int from;
			int to;
			List<Integer> columnsToShow;
			if (rtimeseries.getFilter() != null) {
				from = rtimeseries.getFilter().getFrom();
				to = rtimeseries.getFilter().getTo();
				columnsToShow = rtimeseries.getFilter().getColsNumberToShow();
			} else {
				from = 0;
				to = 9;
				columnsToShow = null;
			}
			String absCsvPath =  rtimeseries.getCsvFile(); // pathFile;
			csvtimeSeriesParser.processFile(absCsvPath, from, to, columnsToShow);
			List<List<String>> table = csvtimeSeriesParser.getTableToShow();
			int writableWidthTwips = wmlPack.getDocumentModel().getSections().get(0).getPageDimensions().getWritableWidthTwips();
			
			int cellWidthTwips = new Double(Math.floor(writableWidthTwips)).intValue();
			
			TableContent tableContent = new TableContent(cellWidthTwips);
			//TableContent tableContent = new TableContent();
			tableContent.setTableStyle("TimeSeriesTable");
			tableContent.addRow();
			
			List<String> header = csvtimeSeriesParser.getHeader();
			for (int i = 0; i < header.size(); i++) {
				PContent pcontent = new PContent();
				RContent rcontent = new RContent();
				rcontent.addText(header.get(i));
				pcontent.addRun(rcontent);
				tableContent.getRow(0).addCell(pcontent);
			}
			tableContent.insertHeader(0);
			tableContent.getRow(0).insertColor("FF0000");
			for (int i = 1; i < table.size(); i++) {
				tableContent.addRow();
				log.debug("transforming........");
				for (int j = 0; j < table.get(i).size(); j++) {
					PContent pcontent = new PContent();
					RContent rcontent = new RContent();
					rcontent.addText(table.get(i).get(j));
					pcontent.addRun(rcontent);
					tableContent.getRow(i).addCell(pcontent);
				}
			}
			ArrayList<Content> list = new ArrayList<Content>();
			list.add(tableContent);
			return list;
		} catch (ParseException e) {
			log.warn("There are errors in the CSV file!");
		} catch (IOException e) {
			log.warn("Cannot find CSV and/or xml style file!");
		} catch (ProcessingException e) {
			log.warn("Problem in processing the CSV file!");
		} 
		return null;
	}

}
