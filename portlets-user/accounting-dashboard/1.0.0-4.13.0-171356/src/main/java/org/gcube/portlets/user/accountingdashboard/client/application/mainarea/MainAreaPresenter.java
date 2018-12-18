package org.gcube.portlets.user.accountingdashboard.client.application.mainarea;

import java.util.logging.Logger;

import org.gcube.portlets.user.accountingdashboard.client.application.ApplicationPresenter;
import org.gcube.portlets.user.accountingdashboard.client.application.controller.Controller;
import org.gcube.portlets.user.accountingdashboard.client.application.mainarea.filter.FilterAreaPresenter;
import org.gcube.portlets.user.accountingdashboard.client.application.mainarea.report.ReportAreaPresenter;
import org.gcube.portlets.user.accountingdashboard.client.place.NameTokens;

import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.HasUiHandlers;
import com.gwtplatform.mvp.client.Presenter;
import com.gwtplatform.mvp.client.View;
import com.gwtplatform.mvp.client.annotations.NameToken;
import com.gwtplatform.mvp.client.annotations.NoGatekeeper;
import com.gwtplatform.mvp.client.annotations.ProxyStandard;
import com.gwtplatform.mvp.client.presenter.slots.SingleSlot;
import com.gwtplatform.mvp.client.proxy.ProxyPlace;

/**
 * 
 * @author Giancarlo Panichi
 *
 */
public class MainAreaPresenter
		extends Presenter<MainAreaPresenter.MainAreaView, MainAreaPresenter.MainAreaPresenterProxy>
		implements MainAreaUiHandlers {

	@SuppressWarnings("unused")
	private static Logger logger = Logger.getLogger("");

	interface MainAreaView extends View, HasUiHandlers<MainAreaPresenter> {

	}

	@ProxyStandard
	@NameToken(NameTokens.MAIN_AREA)
	@NoGatekeeper
	interface MainAreaPresenterProxy extends ProxyPlace<MainAreaPresenter> {
	}

	public static final SingleSlot<FilterAreaPresenter> SLOT_FILTER = new SingleSlot<>();
	public static final SingleSlot<ReportAreaPresenter> SLOT_REPORT = new SingleSlot<>();

	@SuppressWarnings("unused")
	private EventBus eventBus;
	@SuppressWarnings("unused")
	private Controller controller;
	private FilterAreaPresenter filterAreaPresenter;
	private ReportAreaPresenter reportAreaPresenter;

	@Inject
	MainAreaPresenter(EventBus eventBus, MainAreaView view, MainAreaPresenterProxy proxy, Controller controller,
			FilterAreaPresenter filterAreaPresenter, ReportAreaPresenter reportAreaPresenter) {
		super(eventBus, view, proxy, ApplicationPresenter.SLOT_MAIN);
		this.eventBus = eventBus;
		this.controller = controller;
		this.filterAreaPresenter = filterAreaPresenter;
		this.reportAreaPresenter = reportAreaPresenter;
		getView().setUiHandlers(this);
		addProviders();
		bindToEvent();
	}

	private void addProviders() {

	}

	private void bindToEvent() {

	}

	@Override
	protected void onBind() {
		super.onBind();
		setInSlot(SLOT_FILTER, filterAreaPresenter);
		setInSlot(SLOT_REPORT, reportAreaPresenter);
	}

}
