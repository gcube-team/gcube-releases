package org.gcube.portlets.user.speciesdiscovery.server.stream;

import java.net.URI;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.gcube.data.spd.model.KeyValue;
import org.gcube.data.spd.model.PointInfo;
import org.gcube.data.streams.Stream;
import org.gcube.portlets.user.speciesdiscovery.shared.Occurrence;
import org.gcube.portlets.user.speciesdiscovery.shared.util.NormalizeString;


/**
 * The Class IteratorPointInfo.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Feb 9, 2017
 */
public class IteratorPointInfo implements Stream<PointInfo> {

	protected Logger logger = Logger.getLogger(IteratorPointInfo.class);
	private Iterator<Occurrence> iterator;

	/**
	 * Instantiates a new iterator point info.
	 *
	 * @param iterator the iterator
	 */
	public IteratorPointInfo(Iterator<Occurrence> iterator) {
		this.iterator = iterator;
	}

	/* (non-Javadoc)
	 * @see org.gcube.data.streams.Stream#hasNext()
	 */
	@Override
	public boolean hasNext() {
		if (iterator.hasNext())
			return true;
		return false;
	}

	/* (non-Javadoc)
	 * @see org.gcube.data.streams.Stream#next()
	 */
	@Override
	public PointInfo next() {
		Occurrence occrs = iterator.next();
//		Coordinate coordinate = null;
		PointInfo pointInfo = null;

		if (occrs != null){
			logger.trace("get occurences id "+occrs.getServiceId());
			Double decimalLatitude = null;
			Double decimalLongitude = null;

			try{
			 decimalLatitude = Double.valueOf(occrs.getDecimalLatitude());
			 decimalLongitude = Double.valueOf(occrs.getDecimalLongitude());
			}

			catch (Exception e) {
				logger.error("error in get coordinate return null");
				return null;
			}

			pointInfo = new PointInfo(decimalLongitude, decimalLatitude);
			List<KeyValue> listMetaData = new ArrayList<KeyValue>();

			listMetaData.add(new KeyValue(Occurrence.BASIS_OF_RECORD, NormalizeString.validateUndefined(occrs.getBasisOfRecord())));
			listMetaData.add(new KeyValue(Occurrence.CATALOGUE_NUMBER, NormalizeString.validateUndefined(occrs.getCatalogueNumber())));
			listMetaData.add(new KeyValue(Occurrence.CITATION, NormalizeString.validateUndefined(occrs.getCitation())));
			listMetaData.add(new KeyValue(Occurrence.COLLECTION_CODE, NormalizeString.validateUndefined(occrs.getCollectionCode())));
			listMetaData.add(new KeyValue(Occurrence.COORDINATE_INMETERS, NormalizeString.validateUndefined(occrs.getCoordinateUncertaintyInMeters())));

			listMetaData.add(new KeyValue(Occurrence.COUNTRY, NormalizeString.validateUndefined(occrs.getCountry())));
			listMetaData.add(new KeyValue(Occurrence.DATAPROVIDER, NormalizeString.validateUndefined(occrs.getDataProvider())));
			listMetaData.add(new KeyValue(Occurrence.DATASET, NormalizeString.validateUndefined(occrs.getDataSet())));

			listMetaData.add(new KeyValue(Occurrence.SCIENTIFICNAMEAUTHORSHIP, NormalizeString.validateUndefined(occrs.getScientificNameAuthorship())));
			listMetaData.add(new KeyValue(Occurrence.LSID, NormalizeString.validateUndefined(occrs.getLsid())));
			listMetaData.add(new KeyValue(Occurrence.CREDITS, NormalizeString.validateUndefined(occrs.getCredits())));

			listMetaData.add(new KeyValue(Occurrence.DATASOURCE, NormalizeString.validateUndefined(occrs.getDataSource())));

			listMetaData.add(new KeyValue(Occurrence.EVENT_DATE, NormalizeString.validateUndefined(occrs.getEventDate())));

			listMetaData.add(new KeyValue(Occurrence.FAMILY, NormalizeString.validateUndefined(occrs.getFamily())));

			listMetaData.add(new KeyValue(Occurrence.ID_FIELD, NormalizeString.validateUndefined(""+occrs.getId())));
			listMetaData.add(new KeyValue(Occurrence.INSTITUTE_CODE, NormalizeString.validateUndefined(occrs.getInstitutionCode())));

			listMetaData.add(new KeyValue(Occurrence.LOCALITY, NormalizeString.validateUndefined(occrs.getLocality())));
			listMetaData.add(new KeyValue(Occurrence.MAX_DEPTH, NormalizeString.validateUndefined(occrs.getMaxDepth())));
			listMetaData.add(new KeyValue(Occurrence.MIN_DEPTH, NormalizeString.validateUndefined(occrs.getMinDepth())));
			listMetaData.add(new KeyValue(Occurrence.MODIFIED, NormalizeString.validateUndefined(occrs.getModified())));

			listMetaData.add(new KeyValue(Occurrence.RECORD_BY, NormalizeString.validateUndefined(occrs.getRecordedBy())));
			listMetaData.add(new KeyValue(Occurrence.SCIENTIFICNAME, NormalizeString.validateUndefined(occrs.getScientificName())));

			listMetaData.add(new KeyValue(Occurrence.DECIMAL_LATITUDE, decimalLatitude+""));
			listMetaData.add(new KeyValue(Occurrence.DECIMAL_LONGITUDE, decimalLongitude+""));

//			pointInfo.setPropertiesMap(listMetaData);
			pointInfo.setPropertiesList(listMetaData);
			logger.trace("create new object point info lat: "+decimalLatitude +" long: "+decimalLongitude);
			return pointInfo;
		}

		logger.trace("get occurences null - return null");
		return null;

	}

	/* (non-Javadoc)
	 * @see java.util.Iterator#remove()
	 */
	@Override
	public void remove() {
		iterator.remove();
		// throw new UnsupportedOperationException();
	}

	/**
	 * Iterator.
	 *
	 * @return the iterator
	 */
	public Iterator<PointInfo> iterator() {
		return this;
	}

	/* (non-Javadoc)
	 * @see org.gcube.data.streams.Stream#close()
	 */
	@Override
	public void close() {
	}

	/* (non-Javadoc)
	 * @see org.gcube.data.streams.Stream#locator()
	 */
	@Override
	public URI locator() throws IllegalStateException {
		return null;
	}

	/* (non-Javadoc)
	 * @see org.gcube.data.streams.Stream#isClosed()
	 */
	@Override
	public boolean isClosed() {
		return false;

	}
}
