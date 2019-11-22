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
import org.gcube.portlets.widgets.wsexplorer.client.WorkspaceExplorerConstants;
import org.gcube.portlets.widgets.wsexplorer.client.resources.WorkspaceExplorerResources;
import org.gcube.portlets.widgets.wsexplorer.client.view.SelectionItem;
import org.gcube.portlets.widgets.wsexplorer.client.view.gcubeitem.DialogShowGcubeItem;
import org.gcube.portlets.widgets.wsexplorer.shared.Item;

import com.github.gwtbootstrap.client.ui.Pagination;
import com.google.gwt.cell.client.ButtonCell;
import com.google.gwt.cell.client.Cell.Context;
import com.google.gwt.cell.client.DateCell;
import com.google.gwt.cell.client.ImageResourceCell;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.ColumnSortEvent;
import com.google.gwt.user.cellview.client.ColumnSortList.ColumnSortInfo;
import com.google.gwt.user.cellview.client.SimplePager;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.view.client.AbstractDataProvider;
import com.google.gwt.view.client.ListDataProvider;

/**
 * The Class PackagesTable.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Feb 19, 2015
 * @param <T> the generic type
 */
public class ItemsTable<T extends Item> extends AbstractItemsCellTable<T> implements SelectionItem{

	private Column<T, ImageResource> icon;
	private TextColumn<T> name;
	private TextColumn<T> owner;
	public  DateTimeFormat dtformat = DateTimeFormat.getFormat("dd MMM hh:mm aaa yyyy");
	public ImageResource info = WorkspaceExplorerResources.ICONS.infoSquare();

	private AbstractDataProvider<T> dataProvider;

	/**
	 * The Enum DISPLAY_FIELD.
	 *
	 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
	 * Feb 1, 2016
	 */
	public static enum DISPLAY_FIELD{ICON, NAME, OWNER, CREATION_DATE};
	private List<DISPLAY_FIELD> displayFields;
	private Column<T, Date> dateColumn;
	private List<String> displayProperties;
	private boolean showGcubeItemsInfo;
	private DISPLAY_FIELD startSortByColumn;
	private boolean isAsyncronusTable;


	/**
	 * Instantiates a new items table.
	 *
	 * @param eventBus the event bus
	 * @param showMoreInfo the show more info
	 * @param fields the fields
	 * @param displayProperties the display properties
	 * @param showGcubeItemsInfo the show gcube items info
	 * @param startSortByColumn the start sort by column
	 */
	public ItemsTable(HandlerManager eventBus, boolean showMoreInfo, DISPLAY_FIELD[] fields, List<String> displayProperties, boolean showGcubeItemsInfo, DISPLAY_FIELD startSortByColumn) {
		this.eventBus = eventBus;
		this.startSortByColumn = startSortByColumn;
		setDisplayFields(fields);
		setDisplayProperties(displayProperties);
		setShowGcubeItemsInfo(showGcubeItemsInfo);
		//initTable(null, null);
	}


	/**
	 * Sets the show gcube items info.
	 *
	 * @param showGcubeItemsInfo the new show gcube items info
	 */
	private void setShowGcubeItemsInfo(boolean showGcubeItemsInfo) {
		this.showGcubeItemsInfo = showGcubeItemsInfo;
	}


	/* (non-Javadoc)
	 * @see org.gcube.portlets.widgets.wsexplorer.client.view.grid.AbstractItemsCellTable#updateItems(java.util.List, boolean)
	 */
	public void updateItems(List<T> items, boolean removeOldItems) {
		super.updateItems(items, removeOldItems);
	}


	/**
	 * Adds the items.
	 *
	 * @param items the items
	 */
	public void addItems(List<T> items) {
		super.addItems(items);
	}


