package org.gcube.portlets.widgets.file_dw_import_wizard.client;

import org.gcube.portlets.widgets.file_dw_import_wizard.client.rpc.ImportService;
import org.gcube.portlets.widgets.file_dw_import_wizard.client.source.local.LocalSource;
import org.gcube.portlets.widgets.file_dw_import_wizard.client.source.workspace.WorkspaceSource;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class filedwimportwizard implements EntryPoint {

  public void onModuleLoad() {
	  ImportService.Utility.getInstance().init(new AsyncCallback<Void>() {

			@Override
			public void onFailure(Throwable caught) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onSuccess(Void result) {
				// TODO Auto-generated method stub
				
			}
		} );
		  //CSVTargetRegistry.getInstance().add(new DemoCSVTarget());
		ImportWizard importWizard = new ImportWizard("StatisticalFileTarget", LocalSource.INSTANCE, WorkspaceSource.INSTANCE);

//		  ImportWizard importWizard = new ImportWizard("StatisticalFileTarget");
			importWizard.show();
  }
}
