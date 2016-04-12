/**
 * 
 */
package org.gcube.portlets.user.speciesdiscovery.client.event;

import org.gcube.portlets.user.speciesdiscovery.shared.OccurrencesSaveEnum;
import org.gcube.portlets.user.speciesdiscovery.shared.SaveFileFormat;
import org.gcube.portlets.user.speciesdiscovery.shared.SearchResultType;

import com.google.gwt.event.shared.GwtEvent;

/**
 * 
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 *
 */
public class SaveItemsEvent extends GwtEvent<SaveItemsEventHandler> {
	
	public static final GwtEvent.Type<SaveItemsEventHandler> TYPE = new Type<SaveItemsEventHandler>();

	@Override
	public Type<SaveItemsEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(SaveItemsEventHandler handler) {
		handler.onSaveOccurrences(this);	
	}
	
	protected SaveFileFormat fileFormat;
	protected SearchResultType itemType;
	private int expectedPoints;
	private OccurrencesSaveEnum csvType;


	/**
	 * @param taxonomyItem 
	 * @param fileFormat
	 * @param count 
	 */
	public SaveItemsEvent(SearchResultType itemType, SaveFileFormat fileFormat, int expectedPoints, OccurrencesSaveEnum csvType) {
		this.fileFormat = fileFormat;
		this.itemType = itemType;
		this.expectedPoints = expectedPoints;
		this.setCsvType(csvType);
	}

	/**
	 * @return the fileFormat
	 */
	public SaveFileFormat getFileFormat() {
		return fileFormat;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("SaveOccurrencesEvent [fileFormat=");
		builder.append(fileFormat);
		builder.append("]");
		return builder.toString();
	}

	public SearchResultType getItemType() {
		return itemType;
	}

	public void setItemType(SearchResultType itemType) {
		this.itemType = itemType;
	}

	public void setFileFormat(SaveFileFormat fileFormat) {
		this.fileFormat = fileFormat;
	}

	public int getExpectedPoints() {
		return expectedPoints;
	}

	public void setCsvType(OccurrencesSaveEnum csvType) {
		this.csvType = csvType;
	}

	public OccurrencesSaveEnum getCsvType() {
		return csvType;
	}	
}
