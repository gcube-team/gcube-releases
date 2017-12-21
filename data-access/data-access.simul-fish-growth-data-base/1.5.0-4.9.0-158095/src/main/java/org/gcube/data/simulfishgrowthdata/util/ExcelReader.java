package org.gcube.data.simulfishgrowthdata.util;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.ss.util.CellReference;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class ExcelReader {

	public static int KIND_SAMPLE = 1;
	public static int KIND_LIMITS = 2;

	synchronized static public ExcelReader instance(int kind) {
		if (kind == KIND_SAMPLE)
			return new ExcelDataReader();
		else if (kind == KIND_LIMITS)
			return new ExcelLimitsReader();
		throw new RuntimeException(String.format("Uknown kind [%s]", kind));
	}

	protected static final Logger logger = LoggerFactory.getLogger(ExcelReader.class);

	protected ExcelReader() {
		super();
	}

	public void importLocal(final Session session, final long simulModelId, final String filename)
			throws EncryptedDocumentException, InvalidFormatException, IOException {
		importData(session, simulModelId, filename, new File(filename));
	}

	public void importRemote(final Session session, final long simulModelId, final String fileUrl)
			throws EncryptedDocumentException, InvalidFormatException, MalformedURLException, IOException {
		// https causes reader to fail
		importData(session, simulModelId, fileUrl, new URL(fileUrl.replace("https://", "http://")));
	}

	public void importData(final Session session, final long simulModelId, final String uploadSource, final URL fileUrl)
			throws IOException, EncryptedDocumentException, InvalidFormatException {
		if (logger.isTraceEnabled()) {
			logger.trace(String.format("Importing for simulModelId [%s] from [%s]", simulModelId, fileUrl));
		}
		Workbook workbook = WorkbookFactory.create(fileUrl.openStream());
		importWorkbook(session, simulModelId, uploadSource, workbook);
		workbook.close();
	}

	public void importData(final Session session, final long simulModelId, final String uploadSource, final File file)
			throws IOException, EncryptedDocumentException, InvalidFormatException {
		if (logger.isTraceEnabled()) {
			logger.trace(String.format("Importing for simulModelId [%s] from [%s]", simulModelId, uploadSource));
		}
		Workbook workbook = WorkbookFactory.create(file);
		importWorkbook(session, simulModelId, file.getAbsolutePath(), workbook);
		workbook.close();
	}

	protected abstract void importWorkbook(final Session session, final long simulModelId, final String uploadSource,
			final Workbook workbook);

	/**
	 * debug purposes. outputs the content + info
	 * 
	 * @param sheet
	 */
	public void rawSheet(final Sheet sheet) {
		int rowcnt = 0;
		for (Row row : sheet) {
			logger.debug(String.format("Row [%s] ------------------------------------------------------", ++rowcnt));
			for (Cell cell : row) {
				final CellReference cellRef = new CellReference(row.getRowNum(), cell.getColumnIndex());
				logger.debug(String.format("CellRef [%s]", cellRef.formatAsString()));

				// get the text that appears in the cell by getting the cell
				// value and applying any data formats (Date, 0.00, 1.23e9,
				// $1.23, etc)
				// DataFormatter formatter = new DataFormatter();
				// String text = formatter.formatCellValue(cell);
				// System.out.println(text);

				// Alternatively, get the value and format it yourself
				switch (cell.getCellTypeEnum()) {
				case STRING:
					logger.debug(String.format("Value is string [%s]", cell.getRichStringCellValue().getString()));
					break;
				case NUMERIC:
					if (DateUtil.isCellDateFormatted(cell)) {
						logger.debug(String.format("Value is numeric - date formatted [%s]", cell.getDateCellValue()));
					} else {
						logger.debug(String.format("Value is numeric [%s]", cell.getNumericCellValue()));
					}
					break;
				case BOOLEAN:
					logger.debug(String.format("Value is boolean [%s]", cell.getBooleanCellValue()));
					break;
				case FORMULA:
					logger.debug(String.format("Value is formula [%s]", cell.getCellFormula()));
					break;
				case BLANK:
					logger.debug(String.format("Value is blank"));
					break;
				default:
					logger.debug(String.format("Value without known CellType [%s] !!!", cell.getCellTypeEnum()));
				}
			}
		}

	}

}