package org.gcube.application.aquamaps.aquamapsportlet.client.selections;

import java.sql.Timestamp;
import java.util.ArrayList;

import org.gcube.application.aquamaps.aquamapsportlet.client.RecordDefinitions;
import org.gcube.application.aquamaps.aquamapsportlet.client.constants.AquaMapsPortletCostants;
import org.gcube.application.aquamaps.aquamapsportlet.client.constants.Tags;
import org.gcube.application.aquamaps.aquamapsportlet.client.constants.fields.ResourceFields;
import org.gcube.application.aquamaps.aquamapsportlet.client.constants.types.ClientResourceType;

import com.allen_sauer.gwt.log.client.Log;
import com.gwtext.client.core.SortDir;
import com.gwtext.client.data.JsonReader;
import com.gwtext.client.data.Record;
import com.gwtext.client.data.Store;
import com.gwtext.client.util.DateUtil;
import com.gwtext.client.util.Format;
import com.gwtext.client.widgets.PagingToolbar;
import com.gwtext.client.widgets.grid.CellMetadata;
import com.gwtext.client.widgets.grid.ColumnConfig;
import com.gwtext.client.widgets.grid.ColumnModel;
import com.gwtext.client.widgets.grid.GridPanel;
import com.gwtext.client.widgets.grid.GridView;
import com.gwtext.client.widgets.grid.Renderer;
import com.gwtext.client.widgets.grid.RowParams;
import com.gwtext.client.widgets.layout.FitLayout;

public class ResourceGrid extends GridPanel {
	private Renderer renderTopic = new Renderer() {  
		public String render(Object value, CellMetadata cellMetadata, Record record,  
				int rowIndex, int colNum, Store store) {  
			return Format.format("<b>{0}</b>",new String[]{record.getAsString(ResourceFields.title+"")}); 

		}  
	};  

	private Renderer renderDate = new Renderer() {  
		public String render(Object value, CellMetadata cellMetadata, Record record, int rowIndex,  
				int colNum, Store store) {
			String lastPostStr="";
			try{
			Timestamp time=new Timestamp(Long.parseLong(record.getAsString(ResourceFields.generationtime+"")));  
			lastPostStr = DateUtil.format(time, "M j, Y, g:i a");
			}catch(Exception e){
				Log.warn("Unable to parse generation time, value was "+record.getAsString(ResourceFields.generationtime+""));
			}
			return Format.format("{0}<br/>by {1}", new String[]{lastPostStr, record.getAsString(ResourceFields.author+"")});
			
		}  
	};  


	public ResourceGrid(String title,ClientResourceType type) {
		this.setTitle(title);
		this.setFrame(true);
		this.setLayout(new FitLayout());


		JsonReader reader = new JsonReader(RecordDefinitions.resourceRecordDef);  
		reader.setRoot(Tags.DATA);  
		reader.setTotalProperty(Tags.TOTAL_COUNT);  
		reader.setId(ResourceFields.searchid+""); 
		Store store=new Store(reader);
		String url=AquaMapsPortletCostants.servletUrl.get("tree")+"?treeType="+type.toString();		
		store.setUrl(url);
		store.setDefaultSort(ResourceFields.searchid+"", SortDir.DESC);
		ArrayList<ColumnConfig> columns=new ArrayList<ColumnConfig>();
		columns.add(new ColumnConfig("ID", ResourceFields.searchid+""));
		ColumnConfig nameColumn = new ColumnConfig("Name", ResourceFields.title+"", 420, false, renderTopic, ResourceFields.title+"");  
		nameColumn.setCss("white-space:normal;");  
		columns.add(nameColumn);
		ColumnConfig dateColumn = new ColumnConfig("Time", ResourceFields.generationtime+"", 420, false, renderDate, ResourceFields.generationtime+"");  
		dateColumn.setCss("white-space:normal;");
		columns.add(dateColumn);
		if(type.equals(ClientResourceType.HSPEC)){
			ColumnConfig algorithmColumn = new ColumnConfig("Algorithm", ResourceFields.algorithm+"", 420, false, null, ResourceFields.algorithm+"");  
			algorithmColumn.setCss("white-space:normal;");
			columns.add(algorithmColumn);
		}
		ColumnModel columnModel = new ColumnModel(columns.toArray(new ColumnConfig[columns.size()]));  
		columnModel.setDefaultSortable(true);         
		this.setStore(store);  
		this.setColumnModel(columnModel);
		this.setSize(500, 400);
		GridView view = new GridView() {  
			public String getRowClass(Record record, int index, RowParams rowParams, Store store) {
				rowParams.setBody(Format.format("<p>{0}</p>", record.getAsString(ResourceFields.description+"")));  
				return "x-grid3-row-expanded";}
		};  
		view.setForceFit(true);  
		view.setEnableRowBody(true);
		PagingToolbar pagingToolbar=new PagingToolbar(store);
		pagingToolbar.setDisplayInfo(true);
		store.setAutoLoad(true);
		this.setView(view);
		this.setBottomToolbar(pagingToolbar);
		
	}
}
