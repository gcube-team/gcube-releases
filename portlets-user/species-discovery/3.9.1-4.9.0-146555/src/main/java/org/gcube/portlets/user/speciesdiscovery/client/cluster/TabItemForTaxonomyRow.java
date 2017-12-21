package org.gcube.portlets.user.speciesdiscovery.client.cluster;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.gcube.portlets.user.speciesdiscovery.client.SpeciesDiscovery;
import org.gcube.portlets.user.speciesdiscovery.client.resources.Resources;
import org.gcube.portlets.user.speciesdiscovery.shared.LightTaxonomyRow;
import org.gcube.portlets.user.speciesdiscovery.shared.TaxonomyRow;
import org.gcube.portlets.user.speciesdiscovery.shared.util.NormalizeString;

import com.allen_sauer.gwt.log.client.Log;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Info;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.TabPanel;
import com.extjs.gxt.ui.client.widget.Text;
import com.extjs.gxt.ui.client.widget.VerticalPanel;
import com.extjs.gxt.ui.client.widget.layout.ColumnLayout;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;

/**
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 *
 */
public class TabItemForTaxonomyRow {

	private TabPanel tabPanel;
	private AbstractImagePrototype imageLoading = AbstractImagePrototype.create(Resources.INSTANCE.loadingBalls());
	private EventBus eventBus;

//	private List<TaxonomyRow> parents = null;
	private TablesForTaxonomyRow tablesForTaxonomyRow;

	private LightTaxonomyRow currentTaxonomy;

	public void setCurrentTaxonomy(LightTaxonomyRow taxonomy){
		this.currentTaxonomy = taxonomy;
	}


	public TabItemForTaxonomyRow(TablesForTaxonomyRow tablesForTaxonomyRow, TabPanel tabPanel2, EventBus eventBus2) {
		this.tablesForTaxonomyRow = tablesForTaxonomyRow;
		this.tabPanel = tabPanel2;
		this.eventBus = eventBus2;
	}


	/**
	 *
	 * @param taxonomy
	 * @param isNewTab -  set true if has been clicked a classification link, or false otherwise
	 * @return
	 */

	public VerticalPanel getPanelClassificationForTaxonomy(LightTaxonomyRow taxonomy, String dataSource, boolean isNewTab){

		VerticalPanel vpClassification = new VerticalPanel();
		vpClassification.setSpacing(5);

		if(taxonomy!=null){

			//CREATE CLASSIFICATION FOR PARENT ITEM
			int parentsSize = taxonomy.getParents().size();
				for(int i=parentsSize-1; i>=0; i--){
					createPanelForParentTaxonomy(taxonomy.getParents().get(i), vpClassification, taxonomy.getServiceId(), isNewTab, dataSource, i+1);
				}

			//CREATE CLASSIFICATION FOR ITEM
			createPanelForParentTaxonomy(taxonomy, vpClassification, taxonomy.getServiceId(), isNewTab, dataSource, 0);
		}

		return vpClassification;

	}

	/**
	 * Create a scientific classification: for each parent rank create a link, for current search item create a label
	 * @param taxon
	 * @param vpClassification
	 * @param taxonomyServiceId
	 * @param isNewTab
	 * @param parentIndex
	 */
	public void createPanelForParentTaxonomy(LightTaxonomyRow taxon, VerticalPanel vpClassification, String taxonomyServiceId, boolean isNewTab, String dataSource, int parentIndex){

		if(taxon==null)
			return;

//			createPanelForParentTaxonomy(taxon.getParents(), vpClassification, taxonomyServiceId, isNewTab, dataSource);

		HorizontalPanel hp = new HorizontalPanel();
		Text labelRank;
		if(!NormalizeString.isUndefined(taxon.getRank()))
			labelRank = new Text(taxon.getRank()+":");
		else
			labelRank = new Text("Rank not found"+":");
//			labelRank.setStyleAttribute("margin-right", "10px");
		hp.add(labelRank);
		hp.setCellWidth(labelRank, "120px");

		Anchor anchor = getAnchorByTaxonomy(taxon, dataSource, parentIndex);
		hp.add(anchor);
		hp.setCellHorizontalAlignment(anchor, HorizontalPanel.ALIGN_LEFT);
		vpClassification.add(hp);

		//DEBUG
//		System.out.println("taxon.getName()" +taxon.getName() + " serviceid  : "+taxon.getServiceId() + " taxonomyServiceId "+taxonomyServiceId);
//		System.out.println("taxon.getServiceId().compareTo(taxonomyServiceId)==0 "+ taxon.getServiceId().compareTo(taxonomyServiceId));
//		System.out.println("isNewTab"+ isNewTab);
		if(taxon.getServiceId().compareTo(taxonomyServiceId)==0 && isNewTab){
			loadChildrenListOfItem(vpClassification, taxon, dataSource, taxon.getName(), -1); //parentIndex == -1 means that parent is currentTaxonomy
		}
	}

