/**
 *
 */
package org.gcube.portlets.user.workspaceexplorerapp.client.grid;

import gwt.material.design.client.base.MaterialImageCell;
import gwt.material.design.client.constants.IconPosition;
import gwt.material.design.client.constants.IconType;
import gwt.material.design.client.constants.ImageType;
import gwt.material.design.client.ui.MaterialIcon;
import gwt.material.design.client.ui.MaterialImage;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.gcube.portlets.user.workspaceexplorerapp.client.Util;
import org.gcube.portlets.user.workspaceexplorerapp.client.event.LoadFolderEvent;
import org.gcube.portlets.user.workspaceexplorerapp.client.view.PopupContextMenu;
import org.gcube.portlets.user.workspaceexplorerapp.client.view.SelectionItem;
import org.gcube.portlets.user.workspaceexplorerapp.shared.Item;

import com.github.gwtbootstrap.client.ui.Pagination;
import com.google.gwt.cell.client.ButtonCell;
import com.google.gwt.cell.client.Cell.Context;
import com.google.gwt.cell.client.DateCell;
import com.google.gwt.cell.client.SafeHtmlCell;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.ColumnSortEvent;
import com.google.gwt.user.cellview.client.ColumnSortEvent.ListHandler;
import com.google.gwt.user.cellview.client.ColumnSortList.ColumnSortInfo;
import com.google.gwt.user.cellview.client.SimplePager;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
/**
 * The Class PackagesTable.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Feb 19, 2015
 */
public class ItemsTable extends AbstractItemsCellTable implements SelectionItem{

	private DateTimeFormat dateFormatter = DateTimeFormat.getFormat("dd MMM hh:mm aaa yyyy");

	/**
	 * The Enum DISPLAY_FIELD.
	 *
	 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
	 * Feb 1, 2016
	 */
//	public static enum DISPLAY_FIELD{ICON, NAME, OWNER};
	private List<DisplayField> displayFields;

	private Map<String, Integer> mapFieldToColumnIndex = new HashMap<String, Integer>();

	private Column<Item, Date> dateColumn;

