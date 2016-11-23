/**
 * 
 */
package org.gcube.portlets.user.speciesdiscovery.client.event;

import java.util.ArrayList;
import java.util.List;

import org.gcube.portlets.user.speciesdiscovery.shared.OccurrencesSaveEnum;
import org.gcube.portlets.user.speciesdiscovery.shared.SaveFileFormat;

import com.google.gwt.event.shared.GwtEvent;

/**
 * 
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 *
 */
public class CreateOccurrenceJobEvent extends GwtEvent<CreateOccurrenceJobEventHandler> {
	
	public static final GwtEvent.Type<CreateOccurrenceJobEventHandler> TYPE = new Type<CreateOccurrenceJobEventHandler>();
	private SaveFileFormat fileFormat;
	private int expectedOccurrences;
	private OccurrencesSaveEnum saveEnum;
	private List<String> listDataSourceFound;
	private String searchTerm;
	private boolean isByDataSource;

	@Override
	public Type<CreateOccurrenceJobEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(CreateOccurrenceJobEventHandler handler) {
		handler.onCreateSpeciesJob(this);	
	}

	/**
	 * 
	 * @param fileFormat
	 * @param expectedOccurrences
	 * @param save
	 * @param listDataSourceFound
	 * @param searchTerm
	 * @param isByDataSource
	 */
	public CreateOccurrenceJobEvent(SaveFileFormat fileFormat, int expectedOccurrences, OccurrencesSaveEnum save, List<String> listDataSourceFound, String searchTerm, boolean isByDataSource) {
		this.fileFormat = fileFormat;
		this.expectedOccurrences = expectedOccurrences;
		this.saveEnum = save;
		this.listDataSourceFound = listDataSourceFound;
		this.searchTerm = searchTerm;
		this.isByDataSource = isByDataSource;
	}
	
	public OccurrencesSaveEnum getSaveEnum() {
		return saveEnum;
	}

	public void setSaveEnum(OccurrencesSaveEnum saveEnum) {
		this.saveEnum = saveEnum;
	}

	public List<String> getListDataSourceFound() {
		return listDataSourceFound;
	}

	public void setListDataSourceFound(ArrayList<String> listDataSourceFound) {
		this.listDataSourceFound = listDataSourceFound;
	}

	public SaveFileFormat getFileFormat() {
		return fileFormat;
	}

	public void setFileFormat(SaveFileFormat fileFormat) {
		this.fileFormat = fileFormat;
	}

	public int getExpectedOccurrences() {
		return expectedOccurrences;
	}

	public void setExpectedOccurrences(int expectedOccurrences) {
		this.expectedOccurrences = expectedOccurrences;
	}

	public String getSearchTerm() {
		return searchTerm;
	}

	public void setSearchTerm(String searchTerm) {
		this.searchTerm = searchTerm;
	}

	public boolean isByDataSource() {
		return isByDataSource;
	}

	public void setByDataSource(boolean isByDataSource) {
		this.isByDataSource = isByDataSource;
	}
}