	private void loadChildrenListOfItem(final VerticalPanel vpClassification, final LightTaxonomyRow taxonomyItem, final String dataSource, final String parentName, final int parentIndex){

		final ContentPanel cp = new ContentPanel();
		cp.setId("cp" + taxonomyItem.getId());
		cp.setHeaderVisible(false);
		cp.setBodyBorder(false);
		cp.add(imageLoading.createImage());
		vpClassification.add(cp);

		//FOR DEBUG
//		TaxonomyRow printParent = taxonomyItem;
//		System.out.println("Load child of item name: " + parentName + " parent id: " + taxonomyItem.getServiceId());
//		while(printParent!=null){
//
//			System.out.println("Parent Name: " + printParent.getName() + " parent id: " +printParent.getServiceId());
//			printParent = printParent.getParent();
//		}

		SpeciesDiscovery.taxonomySearchService.loadListChildrenByParentId(taxonomyItem.getServiceId(), new AsyncCallback<ArrayList<LightTaxonomyRow>>() {

			@Override
			public void onSuccess(ArrayList<LightTaxonomyRow> result) {
				Log.trace("Children returned in client: " + result.size() + " for parentName " +parentName);

				LayoutContainer lc = new LayoutContainer();
				lc.setLayout(new ColumnLayout());

				vpClassification.remove(cp);

				HorizontalPanel hp = new HorizontalPanel();
				Text labelRank = new Text("Rank not found:");
				hp.add(labelRank);
				hp.setCellWidth(labelRank, "120px");

				boolean setRank = false;

				if(result.size()>0){
					for (int i=0; i<result.size()-1; i++) {
						LightTaxonomyRow taxonomy = result.get(i);
//						//FOR DEBUG
//						System.out.println("child "+taxonomy);

						//SET PARENTS
						taxonomy.setParent(Arrays.asList(taxonomyItem));

						//SET RANK IF IS NOT EMPTY
						if(!setRank)
							setRank = replaceLabelRank(hp, labelRank, taxonomy.getRank());

						Text comma = new Text(",");
						comma.setStyleAttribute("margin-right", "5px");

						lc.add(getAnchorByTaxonomy(taxonomy, dataSource, parentIndex));
						lc.add(comma);
					}

					LightTaxonomyRow taxonomy = result.get(result.size()-1);

					if(!setRank)
						setRank = replaceLabelRank(hp, labelRank, taxonomy.getRank());

//					//FOR DEBUG
//					System.out.println("last child "+taxonomy);
					//SET PARENTS
					taxonomy.setParent(Arrays.asList(taxonomyItem));
					lc.add(getAnchorByTaxonomy(taxonomy, dataSource,parentIndex));
				}
				else{
					hp.remove(labelRank);
				}

				hp.add(lc);
				hp.setCellHorizontalAlignment(lc, HorizontalPanel.ALIGN_LEFT);
				vpClassification.add(hp);
				vpClassification.layout();
			}

			@Override
			public void onFailure(Throwable caught) {
				Info.display("Error laoding child", "An error occurred in loading, retry.");
				Log.error("Error laoding child", "An error occurred in loading, retry." +caught);

			}
		});

		vpClassification.layout();
//		hp.add(child)
	}


