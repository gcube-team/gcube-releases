/**
 * 
 */
package org.gcube.portlets.user.speciesdiscovery.client;

import java.util.ArrayList;
import java.util.List;

import org.gcube.portlets.user.speciesdiscovery.client.util.GridField;
import org.gcube.portlets.user.speciesdiscovery.client.util.OccurencesGridFields;
import org.gcube.portlets.user.speciesdiscovery.client.util.Util;
import org.gcube.portlets.user.speciesdiscovery.client.util.stream.DataSourceManager;
import org.gcube.portlets.user.speciesdiscovery.client.util.stream.StreamPagingLoader;
import org.gcube.portlets.user.speciesdiscovery.client.util.stream.StreamPagingToolBar;
import org.gcube.portlets.user.speciesdiscovery.shared.SpeciesCapability;

import com.allen_sauer.gwt.log.client.Log;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.user.client.rpc.AsyncCallback;


/**
 * @author "Federico De Faveri defaveri@isti.cnr.it"
 *
 */
public class OccurrencesWindow extends Window {
	
	protected static final String OCCURRENCE_POINTS = "Occurrence points";

	protected static final int PAGE_SIZE = 20;
	
	protected StreamPagingLoader loader;
	protected int count = 0;
	private DataSourceManager dataSourceManager;
	

	public OccurrencesWindow()
	{
		setHeading(OCCURRENCE_POINTS);
		setLayout(new FitLayout());
		setModal(true);
		setResizable(true);
		setSize(1200, 500);

		this.dataSourceManager = DataSourceManager.getInstance();
		
		List<ColumnConfig> config = new ArrayList<ColumnConfig>();
		
		for (GridField field:OccurencesGridFields.values()) config.add(Util.createColumnConfig(field, 150));
		
		final ColumnModel cm = new ColumnModel(config);
		
		initLoader();
		
		
		Grid<ModelData> grid = new Grid<ModelData>(loader.getStore(), cm);  
		grid.setBorders(true);
		
		StreamPagingToolBar toolBar = new StreamPagingToolBar();
		toolBar.bind(loader);
		
		setBottomComponent(toolBar);

		add(grid);  
	}
	
	protected void initLoader()
	{
		loader = new StreamPagingLoader(PAGE_SIZE);
		loader.setDataSource(this.dataSourceManager.getDataSourceByResultType(SpeciesCapability.OCCURRENCESPOINTS));
	}
	
	public void loadOccurences()
	{
		Log.trace("Loading occurrences");

		count = 0;
		
		dataSourceManager.setExpectedOccurencePoints(count);

		SpeciesDiscovery.taxonomySearchService.retrieveOccurencesFromSelection(new AsyncCallback<Integer>() {
			
			@Override
			public void onSuccess(Integer expectedPoints) {
				Log.trace("Expected points: "+expectedPoints);
				dataSourceManager.setExpectedOccurencePoints(expectedPoints);
				count = expectedPoints;
				loader.startLoading(true);
			}
			
			@Override
			public void onFailure(Throwable caught) {
				System.err.println("Error getting occurences");				
			}
		});
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void onHide() {
		super.onHide();
		Scheduler.get().scheduleDeferred(new ScheduledCommand() {
			
			@Override
			public void execute() {
				loader.reset();
			}
		});
	}

}
