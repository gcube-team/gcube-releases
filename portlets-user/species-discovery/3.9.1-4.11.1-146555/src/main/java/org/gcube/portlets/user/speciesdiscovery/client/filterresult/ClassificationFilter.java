package org.gcube.portlets.user.speciesdiscovery.client.filterresult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.gcube.portlets.user.speciesdiscovery.client.ConstantsSpeciesDiscovery;
import org.gcube.portlets.user.speciesdiscovery.client.event.ActiveFilterOnResultEvent;
import org.gcube.portlets.user.speciesdiscovery.client.event.ChangeFilterClassificationOnResultEvent;
import org.gcube.portlets.user.speciesdiscovery.client.model.ClassificationModel;
import org.gcube.portlets.user.speciesdiscovery.shared.MainTaxonomicRankEnum;
import org.gcube.portlets.user.speciesdiscovery.shared.filter.ResultFilter;

import com.allen_sauer.gwt.log.client.Log;
import com.extjs.gxt.ui.client.Style.SelectionMode;
import com.extjs.gxt.ui.client.data.ModelIconProvider;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.store.Record;
import com.extjs.gxt.ui.client.store.Store;
import com.extjs.gxt.ui.client.store.StoreSorter;
import com.extjs.gxt.ui.client.store.TreeStore;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Html;
import com.extjs.gxt.ui.client.widget.form.ComboBox.TriggerAction;
import com.extjs.gxt.ui.client.widget.form.SimpleComboBox;
import com.extjs.gxt.ui.client.widget.form.SimpleComboValue;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.extjs.gxt.ui.client.widget.treepanel.TreePanel;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.AbstractImagePrototype;

/**
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 *
 */
public class ClassificationFilter implements ResultFilterPanelInterface{

	private ContentPanel classifPanel = new ContentPanel();
	private TreePanel<ClassificationModel> treePanel;
	private TreeStore<ClassificationModel> store;
	private final String NORANK = "NO RANK";
	private SimpleComboBox<String> scbGroupByRank;
	private EventBus eventBus;
//	private HashMap<String, String> hashKingdomLevel = new HashMap<String, String>();
	private ToolBar toolbar = new ToolBar();
	private String currentRank;
	
	
	public ClassificationFilter(){
		setHeaderTitle();
		init();
		setAlphanumericStoreSorter();

	    addDefaultNodes();
	    addListners();
	    setExpandTreeLevel(ConstantsSpeciesDiscovery.BIOTACLASSID, true); //expand root level
	    
	    initComboGroupRankFilter();
	    initToolBar();
	    
	    classifPanel.setTopComponent(toolbar);
	    
	}
	
	private SimpleComboBox<String> initComboGroupRankFilter() {

		List<String> ls = new ArrayList<String>();

		for (String rank : MainTaxonomicRankEnum.getListLabels()) ls.add(rank);

		scbGroupByRank = new SimpleComboBox<String>();
		scbGroupByRank.setTypeAhead(true);
		scbGroupByRank.setEditable(false);
		scbGroupByRank.setTriggerAction(TriggerAction.ALL);

		scbGroupByRank.add(ls);
	
		scbGroupByRank.setSimpleValue(MainTaxonomicRankEnum.CLASS.getLabel()); //is Class
		
		 addListnerOnChangeClassificationFilter();

		return scbGroupByRank;
	}
	
	public String getGroupRank(){
		return scbGroupByRank.getSimpleValue();
	}
	
	
	public void addListnerOnChangeClassificationFilter(){
		
		scbGroupByRank.addSelectionChangedListener(new SelectionChangedListener<SimpleComboValue<String>>() {
			
			@Override
			public void selectionChanged(SelectionChangedEvent<SimpleComboValue<String>> se) {
				
				if(eventBus!=null){	
					eventBus.fireEvent(new ChangeFilterClassificationOnResultEvent());
				}
			}
		});

	}
	
	private void initToolBar(){
		toolbar = new ToolBar();
		
		toolbar.setStyleName("toolbar-filter");
		toolbar.setStyleAttribute("padding-right", "1px");
//		toolbar.add(new FillToolItem());
		
		Html textGroupBy = new Html("Group by: ");
		textGroupBy.setStyleAttribute("font-style", "italic");
		textGroupBy.setStyleAttribute("font-size", "10px");
		textGroupBy.setStyleAttribute("padding-left", "5px");
		textGroupBy.setStyleAttribute("padding-right", "5px");
		
		scbGroupByRank.setStyleAttribute("margin-right", "2px");	
		toolbar.add(textGroupBy);

	
		toolbar.add(textGroupBy);
		toolbar.add(scbGroupByRank);
	}
	