	/* (non-Javadoc)
	 * @see org.gcube.portlets.admin.gcubereleases.client.view.AbstractPackageTable#initTable(com.google.gwt.user.cellview.client.AbstractCellTable, com.google.gwt.user.cellview.client.SimplePager, com.github.gwtbootstrap.client.ui.Pagination)
	 */
	@Override
	public void initTable(final SimplePager pager, final Pagination pagination, AbstractDataProvider<T> dataProvider) {
		this.dataProvider = dataProvider;
		initAbstractTable(eventBus, fireEventOnClick, dataProvider, WorkspaceExplorerConstants.ITEMS_PER_PAGE);
		this.dataProvider.addDataDisplay(sortedCellTable);

		//this.isAsyncronusTable = dataProvider instanceof AbstractDataProvider?true:false;
		this.isAsyncronusTable = dataProvider instanceof ListDataProvider?false:true;

		//sortedCellTable.setEmptyTableWidget(new Label(WorkspaceExplorerConstants.WORKSPACE_FOLDER_EMPTY_MESSAGE));
		setEmptyTableMessage(WorkspaceExplorerConstants.WORKSPACE_FOLDER_EMPTY_MESSAGE);

	    if(this.displayFields.contains(DISPLAY_FIELD.ICON)){
			//ICONS
		    ImageResourceCell iconResourceCell = new ImageResourceCell() {
		        public Set<String> getConsumedEvents() {
		            HashSet<String> events = new HashSet<String>();
		            events.add("click");
		            return events;
		        }
		    };

		    icon = new Column<T, ImageResource>(iconResourceCell) {
		        @Override
		        public ImageResource getValue(T dataObj) {
		        	return Util.getImage(dataObj);
		        }
		        /* (non-Javadoc)
		         * @see com.google.gwt.user.cellview.client.Column#render(com.google.gwt.cell.client.Cell.Context, java.lang.Object, com.google.gwt.safehtml.shared.SafeHtmlBuilder)
		         */
		        @Override
		        public void render(Context context, T object, SafeHtmlBuilder sb) {
		        	super.render(context, object, sb);

		        }
		    };
//		    icon.setSortable(false);
		    sortedCellTable.addColumn(icon, "", false);
		    sortedCellTable.setColumnWidth(icon, 32.0, Unit.PX);
	    }

	    if(this.displayFields.contains(DISPLAY_FIELD.NAME)){

			//NAME
			name = new TextColumn<T>() {
				@Override
				public String getValue(T object) {
					if(object==null)
						return "";
					return ((Item) object).getName();
				}

				//ADDING TOOLTIP
				@Override
				public void render(com.google.gwt.cell.client.Cell.Context context, T object, SafeHtmlBuilder sb) {
					if(object == null)
						return;
					sb.appendHtmlConstant("<div title=\""+((Item) object).getName()+"\">");
					super.render(context, object, sb);
					sb.appendHtmlConstant("</div>");
				};
			};

			sortedCellTable.addColumn(name, "Name", true);

			if(!isAsyncronusTable){
				Comparator<T> c = new Comparator<T>() {
					@Override
					public int compare(T o1, T o2) {
						return ((Item) o1).getName().compareTo(((Item) o2).getName());
					}
				};

				sortedCellTable.setComparator(name, c);
			}

	    }

	    if(this.displayFields.contains(DISPLAY_FIELD.OWNER)){

			owner = new TextColumn<T>() {
				@Override
				public String getValue(T object) {
					if(object==null)
						return "";
					return ((Item) object).getOwner() != null ? ((Item) object).getOwner() : "";
				}
			};

			sortedCellTable.addColumn(owner, "Owner", true);

			if(!isAsyncronusTable){
				Comparator<T> c = new Comparator<T>() {
					@Override
					public int compare(T o1, T o2) {
						return ((Item) o1).getOwner().compareTo(((Item) o2).getOwner());
					}
				};
				sortedCellTable.setComparator(owner, c);
			}

	    }

	    if(this.displayFields.contains(DISPLAY_FIELD.CREATION_DATE)){

	    	DateCell date = new DateCell(dtformat);
	    	dateColumn = new Column<T, Date>(date){

				@Override
				public Date getValue(T object) {
					if(object==null)
						return null;
					return ((Item) object).getCreationDate();
				}
	    	};
	    	sortedCellTable.addColumn(dateColumn, "Created", true);

	    	if(!isAsyncronusTable){
		    	Comparator<T> c = new Comparator<T>() {
					@Override
					public int compare(T o1, T o2) {
						if(o1 == null || o1.getCreationDate()==null)
	 						return -1;

		    			if(o2 == null || o2.getCreationDate()==null)
	 						return 1;

		    			Date d1 = ((Item) o1).getCreationDate();
		    			Date d2 = ((Item) o2).getCreationDate();

	//	    			GWT.log(d1.toString() + "is after "+d2.toString() +" ? "+d2.after(d1));

						if(d1.after(d2))
							return 1;
		    			else
		    				return -1;
					}
				};
				GWT.log("date colum sortable");
				sortedCellTable.setComparator(dateColumn,c);
	    	}

	    }


	    if(displayProperties!=null){

	    	 for (final String column : displayProperties) {
	 			//NAME
	    		 TextColumn<T> textColumn = new TextColumn<T>() {
	 				@Override
	 				public String getValue(T object) {
	 					Item extensionItem;
	 					String value = null;
	 					if(object instanceof Item){
	 						extensionItem = object;
	 						value = extensionItem.getGcubeProperties().get(column);
	 					}
	 					return value==null?"":value;
	 				}
	 			};

	 			sortedCellTable.addColumn(textColumn, column, true);
	 			if(!isAsyncronusTable){
		 			Comparator<T> c = new Comparator<T>() {
		 				@Override
		 				public int compare(T o1, T o2) {

		 					if(!(o1 instanceof Item))
		 						return -1;

		 					if(!(o2 instanceof Item))
		 						return 1;

		 					Item e1 = o1;
		 					Item e2 = o2;
		 					String v1 = e1.getGcubeProperties().get(column);
		 					String v2 = e2.getGcubeProperties().get(column);

		 					if(v1==null)
		 						return 1;
		 					if(v2==null)
		 						return -1;

		 					return v1.compareToIgnoreCase(v2);
		 				}
		 			};

		 			sortedCellTable.setComparator(textColumn, c);
	 			}
	 		}
	    }

	    if(showGcubeItemsInfo){

			//ICONS
		    ImageResourceCell showGcubeInfo = new ImageResourceCell() {
		        public Set<String> getConsumedEvents() {
		            HashSet<String> events = new HashSet<String>();
		            events.add("click");
		            return events;
		        }
		    };

		    MyToolTipColumn<T, ImageResource> showGcubeInfoClm = new MyToolTipColumn<T, ImageResource>(showGcubeInfo, "Show gcube properties") {

				@Override
				public ImageResource getValue(T object) {
					return info;
				}

				@Override
		        public void render(Context context, T object, SafeHtmlBuilder sb) {
		        	super.render(context, object, sb);
		        }

				/* (non-Javadoc)
				 * @see com.google.gwt.user.cellview.client.Column#onBrowserEvent(com.google.gwt.cell.client.Cell.Context, com.google.gwt.dom.client.Element, java.lang.Object, com.google.gwt.dom.client.NativeEvent)
				 */
				@Override
				public void onBrowserEvent(
					Context context, Element elem, T object, NativeEvent event) {
				 	super.onBrowserEvent(context, elem, object, event);
		            if ("click".equals(event.getType())) {
		            	Item item = object;
		    	    	DialogShowGcubeItem dg = new DialogShowGcubeItem("Gcube Properties for: "+item.getName(), null, item, true);
//		    	    	dg.setPopupPosition(event.getClientX()-Integer.parseInt(dg.getElement().getStyle().getWidth()), event.getClientY());
		    	    	dg.center();
		            }
				}
			};

		    sortedCellTable.addColumn(showGcubeInfoClm, "", false);
		    sortedCellTable.setColumnWidth(showGcubeInfoClm, 32.0, Unit.PX);
	    }

	    GWT.log("startSortByColumn: "+startSortByColumn);

	    if(startSortByColumn!=null)
		    switch (startSortByColumn) {
			case NAME:
				if(this.displayFields.contains(DISPLAY_FIELD.NAME)){
					sortedCellTable.setInitialSortColumn(name);
				}
				break;
			case OWNER:
				if(this.displayFields.contains(DISPLAY_FIELD.OWNER)){
					sortedCellTable.setInitialSortColumn(owner);
				}
				break;
			case CREATION_DATE:
				if(this.displayFields.contains(DISPLAY_FIELD.CREATION_DATE)){
					sortedCellTable.setDefaultSortOrder(dateColumn, false); // sorts ascending on first click
					sortedCellTable.setInitialSortColumn(dateColumn);
					GWT.log("sortedCellTable: "+sortedCellTable);
				}
				break;
			default:
				break;
			}

		/*final SingleSelectionModel<Item> selectionModel = new SingleSelectionModel<Item>();
		selectionModel.addSelectionChangeHandler(new Handler() {
			@Override
			public void onSelectionChange(SelectionChangeEvent event) {
			}
		});*/
	}

