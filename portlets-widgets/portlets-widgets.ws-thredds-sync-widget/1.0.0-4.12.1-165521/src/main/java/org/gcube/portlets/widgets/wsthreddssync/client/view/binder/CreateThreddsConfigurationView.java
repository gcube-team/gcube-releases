package org.gcube.portlets.widgets.wsthreddssync.client.view.binder;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.gcube.portal.wssynclibrary.shared.thredds.ThCatalogueBean;
import org.gcube.portlets.widgets.wsthreddssync.client.WsThreddsWidget;
import org.gcube.portlets.widgets.wsthreddssync.client.view.FormatUtil;
import org.gcube.portlets.widgets.wsthreddssync.shared.GcubeScope;

import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.ControlGroup;
import com.github.gwtbootstrap.client.ui.Fieldset;
import com.github.gwtbootstrap.client.ui.ListBox;
import com.github.gwtbootstrap.client.ui.Pager;
import com.github.gwtbootstrap.client.ui.TextBox;
import com.github.gwtbootstrap.client.ui.constants.ControlGroupType;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Style.Float;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.DomEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;


// TODO: Auto-generated Javadoc
/**
 * The Class CreateThreddsConfigurationView.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Feb 15, 2018
 */
public abstract class CreateThreddsConfigurationView extends Composite {

	/** The ui binder. */
	private static CreateThreddsConfigurationViewUiBinder uiBinder =
		GWT.create(CreateThreddsConfigurationViewUiBinder.class);


	/**
	 * The Interface CreateFolderConfigurationToThreddsSyncUiBinder.
	 *
	 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
	 * Feb 14, 2018
	 */
	interface CreateThreddsConfigurationViewUiBinder
		extends UiBinder<Widget, CreateThreddsConfigurationView> {
	}

	/** The pager. */
	@UiField
	Pager pager;


	@UiField
	ListBox field_select_scope;

	@UiField
	Button butt_create_new_catalogue;

	@UiField
	Button butt_add_catalogue;


	@UiField
	ListBox field_select_catalogue_name;


	/** The field catalogue name. */
	@UiField
	TextBox field_catalogue_name;

	/** The field folder name. */
	@UiField
	TextBox field_folder_name;


	/** The cg catalogue name. */
	@UiField
	ControlGroup cg_catalogue_name;

	@UiField
	ControlGroup cg_folder_name;

	/** The cg folder name. */
	//@UiField
	//ControlGroup cg_remote_path;

	@UiField
	ControlGroup cg_select_catalogue_name;

	@UiField
	Fieldset fieldset_add_catalogue_bean;


	/** The folder id. */
	private String folderId;

	/** The map VR es. */
	private Map<String, GcubeScope> mapScopes = new HashMap<String, GcubeScope>();


	private Map<String, List<ThCatalogueBean>> mapCatalogueNames = new HashMap<String, List<ThCatalogueBean>>();


	private String currentScope;


	/**
	 * Submit handler.
	 */
	public abstract void submitHandler();

	/**
	 * Sets the error.
	 *
	 * @param visible the visible
	 * @param error the error
	 */
	public abstract void setError(boolean visible, String error);



	/**
	 * Sets the confirm.
	 *
	 * @param visible the visible
	 * @param msg the msg
	 */
	public abstract void setConfirm(boolean visible, String msg);


