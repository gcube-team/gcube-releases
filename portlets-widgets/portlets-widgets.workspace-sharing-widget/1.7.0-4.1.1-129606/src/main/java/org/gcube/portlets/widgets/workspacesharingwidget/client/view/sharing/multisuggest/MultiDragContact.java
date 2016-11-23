package org.gcube.portlets.widgets.workspacesharingwidget.client.view.sharing.multisuggest;

import java.util.ArrayList;
import java.util.List;

import org.gcube.portlets.widgets.workspacesharingwidget.client.ConstantsSharing;
import org.gcube.portlets.widgets.workspacesharingwidget.client.resources.Resources;
import org.gcube.portlets.widgets.workspacesharingwidget.shared.InfoContactModel;

import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.Orientation;
import com.extjs.gxt.ui.client.Style.SortDir;
import com.extjs.gxt.ui.client.Style.VerticalAlignment;
import com.extjs.gxt.ui.client.dnd.GridDragSource;
import com.extjs.gxt.ui.client.dnd.GridDropTarget;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.store.Store;
import com.extjs.gxt.ui.client.store.StoreSorter;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.HorizontalPanel;
import com.extjs.gxt.ui.client.widget.Label;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.VerticalPanel;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.StoreFilterField;
import com.extjs.gxt.ui.client.widget.form.TextArea;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnData;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridCellRenderer;
import com.extjs.gxt.ui.client.widget.layout.CenterLayout;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.RowData;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;
import com.extjs.gxt.ui.client.widget.tips.QuickTip;

/**
 * 
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * @Jan 27, 2015
 *
 */
public class MultiDragContact extends ContentPanel {

	private static final String ALL_CONTACTS = "All Contacts";
	private static final String SHARE_WITH = "Share with...";
	
	public static final int WIDTH_CP = 597;
//	private static final int HEIGHT_DIALOG = 542;
	public static final int HEIGHT_GRID = 310;
	
	public static final int PADDING = 5;

	private GridDropTarget dropSource;
	private GridDropTarget dropTarget;
	private ListStore<InfoContactModel> storeSource = new ListStore<InfoContactModel>();
	private ListStore<InfoContactModel> storeTarget = new ListStore<InfoContactModel>();
	private TextArea textAreaAlreadyShared;
	private Grid<InfoContactModel> gridAllContacts;
	private Grid<InfoContactModel> gridShareWith;

	private Button buttonSelectedLeft;
	private Button buttonSelectedRight;
	private Button buttonAllRight;
	private Button buttonAllLeft;
//	private boolean hideOnPressOkButton;
//	private String headTitle;
	private String leftListContactsTitle;
	private String rightListContactsTitle;
	private Label allContacts;
	private Label shareWith;

	public MultiDragContact(String leftListContactsTitle, String rightListContactsTitle, boolean visibleAlreadyShared) {
		this(visibleAlreadyShared);
//		setHeadTitle(headTitle);
		setLeftListContactsTitle(leftListContactsTitle);
		setRightListContactsTitle(rightListContactsTitle);
	}

