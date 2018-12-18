/**
 * 
 */
package org.gcube.portlets.widgets.file_dw_import_wizard.client;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.gcube.portlets.widgets.file_dw_import_wizard.client.general.WizardCard;
import org.gcube.portlets.widgets.file_dw_import_wizard.client.general.WizardWindow;
import org.gcube.portlets.widgets.file_dw_import_wizard.client.rpc.ImportService;
import org.gcube.portlets.widgets.file_dw_import_wizard.client.source.local.LocalSource;
import org.gcube.portlets.widgets.file_dw_import_wizard.client.source.local.Source;

import com.extjs.gxt.ui.client.event.WindowEvent;
import com.extjs.gxt.ui.client.event.WindowListener;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.i18n.client.AutoDirectionHandler.Target;
import com.google.gwt.user.client.rpc.AsyncCallback;


public class ImportWizard extends WizardWindow   {

	public static final ArrayList<Source> DEFAULT_SOURCES = new ArrayList<Source>();
	static {
		DEFAULT_SOURCES.add(new LocalSource());
	}

	protected ImportSession importSession;

	/**
	 * The id of the {@link Target} to use.
	 * @param targetId
	 */
	public ImportWizard(String targetId)
	{
		this(targetId, DEFAULT_SOURCES);
	}
	
	public static <T> ArrayList<T> asList(T ... array)
	{
		ArrayList<T> list = new ArrayList<T>();
		if (array!=null) for (T item:array) list.add(item);
		return list;
	}
	public ImportWizard(String targetId, Source ...source )
	{
		this(targetId, asList(source));
	}
	
	/**
	 * Creates a new Import Wizard with the specified {@link CSVSource} sources.
	 * @param sources the csv source to use.
	 */
	public ImportWizard(final String targetId,  ArrayList<Source> source)
	{
		super("Darwin Core/General  import wizard");
		setWidth(450);
		setHeight(350);
	

		importSession = new ImportSession();

		WizardCard fileSelectionCard = new FileTypeSelectionCard(importSession);
		addCard(fileSelectionCard);
		Logger logger = Logger.getLogger("logger");
		
		logger.log(Level.SEVERE,"Aftere sourceselection type is : " + importSession.getType());
		WizardCard sourceSelectionCard = new SourceSelectionCard(importSession, source);
		addCard(sourceSelectionCard);
		
		WizardCard uploadCard = new UploadCard(importSession, source);
		addCard(uploadCard); 

		logger.log(Level.SEVERE,"Aftere uploadCard type is : " + importSession.getType());

		WizardCard creationCard = new ImportCard(importSession);
		addCard(creationCard);
		logger.log(Level.SEVERE,"Aftere creationCard type is : " + importSession.getType());

		addWindowListener(new WindowListener(){

			@Override
			public void windowShow(WindowEvent we) {
				mask("Initializing...");
			
				Scheduler.get().scheduleDeferred(new ScheduledCommand() {

					@Override
					public void execute() {
						
						ImportService.Utility.getInstance().createSessionId(targetId, importSession.getType(),new AsyncCallback<String>() {

							@Override
							public void onSuccess(String result) {
								importSession.setId(result);
								unmask();
							}
							
							@Override
							public void onFailure(Throwable caught) {
								showErrorAndHide("Error initializing the wizard", "Error getting the sessionId", "", caught);
							}
						});
					}

				});
			}
		});	
	}
}
