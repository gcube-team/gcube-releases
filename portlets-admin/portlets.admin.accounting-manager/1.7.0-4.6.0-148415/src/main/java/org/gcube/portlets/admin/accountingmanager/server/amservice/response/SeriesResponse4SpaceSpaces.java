package org.gcube.portlets.admin.accountingmanager.server.amservice.response;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.SortedMap;

import org.gcube.accounting.analytics.Filter;
import org.gcube.portlets.admin.accountingmanager.shared.data.Spaces;
import org.gcube.portlets.admin.accountingmanager.shared.data.response.SeriesSpace;
import org.gcube.portlets.admin.accountingmanager.shared.data.response.space.SeriesSpaceSpaces;
import org.gcube.portlets.admin.accountingmanager.shared.data.response.space.SeriesSpaceData;
import org.gcube.portlets.admin.accountingmanager.shared.data.response.space.SeriesSpaceDataSpaces;
import org.gcube.portlets.admin.accountingmanager.shared.exception.ServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Categories Series Response 4 Space
 * 
   * @author Giancarlo Panichi
 *
 * 
 */
public class SeriesResponse4SpaceSpaces extends SeriesResponseBuilder {
	protected static Logger logger = LoggerFactory
			.getLogger(SeriesResponse4SpaceSpaces.class);
	private Spaces spaces;
	private SortedMap<Filter, SortedMap<Calendar, Long>> spaceSM;

	public SeriesResponse4SpaceSpaces(Spaces spaces,
			SortedMap<Filter, SortedMap<Calendar, Long>> spaceSM) {
		this.spaces = spaces;
		this.spaceSM = spaceSM;
	}

	@Override
	public void buildSeriesResponse() throws ServiceException {
		try {
			if (spaceSM == null || spaceSM.isEmpty()) {
				logger.error("Error creating series for space accounting: No data available!");
				throw new ServiceException("No data available!");
			}

			ArrayList<SeriesSpaceDataSpaces> seriesSpaceDataContextList = new ArrayList<>();

			for (Filter spaceValue : spaceSM.keySet()) {

				ArrayList<SeriesSpaceData> series = new ArrayList<>();
				SortedMap<Calendar, Long> infos = spaceSM.get(spaceValue);
				for (Calendar calendar : infos.keySet()) {
					Long value = infos.get(calendar);
					if(value==null){
						value=0L;
					}
					series.add(new SeriesSpaceData(calendar.getTime(), value));

				}
				SeriesSpaceDataSpaces seriesSpaceDataContext = new SeriesSpaceDataSpaces(
						spaceValue.getValue(), series);
				seriesSpaceDataContextList.add(seriesSpaceDataContext);

			}

			SeriesSpaceSpaces seriesSpaceContext = new SeriesSpaceSpaces(
					spaces, seriesSpaceDataContextList);
			SeriesSpace seriesSpace = new SeriesSpace(seriesSpaceContext);

			seriesResponseSpec.setSr(seriesSpace);

		} catch (Throwable e) {
			logger.error("Error creating series for space accounting categories chart: "
					+ e.getLocalizedMessage());
			e.printStackTrace();
			throw new ServiceException(
					"Error creating series for space accounting categories chart: "
							+ e.getLocalizedMessage());
		}

	}
}