	/**
	 * 
	 * @param headTitle
	 * @param leftListContactsTitle
	 * @param rightListContactsTitle
	 * @param visibleAlreadyShared
	 * @param hideOnPressOk
	 */
	public MultiDragContact(boolean visibleAlreadyShared) {
		this.setHeaderVisible(false);
		this.setBodyBorder(false);
		this.setBorders(false);
		
//		setStyleAttribute("margin", "10px");
		setBodyStyle("padding: "+PADDING+"px; background: none");
		// SORTING STORE
		setGropUserStoreSorter(storeSource);
		setGropUserStoreSorter(storeTarget);

		ContentPanel cpAlreadyShared = new ContentPanel();
		cpAlreadyShared.setSize(WIDTH_CP, 60);
		cpAlreadyShared.setHeaderVisible(false);
		cpAlreadyShared.setVisible(visibleAlreadyShared);
		
		cpAlreadyShared.setLayout(new FitLayout());

		VerticalPanel vpShared = new VerticalPanel();
		vpShared.setVerticalAlign(VerticalAlignment.MIDDLE);
		vpShared.setHorizontalAlign(HorizontalAlignment.CENTER);
		vpShared.setStyleAttribute("padding", "5px");
		vpShared.setLayout(new FitLayout());
		HorizontalPanel hpSharedContacts = new HorizontalPanel();
		hpSharedContacts.setHorizontalAlign(HorizontalAlignment.CENTER);
		hpSharedContacts.setVerticalAlign(VerticalAlignment.MIDDLE);
		textAreaAlreadyShared = new TextArea();
		// textField.setFieldLabel("Already shared with");
		// textField.setHeight(30);
		textAreaAlreadyShared.setWidth(501);
		textAreaAlreadyShared.setHeight(43);
		cpAlreadyShared.setStyleAttribute("padding-bottom", "5px");
		textAreaAlreadyShared.setReadOnly(true);
		cpAlreadyShared.add(textAreaAlreadyShared);

		Label label = new Label("Already shared with");
		label.setStyleAttribute("padding-right", "10px");

		hpSharedContacts.add(label);
		hpSharedContacts.add(textAreaAlreadyShared);
		vpShared.add(hpSharedContacts);
		cpAlreadyShared.add(vpShared);
		add(cpAlreadyShared);

		final ContentPanel cp = new ContentPanel();
		cp.setSize(WIDTH_CP, 370);
		cp.setHeaderVisible(false);
		cp.setLayout(new RowLayout(Orientation.HORIZONTAL));

		final VerticalPanel vpAllContacts = new VerticalPanel();
		vpAllContacts.setHorizontalAlign(HorizontalAlignment.CENTER);
		
		allContacts = new Label(ALL_CONTACTS);
		vpAllContacts.add(allContacts);
		
		gridAllContacts = new Grid<InfoContactModel>(storeSource,
				createColumnModel());

		storeSource.setDefaultSort(InfoContactModel.FULLNAME, SortDir.ASC);
		storeSource.sort(InfoContactModel.FULLNAME, SortDir.ASC);

		storeTarget.setDefaultSort(InfoContactModel.FULLNAME, SortDir.ASC);
		storeTarget.sort(InfoContactModel.FULLNAME, SortDir.ASC);

		final StoreFilterField<InfoContactModel> filter = new StoreFilterField<InfoContactModel>() {

			@Override
			protected boolean doSelect(Store<InfoContactModel> store,
					InfoContactModel parent, InfoContactModel record,
					String property, String filter) {

				String name = record.getName();
				name = name.toLowerCase();
				if (name.contains(filter.toLowerCase())) {
					return true;
				}
				return false;
			}

		};

		// filter.setFieldLabel("Filter Contacts");
//		filter.setWidth(247);
		filter.setEmptyText("Filter All Contacts");
		HorizontalPanel hp = new HorizontalPanel();
		hp.setStyleAttribute("padding-top", "5px");
		hp.setStyleAttribute("padding-bottom", "5px");
		hp.add(filter);
		filter.bind(storeSource);

		gridAllContacts.setHeight(HEIGHT_GRID);
		gridAllContacts.setBorders(false);
		gridAllContacts.getView().setAutoFill(true);
		// gridAllContacts.setAutoExpandColumn(InfoContactModel.FULLNAME);
		gridAllContacts.setBorders(true);

		vpAllContacts.add(hp);
		vpAllContacts.add(gridAllContacts);

		RowData rowData = new RowData(.4, 1);
		rowData.setMargins(new Margins(6));
		cp.add(vpAllContacts, rowData);

		rowData = new RowData(.2, 1);
		rowData.setMargins(new Margins(6));
		LayoutContainer lc = createMoveContactsContainer();
		cp.add(lc, rowData);

		rowData = new RowData(.4, 1);
		rowData.setMargins(new Margins(6, 6, 6, 0));

		final VerticalPanel vpShareWith = new VerticalPanel();
		vpShareWith.setHorizontalAlign(HorizontalAlignment.CENTER);
		
		shareWith = new Label(SHARE_WITH);
		vpShareWith.add(shareWith);
		
		gridShareWith = new Grid<InfoContactModel>(storeTarget,createColumnModel());

		final StoreFilterField<InfoContactModel> filter2 = new StoreFilterField<InfoContactModel>() {

			@Override
			protected boolean doSelect(Store<InfoContactModel> store,
					InfoContactModel parent, InfoContactModel record,
					String property, String filter) {

				String name = record.getName();
				name = name.toLowerCase();
				if (name.contains(filter.toLowerCase())) {
					return true;
				}
				return false;
			}

		};

		// filter.setFieldLabel("Filter Contacts");
//		filter2.setWidth(247);
		filter2.setEmptyText("Filter Share with");

		hp = new HorizontalPanel();
		hp.setStyleAttribute("padding-top", "5px");
		hp.setStyleAttribute("padding-bottom", "5px");
		hp.add(filter2);
		filter2.bind(storeTarget);

		gridShareWith.setHeight(HEIGHT_GRID);
		gridShareWith.setBorders(false);
		gridShareWith.getView().setAutoFill(true);
		// gridShareWith.setAutoExpandColumn(InfoContactModel.FULLNAME);
		gridShareWith.setBorders(true);
		vpShareWith.add(hp);
		vpShareWith.add(gridShareWith);

		cp.add(vpShareWith, rowData);

		new GridDragSource(gridAllContacts);
		new GridDragSource(gridShareWith);

		dropSource = new GridDropTarget(gridAllContacts);
		dropSource.setAllowSelfAsSource(false);

		dropTarget = new GridDropTarget(gridShareWith);
		dropTarget.setAllowSelfAsSource(false);

		add(cp);

	    cp.addListener(Events.Render, new Listener<BaseEvent>() {

			@Override
			public void handleEvent(BaseEvent be) {
				int width = gridAllContacts.getWidth();
				filter.setWidth(width-2);
				filter2.setWidth(width-2);
				gridAllContacts.setWidth(width);
				vpAllContacts.setWidth(width+7);
				gridShareWith.setWidth(width);
				vpShareWith.setWidth(width+10);
				vpShareWith.layout();
				vpAllContacts.layout();
				cp.layout();
			}
		});

		// needed to enable quicktips (qtitle for the heading and qtip for the
		// content) that are setup in the change GridCellRenderer
		new QuickTip(gridAllContacts);
	}