	/**
	 * Instantiates a new items table.
	 *
	 * @param eventBus the event bus
	 * @param showMoreInfo the show more info
	 * @param fields the fields
	 */
	public ItemsTable(HandlerManager eventBus, boolean showMoreInfo, DisplayField[] fields) {
		super(eventBus, showMoreInfo);
		setDisplayFields(fields);
		initTable(null, null);

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
		if(items!=null)
			dataProvider.getList().addAll(items);
	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.admin.gcubereleases.client.view.AbstractPackageTable#initTable(com.google.gwt.user.cellview.client.AbstractCellTable, com.google.gwt.user.cellview.client.SimplePager, com.github.gwtbootstrap.client.ui.Pagination)
	 */
	@Override
	public void initTable(final SimplePager pager, final Pagination pagination) {
		dataGrid.setEmptyTableWidget(new Label("No data."));

	    if(this.displayFields.contains(DisplayField.ICON)){

	    	MaterialImageCell myIcon = new MaterialImageCell(){
	    		 public Set<String> getConsumedEvents() {
			            HashSet<String> events = new HashSet<String>();
			            events.add("click");
			            return events;
			        }
	    	};

		     // IMAGE
	         Column<Item, MaterialImage> icon = new Column<Item, MaterialImage>(myIcon) {
	            @Override
	            public MaterialImage getValue(final Item object) {
	            	MaterialImage img = new MaterialImage();
	            	img.setUrl(Util.getImageResource(object).getSafeUri().asString());
	            	img.setWidth("32px");
	            	img.setHeight("32px");
	            	img.setType(ImageType.DEFAULT);

	            	if(object.isFolder()){
	            		img.addStyleName("navigation-cursor");
	            	}

	                return img;
	            }

	            public void onBrowserEvent(com.google.gwt.cell.client.Cell.Context context, Element elem, Item object, NativeEvent event) {
	            	super.onBrowserEvent(context, elem, object, event);
	            	GWT.log("onBrowserEvent ON IMG "+event.getType());
	            	// Handle the click event.
					if ("click".equals(event.getType())) {
						if(object.isFolder()){
		            		GWT.log("clicked folder onBrowserEvent "+object);
		            		eventBus.fireEvent(new LoadFolderEvent(object));
		            	}
					}
	            };
	        };

		    icon.setSortable(false);
//			packageTable.addColumn(icon, "");
		    dataGrid.addColumn(icon);
			dataGrid.setColumnWidth(icon, "45px");
	    }

	    Column<Item, SafeHtml> nameColumn;
		if(this.displayFields.contains(DisplayField.NAME)){

	    	final SafeHtmlCell progressCell = new SafeHtmlCell();

	        nameColumn = new Column<Item, SafeHtml>(progressCell) {

	            @Override
	            public SafeHtml getValue(Item object) {
	                SafeHtmlBuilder sb = new SafeHtmlBuilder();
	                sb.appendHtmlConstant("<div style=''>");
	                sb.appendHtmlConstant(object.getName());
	                String formatDate = dateFormatter.format(object.getCreationDate());
	                String tooltipDate = "Created: "+formatDate;
	                sb.appendHtmlConstant("<div title='"+tooltipDate+"' style='display: block; text-align: left; font-size: 11px; font-family: Arial, Courier; opacity: 0.8;'>");
	                sb.appendHtmlConstant(formatDate);
	                sb.appendHtmlConstant("</div></div>");
	                return sb.toSafeHtml();
	            }
	        };

            nameColumn.setSortable(true);
            ListHandler<Item> nameColumnHandler = new ListHandler<Item>(dataProvider.getList());
        	nameColumnHandler.setComparator(nameColumn, new Comparator<Item>() {
				@Override
				public int compare(Item o1, Item o2) {

					if(o1==null)
						return 1;
					else if(o2==null)
						return -1;

					//XOR
					if (o1.isSpecialFolder() ^ o2.isSpecialFolder()) return o1.isSpecialFolder() ? -1 : 1;
					if (o1.isFolder() ^ o2.isFolder()) return o1.isFolder() ? -1 : 1;

					return String.CASE_INSENSITIVE_ORDER.compare(o1.getName(), o2.getName());
				}
			});

			dataGrid.addColumnSortHandler(nameColumnHandler);
	        dataGrid.addColumn(nameColumn);
	        mapFieldToColumnIndex.put(DisplayField.NAME.getLabel(), dataGrid.getColumnIndex(nameColumn));
	        dataGrid.getColumnSortList().push(nameColumn);
	    }

	    if(this.displayFields.contains(DisplayField.OWNER)){

	    	TextColumn<Item> owner = new TextColumn<Item>() {
				@Override
				public String getValue(Item object) {
					return object.getOwner() != null ? object.getOwner() : "";
				}
			};

			owner.setSortable(true);
			ListHandler<Item> ownerColumnHandler = new ListHandler<Item>(dataProvider.getList());
			ownerColumnHandler.setComparator(owner, new Comparator<Item>() {
				@Override
				public int compare(Item o1, Item o2) {

					if(o1==null)
						return 1;
					else if (o2==null)
						return -1;

					return o1.getOwner().compareTo(o2.getOwner());
				}
			});

			dataGrid.addColumnSortHandler(ownerColumnHandler);
			dataGrid.addColumn(owner);
			mapFieldToColumnIndex.put(DisplayField.OWNER.getLabel(), dataGrid.getColumnIndex(owner));
	    }


	    if(this.displayFields.contains(DisplayField.CREATION_DATE)){

	    	DateCell dateCell = new DateCell();
	    	dateColumn = new Column<Item, Date>(dateCell) {
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

	        dataGrid.addColumnSortHandler(dateColumnHandler);
//	        dataGrid.addColumn(dateColumn);
//	        mapFieldToColumnIndex.put(DisplayField.CREATION_DATE.getLabel(), dataGrid.getColumnIndex(dateColumn));
	    }


	    Column<Item, MaterialIcon> moreColumn = new Column<Item, MaterialIcon>(new MaterialIconCell()){

			@Override
			public MaterialIcon getValue(final Item object) {
				MaterialIcon button = new MaterialIcon();
				button.setIconColor("black");
				button.setWidth("15px");
//				button.setWaves(WavesType.DEFAULT);
				button.setIconPosition(IconPosition.RIGHT);
				button.getElement().getStyle().setMarginRight(17.0,Unit.PX);
				button.setIconType(IconType.MORE_VERT);
				return button;
			}

			/* (non-Javadoc)
			 * @see com.google.gwt.user.cellview.client.Column#onBrowserEvent(com.google.gwt.cell.client.Cell.Context, com.google.gwt.dom.client.Element, java.lang.Object, com.google.gwt.dom.client.NativeEvent)
			 */
			@Override
			public void onBrowserEvent(Context context, Element elem, Item object, NativeEvent event) {


				// Handle the click event.
				if ("click".equals(event.getType())) {
					  //skip right click
					if (event.getButton() == NativeEvent.BUTTON_RIGHT) {
						return;
					}
					PopupContextMenu popupCM = new PopupContextMenu(true, eventBus, object);
					popupCM.showPopup(event.getClientX()-100, Window.getScrollTop()+event.getClientY());
				}
			}

	    };
	    moreColumn.setSortable(false);
//		packageTable.addColumn(moreColumn, "");
	    dataGrid.addColumn(moreColumn);
		dataGrid.setColumnWidth(moreColumn, "50px");
	}

	/**
	 * Sets the display fields.
	 *
	 * @param fields the new display fields
	 */
	public void setDisplayFields(DisplayField[] fields) {
		this.displayFields = fields!=null && fields.length>0?Arrays.asList(fields):Arrays.asList(DisplayField.values());
	}

	/**
	 * Gets the display fields.
	 *
	 * @return the displayFields
	 */
	public List<DisplayField> getDisplayFields() {
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
	public Set<Item> getSelectedItems() {
		Set<Item> items = msm.getSelectedSet();

		if(items==null || items.isEmpty())
			return null;

		return items;
	}

	/**
	 * @param label
	 */
	public void sortDataBy(String label) {
		GWT.log("sort by "+label);
		boolean isAscending = true;
		if(this.dataGrid.getColumnSortList().size()>0)
			isAscending = this.dataGrid.getColumnSortList().get(0).isAscending();

		boolean removeDateColumn = false;
		if(label.compareTo(DisplayField.CREATION_DATE.getLabel())==0){
	        dataGrid.addColumn(dateColumn);
	        mapFieldToColumnIndex.put(DisplayField.CREATION_DATE.getLabel(), dataGrid.getColumnIndex(dateColumn));
	        removeDateColumn = true;
		}

		int colIndex = mapFieldToColumnIndex.get(label);

		GWT.log("colIndex " + colIndex);
		GWT.log("isAscending " + isAscending);
		if(colIndex>-1){
			Column<Item, ?> column = this.dataGrid.getColumn(colIndex);
			if (column != null && column.isSortable()) {
				this.dataGrid.getColumnSortList().clear();
				setSortedColumn(column, !isAscending);
				ColumnSortEvent.fire(dataGrid, dataGrid.getColumnSortList());

				if(removeDateColumn)
					dataGrid.removeColumn(colIndex);
			}
		}
	}

	/**
	 * Displays the appropriate sorted icon in the header of the column for the given index.
	 *
	 * @param columnIndex
	 *            of the column to mark as sorted
	 * @param ascending
	 *            <code>true</code> for ascending icon, <code>false</code> for descending icon
	 */
	 public void setSortedColumn(Column<Item, ?> column,  boolean ascending) {

	      if (column != null && column.isSortable()) {
	           ColumnSortInfo info = this.dataGrid.getColumnSortList().push(column);
	           GWT.log("ColumnSortInfo is anscending "+info.isAscending());
	           if (info.isAscending() != ascending) {
	        	   this.dataGrid.getColumnSortList().push(column);
	           }
	      }
	 }
}
