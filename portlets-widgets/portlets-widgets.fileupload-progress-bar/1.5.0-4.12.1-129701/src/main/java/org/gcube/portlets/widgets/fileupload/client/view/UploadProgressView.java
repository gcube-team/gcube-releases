package org.gcube.portlets.widgets.fileupload.client.view;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiFactory;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

public final class UploadProgressView extends Composite {

  interface UploadProgressViewUiBinder extends UiBinder<Widget, UploadProgressView> {
  }
  private static UploadProgressViewUiBinder uiBinder = GWT.create(UploadProgressViewUiBinder.class);
  private HandlerManager eventBus;
  
  @UiField
  FileSubmit fileSubmit;
  @UiField
  UploadProgress uploadProgress;
  UploadProgressDialog dlg;
  
  protected UploadProgressView(UploadProgressDialog dlg, HandlerManager eventBus) {
	this.eventBus = eventBus; 
	this.dlg = dlg;
    initWidget(uiBinder.createAndBindUi(this));   
  }
  
  /** Used by UiBinder to instantiate FileSubmit */
  @UiFactory 
  FileSubmit makeFileSubmit() {
    return new FileSubmit(dlg, eventBus);
  }
  
  protected void showRegisteringResult(boolean result, String customFeedback) {
	  fileSubmit.showRegisteringResult(result, customFeedback);
  }
  
  protected void submitForm() {
	  fileSubmit.submitForm();
  }
}