	public void setEventBus(EventBus eventBus){
		this.eventBus = eventBus;
	}
	
	
	
	private void addDefaultNodes(){
		
	    //INSERT STATIC LEVELS
	    store.insert(createRoot(), 0, false);
//	    store.add(store.getRootItems().get(0), createChildren(),false);
	}
	
	private void init() {
		
		store = new TreeStore<ClassificationModel>();  

	    treePanel = new TreePanel<ClassificationModel>(store){
	    	
			@Override
			public boolean hasChildren(ClassificationModel parent) {
				if (!parent.isLeaf()) {
					return true;
				}
				return super.hasChildren(parent);
			}
	    		
	    };  
	    
		// SET icons in tree panel
		treePanel.setIconProvider(new ModelIconProvider<ClassificationModel>() {

			public AbstractImagePrototype getIcon(ClassificationModel model) {
				if (!model.isLeaf()){
					
//					return Resources.getIconByFolderItemType(model.getGXTFolderItemType());
				}
				return null;
			}
		});

		treePanel.setStateful(false);
		
		// statefull components need a defined id
		treePanel.setId("treeClassification");
		
	    treePanel.setDisplayProperty(ConstantsSpeciesDiscovery.NAME);  
	    //Single selection Mode
	    treePanel.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

	    classifPanel.add(treePanel);
	}

	@Override
	public ContentPanel getPanel() {
		return classifPanel;
	}

	@Override
	public String getName() {
		return ResultFilterPanelEnum.CLASSIFICATION.getLabel();
	}

	@Override
	public void setHeaderTitle() {
		classifPanel.setHeading(this.getName());
	}
	
	
	private ClassificationModel createRoot(){
		return new ClassificationModel(ConstantsSpeciesDiscovery.BIOTACLASSID, ConstantsSpeciesDiscovery.BIOTACLASS, null, null, false);
	}
	
	
	private void setAlphanumericStoreSorter(){
		
		// Sorting
		store.setStoreSorter(new StoreSorter<ClassificationModel>() {

			@Override
			public int compare(Store<ClassificationModel> store, ClassificationModel m1, ClassificationModel m2, String property) {
				boolean m1Folder = m1.isLeaf();
				boolean m2Folder = m2.isLeaf();

				if (m1Folder && !m2Folder) {
					return -1;
				} else if (!m1Folder && m2Folder) {
					return 1;
				}

				if(m1.getName().compareToIgnoreCase(m2.getName())<0)
					return -1;
				else
					return 1;
		
			}
		});
	}
	
	
	public void setExpandTreeLevel(String identifier, boolean bool) {
		ClassificationModel item = getFileModelByIdentifier(identifier);
		if(item!=null)
			treePanel.setExpanded(item, bool); 
	}
	
	/**
	 * 
	 * @param id
	 * @return
	 */
	public ClassificationModel getFileModelByIdentifier(String id){
		
		return treePanel.getStore().findModel(ConstantsSpeciesDiscovery.ID, id);
		
	}
	
	
	/**
	 * 
	 * @param fileTarget (MANDATORY)
	 * @param newName  (MANDATORY)
	 * @param extension OPTIONAL - string or null
	 */
	public boolean renameItem(String identifier, String newName, Integer counter) {
		
//		FileModel fileTarget = treePanel.getStore().findModel(ConstantsExplorer.IDENTIFIER, identifier);
		
		ClassificationModel fileTarget = getFileModelByIdentifier(identifier);
		
		return renameItem(fileTarget,newName,counter);
		
	}
	
	/**
	 * 
	 * @param identifier
	 * @param counter
	 * @return
	 */
	public boolean updateItemCounter(String parentId, String itemIdentifier, String itemName, String rank, String itemBaseTaxonId, String baseTaxonName, int counter) {
		
		ClassificationModel fileTarget = getFileModelByIdentifier(itemIdentifier);
		
		if(fileTarget==null){
			ClassificationModel parent = getFileModelByIdentifier(parentId);
//			store.add(parent, new ClassificationModel(itemIdentifier,itemName, itemBaseTaxonId, baseTaxonName, true),false);
			store.add(parent, new ClassificationModel(itemIdentifier, itemName, rank, itemBaseTaxonId, baseTaxonName, true, counter),false);

			fileTarget = getFileModelByIdentifier(itemIdentifier);
		}
		else
			fileTarget.setCountOf(counter);
		

		return renameItem(fileTarget, itemName, counter);
		
	}
	
