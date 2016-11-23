package org.gcube.application.aquamaps.aquamapsportlet.client;

import org.gcube.application.aquamaps.aquamapsportlet.client.constants.types.ClientObjectType;
import org.gcube.application.aquamaps.aquamapsportlet.client.rpc.data.BoundingBox;
import org.gcube.application.aquamaps.aquamapsportlet.client.rpc.data.Msg;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.gwtext.client.core.EventObject;
import com.gwtext.client.widgets.Button;
import com.gwtext.client.widgets.Window;
import com.gwtext.client.widgets.event.ButtonListenerAdapter;
import com.gwtext.client.widgets.form.Checkbox;
import com.gwtext.client.widgets.form.FormPanel;
import com.gwtext.client.widgets.form.NumberField;
import com.gwtext.client.widgets.form.TextField;
import com.gwtext.client.widgets.form.ValidationException;
import com.gwtext.client.widgets.form.Validator;
import com.gwtext.client.widgets.layout.FitLayout;

public class ObjectEditing extends Window {

	TextField titleField= new TextField("Title");
	TextField bboxField= new TextField("Geographic map extent");
	NumberField thresholdField= new NumberField("Threshold");
	Checkbox gisField = new Checkbox("Enable GIS Generation");
	Button done= new Button("Done");
	Button cancel=new Button("Cancel");

	private ClientObjectType type=ClientObjectType.Biodiversity;
	
	private ObjectEditing instance=this;

	static private Validator bboxValidator= new Validator(){
		public boolean validate(String value) throws ValidationException {
			try{
				BoundingBox val=new BoundingBox();
				val.parse(value);
			}catch(Exception e){
				//				AquaMapsPortlet.get().showMessage("Invalid value, please use (N,S,W,E) convention");
				throw new ValidationException("Invalid value, please use (N,S,W,E) convention");

			}
			return true;
		}
	};


	public ObjectEditing(final String title, String typeStr, String bbox,Float threshold,boolean gis) {
		this.setFrame(true);
		this.type=ClientObjectType.valueOf(typeStr);
		this.setTitle("Editing "+title+" ("+type+")");
		this.setLayout(new FitLayout());
		this.setSize(380,190);
		this.setResizable(false);
		titleField.setValue(title);
		bboxField.setValue(bbox);
		bboxField.setValidator(bboxValidator);
		thresholdField.setMaxValue(1);
		thresholdField.setMinValue(0);
		thresholdField.setValue(threshold);
		gisField.setChecked(gis);

		final FormPanel form=new FormPanel();
		form.setFrame(true);
		form.setMonitorValid(true);
		form.setAutoHeight(true);
		form.setAutoWidth(true);
		form.add(titleField);
		form.add(bboxField);
		if(type.equals(ClientObjectType.Biodiversity))form.add(thresholdField);
		form.add(gisField);

		done.addListener(new ButtonListenerAdapter(){
			@Override
			public void onClick(Button button, EventObject e) {
				if(form.getForm().isValid()){
					AquaMapsPortlet.get().showLoading("Changing settings...", instance.getId());
					AquaMapsPortlet.localService.updateObject(title, titleField.getValueAsString(), type, bboxField.getValueAsString(), (Float) thresholdField.getValue(), gisField.getValue(),
							new AsyncCallback<Msg>() {
						public void onSuccess(Msg result) {
							AquaMapsPortlet.get().hideLoading(instance.getId());								
							AquaMapsObjectsSettingsPanel.get().reload();
							instance.close();
						}

						public void onFailure(Throwable caught) {
							AquaMapsPortlet.get().hideLoading(instance.getId());
							AquaMapsPortlet.get().showMessage("Unable to edit settings");
						}

					});
				}else AquaMapsPortlet.get().showMessage("Unable to continue, check fields value");
			}
		});
		cancel.addListener(new ButtonListenerAdapter(){
			@Override
			public void onClick(Button button, EventObject e) {
				instance.close();
			}
		});
		form.setButtons(new Button[]{done,cancel});
		this.add(form);
	}

}
