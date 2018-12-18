package org.gcube.data.simulfishgrowthdata.util;

import java.util.HashMap;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gr.i2s.fishgrowth.model.WeightLimit;

public class ExcelLimitsReader extends ExcelReader {
	private static final Logger logger = LoggerFactory.getLogger(ExcelLimitsReader.class);

	int fcrIdx;
	int sfrIdx;
	int sgrIdx;
	int mortalityIdx;

	int headersCount;

	protected ExcelLimitsReader() {
		// predefined
		fcrIdx = 0;
		sfrIdx = 1;
		sgrIdx = 2;
		mortalityIdx = 3;

		headersCount = 1;

	}

	protected void importWorkbook(final Session session, final long simulModelId, final String uploadSource,
			final Workbook workbook) {
		final Sheet sheet = workbook.getSheetAt(0);
		if (logger.isTraceEnabled()) {
			logger.trace(String.format("Sheet 0 %s", sheet));
		}

		// checkSheet(sheet);

		Map<Integer, Integer> idxToKind = new HashMap<Integer, Integer>();
		idxToKind.put(fcrIdx, WeightLimit.KPI_KIND_FCR);
		idxToKind.put(sfrIdx, WeightLimit.KPI_KIND_SFR);
		idxToKind.put(sgrIdx, WeightLimit.KPI_KIND_SGR);
		idxToKind.put(mortalityIdx, WeightLimit.KPI_KIND_MORTALITY);

		int rowcnt = 0;
		for (Row row : sheet) {
			rowcnt++;
			logger.debug(String.format("Row [%s] ------------------------------------------------------", rowcnt));
			if (rowcnt <= headersCount) {
				logger.debug(String.format("Row [%s] is header (%s row are headers)", rowcnt, headersCount));
				continue;

			}
			for (Cell cell : row) {
				int colIdx = cell.getColumnIndex();
				if (!idxToKind.containsKey(colIdx)) {
					logger.trace(String.format("Bypassing column [%s] as it isnt mapped", colIdx));
					continue;
				}
				if (cell.getCellTypeEnum() == CellType.BLANK) {
					continue;
				}
				final WeightLimit data = new WeightLimit();
				data.setSimulModelId(simulModelId);
				data.setUploadSource(new Utils().limitLength(uploadSource, 99));
				data.setKpiKind(idxToKind.get(colIdx));
				// mandatory
				data.setToWeight(cell.getNumericCellValue());
				logger.trace(String.format("Inserting from cell at row[%s]col[%s] data [%s]", rowcnt, colIdx, data));
				session.save(data);
			}
		}

	}

}