	/**
	 *
	 * @param hp
	 * @param oldLabelRank
	 * @param rank
	 * @return true if label is replaced
	 */
	private boolean replaceLabelRank(HorizontalPanel hp, Text oldLabelRank, String rank){

		//SET NEW LABEL RANK IF IS NOT EMPTY
		if(!NormalizeString.isUndefined(rank)){

//			System.out.println("In if Taxonomy set rank " + taxonomy.getRank());
			hp.remove(oldLabelRank);
			Text setlabelRank = new Text(rank+":");
			hp.add(setlabelRank);
			hp.setCellWidth(setlabelRank, "120px");
			return true;
		}

		return false;

	}


	/**
	 *
	 * @param taxon
	 * @param dataSource
	 * @param parentIndex - if parent index is equal -1.. the method set parent item of taxon as currentItem
	 * if parent index is equal -2.. the method not set parents of taxon
	 * @return
	 */

	private Anchor getAnchorByTaxonomy(final LightTaxonomyRow taxon, final String dataSource, final int parentIndex)
	{
		Anchor anchor = null;

		if(taxon!=null){

			anchor = new Anchor("<nobr>"+taxon.getName()+"</nobr>", true);

			anchor.addClickHandler(new ClickHandler() {

				@Override
				public void onClick(ClickEvent event) {

//					System.out.println("parent index: "+parentIndex + " size "+currentTaxonomy.getParents().size());

					if(parentIndex >=0 && parentIndex<=currentTaxonomy.getParents().size()){
//						System.out.println("set parent true");
						taxon.setParent(currentTaxonomy.getParents().subList(parentIndex, currentTaxonomy.getParents().size()));
					}
					else if(parentIndex == -1){ //items loaded from get children - the current Taxonomy item is the parent

						List<LightTaxonomyRow> list = new ArrayList<LightTaxonomyRow>();
						list.add(currentTaxonomy);
						list.addAll(currentTaxonomy.getParents());

//						System.out.println("parentIndex == -1");
						printParents(taxon);
						taxon.setParent(list);
					}


					printParents(taxon);

					tablesForTaxonomyRow.addTabItem(taxon, dataSource);

				}
			});
		}

		return anchor;
	}


	void printParents(LightTaxonomyRow taxon){

		System.out.println("principal "+taxon.getName());
		int i = 0;
		for (LightTaxonomyRow tax : taxon.getParents()) {
			System.out.println(++i + " parent name " + tax.getName());
		}
	}


