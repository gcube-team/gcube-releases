package org.gcube.portlets.widgets.workspacesharingwidget.client.view.sharing.user;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.gcube.portlets.widgets.workspacesharingwidget.client.ConstantsSharing;
import org.gcube.portlets.widgets.workspacesharingwidget.client.WorkspaceSharingController;
import org.gcube.portlets.widgets.workspacesharingwidget.client.resources.Resources;
import org.gcube.portlets.widgets.workspacesharingwidget.shared.InfoContactModel;
import org.gcube.portlets.widgets.workspacesharingwidget.shared.UserVRE;

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
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.store.Store;
import com.extjs.gxt.ui.client.store.StoreSorter;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.HorizontalPanel;
import com.extjs.gxt.ui.client.widget.Label;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.VerticalPanel;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.extjs.gxt.ui.client.widget.form.ComboBox.TriggerAction;
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
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Random;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlexTable;

/**
 * 
 * @author Giancarlo Panichi
 *
 */
public class PanelMultiDragWorkspaceContact extends ContentPanel {

	//
	private InfoContactModel owner;
	private List<InfoContactModel> targetContact;

	//
	private List<UserVRE> vresList;
	private HashMap<UserVRE, List<InfoContactModel>> contactsMap;
	//
	private static final String ALL_CONTACTS = "All Contacts";
	private static final String SHARE_WITH = "Share with...";

	//
	public static final String LOADING = "Loading";
	public static final String LOADINGSTYLE = "x-mask-loading";

	private static final int HEIGHT_ALREADY_SHARED_PANEL = 80;
	private static final int WIDTH_ALREADY_SHARED_PANEL = 597;
	private static final int HEIGHT_VRE_PANEL = 40;
	private static final int WIDTH_VRE_PANEL = 597;
	private static final int HEIGHT_CONTACTS_PANEL = 400;
	private static final int WIDTH_CONTACTS_PANEL = 597;

	private static final int WIDTH_ALREADY_SHARED_TEXT_AREA = 508;
	private static final int HEIGHT_ALREADY_SHARED_TEXT_AREA = 68;

	private static final int HEIGHT_GRID = 270;

	private static final int PADDING = 5;

	//
	private GridDropTarget dropSource;
	private GridDropTarget dropTarget;
	private ListStore<InfoContactModel> storeSource = new ListStore<InfoContactModel>();
	private ListStore<InfoContactModel> storeTarget = new ListStore<InfoContactModel>();
	private Grid<InfoContactModel> gridAllContacts;
	private Grid<InfoContactModel> gridShareWith;

	private Button buttonSelectedLeft;
	private Button buttonSelectedRight;
	private Button buttonAllRight;
	private Button buttonAllLeft;

	private Label labelAllContacts;
	private Label labelShareWith;
	private ComboBox<UserVRE> vreListCombo;
	private ListStore<UserVRE> vreListStore;
	private TextArea textAreaAlreadyShared;
	private ContentPanel alreadySharedPanel;

	public PanelMultiDragWorkspaceContact(InfoContactModel owner, List<InfoContactModel> targetContact) {
		super();
		GWT.log("PanelMultiDragWorkspaceContact()");
		this.owner = owner;
		this.targetContact = targetContact;
		this.contactsMap = new HashMap<>();
		try {
			init();
			create();
			setAlreadySharedContacts();
			setTargetContacts();
			retrieveVREs();
		} catch (Throwable e) {
			GWT.log("Error in MultiDragContact: " + e.getLocalizedMessage(), e);
		}
	}

	private void init() {
		GWT.log("PanelMultiDragWorkspaceContact Init");
		setHeaderVisible(false);
		setBodyBorder(false);
		setBorders(false);
		// setStyleAttribute("margin", "10px");
		setBodyStyle("padding: " + PADDING + "px; background: none");

	}

