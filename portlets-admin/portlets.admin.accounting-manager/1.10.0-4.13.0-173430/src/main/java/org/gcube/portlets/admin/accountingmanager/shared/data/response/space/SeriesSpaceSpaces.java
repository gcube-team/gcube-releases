package org.gcube.portlets.admin.accountingmanager.shared.data.response.space;

import java.util.ArrayList;

import org.gcube.portlets.admin.accountingmanager.shared.data.ChartType;
import org.gcube.portlets.admin.accountingmanager.shared.data.Spaces;

/**
 * 
  * @author Giancarlo Panichi
 *
 *
 */
public class SeriesSpaceSpaces extends SeriesSpaceDefinition {

	private static final long serialVersionUID = -1704880534497695545L;
	private Spaces spaces;
	private ArrayList<SeriesSpaceDataSpaces> seriesSpaceDataCategoriesList;

	public SeriesSpaceSpaces() {
		super();
		this.chartType = ChartType.Spaces;

	}

	public SeriesSpaceSpaces(Spaces spaces,
			ArrayList<SeriesSpaceDataSpaces> seriesSpaceDataCategoriesList) {
		super();
		this.chartType = ChartType.Spaces;
		this.spaces = spaces;
		this.seriesSpaceDataCategoriesList = seriesSpaceDataCategoriesList;
	}

	public Spaces getSpaces() {
		return spaces;
	}

	public void setSpaces(Spaces spaces) {
		this.spaces = spaces;
	}

	public ArrayList<SeriesSpaceDataSpaces> getSeriesSpaceDataCategoriesList() {
		return seriesSpaceDataCategoriesList;
	}

	public void setSeriesSpaceDataCategoriesList(
			ArrayList<SeriesSpaceDataSpaces> seriesSpaceDataCategoriesList) {
		this.seriesSpaceDataCategoriesList = seriesSpaceDataCategoriesList;
	}

	@Override
	public String toString() {
		return "SeriesSpaceSpaces [spaces=" + spaces
				+ ", seriesSpaceDataCategoriesList="
				+ seriesSpaceDataCategoriesList + "]";
	}

}