	/**
	 * Displays the appropriate sorted icon in the header of the column for the given index.
	 *
	 * @param columnIndex
	 *            of the column to mark as sorted
	 * @param ascending
	 *            <code>true</code> for ascending icon, <code>false</code> for descending icon
	 */
	 public void setSortedColumn(int columnIndex, boolean ascending) {
		 GWT.log("Column index: "+columnIndex);
		 GWT.log("ascending: "+ascending);
	      Column<T, ?> column = sortedCellTable.getColumn(columnIndex);
	      if (column != null && column.isSortable()) {
	           ColumnSortInfo info = sortedCellTable.getColumnSortList().push(column);
//	           ColumnSortEvent.fire(cellTable, cellTable.getColumnSortList());
	     	   GWT.log("info.isAscending(): "+info.isAscending());
	           if (info.isAscending() != ascending) {
	        	   sortedCellTable.getColumnSortList().push(column);
	        	   ColumnSortEvent.fire(sortedCellTable, sortedCellTable.getColumnSortList());
	           }
	      }
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
	 * Sets the display properties.
	 *
	 * @param properties the new display properties
	 */
	public void setDisplayProperties(List<String> properties){
		this.displayProperties = properties;
	}


	/**
	 * Reset columns table.
	 */
	public void reInitColumnsTable(){
		int count = sortedCellTable.getColumnCount();
		for(int i=0;i<count;i++){
			sortedCellTable.removeColumn(0);
		}
		initTable(null, null, dataProvider);
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
	public T getSelectedItem() {
		return ssm.getSelectedObject();
	}


	/**
	 * Sets the empty table message.
	 *
	 * @param msg the new empty table message
	 */
	public void setEmptyTableMessage(String msg){
		msg = msg!=null?msg:WorkspaceExplorerConstants.WORKSPACE_FOLDER_EMPTY_MESSAGE;
		if(sortedCellTable!=null)
			sortedCellTable.setEmptyTableWidget(new Label(msg));
	}
}