	/**
	 * Because this class has a default constructor, it can
	 * be used as a binder template. In other words, it can be used in other
	 * *.ui.xml files as follows:
	 * <ui:UiBinder xmlns:ui="urn:ui:com.google.gwt.uibinder"
	 *   xmlns:g="urn:import:**user's package**">
	 *  <g:**UserClassName**>Hello!</g:**UserClassName>
	 * </ui:UiBinder>
	 * Note that depending on the widget that is used, it may be necessary to
	 * implement HasHTML instead of HasText.
	 *
	 * @param folderId the folder id
	 */
	public CreateThreddsConfigurationView(String folderId) {
		this.folderId = folderId;

		initWidget(uiBinder.createAndBindUi(this));

		pager.getLeft().setVisible(false);

		pager.getRight().addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				setError(false, "");
				boolean isValid = validateSubmit();
				if(isValid)
					submitHandler();

			}
		});

		cg_catalogue_name.getElement().getStyle().setMarginTop(20, Unit.PX);

		field_select_scope.addChangeHandler(new ChangeHandler() {

			@Override
			public void onChange(ChangeEvent event) {
				//String scope = field_select_scope.getSelectedItemText();
				String value = field_select_scope.getSelectedValue();
				GWT.log("Selected SCOPE: "+value);
				loadCatalogueNamesForScope(value);
			}
		});


		WsThreddsWidget.wsThreddsSyncService.getListOfScopesForLoggedUser(new AsyncCallback<List<GcubeScope>>() {

			@Override
			public void onSuccess(List<GcubeScope> result) {


				for (GcubeScope gcubeScope : result) {
					String toValue = FormatUtil.toScopeValue(gcubeScope);
					mapScopes.put(gcubeScope.getScopeName(), gcubeScope);
					field_select_scope.addItem(toValue, gcubeScope.getScopeName());

				}

				if(result.size()>0){
					field_select_scope.setSelectedValue(result.get(0).getScopeName());
					//field_select_vre.setSelectedIndex(0);
					//field_select_vre.fireEvent(DomEvent.fireNativeEvent(nativeEvent, handlerSource););
					DomEvent.fireNativeEvent(Document.get().createChangeEvent(), field_select_scope);
				}
			}

			@Override
			public void onFailure(Throwable caught) {
				// TODO Auto-generated method stub

			}
		});


		butt_create_new_catalogue.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {

				fieldset_add_catalogue_bean.setVisible(true);
			}
		});

		butt_add_catalogue.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				cg_catalogue_name.setType(ControlGroupType.NONE);
				setError(false, "");

				if (field_catalogue_name.getValue() == null || field_catalogue_name.getValue().isEmpty()) {
					cg_catalogue_name.setType(ControlGroupType.ERROR);
					setError(true, "Catalogue Name is required");
					return;
				}

				addCatalogueNameForCurrentScope(field_catalogue_name.getValue());
				fieldset_add_catalogue_bean.setVisible(false);
				initFieldCatalogueName();
			}
		});


		butt_add_catalogue.getElement().getStyle().setFloat(Float.RIGHT);
		butt_add_catalogue.getElement().getStyle().setMarginRight(90, Unit.PX);

	}


	/**
	 * Fill selectable catalogue names.
	 *
	 * @param scope the scope
	 * @param result the result
	 */
	private void fillSelectableCatalogueNames(String scope, List<ThCatalogueBean> result){

		field_select_catalogue_name.clear();
		field_select_catalogue_name.setEnabled(true);

		if(result!=null && result.size()>0){

			mapCatalogueNames.put(scope, result);
			for (ThCatalogueBean thCatalogueBean : result) {
				field_select_catalogue_name.addItem(thCatalogueBean.getName(), thCatalogueBean.getName());

				if(thCatalogueBean.isDefault()){
					field_select_catalogue_name.setSelectedValue(thCatalogueBean.getName());
				}

			}
		}
	}



	/**
	 * Adds the catalogue name for current scope.
	 *
	 * @param catalogueName the catalogue name
	 */
	private void addCatalogueNameForCurrentScope(String catalogueName) {

		List<ThCatalogueBean> listCtlgs = mapCatalogueNames.get(currentScope);

		if(listCtlgs==null){
			listCtlgs = new ArrayList<ThCatalogueBean>();
		}

		listCtlgs.add(new ThCatalogueBean(catalogueName, null, true));
		fillSelectableCatalogueNames(currentScope, listCtlgs);
	}

	/**
	 * Load catalogue names for scope.
	 *
	 * @param scope the scope
	 */
	private void loadCatalogueNamesForScope(String scope){
		this.currentScope = scope;
		field_select_catalogue_name.setEnabled(false);
		initFieldCatalogueName();

		List<ThCatalogueBean> listCtlgs = mapCatalogueNames.get(scope);

		if(listCtlgs==null){

			WsThreddsWidget.wsThreddsSyncService.getAvailableCataloguesForScope(scope, new AsyncCallback<List<ThCatalogueBean>>() {

				@Override
				public void onFailure(Throwable caught) {
					GWT.log("Error: "+caught.getMessage());
					field_select_catalogue_name.setEnabled(true);

				}

				@Override
				public void onSuccess(List<ThCatalogueBean> result) {
					GWT.log("Fill Catalogue Names: "+result);
					fillSelectableCatalogueNames(currentScope, result);

				}
			});
		}else{

			fillSelectableCatalogueNames(scope, listCtlgs);
		}
	}


	/**
	 * Inits the field catalogue name.
	 */
	private void initFieldCatalogueName() {

		field_catalogue_name.setText("");
		field_folder_name.setText("");
	}

	/**
	 * Validate submit.
	 *
	 * @return true, if successful
	 */
	protected boolean validateSubmit() {
		cg_catalogue_name.setType(ControlGroupType.NONE);
		cg_folder_name.setType(ControlGroupType.NONE);
		//cg_remote_path.setType(ControlGroupType.NONE);

		if(field_select_catalogue_name.getSelectedItemText()==null){
			cg_catalogue_name.setType(ControlGroupType.ERROR);
			setError(true, "You must select a Catalogue!!!");
			return false;
		}

		if(field_folder_name.getValue() == null || field_folder_name.getValue().isEmpty()){
			cg_folder_name.setType(ControlGroupType.WARNING);
			setConfirm(true, "The Catalogue Entry is empty. Do you want continue anyway?");
			return false;
		}else if(field_folder_name.getValue().startsWith("/")){
			cg_folder_name.setType(ControlGroupType.ERROR);
			setError(true, "The Catalogue Entry must be a relative URL. It does not start with '/'");
			return false;
		}

		return true;
	}


	/**
	 * Gets the selected catalogue.
	 *
	 * @return the selected catalogue
	 */
	public ThCatalogueBean getSelectedCatalogue(){

		List<ThCatalogueBean> listCatalogues = mapCatalogueNames.get(currentScope);

		String selectedCatalogueName = getCatalogueName();
		for (ThCatalogueBean thCatalogueBean : listCatalogues) {
			if(thCatalogueBean.getName().compareTo(selectedCatalogueName)==0)
				return thCatalogueBean;
		}

		return null;
	}


	/**
	 * Gets the folder name.
	 *
	 * @return the folder name
	 */
	public String getFolderName(){
		return field_folder_name.getValue();
	}


	/**
	 * Gets the catalogue name.
	 *
	 * @return the catalogue name
	 */
	public String getCatalogueName(){
		return field_select_catalogue_name.getSelectedItemText();
	}



	/**
	 * Gets the selected scope.
	 *
	 * @return the selected scope
	 */
	public GcubeScope getSelectedScope(){
		//String item = field_select_scope.getSelectedItemText();
		String text = field_select_scope.getSelectedValue();
		return mapScopes.get(text);
	}




}
