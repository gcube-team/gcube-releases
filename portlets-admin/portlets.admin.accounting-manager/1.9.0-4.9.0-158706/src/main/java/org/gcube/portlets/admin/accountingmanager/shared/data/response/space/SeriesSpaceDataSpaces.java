package org.gcube.portlets.admin.accountingmanager.shared.data.response.space;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * 
 * @author Giancarlo Panichi
 *
 *
 */
public class SeriesSpaceDataSpaces implements Serializable {

	private static final long serialVersionUID = 1601335458514606805L;
	private String space;
	private ArrayList<SeriesSpaceData> series;

	public SeriesSpaceDataSpaces() {
		super();
	}

	/**
	 * 
	 * @param space
	 *            space
	 * @param series
	 *            array list of series space data
	 */
	public SeriesSpaceDataSpaces(String space, ArrayList<SeriesSpaceData> series) {
		super();
		this.space = space;
		this.series = series;
	}

	public String getSpace() {
		return space;
	}

	public void setSpace(String space) {
		this.space = space;
	}

	public ArrayList<SeriesSpaceData> getSeries() {
		return series;
	}

	public void setSeries(ArrayList<SeriesSpaceData> series) {
		this.series = series;
	}

	@Override
	public String toString() {
		return "SeriesSpaceDataSpaces [space=" + space + ", series=" + series + "]";
	}

}
