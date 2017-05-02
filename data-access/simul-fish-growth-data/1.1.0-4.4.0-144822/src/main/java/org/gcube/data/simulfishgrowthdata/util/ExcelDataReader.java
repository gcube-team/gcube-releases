package org.gcube.data.simulfishgrowthdata.util;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gr.i2s.fishgrowth.model.SampleData;

public class ExcelDataReader extends ExcelReader {
	protected static final Logger logger = LoggerFactory.getLogger(ExcelDataReader.class);

	private static final double TEMPERATURE_OUT_OF_RANGE = 9999;

	int dateFromIdx;
	int dateToIdx;
	int openWeightIdx;
	int closeWeightIdx;
	int avgTemperatureIdx;
	int openFishNoIdx;
	int closeFishNoIdx;
	int fcrIdx;
	int mortalityRateIdx;
	int sfrIdx;
	int sgrIdx;

	int headersCount;

	protected ExcelDataReader() {
		// predefined
		dateFromIdx = 1;
		dateToIdx = 2;
		openWeightIdx = 3;
		closeWeightIdx = 4;
		avgTemperatureIdx = 5;
		openFishNoIdx = 6;
		closeFishNoIdx = 7;
		fcrIdx = 8;
		mortalityRateIdx = 9;
		sfrIdx = 10;
		sgrIdx = 10;

		headersCount = 1;

	}

	protected void importWorkbook(final Session session, final long simulModelId, final String uploadSource,
			final Workbook workbook) {
		final Sheet sheet = workbook.getSheetAt(0);
		if (logger.isTraceEnabled()) {
			logger.trace(String.format("Sheet 0 %s", sheet));
		}

		// checkSheet(sheet);

		int rowcnt = 0;
		for (Row row : sheet) {
			rowcnt++;
			logger.debug(String.format("Row [%s] ------------------------------------------------------", rowcnt));
			if (rowcnt <= headersCount) {
				logger.debug(String.format("Row [%s] is header (%s row are headers)", rowcnt, headersCount));
				continue;

			}
			final SampleData data = new SampleData();
			data.setSimulModelId(simulModelId);
			data.setUploadSource(uploadSource);
			// mandatory
			data.setDateFrom(row.getCell(dateFromIdx).getDateCellValue());
			data.setDateTo(row.getCell(dateToIdx).getDateCellValue());
			data.setOpenWeight(row.getCell(openWeightIdx).getNumericCellValue());
			data.setCloseWeight(row.getCell(closeWeightIdx).getNumericCellValue());
			data.setAvgTemperature((int) row.getCell(avgTemperatureIdx).getNumericCellValue());
			data.setOpenFishNo((int) row.getCell(openFishNoIdx).getNumericCellValue());
			data.setCloseFishNo((int) row.getCell(closeFishNoIdx).getNumericCellValue());
			data.setFcr(row.getCell(fcrIdx).getNumericCellValue());
			data.setSfr(row.getCell(sfrIdx).getNumericCellValue());
			data.setSgr(row.getCell(sgrIdx).getNumericCellValue());
			// optional
			Cell cell = row.getCell(mortalityRateIdx);
			if (cell.getCellTypeEnum() == CellType.BLANK)
				data.setMortalityRate(TEMPERATURE_OUT_OF_RANGE);
			else
				data.setMortalityRate(cell.getNumericCellValue());
			logger.debug(String.format("Inserting from row [%s] data [%s] ", rowcnt, data));
			session.save(data);
		}

	}
}
