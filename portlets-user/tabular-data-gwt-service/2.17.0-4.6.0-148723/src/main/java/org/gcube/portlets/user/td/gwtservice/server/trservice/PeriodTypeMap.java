package org.gcube.portlets.user.td.gwtservice.server.trservice;

import java.util.ArrayList;
import java.util.List;

import org.gcube.data.analysis.tabulardata.model.ValueFormat;
import org.gcube.data.analysis.tabulardata.model.time.PeriodType;
import org.gcube.portlets.user.td.widgetcommonevent.shared.tr.column.PeriodDataType;
import org.gcube.portlets.user.td.widgetcommonevent.shared.tr.column.ValueDataFormat;

/**
 * 
 * @author Giancarlo Panichi
 *
 *
 */
public class PeriodTypeMap {

	public static PeriodType map(PeriodDataType periodDataType) {
		if (periodDataType.getName().compareTo(PeriodType.CENTURY.name()) == 0) {
			return PeriodType.CENTURY;
		} else {
			if (periodDataType.getName().compareTo(PeriodType.DAY.name()) == 0) {
				return PeriodType.DAY;
			} else {
				if (periodDataType.getName()
						.compareTo(PeriodType.DECADE.name()) == 0) {
					return PeriodType.DECADE;
				} else {
					if (periodDataType.getName().compareTo(
							PeriodType.MONTH.name()) == 0) {
						return PeriodType.MONTH;
					} else {
						if (periodDataType.getName().compareTo(
								PeriodType.QUARTER.name()) == 0) {
							return PeriodType.QUARTER;
						} else {
							if (periodDataType.getName().compareTo(
									PeriodType.YEAR.name()) == 0) {
								return PeriodType.YEAR;
							} else {
								return null;

							}
						}
					}
				}

			}
		}

	}

	public static PeriodDataType map(PeriodType periodType) {
		if (periodType == null) {
			return null;
		} else {
			ArrayList<ValueDataFormat> valueDataFormats = new ArrayList<ValueDataFormat>();
			List<ValueFormat> listTimeFormat = periodType.getAcceptedFormats();
			for (ValueFormat valueF : listTimeFormat) {
				ValueDataFormat valueDataFormat = new ValueDataFormat(
						valueF.getId(), valueF.getExample(), valueF.getRegExpr());
				valueDataFormats.add(valueDataFormat);
			}
			PeriodDataType periodDataType = new PeriodDataType(
					periodType.name(), periodType.getName(), valueDataFormats);
			return periodDataType;
		}
	}

}
