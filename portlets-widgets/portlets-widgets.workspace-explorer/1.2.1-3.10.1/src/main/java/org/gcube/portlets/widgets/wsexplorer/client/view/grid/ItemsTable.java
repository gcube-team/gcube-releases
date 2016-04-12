/**
 *
 */
package org.gcube.portlets.widgets.wsexplorer.client.view.grid;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.gcube.portlets.widgets.wsexplorer.client.Util;
import org.gcube.portlets.widgets.wsexplorer.client.view.SelectionItem;
import org.gcube.portlets.widgets.wsexplorer.shared.Item;

import com.github.gwtbootstrap.client.ui.Pagination;
import com.google.gwt.cell.client.ButtonCell;
import com.google.gwt.cell.client.Cell.Context;
import com.google.gwt.cell.client.DateCell;
import com.google.gwt.cell.client.ImageResourceCell;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.cellview.client.AbstractCellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.ColumnSortEvent.ListHandler;
import com.google.gwt.user.cellview.client.SimplePager;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
/**
 * The Class PackagesTable.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Feb 19, 2015
 */
public class ItemsTable extends AbstractItemsCellTable implements SelectionItem{

	private Column<Item, ImageResource> icon;
	private TextColumn<Item> name;
	private TextColumn<Item> owner;
	public DateTimeFormat dtformat = DateTimeFormat.getFormat("dd MMM hh:mm aaa yyyy");
//	private Item selectedItem = null;

	/**
	 * The Enum DISPLAY_FIELD.
	 *
	 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
	 * Feb 1, 2016
	 */
	public static enum DISPLAY_FIELD{ICON, NAME, OWNER, CREATION_DATE};
	private List<DISPLAY_FIELD> displayFields;
	private Column<Item, Date> dateColumn;

	/**
	 * Instantiates a new items table.
	 *
	 * @param eventBus the event bus
	 * @param showMoreInfo the show more info
	 * @param fields the fields
	 */
	public ItemsTable(HandlerManager eventBus, boolean showMoreInfo, DISPLAY_FIELD[] fields) {
		super(eventBus, showMoreInfo);
		setDisplayFields(fields);
		initTable(cellTable, null, null);
	}


	/* (non-Javadoc)
	 * @see org.gcube.portlets.widgets.wsexplorer.client.view.grid.AbstractItemsCellTable#updateItems(java.util.List, boolean)
	 */
	public void updateItems(List<Item> items, boolean removeOldItems) {
		super.updateItems(items, removeOldItems);
	}