	private LayoutContainer createMoveContactsContainer() {

		LayoutContainer lc = new LayoutContainer();
		lc.setLayout(new CenterLayout());
		VerticalPanel vp1 = new VerticalPanel();
		vp1.setHorizontalAlign(HorizontalAlignment.CENTER);
		vp1.setVerticalAlign(VerticalAlignment.MIDDLE);

		buttonSelectedLeft = new Button();
		buttonSelectedLeft.setIcon(Resources.getSelectedLeft());
		buttonSelectedLeft.setToolTip("Move selected contact/s from 'All Contact' to 'Share with'");
		buttonSelectedLeft
				.addSelectionListener(new SelectionListener<ButtonEvent>() {

					@Override
					public void componentSelected(ButtonEvent ce) {

						List<InfoContactModel> selectedItems = gridAllContacts
								.getSelectionModel().getSelectedItems();

						if (selectedItems != null && selectedItems.size() > 0) {

							for (InfoContactModel extendedInfoContactModel : selectedItems) {
								storeTarget.add(extendedInfoContactModel);
								storeSource.remove(extendedInfoContactModel);
							}
						}
					}
				});

		buttonSelectedRight = new Button();
		buttonSelectedRight.setIcon(Resources.getSelectedRight());
		buttonSelectedRight.setToolTip("Move selected contact/s from 'Share with' to 'All Contact'");

		buttonSelectedRight
				.addSelectionListener(new SelectionListener<ButtonEvent>() {

					@Override
					public void componentSelected(ButtonEvent ce) {

						List<InfoContactModel> selectedItems = gridShareWith
								.getSelectionModel().getSelectedItems();

						if (selectedItems != null && selectedItems.size() > 0) {

							for (InfoContactModel extendedInfoContactModel : selectedItems) {
								storeSource.add(extendedInfoContactModel);
								storeTarget.remove(extendedInfoContactModel);
							}
						}
					}
				});

		buttonAllLeft = new Button();
		buttonAllLeft.setIcon(Resources.getAllLeft());
		buttonAllLeft.setToolTip("Move all contact/s from 'All Contact' to 'Share with'");

		buttonAllLeft
				.addSelectionListener(new SelectionListener<ButtonEvent>() {

					@Override
					public void componentSelected(ButtonEvent ce) {

						if (storeSource != null && storeSource.getCount() > 0) {
							for (InfoContactModel extendedInfoContactModel : storeSource
									.getModels()) {
								storeTarget.add(extendedInfoContactModel);
								storeSource.remove(extendedInfoContactModel);
							}
						}

					}
				});

		buttonAllRight = new Button();
		buttonAllRight.setIcon(Resources.getAllRight());
		buttonAllRight
				.setToolTip("Move all contact/s from 'Share with' to 'All Contact'");

		buttonAllRight
				.addSelectionListener(new SelectionListener<ButtonEvent>() {

					@Override
					public void componentSelected(ButtonEvent ce) {

						if (storeTarget != null && storeTarget.getCount() > 0) {
							for (InfoContactModel extendedInfoContactModel : storeTarget
									.getModels()) {
								storeSource.add(extendedInfoContactModel);
								storeTarget.remove(extendedInfoContactModel);
							}
						}

					}
				});

		vp1.add(buttonSelectedLeft);
		vp1.add(buttonAllLeft);
		vp1.add(buttonSelectedRight);
		vp1.add(buttonAllRight);

		lc.add(vp1);

		vp1.addListener(Events.Render, new Listener<BaseEvent>() {

			@Override
			public void handleEvent(BaseEvent be) {
				buttonSelectedLeft.el().setStyleAttribute("margin-top", "40px");
				buttonAllLeft.el().setStyleAttribute("margin-top", "5px");
				buttonSelectedRight.el()
						.setStyleAttribute("margin-top", "20px");
				buttonAllRight.el().setStyleAttribute("margin", "5px");

			}
		});

		return lc;
	}

