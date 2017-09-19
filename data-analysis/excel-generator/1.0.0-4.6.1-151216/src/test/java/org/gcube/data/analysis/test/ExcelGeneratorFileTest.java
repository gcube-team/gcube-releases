package org.gcube.data.analysis.test;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.gcube.data.analysis.excel.data.TableMetaData;
import org.gcube.data.analysis.excel.engine.ExcelGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExcelGeneratorFileTest extends ExcelGenerator{

	private Logger logger;
	
	private String fileName;
	
	public ExcelGeneratorFileTest(TableMetaData tableMetadata, String filePath) {
		super(tableMetadata);
		this.logger = LoggerFactory.getLogger(this.getClass());
		this.fileName = filePath;
	}

	@Override
	public void save() {

		this.logger.debug("Writing file...");
		try {
			FileOutputStream file = new FileOutputStream(this.fileName);
			this.document.write(file);
			file.close();
			this.logger.debug("File written");
		} catch (FileNotFoundException e) {
			this.logger.error("Invalid file ",e);
		} catch (IOException e) {
			this.logger.error("Unable to save the file");
		}
		
		
	}

//	public static void main(String[] args) {
//		TableMetaData table = new TableMetaData("testTable");
//		table.addColumn(new TextColumn("First column"));
//		table.addColumn(new NumberColumn("Second column"));
//		table.addColumn(new TimeDateColumn("Date column"));
//		
//		ExcelGeneratorFileTest test = new ExcelGeneratorFileTest(table, "/home/ciro/textexcel.xls");
//		test.generate();
//		test.save();
//	}
	
	public static void main(String[] args) {

        try {

            FileInputStream excelFile = new FileInputStream("/home/ciro/textexcel.xls");
            Workbook workbook = new XSSFWorkbook(excelFile);
            Sheet datatypeSheet = workbook.getSheetAt(0);
            Iterator<Row> iterator = datatypeSheet.iterator();

            while (iterator.hasNext()) {

                Row currentRow = iterator.next();
                Iterator<Cell> cellIterator = currentRow.iterator();

                while (cellIterator.hasNext()) {

                    Cell currentCell = cellIterator.next();
                    //getCellTypeEnum shown as deprecated for version 3.15
                    //getCellTypeEnum ill be renamed to getCellType starting from version 4.0
                    System.out.println(currentCell.getCellStyle().getDataFormatString());


                }
                System.out.println();

            }
            workbook.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
 

    }
	
}