	private void setAlreadySharedContacts() {
		GWT.log("PanelMultiDragWorkspaceContact Set Already Shared Contacts");
		String alreadyShared = "";

		if (targetContact != null && targetContact.size() > 0) {
			if (owner != null) {
				if (targetContact.contains(owner)) {
					targetContact.remove(owner);
				}
				if (targetContact.size() > 0) {
					alreadyShared = owner.getName() + ", ";
					for (int i = 0; i < targetContact.size(); i++) {
						if ((i + 1) == targetContact.size()) {
							alreadyShared += targetContact.get(i).getName();
						} else {
							alreadyShared += targetContact.get(i).getName() + ", ";
						}
					}
				} else {
					alreadyShared = owner.getName();
				}

			} else {
				for (int i = 0; i < targetContact.size(); i++) {
					if ((i + 1) == targetContact.size()) {
						alreadyShared += targetContact.get(i).getName();
					} else {
						alreadyShared += targetContact.get(i).getName() + ", ";
					}
				}
			}
		} else {
			if (owner != null) {
				alreadyShared = owner.getName();
			}
		}

		textAreaAlreadyShared.setValue(alreadyShared);

		GWT.log("PanelMultiDragWorkspaceContact TextArea Set");

	}

	private void setTargetContacts() {
		GWT.log("PanelMultiDragWorkspaceContact Set Target Contacts");

		if (targetContact != null && targetContact.size() > 0) {
			storeTarget.removeAll();
			storeTarget.add(targetContact);
			storeTarget.commitChanges();
		}
	}

	private void retrieveVREs() {
		GWT.log("PanelMultiDragWorkspaceContact load VREs");
		WorkspaceSharingController.rpcWorkspaceSharingService.getUserVREList(new AsyncCallback<List<UserVRE>>() {

			@Override
			public void onFailure(Throwable caught) {
				GWT.log("Error retrieving VRE for user: " + caught.getLocalizedMessage(), caught);
				MessageBox.alert("Error",
						ConstantsSharing.SERVER_ERROR + " retrieving VREs " + ConstantsSharing.TRY_AGAIN, null);

			}

			@Override
			public void onSuccess(List<UserVRE> result) {
				try {
					if (result != null && !result.isEmpty()) {
						GWT.log("Get all VREs loaded from server: " + result.size());
						vresList = result;
						vreListStore.removeAll();
						vreListStore.add(vresList);
						vreListStore.commitChanges();
					} else {
						GWT.log("No VREs found from server");
						vresList = new ArrayList<>();
						MessageBox.alert("Attention", "No VREs found " + ConstantsSharing.TRY_AGAIN, null);

					}
				} catch (Throwable e) {
					GWT.log("Error ");
				}
			}
		});

	}