	private ColumnModel createColumnModel() {
		List<ColumnConfig> configs = new ArrayList<ColumnConfig>();

		ColumnConfig icon = new ColumnConfig("Icon", "", 25);
		configs.add(icon);

		ColumnConfig columnConfigDisplayName = new ColumnConfig(
				InfoContactModel.FULLNAME, "Name", 150);
		configs.add(columnConfigDisplayName);

		ColumnConfig type = new ColumnConfig(InfoContactModel.ISGROUP, "Type",
				50);
		configs.add(type);

		GridCellRenderer<InfoContactModel> iconRender = new GridCellRenderer<InfoContactModel>() {
			@Override
			public String render(InfoContactModel model, String property,
					ColumnData config, int rowIndex, int colIndex,
					ListStore<InfoContactModel> store,
					Grid<InfoContactModel> grid) {
				if (model.isGroup()) {
					return Resources.getIconGroup().getHTML();
				} else {
					return Resources.getIconUser().getHTML();
				}
			}
		};

		icon.setRenderer(iconRender);

		GridCellRenderer<InfoContactModel> displayNameCellRender = new GridCellRenderer<InfoContactModel>() {
			public String render(InfoContactModel model, String property,
					ColumnData config, int rowIndex, int colIndex,
					ListStore<InfoContactModel> store,
					Grid<InfoContactModel> grid) {

				if (model != null) {

					String value = model.get(property);
					if (value != null) {
						return "<span qtitle='' qtip='" + model.getLogin()
								+ "'>" + value + "</span>";
					}
				}

				return "";
			}
		};

		columnConfigDisplayName.setRenderer(displayNameCellRender);

		GridCellRenderer<InfoContactModel> typeRender = new GridCellRenderer<InfoContactModel>() {
			@Override
			public String render(InfoContactModel model, String property,
					ColumnData config, int rowIndex, int colIndex,
					ListStore<InfoContactModel> store,
					Grid<InfoContactModel> grid) {
				Boolean isGroup = (Boolean) model.get(property);
				String color = "#0F4FA8";
				String val = "";
				if (isGroup) {
					val = "Group";
					color = "#05316D";
					return "<span style='font-weight: bold; color:" + color
							+ "'>" + val + "</span>";
				} else {
					val = "User";
					return "<span style='font-weight: bold; color:" + color
							+ "'>" + val + "</span>";
				}
			}
		};

		type.setRenderer(typeRender);
		return new ColumnModel(configs);
	}

