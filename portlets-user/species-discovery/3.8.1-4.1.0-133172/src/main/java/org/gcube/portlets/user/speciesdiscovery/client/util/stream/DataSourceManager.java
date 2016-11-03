package org.gcube.portlets.user.speciesdiscovery.client.util.stream;

import org.gcube.portlets.user.speciesdiscovery.shared.SpeciesCapability;

import com.allen_sauer.gwt.log.client.Log;

public class DataSourceManager {
	
	private static DataSourceManager instance;
	protected boolean onlySelected = false;
	protected boolean isActiveFilters = false;
	protected int expectedPoints = 0;
	
	private ResultRowDataSource resulRowDataSource;
	private TaxonomyRowDataSource taxonomyRowDataSource;
	private OccurrencesDataSource occurrencesDataSource;
	
	private DataSource activeDataSource = null;

	
	private DataSourceManager(){
		
		this.resulRowDataSource = new ResultRowDataSource();
		this.taxonomyRowDataSource = new TaxonomyRowDataSource();
		this.occurrencesDataSource = new OccurrencesDataSource();
		
	}
	
	public static synchronized DataSourceManager getInstance() {
		if (instance == null)
			instance = new DataSourceManager();
		return instance;
	}

	public DataSource getDataSourceByResultType(SpeciesCapability resultType){
		
		
		switch (resultType) {
		
		case RESULTITEM:
			
			Log.trace("RESULTITEM ");
			resulRowDataSource.setActiveFilterOnResult(isActiveFilters);
			resulRowDataSource.setShowOnlySelected(onlySelected);
			activeDataSource = resulRowDataSource;
			return resulRowDataSource;
			
		case OCCURRENCESPOINTS:
			
			Log.trace("OCCURRENCESPOINTS ");
			
			this.occurrencesDataSource.setCount(expectedPoints);
			return occurrencesDataSource;
			
		case TAXONOMYITEM:
			
			Log.trace("TAXONOMYITEM ");
			
			taxonomyRowDataSource.setActiveFilterOnResult(isActiveFilters);
			taxonomyRowDataSource.setShowOnlySelected(onlySelected);
			activeDataSource = taxonomyRowDataSource;
			return taxonomyRowDataSource;

		default:
			return null;
		}
	}


	public boolean isOnlySelected() {
		return onlySelected;
	}


	public void setOnlySelected(boolean onlySelected) {
		this.onlySelected = onlySelected;
		updateOnlySelectedValue();
	}


	public boolean isActiveFilters() {
		return isActiveFilters;
	}

	public void setActiveFilters(boolean isActiveFilters) {
		this.isActiveFilters = isActiveFilters;
		updateActiveFiltersValue();
	}

	public void setExpectedOccurencePoints(Integer expectedPoints) {
		this.expectedPoints = expectedPoints;
		updateExpectedPoints();
	}
	
	private void updateExpectedPoints(){
		this.occurrencesDataSource.setCount(expectedPoints);
	}
	
	private void updateOnlySelectedValue(){
		resulRowDataSource.setShowOnlySelected(onlySelected);
		taxonomyRowDataSource.setShowOnlySelected(onlySelected);
	}
	
	private void updateActiveFiltersValue(){
		resulRowDataSource.setActiveFilterOnResult(isActiveFilters);
		taxonomyRowDataSource.setActiveFilterOnResult(isActiveFilters);
	}
}