	private void create() {
		GWT.log("PanelMultiDragWorkspaceContact Create Start");

		// Already Shared
		GWT.log("PanelMultiDragWorkspaceContact Already Shared Panel");
		alreadySharedPanel = new ContentPanel();
		alreadySharedPanel.setSize(WIDTH_ALREADY_SHARED_PANEL, HEIGHT_ALREADY_SHARED_PANEL);
		alreadySharedPanel.setHeaderVisible(false);
		alreadySharedPanel.setLayout(new FitLayout());
		alreadySharedPanel.setStyleAttribute("padding-bottom", "5px");

		HorizontalPanel hpSharedContacts = new HorizontalPanel();
		hpSharedContacts.setHorizontalAlign(HorizontalAlignment.CENTER);
		hpSharedContacts.setVerticalAlign(VerticalAlignment.MIDDLE);
		textAreaAlreadyShared = new TextArea();

		textAreaAlreadyShared.setWidth(WIDTH_ALREADY_SHARED_TEXT_AREA);
		textAreaAlreadyShared.setHeight(HEIGHT_ALREADY_SHARED_TEXT_AREA);
		textAreaAlreadyShared.setReadOnly(true);
		textAreaAlreadyShared.setStyleAttribute("background-color", "white");
		alreadySharedPanel.add(textAreaAlreadyShared);

		Label label = new Label("Already shared with");
		label.setStyleAttribute("padding", "6px");
		label.setStyleAttribute("font-size", "12px");
		label.setStyleAttribute("width", "68px");
		label.setStyleAttribute("display", "block");

		hpSharedContacts.add(label);
		hpSharedContacts.add(textAreaAlreadyShared);
		alreadySharedPanel.add(hpSharedContacts);
		add(alreadySharedPanel);

		//
		GWT.log("PanelMultiDragWorkspaceContact Create VREPanel");

		// VRE
		final ContentPanel vrePanel = new ContentPanel();
		vrePanel.setId("vre-panel-share-admin" + Random.nextInt());
		vrePanel.setSize(WIDTH_VRE_PANEL, HEIGHT_VRE_PANEL);
		vrePanel.setHeaderVisible(false);
		vrePanel.setLayout(new RowLayout(Orientation.HORIZONTAL));

		// VRE Select
		vreListStore = new ListStore<UserVRE>();

		vreListCombo = new ComboBox<UserVRE>();
		vreListCombo.setStore(vreListStore);
		vreListCombo.setDisplayField(UserVRE.GROUP_NAME);
		vreListCombo.setWidth(552);
		vreListCombo.setEditable(true);
		vreListCombo.setAllowBlank(false);
		vreListCombo.setForceSelection(true);
		vreListCombo.setTypeAhead(true);
		vreListCombo.setTriggerAction(TriggerAction.ALL);

		vreListCombo.addSelectionChangedListener(new SelectionChangedListener<UserVRE>() {

			@Override
			public void selectionChanged(SelectionChangedEvent<UserVRE> selected) {
				gridAllContacts.focus();
				retrieveContactList(selected.getSelectedItem());
			}
		});

		FlexTable vreLayout = new FlexTable();
		vreLayout.setCellSpacing(6);
		vreLayout.setWidth("565px");
		// FlexCellFormatter cellFormatter = vreLayout.getFlexCellFormatter();
		Label vreLabel = new Label("VRE:");
		vreLabel.setStyleAttribute("padding", "5px");
		vreLayout.setWidget(0, 0, vreLabel);
		vreLayout.setWidget(0, 1, vreListCombo);
		vrePanel.add(vreLayout);

		add(vrePanel);

		//
		GWT.log("PanelMultiDragWorkspaceContact Create Grids Panel");
		setGropUserStoreSorter(storeSource);
		setGropUserStoreSorter(storeTarget);

		final ContentPanel cp = new ContentPanel();
		cp.setId("All-Contacts-Share-Contacts-" + Random.nextInt());
		cp.setSize(WIDTH_CONTACTS_PANEL, HEIGHT_CONTACTS_PANEL);
		cp.setHeaderVisible(false);
		cp.setLayout(new RowLayout(Orientation.HORIZONTAL));

		final VerticalPanel vpAllContacts = new VerticalPanel();
		vpAllContacts.setHorizontalAlign(HorizontalAlignment.CENTER);

		labelAllContacts = new Label(ALL_CONTACTS);
		vpAllContacts.add(labelAllContacts);

		gridAllContacts = new Grid<InfoContactModel>(storeSource, createColumnModel());

		storeSource.setDefaultSort(InfoContactModel.FULLNAME, SortDir.ASC);
		storeSource.sort(InfoContactModel.FULLNAME, SortDir.ASC);

		storeTarget.setDefaultSort(InfoContactModel.FULLNAME, SortDir.ASC);
		storeTarget.sort(InfoContactModel.FULLNAME, SortDir.ASC);

		final StoreFilterField<InfoContactModel> filter = new StoreFilterField<InfoContactModel>() {

			@Override
			protected boolean doSelect(Store<InfoContactModel> store, InfoContactModel parent, InfoContactModel record,
					String property, String filter) {

				String name = record.getName();
				name = name.toLowerCase();
				if (name.contains(filter.toLowerCase())) {
					return true;
				}
				return false;
			}

		};

		//
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

		labelShareWith = new Label(SHARE_WITH);
		vpShareWith.add(labelShareWith);

		gridShareWith = new Grid<InfoContactModel>(storeTarget, createColumnModel());

		final StoreFilterField<InfoContactModel> filter2 = new StoreFilterField<InfoContactModel>() {

			@Override
			protected boolean doSelect(Store<InfoContactModel> store, InfoContactModel parent, InfoContactModel record,
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
		// filter2.setWidth(247);
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
				GWT.log("PanelMultiDragWorkspaceContact CP Render Called");
				int width = gridAllContacts.getWidth();
				filter.setWidth(width - 2);
				filter2.setWidth(width - 2);
				gridAllContacts.setWidth(width);
				vpAllContacts.setWidth(width + 7);
				gridShareWith.setWidth(width);
				vpShareWith.setWidth(width + 10);
				vpShareWith.layout();
				vpAllContacts.layout();
				cp.layout();

			}
		});