	private Text getLabelByTaxonomy(final TaxonomyRow taxon)
	{
		Text label  = null;

		if(taxon!=null){
			if(!NormalizeString.isUndefined(taxon.getName()))
				label = new Text(taxon.getName());
			else
				label = new Text("Name not found");
		}
		return label;
	}

//	public Html getHTMLTableForTaxonomy(TaxonomyRow row, boolean isNewTab){
//
//		//Init values
//		String dataProviderName = "";
//		String dataSetCitation= "";
//		String matchingAccordionTo= "";
//		String rank= "";
////		String matchingCredits= "";
//
//		String statusName = "";
//		String dateModified = "";
//		String statusRemark = "";
//
//		String author = "";
//		String lsid = "";
//		String credits = "";
//
//		String propertiesHtml = "";
//
//		if(row.getDataProviderName()!=null) dataProviderName = row.getDataProviderName();
//		if(row.getStatusName()!=null) statusName = row.getStatusName();
//		if(row.getDateModified()!=null) dateModified = row.getDateModified();
//
//		if(row.getDataSetCitation()!=null) dataSetCitation = row.getDataSetCitation();
//		if(row.getRank()!=null) rank = row.getRank();
//		if(row.getAccordingTo()!=null) matchingAccordionTo = row.getAccordingTo();
//
//		if(row.getStatusRemarks()!=null) statusRemark = row.getStatusRemarks();
//
//		if(row.getAuthor()!=null) author = row.getAuthor();
//
//		if(row.getLsid()!=null) lsid = row.getLsid();
//
//		if(row.getCredits()!=null) credits = row.getCredits();
//
//
//		if(row.getProperties()!=null){
//
//			List<ItemParameter>  hashProperties = row.getProperties();
//			Collections.sort(hashProperties, ItemParameter.COMPARATOR);
//
//			propertiesHtml+="<table class=\"parameters\">";
//
//			for (ItemParameter itemParameter : hashProperties) {
//
//				propertiesHtml+=
//					"<tr>" +
//					"	<td class=\"title\">"+itemParameter.getKey()+"</td>" +
//					"	<td>"+itemParameter.getValue()+"</td>" +
//					"</tr>";
//			}
//
//			propertiesHtml+="</table>";
//		}
//
//
//		//Create list common name
//		String commonNames = "";
//
////		if(isNewTab){
//			if(row.getCommonNames()!=null){
//				for (CommonName comName : row.getCommonNames()) {
//					commonNames+= "<b>"+comName.getName()+"</b>" +" ("+comName.getLanguage()+") - ";
//				}
//			}
////		}
//
//
//		String table = "<table class=\"imagetable\">";
//
////		if(isNewTab)
//			table+=
//				"<tr>" +
//				"	<td class=\"title\">"+TaxonomyGridField.COMMON_NAMES.getName()+" (Language)</td>" +
//				"	<td>"+commonNames+"</td>" +
//				"</tr>";
//
//
//		table+="<tr>" +
//		"	<td class=\"title\">"+TaxonomyGridField.STATUSREFNAME.getName()+"</td>" +
//		"	<td>"+statusName+"</td>" +
//		"</tr>" +
//		"<tr>" +
//		"	<td class=\"title\">"+TaxonomyGridField.STATUS_REMARKS.getName()+"</td>" +
//		"	<td>"+statusRemark+"</td>" +
//		"</tr>" +
//		"<tr>" +
//		"	<td class=\"title\">"+TaxonomyGridField.DATASOURCE.getName()+"</td>" +
//		"	<td>"+dataProviderName+"</td>" +
//		"</tr>" +
//		"<tr>" +
//		"	<td class=\"title\">"+TaxonomyGridField.DATEMODIFIED.getName()+"</td>" +
//		"	<td>"+dateModified+"</td>" +
//		"</tr>" +
//		"<tr>" +
//		"	<td class=\"title\">"+TaxonomyGridField.MATCHING_RANK.getName()+"</td>" +
//		"	<td>"+rank+"</td>" +
//		"</tr>" +
//		"<tr>" +
//		"	<td class=\"title\">"+TaxonomyGridField.CITATION.getName()+"</td>" +
//		"	<td>"+dataSetCitation+"</td>" +
//		"</tr>" +
////		"<tr>" +
////		"	<td class=\"title\">"+TaxonomyGridField.MATCHING_AUTHOR.getName()+"</td>" +
////		"	<td>"+matchingAccordionTo+"</td>" +
////		"</tr>" +
//		"<tr>" +
//		"	<td class=\"title\">"+TaxonomyGridField.LSID.getName()+"</td>" +
//		"	<td>"+lsid+"</td>" +
//		"</tr>" +
//		"<tr>" +
//		"	<td class=\"title\">"+TaxonomyGridField.AUTHOR.getName()+"</td>" +
//		"	<td>"+author+"</td>" +
//		"</tr>" +
//		"<tr>" +
//		"	<td class=\"title\">"+TaxonomyGridField.CREDITS.getName()+"</td>" +
//		"	<td>"+credits+"</td>" +
//		"</tr>" +
//
//		"<tr>" +
//		"	<td class=\"title\">"+TaxonomyGridField.PROPERTIES.getName()+"</td>" +
//		"	<td>"+propertiesHtml+"</td>" +
//		"</tr>" +
//
//		"</table>";
//		return new Html(table);
//
//	}

//
//	public FlexTable getHTMLTableForTaxonomyWithRef(final LightTaxonomyRow row,boolean isNewTab, final String dataSource) {
//
//		// Init values
//		String dataProviderName = "";
//		String dataSetCitation = "";
//		String matchingAccordionTo = "";
//		String rank = "";
//		// String matchingCredits= "";
//
//		String statusName = "";
//		String dateModified = "";
//		String statusRemark = "";
//
//		String author = "";
//		String lsid = "";
//		String credits = "";
//
//		String refId = null;
//
//		String propertiesHtml = "";
//
//		if (row.getDataProviderName() != null)
//			dataProviderName = row.getDataProviderName();
//		if (row.getStatusName() != null)
//			statusName = row.getStatusName();
//		if (row.getDateModified() != null)
//			dateModified = row.getDateModified();
//
//		if (row.getDataSetCitation() != null)
//			dataSetCitation = row.getDataSetCitation();
//		if (row.getRank() != null)
//			rank = row.getRank();
//		if (row.getAccordingTo() != null)
//			matchingAccordionTo = row.getAccordingTo();
//
//		if (row.getStatusRemarks() != null)
//			statusRemark = row.getStatusRemarks();
//
//		if (row.getAuthor() != null)
//			author = row.getAuthor();
//
//		if (row.getLsid() != null)
//			lsid = row.getLsid();
//
//		if (row.getCredits() != null)
//			credits = row.getCredits();
//
//		if (row.getStatusRefId() != null && !row.getStatusRefId().isEmpty())
//			refId = row.getStatusRefId();
//
//		if (row.getProperties() != null) {
//
//			List<ItemParameter> hashProperties = row.getProperties();
//			Collections.sort(hashProperties,
//					ItemParameter.COMPARATOR);
//
//			propertiesHtml += "<table class=\"parameters\">";
//
//			for (ItemParameter itemParameter : hashProperties) {
//
//				propertiesHtml += "<tr>" + "	<td class=\"title\">"
//						+ itemParameter.getKey() + "</td>" + "	<td>"
//						+ itemParameter.getValue() + "</td>" + "</tr>";
//			}
//
//			propertiesHtml += "</table>";
//		}
//
//		// Create list common name
//		String commonNames = "";
//
//		// if(isNewTab){
//		if (row.getCommonNames() != null) {
//			for (CommonName comName : row.getCommonNames()) {
//				commonNames += "<b>" + comName.getName() + "</b>" + " ("
//						+ comName.getLanguage() + ") - ";
//			}
//		}
//		// }
//
//		final FlexTable flexTable = new FlexTable();
//
//		flexTable.setStyleName("imagetable");
//
//		flexTable.setWidget(0, 0,new Label(TaxonomyGridField.COMMON_NAMES.getName()));
//		flexTable.setWidget(0, 1, new Html(commonNames));
//
//		flexTable.setWidget(1, 0,new Label(TaxonomyGridField.STATUSREFNAME.getName()));
//		flexTable.setWidget(1, 1, new Label(statusName));
//
//		if (refId != null) {
//
//			final String status;
//
//			if(!statusRemark.isEmpty())
//				status = statusRemark;
//			else
//				status = statusName;
//
//			final String referenceId = refId;
//
//			flexTable.setWidget(2, 0, new Label(TaxonomyGridField.STATUS_REMARKS.getName()));
//
//			AbstractImagePrototype synonyms = AbstractImagePrototype.create(Resources.INSTANCE.getSearch());
//
//			final Image imageStatusRemark = synonyms.createImage();
//			imageStatusRemark.setStyleName("image-load-synonyms");
//			imageStatusRemark.setAltText("show accepted name");
//			imageStatusRemark.setTitle("show accepted name");
//			final LayoutContainer layoutContainer = new LayoutContainer();
//
//			final Label labelStatusRemark = new Label(statusRemark);
//			labelStatusRemark.addStyleName("status-of");
//			layoutContainer.add(labelStatusRemark);
//
//			imageStatusRemark.addClickHandler(new ClickHandler() {
//
//				@Override
//				public void onClick(ClickEvent event) {
//					flexTable.remove(layoutContainer);
//					if(row.getParents().size()>0)
//						getTaxonomyByReferenceId(flexTable, 2, 1, status, referenceId, dataSource, row.getParents());
//
//				}
//			});
//
//			layoutContainer.add(labelStatusRemark);
//			layoutContainer.add(imageStatusRemark);
//
//			flexTable.setWidget(2, 1, layoutContainer);
//
//		} else {
//			flexTable.setWidget(2, 0, new Label(TaxonomyGridField.STATUS_REMARKS.getName()));
//			flexTable.setWidget(2, 1, new Label(statusRemark));
//		}
//
//
//		//Create row get synonyms
//		flexTable.setWidget(3, 0,new Label(TaxonomyGridField.SYNONYMS.getName()));
//
//		AbstractImagePrototype synonyms = AbstractImagePrototype.create(Resources.INSTANCE.getSearch());
//
//		final Image imageSynonyms = synonyms.createImage();
//		imageSynonyms.setStyleName("image-load-synonyms");
//		imageSynonyms.setAltText("show synonyms");
//		imageSynonyms.setTitle("show synonyms");
//
//		imageSynonyms.addClickHandler(new ClickHandler() {
//
//			@Override
//			public void onClick(ClickEvent event) {
//				flexTable.remove(imageSynonyms);
//
////				DEBUG
////				System.out.println("getSynonyms of "+row.getName() + "  serviceId "+row.getServiceId());
//
//
//				if(row.getParents().size()>0){
//
//
//					//TODO
//					getSynonymsByReferenceId(flexTable, 3, 1, row.getServiceId(),dataSource, row.getParents());
//
//
//
//				}
//			}
//		});
//
//		flexTable.setWidget(3, 1, imageSynonyms);
//
//
//		flexTable.setWidget(4, 0,new Label(TaxonomyGridField.DATASOURCE.getName()));
//		flexTable.setWidget(4, 1, new Label(dataProviderName));
//
//		flexTable.setWidget(5, 0,new Label(TaxonomyGridField.DATEMODIFIED.getName()));
//		flexTable.setWidget(5, 1, new Label(dateModified));
//
//		flexTable.setWidget(6, 0,
//				new Label(TaxonomyGridField.MATCHING_RANK.getName()));
//		flexTable.setWidget(6, 1, new Label(rank));
//
//		flexTable.setWidget(7, 0,
//				new Label(TaxonomyGridField.CITATION.getName()));
//		flexTable.setWidget(7, 1, new Label(dataSetCitation));
//
//		flexTable.setWidget(8, 0, new Label(TaxonomyGridField.LSID.getName()));
//		flexTable.setWidget(8, 1, new Label(lsid));
//
//		flexTable
//				.setWidget(9, 0, new Label(TaxonomyGridField.AUTHOR.getName()));
//		flexTable.setWidget(9, 1, new Label(author));
//
//		flexTable.setWidget(10, 0,
//				new Label(TaxonomyGridField.CREDITS.getName()));
//		flexTable.setWidget(10, 1, new Label(credits));
//
//		flexTable.setWidget(11, 0,
//				new Label(TaxonomyGridField.PROPERTIES.getName()));
//		flexTable.setWidget(11, 1, new Html(propertiesHtml));
//
//		for (int i = 0; i < flexTable.getRowCount(); i++) {
//			flexTable.getFlexCellFormatter().setStyleName(i, 0, "title");
//		}
//
//		return flexTable;
//
//	}

