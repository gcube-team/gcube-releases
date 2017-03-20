package org.gcube.portlets.user.td.expressionwidget.client;

import org.gcube.portlets.user.td.expressionwidget.client.expression.ConditionOnMultiColumnWidget;
import org.gcube.portlets.user.td.expressionwidget.client.utils.UtilsGXT3;
import org.gcube.portlets.user.td.gwtservice.client.rpc.TDGWTServiceAsync;
import org.gcube.portlets.user.td.gwtservice.shared.exception.TDGWTSessionExpiredException;
import org.gcube.portlets.user.td.gwtservice.shared.user.UserInfo;
import org.gcube.portlets.user.td.widgetcommonevent.shared.TRId;
import org.gcube.portlets.user.td.widgetcommonevent.shared.tr.TabResourceType;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.web.bindery.event.shared.EventBus;
import com.sencha.gxt.widget.core.client.container.Viewport;

/**
 * 
 * @author "Giancarlo Panichi" <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 *
 */
public class ExpressionWidgetEntry implements EntryPoint {
	private TRId trId;
	private static final String JSP_TAG_ID = "tdp";

	public void onModuleLoad() {
		Log.debug("ExpressionWidgetEntry");
		callHello();

	}

	private void init() {

		// TabularResource: [ id=86, type=STANDARD, lastTable=[ id=1159,
		// type=Generic]]

		EventBus eventBus= new SimpleEventBus();
		trId = new TRId("86", TabResourceType.STANDARD, "1159");
		
		/*String columnName="zwqvvx";*/

		// Column Expression Dialog
		// ColumnExpressionDialog expressionDialog=new
		// ColumnExpressionDialog("1", ColumnTypeCode.ATTRIBUTE,
		// ColumnDataType.Integer, eventBus);
		// expressionDialog.show();

		// Column Filter Dialog
		// id=2, tableId=8, tableType=Generic
		//ColumnFilterDialog columnFilterDialog=new ColumnFilterDialog(trId,
		// columnName, eventBus);
		//columnFilterDialog.show();

		// Multi Column Filter Dialog
		// MultiColumnFilterDialog multiColumnFilterDialog= new
		// MultiColumnFilterDialog(trId, eventBus);
		// multiColumnFilterDialog.show();

		// Replace Column By Expression Dialog
		//ReplaceColumnByExpressionDialog replaceColumnByExpression= new
		//ReplaceColumnByExpressionDialog(trId, columnName, eventBus);
	    //replaceColumnByExpression.show();

		// HelpReplaceColumnByExpressionDialog
		// HelpReplaceColumnByExpressionDialog help= new
		// HelpReplaceColumnByExpressionDialog(eventBus);
		// help.show();

		// Replace Expression Dialog
		//ColumnMockUp columnMockUp=new ColumnMockUp(null, null, "TestMock",
		//ColumnTypeCode.ATTRIBUTE, ColumnDataType.Text,"");
		//ReplaceExpressionDialog replaceExpressionDialog=new
		//ReplaceExpressionDialog(columnMockUp,trId,eventBus);
		//replaceExpressionDialog.show();
		
		MultiColumnFilterDialog cond = new MultiColumnFilterDialog(trId, eventBus);
		cond.show();
	

	}

	protected void startInDevMode(ConditionOnMultiColumnWidget cond) {
		try {

			RootPanel root = RootPanel.get(JSP_TAG_ID);
			Log.info("Root Panel: " + root);
			if (root == null) {
				Log.info("Div with id " + JSP_TAG_ID
						+ " not found, starting in dev mode");
				Viewport viewport = new Viewport();
				if (cond == null) {
					viewport.setWidget(new HTML(
							"Attention Condition Panel is null!"));

				} else {
					viewport.setWidget(cond);// new HTML("Hello!")
				}
				viewport.onResize();
				RootPanel.get().add(viewport);
			} else {
				Log.info("Application div with id " + JSP_TAG_ID
						+ " found, starting in portal mode");
				/*
				 * PortalViewport viewport = new PortalViewport();
				 * Log.info("Created Viewport");
				 * viewport.setEnableScroll(false);
				 * viewport.setWidget(mainWidget); Log.info("Set Widget");
				 * Log.info("getOffsetWidth(): " + viewport.getOffsetWidth());
				 * Log.info("getOffsetHeight(): " + viewport.getOffsetHeight());
				 * viewport.onResize(); root.add(viewport);
				 * Log.info("Added viewport to root");
				 */
			}
		} catch (Exception e) {
			e.printStackTrace();
			Log.error("Error in attach viewport:" + e.getLocalizedMessage());
		}
	}

	private void callHello() {
		TDGWTServiceAsync.INSTANCE.hello(new AsyncCallback<UserInfo>() {

			@Override
			public void onFailure(Throwable caught) {
				Log.info("No valid user found: " + caught.getMessage());
				if (caught instanceof TDGWTSessionExpiredException) {
					UtilsGXT3.alert("Error", "Expired Session");
				} else {
					UtilsGXT3.alert("Error", "No user found");
				}
			}

			@Override
			public void onSuccess(UserInfo result) {
				Log.info("Hello: " + result.getUsername());
				init();
			}

		});

	}

	
}