	/**
	 * 
	 * @param fileTarget (MANDATORY)
	 * @param newName  (MANDATORY)
	 * @param extension OPTIONAL - string or null
	 */
	private boolean renameItem(ClassificationModel fileTarget, String newName, Integer counter) {

		if(fileTarget!=null){
			Record record = treePanel.getStore().getRecord(fileTarget);
			if(record!=null){
				if(counter!= null)
					if(newName!=null)
						record.set(ConstantsSpeciesDiscovery.NAME, newName+"("+counter.intValue()+")");
					else
						record.set(ConstantsSpeciesDiscovery.NAME, fileTarget.getName()+"("+counter.intValue()+")");
				else
					if(newName!=null)
						record.set(ConstantsSpeciesDiscovery.NAME, newName);
					else
						record.set(ConstantsSpeciesDiscovery.NAME, fileTarget.getName());
				
				return true;
			}
			else
				Log.error("Record Error: file target with " + fileTarget.getId() + " identifier not exist in store" );
		}
		else
			Log.error("Rename Error: file target not exist in store" );
		
		return false;
	}
	
	
	public void loadDataSourceClassification(HashMap<String, ClassificationModel> result, String rank) {
		
		currentRank = rank;
		
		for(String key: result.keySet()){

			ClassificationModel cm = result.get(key);
		
			String name = cm.getClassificationRank() + " - "+cm.getName();
			
			String kingdomValue = cm.getBaseTaxonName();

			//if kingdom value not exists create new kingdom folder in the tree
			if(getFileModelByIdentifier(kingdomValue)==null){
				store.add(store.getRootItems().get(0), new ClassificationModel(kingdomValue, kingdomValue, cm.getBaseTaxonId(),cm.getBaseTaxonName(), false),false);
				setExpandTreeLevel(kingdomValue, true);
			}
			

			if(cm.getBaseTaxonName().equalsIgnoreCase(ConstantsSpeciesDiscovery.UNKNOWN)){
				
				if(rank.compareToIgnoreCase(cm.getClassificationRank())==0) //in this case was found rank but was not found kingdom
					name = cm.getClassificationRank() +" - "+cm.getName();
				else
//					name = "["+NORANK+" " + rank+"] - " + cm.getClassificationRank() +" - "+cm.getName();
					name = "["+NORANK+" " + rank+"] - " + cm.getClassificationRank();
			
			}
			updateItemCounter(kingdomValue, cm.getId(), name, cm.getClassificationRank(), cm.getBaseTaxonId(),cm.getBaseTaxonName(), cm.getCountOf());

//			System.out.println("################################ key class : "+ key +", item name: "+ cm.getName() + ", item id: " +cm.getId() + ",  item baseTaxon: "+cm.getBaseTaxonName() + ", item count " +cm.getGroupedIdClassificationList().size() );	
//			for(Integer id: cm.getGroupedIdClassificationList()){	
//				System.out.println("current id "+ id);
//			}
		}
	}
	
	
	private void addListners(){
		
		treePanel.getSelectionModel().addSelectionChangedListener(new SelectionChangedListener<ClassificationModel>() {

			@Override
			public void selectionChanged(SelectionChangedEvent<ClassificationModel> selectedEvent) {
				
				ClassificationModel cm = selectedEvent.getSelectedItem();

				if(cm!=null){
					if(cm.isLeaf()){
						ResultFilter activeFilter = new ResultFilter();
						activeFilter.setByClassification(true);
//						activeFilter.setListByClassification(cm.getGroupedIdClassificationList());
//						String key = cm.getName().substring(0, cm.getName().indexOf("("));
//						activeFilter.setFilterValue(cm.getName().substring(0, cm.getName().indexOf("(")));
						
						activeFilter.setClassification(currentRank, cm.getId(), cm.getCountOf());
						
						activeFilter.setFilterValue(cm.getId()); //cm.getId() is taxonId stored into DB 

						treePanel.disableEvents(true);
						treePanel.getSelectionModel().deselect(cm);
						treePanel.enableEvents(true);
						
						eventBus.fireEvent(new ActiveFilterOnResultEvent(activeFilter));
					}
				}
			}
		});
		
	}

	public void reset() {
		store.removeAll();
		addDefaultNodes();
		setExpandTreeLevel(ConstantsSpeciesDiscovery.BIOTACLASSID, true); //expand BIOTACLASSID level
	}


	@Override
	public void loadDataSource(HashMap<String, Integer> result) {
		return;
	}

}