	public void getSynonymsByReferenceId(FlexTable flexTable, int row, int col, String refId, final String dataSource, final List<LightTaxonomyRow> parents){

		final LayoutContainer layoutContainer = new LayoutContainer();
		final Image loading = AbstractImagePrototype.create(Resources.INSTANCE.loadingBalls()).createImage();
		layoutContainer.add(loading);

		flexTable.setWidget(row, col, layoutContainer);

		SpeciesDiscovery.taxonomySearchService.retrieveSynonymsByRefId(refId, new AsyncCallback<List<LightTaxonomyRow>>() {

			@Override
			public void onFailure(Throwable caught) {
				Info.display("Error laoding child", "An error occurred in loading, retry.");
				Log.error("Error laoding child", "An error occurred in loading, retry." +caught);

			}

			@Override
			public void onSuccess(List<LightTaxonomyRow> result) {

				Log.trace("getReferenceById return " +result.size() + " items");
//				System.out.println("getReferenceById return " +result.size() + " items");
				if(result.size()>0){
					layoutContainer.remove(loading);

					LayoutContainer lc = new LayoutContainer();
					lc.setLayout(new ColumnLayout());

					if(result.size()>0){
						for (int i=0; i<result.size()-1; i++) {
							LightTaxonomyRow taxonomy = result.get(i);
//							System.out.println("child "+taxonomy);

							//SET PARENTS
							taxonomy.setParent(parents);

							Text comma = new Text(",");
							comma.setStyleAttribute("margin-right", "5px");



							lc.add(getAnchorByTaxonomy(taxonomy, dataSource,-2));


							lc.add(comma);
						}

						LightTaxonomyRow taxonomy = result.get(result.size()-1);

//						System.out.println("child "+taxonomy);
						//SET PARENTS
						taxonomy.setParent(parents);

						lc.add(getAnchorByTaxonomy(taxonomy, dataSource,-2));
					}
					layoutContainer.add(lc);

				}
				else{
					layoutContainer.remove(loading);
					layoutContainer.add(new Label("not found"));
				}

				layoutContainer.layout();
			}
		});

	}