	public void addSourceContacts(List<InfoContactModel> listContact) {

		gridAllContacts.mask("", ConstantsSharing.LOADINGSTYLE);
		if (listContact != null && listContact.size() > 0) {

			// for (InfoContactModel infoContactModel : listContact) {
			//
			// if(infoContactModel.getName()!=null &&
			// !infoContactModel.getName().isEmpty())
			// storeSource.add(infoContactModel);
			// }

			storeSource.add(listContact);
		}
		gridAllContacts.unmask();
	}

	public void addAlreadySharedContacts(List<InfoContactModel> listContact) {

		gridShareWith.mask("", ConstantsSharing.LOADINGSTYLE);
		if (listContact != null && listContact.size() > 0) {

			String alreadyShared = "";
			for (int i = 0; i < listContact.size() - 1; i++)
				alreadyShared += listContact.get(i).getName() + ", ";

			alreadyShared += listContact.get(listContact.size() - 1).getName();

			textAreaAlreadyShared.setValue(alreadyShared);
		}
		gridShareWith.unmask();

	}

	public void addTargetContacts(List<InfoContactModel> listContact) {
		if (listContact != null && listContact.size() > 0)
			storeTarget.add(listContact);
	}

	/**
	 * @param infoContactModel
	 */
	public void addSourceContact(InfoContactModel contact) {
		if (contact != null)
			storeSource.add(contact);
	}
	
	public void addTargetContact(InfoContactModel contact) {
		if (contact != null)
			storeTarget.add(contact);
	}

	public List<InfoContactModel> getTargetListContact() {
		return storeTarget.getModels();
	}

	private void setGropUserStoreSorter(ListStore<InfoContactModel> store) {

		// Sorting files
		store.setStoreSorter(new StoreSorter<InfoContactModel>() {

			@Override
			public int compare(Store<InfoContactModel> store,
					InfoContactModel m1, InfoContactModel m2, String property) {
				boolean m1Group = m1.isGroup();
				boolean m2Group = m2.isGroup();

				if (m1Group && !m2Group) {
					return -1;
				} else if (!m1Group && m2Group) {
					return 1;
				}

				return m1.getName().compareToIgnoreCase(m2.getName());
			}
		});
	}
	
//	public void setHeadTitle(String headTitle){
//		this.headTitle = headTitle;
//		
//		if(headTitle==null)
//			this.setHeading(GROUP_DRAGGING_CONTACTS);
//		else
//			this.setHeading(headTitle);
//	}
	
	public void setLeftListContactsTitle(String leftListContactsTitle) {
		this.leftListContactsTitle = leftListContactsTitle;
		
		if(leftListContactsTitle==null)
			allContacts.setText(ALL_CONTACTS);
		else
			allContacts.setText(leftListContactsTitle);
	}
	
	public void setRightListContactsTitle(String rightListContactsTitle) {
		this.rightListContactsTitle = rightListContactsTitle;
		
		if(rightListContactsTitle==null)
			shareWith.setText(SHARE_WITH);
		else
			shareWith.setText(rightListContactsTitle);
	}

	public String getLeftListContactsTitle() {
		return leftListContactsTitle;
	}

//	public String getHeadTitle() {
//		return headTitle;
//	}

	public String getRightListContactsTitle() {
		return rightListContactsTitle;
	}

}
