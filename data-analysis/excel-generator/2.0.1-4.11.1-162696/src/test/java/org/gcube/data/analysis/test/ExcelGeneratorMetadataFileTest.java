package org.gcube.data.analysis.test;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.gcube.data.analysis.excel.ColumnModel.ColumnType;
import org.gcube.data.analysis.excel.data.StringColumn;
import org.gcube.data.analysis.excel.engine.ExcelGeneratorMetadata;
import org.gcube.data.analysis.excel.metadata.MetadataTable;
import org.gcube.data.analysis.excel.metadata.MetadataTableImpl;
import org.gcube.data.analysis.excel.metadata.format.CatchMeasureFormat;
import org.gcube.data.analysis.excel.metadata.format.CodeList;
import org.gcube.data.analysis.excel.metadata.format.CodelistDataFormat;
import org.gcube.data.analysis.excel.metadata.format.GenericFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExcelGeneratorMetadataFileTest extends ExcelGeneratorMetadata{

	private Logger logger;
	
	private String fileName;
	
	public ExcelGeneratorMetadataFileTest(MetadataTable table, String filePath) {
		super(table);
		this.logger = LoggerFactory.getLogger(this.getClass());
		this.fileName = filePath;
	}

	@Override
	public void save() {

		this.logger.debug("Writing file...");
		try {
			FileOutputStream file = new FileOutputStream(this.fileName);
			getFinalDocument().write(file);
			file.close();
			this.logger.debug("File written");
		} catch (FileNotFoundException e) {
			this.logger.error("Invalid file ",e);
		} catch (IOException e) {
			this.logger.error("Unable to save the file");
		}
		
		
	}

	public static void main(String[] args) {
		MetadataTableImpl table = new MetadataTableImpl("testTableMetadata");

		CodeList codelist = new CodeList("codelist");
		codelist.addColumn("id", Arrays.asList("id1","id2","id3"),false);
		codelist.addColumn("names", Arrays.asList("italy","germany","france"),true);
		List<Map<String, List<String>>> codelistColumns = new ArrayList<>();
		Map<String, List<String>> codelistColumn1 = new HashMap<>();
		codelistColumn1.put("id", Arrays.asList("id1","id2","id3"));
		Map<String, List<String>> codelistColumn2 = new HashMap<>();
		codelistColumn2.put("names", Arrays.asList("italy","germany","france"));
		codelistColumns.add(codelistColumn1);
		codelistColumns.add(codelistColumn2);
		table.addColumn(new StringColumn("id", new GenericFormat("idFormat", "numeric"),new ArrayList<>(Arrays.asList("1","2","3")),ColumnType.ATTRIBUTE));
		table.addColumn(new StringColumn("primaryMeasure", new CatchMeasureFormat("catchmeasureFormat", Arrays.asList("numeric","string")),ColumnType.MEASURE));
		table.addColumn(new StringColumn("timeColumn", new GenericFormat("timeFormat", "hh/mm/ss"),new ArrayList<>(Arrays.asList("10/32/00","11/32/00","12/32/00")),ColumnType.TIMEDIMENSION));
		table.addColumn(new StringColumn("codeColumn", new CodelistDataFormat("codelistColumn",Arrays.asList("id2","id1","id2"),Arrays.asList("value1","value2","value3")),ColumnType.ATTRIBUTE));
		ExcelGeneratorMetadataFileTest test = new ExcelGeneratorMetadataFileTest(table, "/home/ciro/textexcel.xls");
		test.generate();
		test.save();
	}
	

	
//	public static void main(String[] args) {
//
//        try {
//
//            FileInputStream excelFile = new FileInputStream("/home/ciro/textexcel.xls");
//            Workbook workbook = new XSSFWorkbook(excelFile);
//            Sheet datatypeSheet = workbook.getSheetAt(0);
//            Iterator<Row> iterator = datatypeSheet.iterator();
//
//            while (iterator.hasNext()) {
//
//                Row currentRow = iterator.next();
//                Iterator<Cell> cellIterator = currentRow.iterator();
//
//                while (cellIterator.hasNext()) {
//
//                    Cell currentCell = cellIterator.next();
//                    //getCellTypeEnum shown as deprecated for version 3.15
//                    //getCellTypeEnum ill be renamed to getCellType starting from version 4.0
//                    System.out.println(currentCell.getCellStyle().getDataFormatString());
//
//
//                }
//                System.out.println();
//
//            }
//            workbook.close();
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
// 
//
//    }
	
}