	public void getTaxonomyByReferenceId(FlexTable flexTable, int row, int col, final String statusRemark, String refId, final String dataSource, final List<LightTaxonomyRow> parents){

		final LayoutContainer layoutContainer = new LayoutContainer();
//		hp.getElement().getStyle().setBorderStyle(BorderStyle.NONE);
//		hp.getElement().getStyle().setBorderWidth(0, Unit.PX);
		final Label status = new Label(statusRemark);

		layoutContainer.add(status);
//		hp.setCellWidth(status, "160px");
		final Image loading = AbstractImagePrototype.create(Resources.INSTANCE.loadingBalls()).createImage();
		layoutContainer.add(loading);

		flexTable.setWidget(row, col, layoutContainer);

		List<String> listId = new ArrayList<String>();
		listId.add(refId);

		SpeciesDiscovery.taxonomySearchService.retrieveTaxonomyByIds(listId, new AsyncCallback<List<LightTaxonomyRow>>() {

			@Override
			public void onFailure(Throwable caught) {
				Info.display("Error laoding child", "An error occurred in loading, retry.");
				Log.error("Error laoding child", "An error occurred in loading, retry." +caught);

			}

			@Override
			public void onSuccess(List<LightTaxonomyRow> result) {

				Log.trace("getReferenceById return " +result.size() + " items");
//				System.out.println("getReferenceById return " +result.size() + " items");
				if(result.size()>0){
					layoutContainer.remove(status);
					layoutContainer.remove(loading);

					Label labelOf = new Label(statusRemark + " for ");
					layoutContainer.add(labelOf);

					LayoutContainer lc = new LayoutContainer();
					lc.setLayout(new ColumnLayout());

					if(result.size()>0){
						for (int i=0; i<result.size()-1; i++) {
							LightTaxonomyRow taxonomy = result.get(i);
//							System.out.println("child "+taxonomy);

							//SET PARENTS
							taxonomy.setParent(parents);

							Text comma = new Text(",");
							comma.setStyleAttribute("margin-right", "5px");

							lc.add(comma);
						}

						LightTaxonomyRow taxonomy = result.get(result.size()-1);

						//FOR DEBUG
//						System.out.println("child "+taxonomy);
						//SET PARENTS
						taxonomy.setParent(parents);

						//TODO
						lc.add(getAnchorByTaxonomy(taxonomy, dataSource, -2));


					}
					layoutContainer.add(lc);

				}
				layoutContainer.layout();
			}
		});

	}
}