	/**
	 * Adds the items.
	 *
	 * @param items the items
	 */
	public void addItems(List<Item> items) {

		for (int i=0; i<items.size(); i++) {
			dataProvider.getList().add(i, items.get(i));
		}

	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.admin.gcubereleases.client.view.AbstractPackageTable#initTable(com.google.gwt.user.cellview.client.AbstractCellTable, com.google.gwt.user.cellview.client.SimplePager, com.github.gwtbootstrap.client.ui.Pagination)
	 */
	@Override
	public void initTable(final AbstractCellTable<Item> abstractCellTable, final SimplePager pager, final Pagination pagination) {
		abstractCellTable.setEmptyTableWidget(new Label("No data."));

	    if(this.displayFields.contains(DISPLAY_FIELD.ICON)){
			//ICONS
		    ImageResourceCell iconResourceCell = new ImageResourceCell() {
		        public Set<String> getConsumedEvents() {
		            HashSet<String> events = new HashSet<String>();
		            events.add("click");
		            return events;
		        }
		    };

		    icon = new Column<Item, ImageResource>(iconResourceCell) {
		        @Override
		        public ImageResource getValue(Item dataObj) {
		        	return Util.getImage(dataObj);
		        }
		        /* (non-Javadoc)
		         * @see com.google.gwt.user.cellview.client.Column#render(com.google.gwt.cell.client.Cell.Context, java.lang.Object, com.google.gwt.safehtml.shared.SafeHtmlBuilder)
		         */
		        @Override
		        public void render(Context context, Item object, SafeHtmlBuilder sb) {
		        	super.render(context, object, sb);

		        }
		    };
		    icon.setSortable(false);
			abstractCellTable.addColumn(icon, "");
		    abstractCellTable.setColumnWidth(icon, 10.0, Unit.PCT);
	    }

	    if(this.displayFields.contains(DISPLAY_FIELD.NAME)){
			//ARTIFACT ID
			name = new TextColumn<Item>() {
				@Override
				public String getValue(Item object) {
					return object.getName();
				}
			};
			name.setSortable(true);

			abstractCellTable.addColumn(name, "Name");

//			double width = displayFields.size()>1?50:90;
//			abstractCellTable.setColumnWidth(name, width, Unit.PCT);

			ListHandler<Item> nameColumnHandler = new ListHandler<Item>(dataProvider.getList());
			nameColumnHandler.setComparator(name, new Comparator<Item>() {
				@Override
				public int compare(Item o1, Item o2) {
					return o1.getName().compareTo(o2.getName());
				}
			});

			abstractCellTable.addColumnSortHandler(nameColumnHandler);

	    }

	    if(this.displayFields.contains(DISPLAY_FIELD.OWNER)){

			owner = new TextColumn<Item>() {
				@Override
				public String getValue(Item object) {
					return object.getOwner() != null ? object.getOwner() : "";
				}
			};

			owner.setSortable(true);
			abstractCellTable.addColumn(owner, "Owner");
//			double width = displayFields.size()==2 ?90:40;
//			abstractCellTable.setColumnWidth(owner, width, Unit.PCT);

			ListHandler<Item> ownerColumnHandler = new ListHandler<Item>(dataProvider.getList());
			ownerColumnHandler.setComparator(owner, new Comparator<Item>() {
				@Override
				public int compare(Item o1, Item o2) {
					return o1.getOwner().compareTo(o2.getOwner());
				}
			});
			abstractCellTable.addColumnSortHandler(ownerColumnHandler);

	    }

	    if(this.displayFields.contains(DISPLAY_FIELD.CREATION_DATE)){

	    	DateCell date = new DateCell(dtformat);
	    	dateColumn = new Column<Item, Date>(date){

				@Override
				public Date getValue(Item object) {
					return object.getCreationDate();
				}
	    	};

	    	dateColumn.setSortable(true);
	    	ListHandler<Item> dateColumnHandler = new ListHandler<Item>(dataProvider.getList());
	    	dateColumnHandler.setComparator(dateColumn, new Comparator<Item>() {
				@Override
				public int compare(Item o1, Item o2) {
					return o1.getCreationDate().compareTo(o2.getCreationDate());
				}
			});
			abstractCellTable.addColumnSortHandler(dateColumnHandler);
	    	abstractCellTable.addColumn(dateColumn, "Created");
	    }

		/*final SingleSelectionModel<Item> selectionModel = new SingleSelectionModel<Item>();
		selectionModel.addSelectionChangeHandler(new Handler() {
			@Override
			public void onSelectionChange(SelectionChangeEvent event) {
			}
		});*/
	}

	/**
	 * Sets the display fields.
	 *
	 * @param fields the new display fields
	 */
	public void setDisplayFields(DISPLAY_FIELD[] fields) {
		this.displayFields = fields!=null && fields.length>0?Arrays.asList(fields):Arrays.asList(DISPLAY_FIELD.values());
	}

	/**
	 * Gets the display fields.
	 *
	 * @return the displayFields
	 */
	public List<DISPLAY_FIELD> getDisplayFields() {
		return displayFields;
	}

	/**
	 * The Class ButtonImageCell.
	 *
	 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
	 * Feb 1, 2016
	 */
	public class ButtonImageCell extends ButtonCell{

	    /* (non-Javadoc)
    	 * @see com.google.gwt.cell.client.AbstractSafeHtmlCell#render(com.google.gwt.cell.client.Cell.Context, java.lang.Object, com.google.gwt.safehtml.shared.SafeHtmlBuilder)
    	 */
    	@Override
	    public void render(com.google.gwt.cell.client.Cell.Context context,
	            String value, SafeHtmlBuilder sb) {
	        SafeHtml html = SafeHtmlUtils.fromTrustedString(new Image(value).toString());
	        sb.append(html);
	    }
	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.widgets.wsexplorer.client.notification.SelectionItemHandler#getSelectionItem()
	 */
	@Override
	public Item getSelectedItem() {
		return ssm.getSelectedObject();
	}
}