		// needed to enable quicktips (qtitle for the heading and qtip for the
		// content) that are setup in the change GridCellRenderer
		new QuickTip(gridAllContacts);
		GWT.log("PanelMultiDragWorkspaceContact view Created");
	}

	private void retrieveContactList(final UserVRE userVRE) {

		if (!contactsMap.isEmpty() && contactsMap.containsKey(userVRE)) {
			List<InfoContactModel> contacts = contactsMap.get(userVRE);
			updateStoreSource(contacts);

		} else {
			gridAllContacts.mask(LOADING, LOADINGSTYLE);
			GWT.log("PanelMultiDragWorkspaceContact retrieve contacts list");
			WorkspaceSharingController.rpcWorkspaceSharingService.getAllContactsByVRE(userVRE,
					new AsyncCallback<List<InfoContactModel>>() {

						@Override
						public void onFailure(Throwable e) {
							GWT.log("Error retrieving all contacts by VRE: " + e.getLocalizedMessage(), e);
							MessageBox.alert("Error", ConstantsSharing.SERVER_ERROR
									+ " retrieving Contacts in this VRE " + ConstantsSharing.TRY_AGAIN, null);
							gridAllContacts.unmask();
							gridAllContacts.repaint();
						}

						@Override
						public void onSuccess(List<InfoContactModel> result) {
							GWT.log("Contacts retrieved from server");
							contactsMap.put(userVRE, result);
							updateStoreSource(result);
							gridAllContacts.unmask();
							gridAllContacts.repaint();
						}
					});
		}
	}

	private void updateStoreSource(List<InfoContactModel> result) {
		GWT.log("Update SourceStore");
		storeSource.removeAll();
		result.removeAll(storeTarget.getModels());
		result.remove(owner);
		storeSource.add(result);
		storeSource.commitChanges();

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
		buttonSelectedLeft.addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {

				List<InfoContactModel> selectedItems = gridAllContacts.getSelectionModel().getSelectedItems();

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

		buttonSelectedRight.addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {

				List<InfoContactModel> selectedItems = gridShareWith.getSelectionModel().getSelectedItems();

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

		buttonAllLeft.addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {

				if (storeSource != null && storeSource.getCount() > 0) {
					for (InfoContactModel extendedInfoContactModel : storeSource.getModels()) {
						storeTarget.add(extendedInfoContactModel);
						storeSource.remove(extendedInfoContactModel);
					}
				}

			}
		});

		buttonAllRight = new Button();
		buttonAllRight.setIcon(Resources.getAllRight());
		buttonAllRight.setToolTip("Move all contact/s from 'Share with' to 'All Contact'");

		buttonAllRight.addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {

				if (storeTarget != null && storeTarget.getCount() > 0) {
					for (InfoContactModel extendedInfoContactModel : storeTarget.getModels()) {
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
				buttonSelectedRight.el().setStyleAttribute("margin-top", "20px");
				buttonAllRight.el().setStyleAttribute("margin", "5px");

			}
		});

		return lc;
	}

	private ColumnModel createColumnModel() {
		List<ColumnConfig> configs = new ArrayList<ColumnConfig>();

		ColumnConfig icon = new ColumnConfig("Icon", "", 25);
		configs.add(icon);

		ColumnConfig columnConfigDisplayName = new ColumnConfig(InfoContactModel.FULLNAME, "Name", 150);
		configs.add(columnConfigDisplayName);

		ColumnConfig type = new ColumnConfig(InfoContactModel.ISGROUP, "Type", 50);
		configs.add(type);

		GridCellRenderer<InfoContactModel> iconRender = new GridCellRenderer<InfoContactModel>() {
			@Override
			public String render(InfoContactModel model, String property, ColumnData config, int rowIndex, int colIndex,
					ListStore<InfoContactModel> store, Grid<InfoContactModel> grid) {
				if (model.isGroup()) {
					return Resources.getIconGroup().getHTML();
				} else {
					return Resources.getIconUser().getHTML();
				}
			}
		};

		icon.setRenderer(iconRender);

		GridCellRenderer<InfoContactModel> displayNameCellRender = new GridCellRenderer<InfoContactModel>() {
			public String render(InfoContactModel model, String property, ColumnData config, int rowIndex, int colIndex,
					ListStore<InfoContactModel> store, Grid<InfoContactModel> grid) {

				if (model != null) {

					String value = model.get(property);
					if (value != null) {
						return "<span qtitle='' qtip='" + model.getLogin() + "'>" + value + "</span>";
					}
				}

				return "";
			}
		};

		columnConfigDisplayName.setRenderer(displayNameCellRender);

		GridCellRenderer<InfoContactModel> typeRender = new GridCellRenderer<InfoContactModel>() {
			@Override
			public String render(InfoContactModel model, String property, ColumnData config, int rowIndex, int colIndex,
					ListStore<InfoContactModel> store, Grid<InfoContactModel> grid) {
				Boolean isGroup = (Boolean) model.get(property);
				String color = "#0F4FA8";
				String val = "";
				if (isGroup) {
					val = "Group";
					color = "#05316D";
					return "<span style='font-weight: bold; color:" + color + "'>" + val + "</span>";
				} else {
					val = "User";
					return "<span style='font-weight: bold; color:" + color + "'>" + val + "</span>";
				}
			}
		};

		type.setRenderer(typeRender);
		return new ColumnModel(configs);
	}

	public List<InfoContactModel> getTargetListContact() {
		List<InfoContactModel> target = new ArrayList<>();
		target.addAll(storeTarget.getModels());
		return target;
	}

	private void setGropUserStoreSorter(ListStore<InfoContactModel> store) {

		// Sorting files
		store.setStoreSorter(new StoreSorter<InfoContactModel>() {

			@Override
			public int compare(Store<InfoContactModel> store, InfoContactModel m1, InfoContactModel m2,
					String property) {
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

	public void setLeftListContactsTitle(String leftListContactsTitle) {
		if (leftListContactsTitle == null)
			labelAllContacts.setText(ALL_CONTACTS);
		else
			labelAllContacts.setText(leftListContactsTitle);
	}

	public void setRightListContactsTitle(String rightListContactsTitle) {

		if (rightListContactsTitle == null)
			labelShareWith.setText(SHARE_WITH);
		else
			labelShareWith.setText(rightListContactsTitle);
	}

	public String getLeftListContactsTitle() {
		return labelAllContacts.getText();
	}

	public String getRightListContactsTitle() {
		return labelShareWith.getText();
	}

}
